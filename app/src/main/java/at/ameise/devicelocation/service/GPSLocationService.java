package at.ameise.devicelocation.service;

import android.Manifest;
import android.app.IntentService;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.model.Report;

/**
 * Created by johannes on 23.01.17.
 */

public class GPSLocationService extends IntentService{

    public static final String KEY_GPS_REQUEST = "gpsRequest";

    private static final String TAG = "GPSLocationService";

    public GPSLocationService() {
        super("GPSLocationService");
    }

    public GPSLocationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called");

        // report id to store the response data
        Long reportId = intent.getLongExtra(KEY_GPS_REQUEST, 0);
        Log.d(TAG, "got report id: " + reportId);

        // check permissions
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission ACCESS_FINE_LOCATION is missing.");
        } else {

            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            // check if GPS is enabled
            if(!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                Log.w(TAG, "GPS is not enabled.");
            } else {

                // get location manager for GPS location
                Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                if(location != null){
                    Log.d(TAG, "GPS lat: " + location.getLatitude() + ", long: " + location.getLongitude());

                    Report report = ((DeviceLocation) getApplication()).getDaoSession().getReportDao().load(reportId);
                    report.setGpsAccuracyRaw(location.getAccuracy());
                    report.setGpsLatitude(location.getLatitude());
                    report.setGpsLongitude(location.getLongitude());

                } else{

                    Log.w(TAG, "getLastKnownLocation() returned null.");
                }


            }

        }
    }
}
