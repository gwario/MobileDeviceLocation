package at.ameise.devicelocation.util;

import android.content.Context;
import android.location.Location;

import at.ameise.devicelocation.R;
import at.ameise.devicelocation.model.Report;

/**
 * Methods to display report data.
 *
 * Created by mariogastegger on 24.01.17.
 */
public final class StringUtil {

    private StringUtil() {}

    public static String naOrValue(Float value) {
        return value != null ? String.valueOf(value) : "N/A";
    }

    public static String naOrValue(Integer value) {
        return value != null ? String.valueOf(value) : "N/A";
    }

    public static String naOrValue(Double value) {
        return value != null ? String.valueOf(value) : "N/A";
    }

    public static float getDeviationGpsMls(Report report) {

        Location locGps = new Location("");
        locGps.setLatitude(report.getGpsLatitude());
        locGps.setLongitude(report.getGpsLongitude());
        locGps.setAccuracy(report.getGpsAccuracyRaw());

        Location locMls = new Location("");
        locMls.setLatitude(report.getMlsLatitude());
        locMls.setLongitude(report.getMlsLongitude());
        locMls.setAccuracy(report.getMlsAccuracyRaw());

        return locGps.distanceTo(locMls);
    }

    public static String getPrettyString(Context context, Report report) {

        StringBuilder sb = new StringBuilder();

        sb.append("Report from ");
        sb.append(android.text.format.DateFormat.format(context.getText(R.string.datetime_format), report.getTimestamp()));
        sb.append("\n\n");

        Float distanceInMeters = null;
        if(report.getGpsLatitude() != null && report.getMlsLatitude() != null) {
            distanceInMeters = getDeviationGpsMls(report);
        }
        sb.append("Deviation[m]:\t");
        sb.append(naOrValue(distanceInMeters));
        sb.append("\n\n");

        if(report.getGpsLatitude() != null) {

            sb.append("Gps data:\n");

            sb.append("\tLatitude:\t");
            sb.append(naOrValue(report.getGpsLatitude()));
            sb.append("\n\tLongitude:\t");
            sb.append(naOrValue(report.getGpsLongitude()));
            sb.append("\n\tAccuracy:\t");
            sb.append(naOrValue(report.getGpsAccuracyRaw()));
            sb.append("\n\n");

        } else {

            sb.append("Gps data: N/A\n\n");
        }

        if(report.getMlsLatitude() != null) {

            sb.append("Mls data:\n");

            sb.append("\tLatitude:\t");
            sb.append(naOrValue(report.getMlsLatitude()));
            sb.append("\n\tLongitude:\t");
            sb.append(naOrValue(report.getMlsLongitude()));
            sb.append("\n\tAccuracy:\t");
            sb.append(naOrValue(report.getMlsAccuracyRaw()));
            sb.append("\n\n");

        } else {

            sb.append("Mls data: N/A\n\n");
        }

        sb.append("Number of wifi accesspoints:\t");
        if(report.getParamWifiAccessPoints() != null) {
            sb.append(String.valueOf(report.getParamWifiAccessPoints().size()));
            sb.append("\n");
        } else {
            sb.append("N/A\n");
        }

        sb.append("Number of cell towers:\t\t");
        if(report.getParamCellTowers() != null) {
            sb.append(String.valueOf(report.getParamCellTowers().size()));
            sb.append("\n");
        } else {
            sb.append("N/A\n");
        }

        sb.append("Mls Request:\n");
        sb.append(report.getMlsRequest());
        sb.append("\nMls Response:\n");
        sb.append(report.getMlsResponse());

        return sb.toString();
    }
}
