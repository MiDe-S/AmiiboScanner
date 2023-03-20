package edu.msu.masiakde.amiiboscanner;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InterfaceAPI {
    @GET("/amiibo/")
    Call<AmiiboInfo> getCourse();
}
