package edu.fci.smartcornea;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Communicator {

    private static Communicator instance;
    private String SERVER_URL = "https://192.168.1.6/3000";
    private static MyAPIEndPointInterface apiservice;

    private Communicator() {
        Log.v("Communicator Singleton ", "instance created");
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(rxAdapter)
                .build();
        apiservice = retrofit.create(MyAPIEndPointInterface.class);
    }

    public static Communicator getInstance() {
        if (instance == null) {
            instance = new Communicator();
        }
        return instance;
    }

    public static void getDomains(String username) {

    }

    public static void greeting() {
        final Observable<String> call = apiservice.greeting("kogo");
        final Subscription subscription = call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.v("TESTREQUEST", "REQUEST DONE");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {

                    }
                });
    }

    public static void login(String username, String password) {
        final Observable<Boolean> call = apiservice.loginService(username, password);
        final Subscription subscription = call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        // TODO pass from login page
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e == null) {
                            // MASSIVE ERROR HANDLING HAPPENS HERE
                        } else {
                            Log.v("onError: ", e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
    }

    public static String MatToJson(Mat matrixRGB) throws JSONException {
        JSONObject faceInJason = new JSONObject();
        if (matrixRGB.isContinuous()) {
            int rows = matrixRGB.rows();
            int columns = matrixRGB.cols();
            int elementSize = (int) matrixRGB.elemSize();
            byte[] matrixData = new byte[columns * rows * elementSize];
            matrixRGB.get(0, 0, matrixData);
            faceInJason.put("rows", matrixRGB.rows());
            faceInJason.put("cols", matrixRGB.cols());
            faceInJason.put("type", matrixRGB.type());
            String dataString = new String(Base64.encode(matrixData, Base64.DEFAULT));
            faceInJason.put("image", dataString);
            Gson gson = new Gson();
            String json = gson.toJson(matrixData);
            /**
             * TODO:
             * do any shit with this json string
             * */
            return json;
        }
        return "";
    }


}
