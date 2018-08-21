package at.ameise.devicelocation.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.model.ReportDao;

/**
 * This service checks the reports. Old 
 * <p>
 */
public class CleanUpService extends IntentService {

    private static final String ACTION_CLEAN_UP = "at.ameise.devicelocation.service.action.CLEAN_UP";
    private static final long MINUTES_AFTER_REPORT = 1 * DateUtils.MINUTE_IN_MILLIS;

    private static PendingIntent pendingIntent;

    public CleanUpService() {
        super("CleanUpService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void scheduleCleanUpService(Context context) {

        long interval = MINUTES_AFTER_REPORT / 2;
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if(pendingIntent == null)
            pendingIntent = PendingIntent.getService(context, 0, new Intent(context, CleanUpService.class)
            .setAction(ACTION_CLEAN_UP), 0);

        Log.d("CleanUp", "Scheduling service...");
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingIntent);
    }

    /**
     * Cancels the service.
     *
     * @param context   the context.
     */
    public static void cancelCleanUpService(Context context) {

        if(pendingIntent != null)
            ((AlarmManager) context.getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("CleanUp", "Got intent...");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLEAN_UP.equals(action)) {
                Log.d("CleanUp", "Doing ma thing...");
                handleActionCleanUp();
            }
        }
    }

    /**
     * Handle action CheckReports in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCleanUp() {

        if(((DeviceLocation) getApplication()).getDaoSession() != null) {
            ReportDao reportDao = ((DeviceLocation) getApplication()).getDaoSession().getReportDao();
            List<Report> reports = reportDao.loadAll();

            for(Report report : reports) {

                Log.d("CleanUp", "Checking report...");
                report.refresh();
                report.resetParamCellTowers();
                report.resetParamWifiAccessPoints();

                boolean notMoreThanMinutesAgo = (MINUTES_AFTER_REPORT + report.getTimestamp()) >= Calendar.getInstance().getTimeInMillis();
                boolean gotGpsData = report.getGpsLatitude() != null;
                boolean gotMlsData = report.getMlsLatitude() != null;
                boolean gotWifiCellData = (report.getParamCellTowers() == null || report.getParamCellTowers().isEmpty())
                    && (report.getParamWifiAccessPoints() == null || report.getParamWifiAccessPoints().isEmpty());
                boolean isIncomplete = !gotGpsData || !gotMlsData;

                if (notMoreThanMinutesAgo) {

                    if (!gotGpsData) {

                        startService(new Intent(this, GPSLocationService.class)
                            .putExtra(GPSLocationService.KEY_GPS_REQUEST, report.getId()));
                    }

                    if (gotWifiCellData && !gotMlsData) {

                        startService(new Intent(getApplicationContext(), GeolocateApiAccessService.class)
                            .putExtra(GeolocateApiAccessService.KEY_API_REQUEST, report.getId()));

                    } else if (!gotWifiCellData) {

                        startService(new Intent(this, CellAndWifiScanService.class)
                            .putExtra(CellAndWifiScanService.KEY_SCAN_REQUEST, report.getId()));
                    }

                } else if (isIncomplete) {

                    Log.d("CleanUp", "Report incomplete and old.");
                    long reportId = report.getId();
                    reportDao.delete(report);
                    Toast.makeText(this, "Deleted incomplete report", Toast.LENGTH_LONG).show();

                    sendBroadcast(new Intent(Events.ACTION_DATASET_CHANGED)
                        .putExtra(Events.KEY_REPORT_ID, reportId));
                }
            }
        }
    }
}
