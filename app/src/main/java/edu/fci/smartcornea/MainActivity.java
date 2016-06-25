package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private Mat mRgba;
    private Mat mGray;
    private OpenCVEngine mOpenCVEngine;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;

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
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mOpenCVEngine = new OpenCVEngine(mCascadeFile.getAbsolutePath(), 0,
                                Constant.LBPH_RADIUS, Constant.LBPH_NEIGHBORS, Constant.LBPH_GRID_X,
                                Constant.LBPH_GRID_Y, Constant.LBPH_THRESHOLD);

                        { // train your dragon
                            is = getResources().openRawResource(R.raw.face);
                            mCascadeFile = new File(cascadeDir, "face.pgm");
                            os = new FileOutputStream(mCascadeFile);

                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                            Mat faceMat = Imgcodecs.imread(mCascadeFile.getAbsolutePath());
                            Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2GRAY);
                            List<Mat> faces = new ArrayList<>();
                            faces.add(faceMat);
                            List<Integer> labels = new ArrayList<>();
                            labels.add(0);
                            mOpenCVEngine.trainRecognizer(faces, labels);

                            is.close();
                            os.close();
                        }

                        cascadeDir.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this.getApplicationContext(), DomainsActivity.class);
        startActivity(intent);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_main);
//        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
//        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mOpenCVEngine.setDetectorMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect faces = new MatOfRect();

        if (mOpenCVEngine != null)
            mOpenCVEngine.detect(mGray, faces);

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            if(facesArray[i].width < 0 || facesArray[i].height < 0) {
                continue;
            }
            int id = mOpenCVEngine.predict(mGray.submat(facesArray[i].y, facesArray[i].y + facesArray[i].height,
                    facesArray[i].x, facesArray[i].x + facesArray[i].width));
            Imgproc.putText(mRgba, String.valueOf(id), facesArray[i].tl(), Core.FONT_HERSHEY_SIMPLEX, 1, Constant.FACE_TEXT_COLOR, 2);
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), Constant.FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }
}
