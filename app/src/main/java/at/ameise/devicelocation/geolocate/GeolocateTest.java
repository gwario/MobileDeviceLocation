package at.ameise.devicelocation.geolocate;

import org.junit.Test;

import java.io.IOException;

import at.ameise.devicelocation.geolocate.api.GeolocateApi;
import at.ameise.devicelocation.geolocate.api.GeolocateRequest;
import at.ameise.devicelocation.model.CellTower;
import at.ameise.devicelocation.model.WifiAccessPoint;
import retrofit2.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GeolocateTest {

    @Test
    public void testGeolocateWithCellTower() throws IOException {
        GeolocateApi api = GeolocateClient.getApi("test");

        CellTower ct = new CellTower();
        ct.setCellId(7289281);
        ct.setLocationAreaCode(2012);
        ct.setMobileCountryCode(23);
        ct.setMobileNetworkCode(10);
        ct.setRadioType("wcdma");
        ct.setSignalStrength(-93);

        GeolocateRequest req = new GeolocateRequest();
        req.getCellTowers().add(ct);
        Response res = api.locate(req).execute();

        System.out.println(res.code());
        System.out.println(res.body());
    }

    @Test
    public void testGeolocateWithWifiAccessPoint() throws IOException {
        GeolocateApi api = GeolocateClient.getApi("test");

        WifiAccessPoint ap = new WifiAccessPoint();
        ap.setMacAddress("01:23:45:67:89:cd");

        GeolocateRequest req = new GeolocateRequest();
        req.getWifiAccessPoints().add(ap);
        Response res = api.locate(req).execute();

        System.out.println(res.code());
        System.out.println(res.body());
    }

    @Test
    public void testGeolocateWithBoth() throws IOException {
        GeolocateApi api = GeolocateClient.getApi("test");

        CellTower ct = new CellTower();
        ct.setCellId(7289281);
        ct.setLocationAreaCode(2012);
        ct.setMobileCountryCode(23);
        ct.setMobileNetworkCode(10);
        ct.setRadioType("wcdma");
        ct.setSignalStrength(-93);
        WifiAccessPoint ap = new WifiAccessPoint();
        ap.setMacAddress("01:23:45:67:89:cd");

        GeolocateRequest req = new GeolocateRequest();
        req.getCellTowers().add(ct);
        req.getWifiAccessPoints().add(ap);
        Response res = api.locate(req).execute();

        System.out.println(res.code());
        System.out.println(res.body());
    }

}