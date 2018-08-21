package at.ameise.devicelocation.geolocate.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Alexander on 20.01.2017.
 */

public interface GeolocateApi {

    @POST("geolocate")
    Call<GeolocateResponse> locate(@Body GeolocateRequest request);

}
