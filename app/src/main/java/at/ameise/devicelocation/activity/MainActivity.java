package at.ameise.devicelocation.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.Events;
import at.ameise.devicelocation.R;
import at.ameise.devicelocation.fragment.ReportDetailFragment;
import at.ameise.devicelocation.fragment.ReportListFragment;
import at.ameise.devicelocation.model.DaoMaster;
import at.ameise.devicelocation.model.DaoSession;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.model.ReportDao;
import at.ameise.devicelocation.service.CellAndWifiScanService;
import at.ameise.devicelocation.service.CleanUpService;
import at.ameise.devicelocation.service.GPSLocationService;
import at.ameise.devicelocation.util.StringUtil;
import at.ameise.devicelocation.util.TempFileUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Reports. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * shows the {@link ReportDetailFragment} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, ReportListFragment.OnFragmentInteractionListener, View.OnClickListener {

    /**
     * Used as dummy listener to always get an up-to-date location.
     */
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Log.d(TAG, "got GPS location update: " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // not used
        }

        @Override
        public void onProviderEnabled(String provider) {
            // not used
        }

        @Override
        public void onProviderDisabled(String provider) {
            // not used
        }
    };

    private static final String TAG = "MainActivity";

    /**
     * Used for the request permission callback (if needed).
     */
    private static final int REQUEST_PERMISSIONS = 100;

    private static final int SEND_MAIL_WITH_ATTACHMENT_REQUEST_CODE = 1335;


    @BindView(R.id.fab_list_action_measure)
    com.getbase.floatingactionbutton.FloatingActionButton fabListMeasure;

    @BindView(R.id.fab_detail_menu)
    com.getbase.floatingactionbutton.FloatingActionsMenu fabDetailMenu;

    @BindView(R.id.fab_detail_action_delete_report)
    com.getbase.floatingactionbutton.FloatingActionButton fabMenuItemDelete;
    @BindView(R.id.fab_detail_action_send_report)
    com.getbase.floatingactionbutton.FloatingActionButton fabMenuItemSendMail;
    @BindView(R.id.fab_detail_action_measure)
    com.getbase.floatingactionbutton.FloatingActionButton fabMenuItemMeasure;


    private Report mDetailItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CleanUpService.scheduleCleanUpService(this);

        ButterKnife.bind(this);

        fabListMeasure.setVisibility(View.VISIBLE);
        fabDetailMenu.setVisibility(View.GONE);

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, ReportListFragment.newInstance())
            .commit();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        //Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();

        // ask for permissions
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                }, REQUEST_PERMISSIONS);

        // button to create new report
        fabListMeasure.setOnClickListener(this);
        fabMenuItemMeasure.setOnClickListener(this);
        fabMenuItemDelete.setOnClickListener(this);
        fabMenuItemSendMail.setOnClickListener(this);

        // check permissions for GPS access
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permission ACCESS_FINE_LOCATION is missing.");
            Toast.makeText(MainActivity.this, "Permission to access device location must be granted!", Toast.LENGTH_LONG).show();

        } else {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // check if GPS is enabled
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Log.w(TAG, "GPS is not enabled.");
                Toast.makeText(MainActivity.this, "GPS must be enabled in order to get full results, please restart the app!", Toast.LENGTH_LONG).show();

            } else {
                // request GPS updates to always get an up-to-date location
                Log.d(TAG, "requesting GPS updates...");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CleanUpService.cancelCleanUpService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        if(getSharedPreferences(DeviceLocation.DEVICE_LOCATION_PREFS, Context.MODE_PRIVATE).getBoolean(DeviceLocation.KEY_ENCRYPTED_DB, false))
            menu.findItem(R.id.action_encrypt_reports).setVisible(false);

        //menu.findItem(R.id.action_delete_all_reports).setVisible(BuildConfig.DEBUG);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_encrypt_reports:
                encryptDatabaseAndRestartApp();
                return true;

            case R.id.action_delete_all_reports:
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        ((DeviceLocation) getApplication()).getDaoSession().getReportDao().deleteAll();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        sendBroadcast(new Intent().setAction(Events.ACTION_DATASET_CHANGED));
                    }
                }.execute();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Encrypts the report database and restarts the app.
     */
    private void encryptDatabaseAndRestartApp() {

        if(!getSharedPreferences(DeviceLocation.DEVICE_LOCATION_PREFS, MODE_PRIVATE).getBoolean(DeviceLocation.KEY_ENCRYPTED_DB, false)) {

            final EditText edittext = new EditText(this);
            edittext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edittext.setTransformationMethod(new PasswordTransformationMethod());
            edittext.setHint(R.string.dialog_database_password_hint);

            final Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_database_encrypt_title)
                .setMessage(R.string.dialog_database_encrypt_text)
                .setView(edittext)
                .setCancelable(true)
                .setPositiveButton(R.string.dialog_database_positive_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new AsyncTask<String, Void, Void>() {
                            @Override
                            protected Void doInBackground(String... strings) {

                                //get all reports
                                List<Report> reportList = ((DeviceLocation) getApplication()).getDaoSession().getReportDao().loadAll();

                                //open the encrypted database
                                DaoMaster.DevOpenHelper newOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), DeviceLocation.ENCRYPTED_REPORTS_DB);
                                Database newDatabase = newOpenHelper.getEncryptedWritableDb(strings[0]);
                                DaoSession newDaoSession = new DaoMaster(newDatabase).newSession();

                                //store all reports
                                newDaoSession.getReportDao().insertInTx(reportList);

                                //update shared preference
                                getSharedPreferences(DeviceLocation.DEVICE_LOCATION_PREFS, MODE_PRIVATE).edit().putBoolean(DeviceLocation.KEY_ENCRYPTED_DB, true).apply();

                                //clear old database
                                ((DeviceLocation) getApplication()).getDaoSession().getReportDao().deleteAll();

                                //close both databases
                                newDatabase.close();
                                newOpenHelper.close();
                                ((DeviceLocation) getApplication()).closeDatabase();

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                finish();
                                startActivity(new Intent(getApplicationContext(), LauncherActivity.class));
                            }
                        }.execute(edittext.getEditableText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_database_negative_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                }).create();

            dialog.show();
        }
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);

        if(canback) {

            fabListMeasure.setVisibility(View.GONE);
            fabDetailMenu.setVisibility(View.VISIBLE);

        } else {

            fabListMeasure.setVisibility(View.VISIBLE);
            fabDetailMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);
        fabDetailMenu.collapse();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);
        fabDetailMenu.collapse();
    }

    @Override
    public void onReportClicked(Report report) {

        mDetailItem = report;
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, ReportDetailFragment.newInstance(report.getId()))
            .addToBackStack(null)
            .commit();
    }


    private void startMailActivityForResult() {

        CharSequence datetime = android.text.format.DateFormat.format(getText(R.string.datetime_format), mDetailItem.getTimestamp());

        //This will inevitably be a security issue because we are saving files onto a flash memory.
        if(TempFileUtil.isExternalStorageWritable()) {

            Log.d("TempFile", "Creating tempfile.");
            TempFileUtil.createExternalStoragePrivateAttachmentFile(this, StringUtil.getPrettyString(this, mDetailItem));

            if(TempFileUtil.hasExternalStoragePrivateAttachmentFile(this)) {
                Log.d("TempFile", "Tempfile has been created.");

                startActivityForResult(Intent.createChooser(new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .setType("plain/text")
                        .putExtra(Intent.EXTRA_STREAM, TempFileUtil.getExternalStoragePrivateAttachmentFileUri(this))
                        .putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.mail_subject), datetime))
                    , "Send via email..."), SEND_MAIL_WITH_ATTACHMENT_REQUEST_CODE);

            } else {

                Toast.makeText(this, "Failed to write temp attachment.", Toast.LENGTH_SHORT).show();
            }
        } else {

            startActivity(Intent.createChooser(new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType("plain/text")
                    .putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.mail_subject), datetime))
                    .putExtra(android.content.Intent.EXTRA_TEXT, StringUtil.getPrettyString(this, mDetailItem))
                , "Send via email..."));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEND_MAIL_WITH_ATTACHMENT_REQUEST_CODE) {

            if(TempFileUtil.hasExternalStoragePrivateAttachmentFile(this)) {

                Log.d("TempFile", "Deleting tempfile.");
                TempFileUtil.deleteExternalStoragePrivateAttachmentFile(this);

                if(TempFileUtil.hasExternalStoragePrivateAttachmentFile(this)) {

                    Log.w("TempFile", "Failed to delete tempfile.");

                } else {

                    Log.d("TempFile", "Tempfile deleted.");
                }
            }
        }
    }

    private AlertDialog createDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_delete_message)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_button_yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    long itemId = mDetailItem.getId();

                    ((DeviceLocation) getApplication()).getDaoSession().getReportDao().delete(mDetailItem);

                    sendBroadcast(new Intent()
                        .setAction(Events.ACTION_DATASET_CHANGED)
                        .putExtra(Events.KEY_REPORT_ID, itemId));

                    onBackPressed();
                }
            })
            .setNegativeButton(R.string.dialog_delete_button_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

        return builder.create();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab_list_action_measure:
                new MeasureTask().execute();
                break;

            case R.id.fab_detail_action_measure:
                new MeasureTask().execute();
                fabDetailMenu.collapse();
                break;

            case R.id.fab_detail_action_delete_report:
                createDeleteDialog().show();
                fabDetailMenu.collapse();
                break;

            case R.id.fab_detail_action_send_report:
                startMailActivityForResult();
                fabDetailMenu.collapse();
                break;
        }
    }

    private class MeasureTask extends AsyncTask<Void, Void, Report> {

        @Override
        protected Report doInBackground(Void... voids) {

            DaoSession daoSession = ((DeviceLocation) getApplication()).getDaoSession();
            ReportDao reportDao = daoSession.getReportDao();
            Report report = new Report();
            report.setTimestamp(System.currentTimeMillis());
            reportDao.insert(report);
            Log.d("DaoExample", "Inserted new report, ID: " + report.getId());

            return report;
        }

        @Override
        protected void onPostExecute(Report report) {
            super.onPostExecute(report);

            sendBroadcast(new Intent()
                .setAction(Events.ACTION_DATASET_CHANGED)
                .putExtra(Events.KEY_REPORT_ID, report.getId()));


            // check permissions
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission ACCESS_FINE_LOCATION is missing.");
                Toast.makeText(MainActivity.this, "Permission to access device location must be granted!", Toast.LENGTH_LONG).show();

            } else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission ACCESS_WIFI_STATE is missing.");
                Toast.makeText(MainActivity.this, "Permission to access device wifi state must be granted!", Toast.LENGTH_LONG).show();

            } else {
                // start IntentService with report id to scan cell towers and wifi access points
                Intent scanIntent = new Intent(MainActivity.this, CellAndWifiScanService.class);
                scanIntent.putExtra(CellAndWifiScanService.KEY_SCAN_REQUEST, report.getId());
                startService(scanIntent);
            }

            // check permissions
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Permission ACCESS_FINE_LOCATION is missing.");

            } else {
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                // check if GPS is enabled
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Log.w(TAG, "GPS is not enabled.");
                    Toast.makeText(MainActivity.this, "GPS must be enabled in order to get full results!", Toast.LENGTH_LONG).show();

                } else {
                    // start IntentService with report id to get current GPS position
                    Intent gpsIntent = new Intent(MainActivity.this, GPSLocationService.class);
                    gpsIntent.putExtra(GPSLocationService.KEY_GPS_REQUEST, report.getId());
                    startService(gpsIntent);
                }
            }
        }
    }
}
