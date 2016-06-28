package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddDomainActivity extends Activity {

    private String addedDomainName;
    private EditText addedDomainNameText;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_domain_activity);
        items = (ArrayList<String>)getIntent().getSerializableExtra("currentDomains");
    }

    public void addNewDomainButton(View view) {
        addedDomainNameText = (EditText) findViewById(R.id.domain_name_input);
        addedDomainName = addedDomainNameText.getText().toString();
        if (addedDomainName.isEmpty()) {
            Toast.makeText(AddDomainActivity.this, "Domain name can not be empty!", Toast.LENGTH_SHORT).show();
        }else if (items.contains(addedDomainName)) {
            Toast.makeText(AddDomainActivity.this, "Domain name already exists!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("newDomainName", addedDomainName);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}