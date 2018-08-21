package at.ameise.devicelocation.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by mariogastegger on 22.01.17.
 */
public final class TempFileUtil {

    private static final String TEMP_FILENAME = "attachment_temp.txt";

    private TempFileUtil() {}

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void createExternalStoragePrivateAttachmentFile(Context context, String content) {
        // Create a path where we will place our private file on external
        // storage.
        File file = new File(context.getApplicationContext().getExternalFilesDir(null), TEMP_FILENAME);

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            OutputStream os = new FileOutputStream(file);
            byte[] data = content.getBytes();
            os.write(data);
            os.close();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    public static void deleteExternalStoragePrivateAttachmentFile(Context context) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getApplicationContext().getExternalFilesDir(null), TEMP_FILENAME);
        if (file != null) {
            file.delete();
        }
    }

    public static boolean hasExternalStoragePrivateAttachmentFile(Context context) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(context.getApplicationContext().getExternalFilesDir(null), TEMP_FILENAME);
        if (file != null) {
            return file.exists();
        }
        return false;
    }

    public static Uri getExternalStoragePrivateAttachmentFileUri(Context context) {
        return Uri.fromFile(new File(context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath(), TEMP_FILENAME));
    }
}
