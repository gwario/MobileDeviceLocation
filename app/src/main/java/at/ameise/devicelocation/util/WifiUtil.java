package at.ameise.devicelocation.util;

import android.net.wifi.ScanResult;

import at.ameise.devicelocation.model.WifiAccessPoint;

/**
 * Created by johannes on 19.01.17.
 */

public class WifiUtil {

    /**
     * According to the Mozilla Location Service, SSIDs ending with '_nomap'
     * should not be collected.
     */
    private final static String EXCLUDE_SUFFIX_SSID = "_nomap";

    public WifiUtil() {
    }

    /**
     * Creates a {@link WifiAccessPoint} out of a {@link ScanResult}
     * @param scanResult the result of a wifi scan.
     * @return  the wifi object.
     */
    public static WifiAccessPoint from(ScanResult scanResult){

        WifiAccessPoint wifiAccessPoint = new WifiAccessPoint();

        // check if wifi should be collected
        if(scanResult.SSID.endsWith(EXCLUDE_SUFFIX_SSID)){
            return null;
        }

        // set all relevant fields
        wifiAccessPoint.setMacAddress(scanResult.BSSID);
        wifiAccessPoint.setSsid(scanResult.SSID);
        wifiAccessPoint.setAge(scanResult.timestamp);
        // used instead of channel (according to MLS)
        wifiAccessPoint.setFrequency(scanResult.frequency);
        wifiAccessPoint.setSignalStrength(scanResult.level);

        // TODO signalToNoiseRatio missing, can not be accessed?

        return wifiAccessPoint;
    }
}
