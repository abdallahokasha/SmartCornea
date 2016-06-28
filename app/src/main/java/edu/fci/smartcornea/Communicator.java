package edu.fci.smartcornea;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// TODO
public class Communicator {

    private static Communicator instance;
    private String SERVER_URL = "http://192.168.1.5:3000/";
    private static MyAPIEndPointInterface apiservice;

    private Communicator() {
        Log.v("Communicator Singleton ", "instance created");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiservice = retrofit.create(MyAPIEndPointInterface.class);
    }

    public static Communicator getInstance() {
        if (instance == null) {
            instance = new Communicator();
        }
        return instance;
    }

    public void getDomains(String username) {

    }

    public void greeting() {
        final Call<User> call = apiservice.register(new User(null, "7amada@gmail.com", "12345678"));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.v("Communicator", "greeting: " + response.code());
                Log.v("Communicator", "greeting: " + response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                Log.v("Communicator", "onFailure: A7A");
            }
        });
    }
}
