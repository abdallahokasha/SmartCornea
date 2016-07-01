package edu.fci.smartcornea.core;

import android.util.Log;

import java.util.List;

import edu.fci.smartcornea.model.Domain;
import edu.fci.smartcornea.model.User;
import edu.fci.smartcornea.util.Constant;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Communicator {

    private static Communicator instance;
    private static SmartCorneaAPIInterface apiService;

    private Communicator() {
//        Log.d("Communicator Singleton ", "instance created");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.SMARTCORNEA_SERVER_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SmartCorneaAPIInterface.class);
    }

    public static Communicator getInstance() {
        if (instance == null) {
            instance = new Communicator();
        }
        return instance;
    }

    public Call<User> login(User user) {
        return apiService.login(user);
    }

    public Call<List<Domain>> listDomains(String id) {
        return apiService.listDomains(id);
    }

    public Call<Domain> createDomain(String id, Domain domain) {
        return apiService.createDomain(id, domain);
    }

    public Call<Void> storeStateFile(String domainId, String stateFile) {
        return apiService.storeStateFile(domainId, stateFile);
    }

    public Call<String> loadStateFile(String domainId) {
        return apiService.loadStateFile(domainId);
    }
}
