package edu.fci.smartcornea.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import edu.fci.smartcornea.R;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginButton(View view) {
        displaySpinner();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSpinner();
                Intent intent = new Intent(LoginActivity.this, DomainsActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }

    private void displaySpinner() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    private void removeSpinner() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}

