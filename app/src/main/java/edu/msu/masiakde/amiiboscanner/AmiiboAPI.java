package edu.msu.masiakde.amiiboscanner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AmiiboAPI {
    @GET("amiibo/")
    Call<AmiiboInfo> getCharacter(@Query("head") String head, @Query("tail") String tail);
}
