package at.ameise.devicelocation.geolocate.api;

/**
 * Created by Alexander on 20.01.2017.
 */

public class GeolocateResponse {

    private Location location;

    private Float accuracy;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public class Location {

        private Float lat;

        private Float lng;

        public Float getLat() {
            return lat;
        }

        public void setLat(Float lat) {
            this.lat = lat;
        }

        public Float getLng() {
            return lng;
        }

        public void setLng(Float lng) {
            this.lng = lng;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "GeolocateResponse{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                '}';
    }

}
