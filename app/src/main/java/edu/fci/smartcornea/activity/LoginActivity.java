package edu.fci.smartcornea.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import edu.fci.smartcornea.R;
import edu.fci.smartcornea.core.Communicator;
import edu.fci.smartcornea.core.DataManager;
import edu.fci.smartcornea.model.User;
import edu.fci.smartcornea.util.Constant;
import retrofit2.Response;

public class LoginActivity extends Activity {

    private EditText usernameText;
    private EditText passwordText;
    private Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameText = (EditText) findViewById(R.id.username_text);
        passwordText = (EditText) findViewById(R.id.password_text);
        communicator = Communicator.getInstance();
    }


    public void loginButton(View view) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        try {
            Response<User> response = communicator.login(new User(null, username, password)).execute();
            if(response.code() == 200) {
                DataManager dm = DataManager.getInstance();
                dm.putObject(Constant.USER_ID, String.valueOf(response.body().getId()));
                Intent intent = new Intent(LoginActivity.this, DomainsActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}

