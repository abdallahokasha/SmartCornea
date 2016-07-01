package edu.fci.smartcornea.core;

import java.util.List;

import edu.fci.smartcornea.model.Domain;
import edu.fci.smartcornea.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SmartCorneaAPIInterface {

    @POST("api/v1/sessions/register")
    Call<User> register(@Body User user);

    @POST("api/v1/sessions/login")
    Call<User> login(@Body User user);

    @GET("/domains/show")
    Call<List<Domain>> listDomains(@Query("user_id") String id);

    @POST("/domains/create")
    Call<Domain> createDomain(@Query("user_id") String id, @Body Domain domain);

    @Multipart
    @POST("/domains/{domainId}/store_state_file")
    Call<Void> storeStateFile(@Path("domainId") String domainId, @Part("recognizer_state") String stateFile);

    @GET("/domains/{domainId}/state_file")
    Call<String> loadStateFile(@Path("domainId") String domainId);
}
