package at.ameise.devicelocation.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.model.ReportDao;

/**
 * Loader which load one specific report.
 *
 * Created by mariogastegger on 20.01.17.
 */
public final class ReportLoader extends AsyncTaskLoader<Report> {

    private static final String TAG = ReportLoader.class.getSimpleName();

    private Report mReport;
    private Long mItemId;
    private BroadcastReceiver mObserver;

    public ReportLoader(Context context, Long itemId) {
        super(context);
        mItemId = itemId;
    }

    @Override
    public Report loadInBackground() {

        Report report;

        Log.d(TAG, "(Re)Loading report "+mItemId);
        ReportDao reportDao = ((DeviceLocation) getContext().getApplicationContext()).getDaoSession().getReportDao();

        //This is necessary to get a different object. Which in turn assures,
        // that onLoadFinished will be called. See http://stackoverflow.com/a/18983190
        if(mReport != null)
            reportDao.detach(mReport);

        report = reportDao.load(mItemId);
        report.resetParamWifiAccessPoints();
        report.resetParamCellTowers();

        return report;
    }

    @Override
    public void deliverResult(Report data) {

        if(isReset())
            return;

        mReport = data;

        if(isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {

        if(mReport != null)
            super.deliverResult(mReport);

        if(mObserver == null) {
            mObserver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    if(intent.hasExtra(Events.KEY_REPORT_ID)
                    && intent.getLongExtra(Events.KEY_REPORT_ID, -1) == mItemId) {

                        Log.d(TAG, String.format("Received %s; report_id %d", intent.getAction(), intent.getLongExtra(Events.KEY_REPORT_ID, -1)));
                        ReportLoader.this.onContentChanged();

                    } else {

                        Log.d(TAG, String.format("Received %s", intent.getAction()));
                        Log.d(Events.class.getSimpleName(), "Content change did not affect the currently loaded report. Ignoring it!");
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Events.ACTION_DATASET_CHANGED);
            getContext().registerReceiver(mObserver, filter);
        }

        if(takeContentChanged() || mReport == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();
    }

    @Override
    protected void onReset() {

        onStopLoading();

        if(mReport != null)
            mReport = null;

        if(mObserver != null) {
            getContext().unregisterReceiver(mObserver);
            mObserver = null;
        }
    }
}
