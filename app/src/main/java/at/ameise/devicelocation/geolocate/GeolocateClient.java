package at.ameise.devicelocation.geolocate;

import android.util.Base64;

import java.io.IOException;

import at.ameise.devicelocation.geolocate.api.GeolocateApi;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alexander on 20.01.2017.
 */

public class GeolocateClient {

    public static GeolocateApi getApi(final String apiKey) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://location.services.mozilla.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();
                        HttpUrl originalHttpUrl = original.url();

                        HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("key", apiKey)
                            .build();

                        // Request customization: add request headers
                        Request.Builder requestBuilder = original.newBuilder().url(url);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).build())
                .build();

        GeolocateApi api = retrofit.create(GeolocateApi.class);
        return api;
    }

}
