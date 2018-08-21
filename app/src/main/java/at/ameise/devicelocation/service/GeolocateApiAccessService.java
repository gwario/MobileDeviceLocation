package at.ameise.devicelocation.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.geolocate.GeolocateClient;
import at.ameise.devicelocation.geolocate.api.GeolocateApi;
import at.ameise.devicelocation.geolocate.api.GeolocateRequest;
import at.ameise.devicelocation.geolocate.api.GeolocateResponse;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.model.ReportDao;
import retrofit2.Response;

/**
 * Created by johannes on 23.01.17.
 */

public class GeolocateApiAccessService extends IntentService {

    static {
        System.loadLibrary("keys");
    }

    public native String getNativeMlsApiKey();

    public static final String KEY_API_REQUEST = "apiRequest";

    private static final String TAG = "GeolocateApiAccessServ";

    public GeolocateApiAccessService(){
        super("GeolocateApiAccessService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeolocateApiAccessService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called");

        // report id to store the response data
        Long reportId = intent.getLongExtra(KEY_API_REQUEST, 0);
        Log.d(TAG, "got report id: " + reportId);

        // objects for api communication
        GeolocateApi api = GeolocateClient.getApi(new String(Base64.decode(getNativeMlsApiKey(), Base64.DEFAULT)));
        GeolocateRequest req = new GeolocateRequest();
        Response<GeolocateResponse> res = null;

        // get dao session and dao
        ReportDao reportDao = ((DeviceLocation) getApplication()).getDaoSession().getReportDao();

        Report report = reportDao.load(reportId);
        // load cell towers and wifi access points from DB
        report.resetParamCellTowers();
        report.resetParamWifiAccessPoints();

        // set request params
        req.setCellTowers(report.getParamCellTowers());
        req.setWifiAccessPoints(report.getParamWifiAccessPoints());

        // get response
        try {
            res = api.locate(req).execute();
        } catch (IOException e) {
            Log.e(TAG, "API request call failed", e);
        }

        Log.d(TAG, res.code() + ", " + res.body());

        if(res.code() == 200) {
            // store response in report object and to db
            GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
            Gson gson = gsonBuilder.create();

            GeolocateResponse geolocateResponse = res.body();
            report.setMlsLatitude(geolocateResponse.getLocation().getLat());
            report.setMlsLongitude(geolocateResponse.getLocation().getLng());
            report.setMlsAccuracyRaw(geolocateResponse.getAccuracy());
            report.setMlsRequest(gson.toJson(req));
            report.setMlsResponse(gson.toJson(geolocateResponse));

            reportDao.update(report);

            // notify DB data change
            sendBroadcast(new Intent()
                    .setAction(Events.ACTION_DATASET_CHANGED)
                    .putExtra(Events.KEY_REPORT_ID, reportId));

        } else{
            Log.e(TAG, "error response from API: " + res.body());
        }
    }
}
