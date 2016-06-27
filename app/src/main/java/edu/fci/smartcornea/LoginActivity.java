package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText usernameText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    public void loginButton(View view) {
        Intent intent = new Intent(this, DomainsActivity.class);
        startActivity(intent);
//        usernameText = (EditText) findViewById(R.id.username_text);
//        passwordText = (EditText) findViewById(R.id.password_text);
//        String username = usernameText.getText().toString();
//        String password = passwordText.getText().toString();
        // TODO send to the service
    }

}

