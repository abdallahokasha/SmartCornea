package edu.fci.smartcornea;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.opencv.core.Mat;

import java.util.ArrayList;

public class TrainingActivity extends Activity {


    private ArrayList<ImageView> images;
    private static int imageCounter = 0;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        images = new ArrayList<>();
        relativeLayout = (RelativeLayout) findViewById(R.id.training_images_layout);
    }

    public void captureImage(View view) {
        ImageView img = new ImageView(this);
        img.setImageResource(R.mipmap.logo);
        img.setId(imageCounter);
        images.add(img);
        if (imageCounter != 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.RIGHT_OF, images.get(imageCounter - 1).getId());
            relativeLayout.addView(images.get(imageCounter), params);
        } else {
            relativeLayout.addView(images.get(imageCounter));
        }
        imageCounter++;
    }
}
