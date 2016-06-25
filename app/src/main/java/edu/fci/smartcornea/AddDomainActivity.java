package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddDomainActivity extends Activity {


    private String addedDomainName;
    private EditText addedDomainNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_domain_activity);
    }

    public void addNewDomainButton(View view) {
        addedDomainNameText = (EditText) findViewById(R.id.domain_name_input);
        addedDomainName = addedDomainNameText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("newDomainName", addedDomainName);
        setResult(RESULT_OK, intent);
        finish();
    }

}
