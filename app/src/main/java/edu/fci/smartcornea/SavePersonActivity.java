package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SavePersonActivity extends Activity {

    private EditText mPersonNameTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_person);
        mPersonNameTextEdit = (EditText) findViewById(R.id.person_name_input);
    }

    public void savePerson(View view) {
        String personName = mPersonNameTextEdit.getText().toString();
        if (personName.isEmpty()) {
            Toast.makeText(SavePersonActivity.this, "Person name can not be empty!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent();
            intent.putExtra("newPersonName", personName);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
