package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainCameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainCameraActivity";

    private Mat mRgba;
    private Mat mGray;
    private OpenCVEngine mOpenCVEngine;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    private LinearLayout mTrainingImagesLayout;
    private Button mTrainButton;
    private Button mSaveButton;
    private Button mCaptureButton;
    private Button mCancelButton;
    private boolean isTraining = false;
    private boolean captureNow = false;

    private ArrayList<ImageView> mTrainingImages;

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
                        File tempDir = getDir("tempDir", Context.MODE_PRIVATE);
                        File mCascadeFile = new File(tempDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        writeFromFile(is, os);

                        mOpenCVEngine = new OpenCVEngine(mCascadeFile.getAbsolutePath(), 0,
                                Constant.LBPH_RADIUS, Constant.LBPH_NEIGHBORS, Constant.LBPH_GRID_X,
                                Constant.LBPH_GRID_Y, Constant.LBPH_THRESHOLD);

                        // load template file from application context
                        File mTemplateFile = new File(tempDir, "template.xml");
                        is = getResources().openRawResource(R.raw.template);
                        os = new FileOutputStream(mTemplateFile);
                        writeFromFile(is, os);
                        mOpenCVEngine.loadRecognizer(mTemplateFile.getAbsolutePath());

                        tempDir.delete();
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

    public MainCameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.main_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mTrainingImagesLayout = (LinearLayout) findViewById(R.id.training_images_layout);
        mTrainButton = (Button) findViewById(R.id.train_button);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mCaptureButton = (Button) findViewById(R.id.capture_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        updateView();
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

        if(isTraining) {
            Rect[] facesArray = faces.toArray();
            if(captureNow) {
                if(facesArray.length == 1) {
                    try {
                        Mat face = mRgba.submat(facesArray[0].y, facesArray[0].y + facesArray[0].height,
                                facesArray[0].x, facesArray[0].x + facesArray[0].width);
                        addNewImage(face);
                    }catch (Exception e) {}
                }
                captureNow = false;
            }
        }else {
            Rect[] facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++) {
                try {
                    int id = mOpenCVEngine.predict(mGray.submat(facesArray[i].y, facesArray[i].y + facesArray[i].height,
                            facesArray[i].x, facesArray[i].x + facesArray[i].width));
                    Imgproc.putText(mRgba, String.valueOf(id), facesArray[i].tl(), Core.FONT_HERSHEY_SIMPLEX, 1, Constant.FACE_TEXT_COLOR, 2);
                }catch (Exception e) {}
            }
        }
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            try {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), Constant.FACE_RECT_COLOR, 2);
            }catch(Exception e) {}
        }
        return mRgba;
    }

    public void trainClicked(View view) {
        isTraining = true;
        mTrainingImages = new ArrayList<>();
        if(mTrainingImagesLayout.getChildCount() > 0) {
            mTrainingImagesLayout.removeAllViewsInLayout();
            mTrainingImagesLayout.postInvalidate();
        }
        updateView();
    }

    public void saveClicked(View view) {
        Intent intent = new Intent(this, SavePersonActivity.class);
        intent.putExtra("images", mTrainingImages);
        startActivity(intent);
        isTraining = false;
        updateView();
    }

    public void captureClicked(View view) {
        captureNow = true;
    }

    public void cancelClicked(View view) {
        isTraining = false;
        updateView();
    }

    private void addNewImage(Mat image) {
        Bitmap bmp = Bitmap.createBitmap(image.cols(), image.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(image, bmp);
        ImageView img = new ImageView(this);
        img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 100, 100, false));
        mTrainingImages.add(img);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTrainingImagesLayout.addView(mTrainingImages.get(mTrainingImages.size() - 1));
            }
        });
        if(mTrainingImages.size() >= 10) {
            mSaveButton.setEnabled(true);
            mSaveButton.postInvalidate();
            mCaptureButton.setEnabled(false);
            mCaptureButton.postInvalidate();
        }
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

    private void updateView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalHeight = getWindow().getDecorView().getHeight();
                if(isTraining) {
                    mOpenCvCameraView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            totalHeight * 7 / 10
                    ));
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            totalHeight * 2 / 10
                    );
                    params.addRule(RelativeLayout.BELOW, R.id.main_activity_surface_view);
                    mTrainingImagesLayout.setLayoutParams(params);
                    mTrainingImagesLayout.setVisibility(View.VISIBLE);
                    mSaveButton.setVisibility(View.VISIBLE);
                    mSaveButton.setEnabled(false);
                    mCaptureButton.setVisibility(View.VISIBLE);
                    mCaptureButton.setEnabled(true);
                    mCancelButton.setVisibility(View.VISIBLE);
                    mTrainButton.setVisibility(View.INVISIBLE);
                }else {
                    mOpenCvCameraView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            totalHeight * 9 / 10
                    ));
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.addRule(RelativeLayout.BELOW, R.id.main_activity_surface_view);
                    mTrainButton.setLayoutParams(params);
                    mTrainButton.setVisibility(View.VISIBLE);
                    mTrainingImagesLayout.setVisibility(View.INVISIBLE);
                    mSaveButton.setVisibility(View.INVISIBLE);
                    mCaptureButton.setVisibility(View.INVISIBLE);
                    mCancelButton.setVisibility(View.INVISIBLE);
                }
            }
        }, 500);
    }
}
