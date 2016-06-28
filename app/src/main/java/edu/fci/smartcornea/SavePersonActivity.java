package edu.fci.smartcornea;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class SavePersonActivity extends Activity {

    private ArrayList<Mat> images_mat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<ImageView> images = (ArrayList<ImageView>)getIntent().getSerializableExtra("images");
        images_mat = new ArrayList<>(images.size());
        for(int i = 0; i < images.size(); ++i) {
            Bitmap bmp = ((BitmapDrawable)images.get(i).getDrawable()).getBitmap();
            images_mat.set(i, new Mat());
            Utils.bitmapToMat(bmp, images_mat.get(i));
        }
    }
}
