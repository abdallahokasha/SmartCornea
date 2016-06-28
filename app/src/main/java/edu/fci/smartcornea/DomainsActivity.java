package edu.fci.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class DomainsActivity extends Activity {

    private final int NEW_DOMAIN_NAME_REQUEST_CODE = 0;
    private Spinner dropdown;
    private ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domains);
        dropdown = (Spinner) findViewById(R.id.domains_dropdown);
        items = new ArrayList<>();
        items.add("Friends");
        items.add("Work");
        items.add("Club");
        // to be removed
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }


    public void addDomainButton(View view) {
        Intent intent = new Intent(this, AddDomainActivity.class);
        intent.putExtra("currentDomains", items);
        startActivityForResult(intent, NEW_DOMAIN_NAME_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_DOMAIN_NAME_REQUEST_CODE && resultCode == RESULT_OK) {
            String domainName = data.getStringExtra("newDomainName");
            items.add(domainName);
            // TODO: create file b esm el domain da
            Toast.makeText(this.getApplicationContext(), "Domain Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }


    public void selectDomain(View view) {
        Intent intent = new Intent(this, MainCameraActivity.class);
        startActivity(intent);
        /**
         * TODO
         * get the selected domain and send it through network
         * to fetch the needed data
         * || select it directly from the local recognizer
         * */
    }

}
