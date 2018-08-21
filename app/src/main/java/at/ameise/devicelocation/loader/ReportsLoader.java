package at.ameise.devicelocation.loader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.model.DaoSession;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.model.ReportDao;

/**
 * Loader which loads all reports.
 *
 * Created by mariogastegger on 20.01.17.
 */
public final class ReportsLoader extends AsyncTaskLoader<List<Report>> {

    private static final String TAG = ReportsLoader.class.getSimpleName();

    private List<Report> mReports;
    private BroadcastReceiver mObserver;

    public ReportsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Report> loadInBackground() {

        List<Report> reports = new ArrayList<>();

        DaoSession daoSession = ((DeviceLocation) getContext().getApplicationContext()).getDaoSession();
        ReportDao reportDao = daoSession.getReportDao();

        reports.addAll(reportDao.queryBuilder().list());

        for(Report report : reports) {
            report.resetParamWifiAccessPoints();
            report.resetParamCellTowers();
        }

        return reports;
    }

    @Override
    public void deliverResult(List<Report> data) {

        if(isReset())
            return;

        mReports = data;

        if(isStarted())
            super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {

        if(mReports != null)
            super.deliverResult(mReports);

        if(mObserver == null) {
            mObserver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, String.format("Received %s", intent.getAction()));
                    ReportsLoader.this.onContentChanged();
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Events.ACTION_DATASET_CHANGED);
            getContext().registerReceiver(mObserver, filter);
        }

        if(takeContentChanged() || mReports == null) {
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

        if(mReports != null)
            mReports = null;

        if(mObserver != null) {
            getContext().unregisterReceiver(mObserver);
            mObserver = null;
        }
    }
}
