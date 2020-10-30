package com.example.android.searchbar.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISuggestAPI {
    @GET("complete/search")
    Observable<String> getSuggestFromYoutube(@Query("q") String query,
                                             @Query("client") String client,
                                             @Query("hl") String language,
                                             @Query("ds") String restrict);


}
