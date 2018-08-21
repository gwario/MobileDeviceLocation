package at.ameise.devicelocation.service;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.model.CellTower;
import at.ameise.devicelocation.model.CellTowerDao;
import at.ameise.devicelocation.model.DaoSession;
import at.ameise.devicelocation.model.WifiAccessPoint;
import at.ameise.devicelocation.model.WifiAccessPointDao;
import at.ameise.devicelocation.util.TelephonyUtil;
import at.ameise.devicelocation.util.WifiUtil;

/**
 * Retrieves the wifi and cell data for a given report.
 *
 * Created by johannes on 13.01.17.
 */
public class CellAndWifiScanService extends IntentService {

    public static final String KEY_SCAN_REQUEST = "scanRequest";

    /**
     * To check if the operation is permitted.
     */
    private boolean CELL_SCAN_PERMITTED;
    private boolean WIFI_SCAN_PERMITTED;

    private static final String TAG = "CellAndWifiScanService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CellAndWifiScanService(String name) {
        super(name);
    }

    public CellAndWifiScanService() {
        super("CellAndWifiScanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called");

        TelephonyManager mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        // report id to link the scanned cell towers and wifi access points
        Long reportId = intent.getLongExtra(KEY_SCAN_REQUEST, 0);
        Log.d(TAG, "got report id: " + reportId);

        // list of scanned cell towers
        final List<CellTower> cellTowerList = new ArrayList<>();

        // list of scanned wifi access points
        final List<WifiAccessPoint> wifiAccessPointList = new ArrayList<>();


        // check permissions
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            CELL_SCAN_PERMITTED = true;
        } else {
            Log.w(TAG, "Permission ACCESS_FINE_LOCATION is missing.");
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            WIFI_SCAN_PERMITTED = true;
        } else {
            Log.w(TAG, "Permission ACCESS_WIFI_STATE is missing.");
        }

        // scan cell towers
        if(CELL_SCAN_PERMITTED){
            Log.d(TAG, "scan cell towers permitted.");

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

                CellLocation cellLocation = mTelephonyManager.getCellLocation();
                if(cellLocation != null) {

                    Log.d(TAG, "CellLocation: " + cellLocation);
                    CellTower cellData = TelephonyUtil.from(cellLocation);

                    if(cellData != null) {
                        cellData.setReportId(reportId);
                        cellTowerList.add(cellData);
                        Log.d(TAG, "added cell to list: " + cellData);

                    } else {
                        Log.w(TAG, "Cell has invalid type - can not be used for Mozilla Location Service.");
                    }
                } else {
                    Log.w(TAG, "CellLocation not available.");
                }

            } else {

                List<CellInfo> cellInfoList = mTelephonyManager.getAllCellInfo();
                if(cellInfoList != null) {

                    Log.d(TAG, "CellInfo: " + cellInfoList);

                    for (CellInfo cellInfo : cellInfoList) {

                        CellTower cellData = TelephonyUtil.from(cellInfo);

                        if(cellData != null) {
                            cellData.setReportId(reportId);
                            cellTowerList.add(cellData);
                            Log.d(TAG, "added cell to list: " + cellData);
                        } else {
                            Log.w(TAG, "Cell has invalid type - can not be used for Mozilla Location Service.");
                        }
                    }

                } else {
                    Log.w(TAG, "CellInfo not available.");
                }
            }
        }

        // scan wifi access points
        if(WIFI_SCAN_PERMITTED){
            Log.w(TAG, "scan wifi permitted.");

            /**
             * If API >= 23 and no location service enabled, getScanResults() returns an empty list.
             *
             * Link: {@Link https://code.google.com/p/android/issues/detail?id=185370}
             */
            List<ScanResult> scanResults = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getScanResults();

            Log.d(TAG, "ScanResults: " + scanResults);

            for(ScanResult sr : scanResults){

                WifiAccessPoint wifiAccessPoint = WifiUtil.from(sr);

                if(wifiAccessPoint != null){
                    wifiAccessPoint.setReportId(reportId);
                    wifiAccessPointList.add(wifiAccessPoint);
                    Log.d(TAG, "added wifi access point to list: " + wifiAccessPoint);
                } else {
                    Log.w(TAG, "Wifi access point can not be used - either hidden or '_nomap'.");
                }
            }
        }

        // get the DAO
        DaoSession daoSession = ((DeviceLocation) getApplication()).getDaoSession();

        // store cell tower objects
        if(!cellTowerList.isEmpty()){
            CellTowerDao cellTowerDao = daoSession.getCellTowerDao();

            cellTowerDao.insertInTx(cellTowerList);
        }

        // store wifi access point objects
        if(!wifiAccessPointList.isEmpty()){
            WifiAccessPointDao wifiAccessPointDao = daoSession.getWifiAccessPointDao();

            wifiAccessPointDao.insertInTx(wifiAccessPointList);
        }

        Log.d(TAG, "cell towers saved: " + cellTowerList.size() + ", wifi access points saved: " + wifiAccessPointList.size());

        // notify DB data change
        sendBroadcast(new Intent()
            .setAction(Events.ACTION_DATASET_CHANGED)
            .putExtra(Events.KEY_REPORT_ID, reportId));

        //start service which performs the mls geolocate request with report id
        Intent apiIntent = new Intent(getApplicationContext(), GeolocateApiAccessService.class);
        apiIntent.putExtra(GeolocateApiAccessService.KEY_API_REQUEST, reportId);
        startService(apiIntent);
    }
}
