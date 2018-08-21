package at.ameise.devicelocation.geolocate.api;

import java.util.ArrayList;
import java.util.List;

import at.ameise.devicelocation.model.CellTower;
import at.ameise.devicelocation.model.WifiAccessPoint;

/**
 * Created by Alexander on 20.01.2017.
 */

public class GeolocateRequest {

    private List<CellTower> cellTowers = new ArrayList<>();

    private List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>();

    public List<CellTower> getCellTowers() {
        return cellTowers;
    }

    public void setCellTowers(List<CellTower> cellTowers) {
        this.cellTowers = cellTowers;
    }

    public List<WifiAccessPoint> getWifiAccessPoints() {
        return wifiAccessPoints;
    }

    public void setWifiAccessPoints(List<WifiAccessPoint> wifiAccessPoints) {
        this.wifiAccessPoints = wifiAccessPoints;
    }


    @Override
    public String toString() {
        return "GeolocateRequest{" +
                "cellTowers=" + cellTowers +
                ", wifiAccessPoints=" + wifiAccessPoints +
                '}';
    }
}
