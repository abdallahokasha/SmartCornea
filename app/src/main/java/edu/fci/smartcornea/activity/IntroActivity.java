package edu.fci.smartcornea.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import edu.fci.smartcornea.R;
import edu.fci.smartcornea.core.OpenCVEngine;
import edu.fci.smartcornea.util.Constant;

public class IntroActivity extends Activity {

    private static final String TAG = "IntroActivity";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("opencv_engine");

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File tempDir = getDir("scTempDir", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(tempDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        writeFromFile(is, os);

                        OpenCVEngine mOpenCVEngine = OpenCVEngine.getInstance();
                        // Initialize OpenCVEngine singleton instance
                        mOpenCVEngine.init(mCascadeFile.getAbsolutePath(), 0,
                                Constant.LBPH_RADIUS, Constant.LBPH_NEIGHBORS, Constant.LBPH_GRID_X,
                                Constant.LBPH_GRID_Y, Constant.LBPH_THRESHOLD);

                        // load training images from assets
                        AssetManager assetManager = getAssets();
                        ArrayList<Mat> faces = new ArrayList<>();
                        ArrayList<Integer> labels = new ArrayList<>();
                        for(int subject = 1; subject <= 25; ++subject) {
                            for(int id = 1; id <= 7; ++id) {
                                is = assetManager.open("s" + subject + "/" + id + ".pgm");
                                File mImageFile = new File(tempDir, "s" + subject + "-" + id + ".pgm");
                                os = new FileOutputStream(mImageFile);
                                writeFromFile(is, os);
                                Mat faceMat = Imgcodecs.imread(mImageFile.getAbsolutePath());
                                Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2GRAY);
                                faces.add(faceMat);
                                labels.add(subject);
                            }
                        }
                        mOpenCVEngine.trainRecognizer(faces, labels);
                        for(int subject = 1; subject <= 25; ++subject) {
                            mOpenCVEngine.setLabelInfo(subject, "Subject" + subject);
                        }
                        tempDir.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!OpenCVLoader.initDebug()) {
                    Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, IntroActivity.this, mLoaderCallback);
                } else {
                    Log.d(TAG, "OpenCV library found inside package. Using it!");
                    mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void writeFromFile(InputStream is, FileOutputStream os) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
