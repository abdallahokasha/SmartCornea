package org.opencv.samples.smartcornea;

import android.util.Base64;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

public class Communicator {


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
