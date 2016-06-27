package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class IntroActivity extends Activity {

    private final int TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_OUT);
    }
}
