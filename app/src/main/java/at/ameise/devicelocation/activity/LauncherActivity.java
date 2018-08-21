package at.ameise.devicelocation.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import at.ameise.devicelocation.DeviceLocation;
import at.ameise.devicelocation.R;

/**
 * The launcher activity which only shows a splash image and opens the subsequent activity.
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "LauncherActivity";
    private static final int MAX_ATTEMPTS = 3;
    private int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                openDatabaseAndStartMainActivity();

            }
        }, 1000);
    }

    /**
     * Open the the report database and decrypts it if necessary.
     * Shows a password prompt.
     */
    private void openDatabaseAndStartMainActivity() {

        if(getSharedPreferences(DeviceLocation.DEVICE_LOCATION_PREFS, MODE_PRIVATE).getBoolean(DeviceLocation.KEY_ENCRYPTED_DB, false)) {

            final EditText edittext = new EditText(this);
            edittext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edittext.setTransformationMethod(new PasswordTransformationMethod());
            edittext.setHint(R.string.dialog_database_password_hint);

            final Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_database_decrypt_title)
                .setMessage(R.string.dialog_database_decrypt_text)
                .setView(edittext)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_database_positive_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new AsyncTask<String, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(String... strings) {

                                Log.d(TAG, "Got password.");

                                return ((DeviceLocation) getApplication()).createOrOpenDatabase(strings[0]);
                            }

                            @Override
                            protected void onPostExecute(Boolean opened) {
                                super.onPostExecute(opened);
                                edittext.setText("");

                                if(opened) {

                                    Log.d(TAG, "Database is loaded, starting MainActivity.");
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                } else {

                                    attempts++;

                                    if(attempts < MAX_ATTEMPTS) {

                                        Toast.makeText(LauncherActivity.this, R.string.toast_database_decrypt_failed, Toast.LENGTH_LONG).show();
                                        openDatabaseAndStartMainActivity();

                                    } else {

                                        Log.d(TAG, "Failed to loaded database, sending app to background.");
                                        finish();
                                        startActivity(new Intent(Intent.ACTION_MAIN)
                                            .addCategory(Intent.CATEGORY_HOME)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                }
                            }
                        }.execute(edittext.getEditableText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_database_negative_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //dialogInterface.dismiss();
                    }
                }).create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                }
            });

            dialog.show();

        } else {

            Log.d(TAG, "Loading unencrypted database...");
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    ((DeviceLocation) getApplication()).createOrOpenDatabase();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);

                    Log.d(TAG, "Database is loaded, starting MainActivity.");
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }.execute();
        }
    }
}