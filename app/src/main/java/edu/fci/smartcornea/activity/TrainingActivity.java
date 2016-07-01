package edu.fci.smartcornea.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

import edu.fci.smartcornea.R;
import edu.fci.smartcornea.core.OpenCVEngine;
import edu.fci.smartcornea.util.Constant;

public class TrainingActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final int NEW_PERSON_NAME_REQUEST_CODE = 0;
    private static final String TAG = "TrainingActivity";

    private Mat mRgba;
    private Mat mGray;
    private OpenCVEngine mOpenCVEngine;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    private LinearLayout mTrainingImagesLayout;
    private Button mSaveButton;
    private Button mCaptureButton;

    private ArrayList<ImageView> mTrainingImages;
    private boolean captureNow = false;

    public TrainingActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_training);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.training_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mTrainingImagesLayout = (LinearLayout) findViewById(R.id.training_images_layout);
        mSaveButton = (Button) findViewById(R.id.save_button);
        mCaptureButton = (Button) findViewById(R.id.capture_button);

        mOpenCVEngine = OpenCVEngine.getInstance();
        mTrainingImages = new ArrayList<>();
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
        mOpenCvCameraView.enableView();
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
        if (captureNow) {
            if (facesArray.length == 1) {
                try {
                    addNewImage(mRgba.submat(facesArray[0]));
                }catch (Exception e) {}
            }
            captureNow = false;
        }
        for (int i = 0; i < facesArray.length; i++) {
            try {
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), Constant.FACE_RECT_COLOR, 2);
            } catch(Exception e) {}
        }
        return mRgba;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PERSON_NAME_REQUEST_CODE && resultCode == RESULT_OK) {
            String personName = data.getStringExtra("newPersonName");
            ArrayList<ImageView> images = mTrainingImages;
            ArrayList<Mat> imagesMat = new ArrayList<>();
            ArrayList<Integer> labels = new ArrayList<>();
            int label = mOpenCVEngine.getRandomLabel();
            for(int i = 0; i < images.size(); ++i) {
                Bitmap bmp = ((BitmapDrawable)images.get(i).getDrawable()).getBitmap();
                Mat face = new Mat();
                Utils.bitmapToMat(bmp, face);
                Imgproc.cvtColor(face, face, Imgproc.COLOR_RGBA2GRAY);
                imagesMat.add(face);
                labels.add(label);
            }
            mOpenCVEngine.updateRecognizer(imagesMat, labels);
            mOpenCVEngine.setLabelInfo(label, personName);
            Toast.makeText(this.getApplicationContext(), "Person Added Successfully, id = " + label, Toast.LENGTH_SHORT).show();
        }
    }

    public void saveClicked(View view) {
        Intent intent = new Intent(this, SavePersonActivity.class);
        startActivityForResult(intent, NEW_PERSON_NAME_REQUEST_CODE);
    }

    public void captureClicked(View view) {
        captureNow = true;
    }

    public void cancelClicked(View view) {
        finish();
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
                if (mTrainingImages.size() >= 10) {
                    mSaveButton.setEnabled(true);
                    mSaveButton.postInvalidate();
                    mCaptureButton.setEnabled(false);
                    mCaptureButton.postInvalidate();
                }
            }
        });
    }
}
