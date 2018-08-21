package at.ameise.devicelocation;

import android.app.Application;

import net.sqlcipher.database.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import at.ameise.devicelocation.model.DaoMaster;
import at.ameise.devicelocation.model.DaoSession;

/**
 * Does initialization and provides methods to access and load the database.
 *
 * Created by mariogastegger on 09.01.17.
 */
public class DeviceLocation extends Application {

    public static final String REPORTS_DB = "reports.db";
    public static final String ENCRYPTED_REPORTS_DB = "reports.enc.db";

    public static final String DEVICE_LOCATION_PREFS = "DeviceLocationPrefs";
    public static final String KEY_ENCRYPTED_DB = "encrypted_db";

    private DaoMaster.DevOpenHelper openHelper;
    private Database database;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        SQLiteDatabase.loadLibs(this);
    }

    public DaoSession getDaoSession() {

        return daoSession;
    }

    /**
     * Creates or opens an encrypted database.
     * Must be called before {@link DeviceLocation#getDaoSession()}!
     *
     * @param password  the password.
     * @return true on success, false if the password was wrong or something else...
     */
    public boolean createOrOpenDatabase(String password) {

        try {

            openHelper = new DaoMaster.DevOpenHelper(this,  DeviceLocation.ENCRYPTED_REPORTS_DB);
            database = openHelper.getEncryptedWritableDb(password);
            daoSession = new DaoMaster(database).newSession();
            return true;

        } catch (Exception e) {

            if(database != null) {
                database.close();
                database = null;
            }

            if(openHelper != null) {
                openHelper.close();
                openHelper = null;
            }

            return false;
        }

    }

    /**
     * Creates or opens an unencrypted database.
     * Must be called before {@link DeviceLocation#getDaoSession()}!
     */
    public void createOrOpenDatabase() {

        openHelper = new DaoMaster.DevOpenHelper(this,  REPORTS_DB);
        database = openHelper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
    }

    /**
     * Closes the currently opened database.
     */
    public void closeDatabase() {

        database.close();
        openHelper.close();
    }
}
