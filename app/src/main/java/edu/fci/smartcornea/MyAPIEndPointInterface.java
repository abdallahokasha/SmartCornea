package edu.fci.smartcornea;


import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MyAPIEndPointInterface {



    @GET("api/v1/sessions/kogo")
    Observable<String> greeting(@Query("") String name);

    @GET("login")
    Observable<Boolean> loginService(@Query("username") String username,
                                     @Query("password") String password);

    @GET("get-domains")
    Observable<List<String>> getDomains(@Query("username") String username);

}
