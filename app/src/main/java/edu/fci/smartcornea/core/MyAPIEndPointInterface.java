package edu.fci.smartcornea.core;

import edu.fci.smartcornea.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MyAPIEndPointInterface {

    @POST("api/v1/sessions/register")
    Call<User> register(@Body User user);

    @POST("api/v1/sessions/login")
    Call<User> login(@Body User user);
}
