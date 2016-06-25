package org.opencv.samples.smartcornea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DomainsActivity extends Activity {

    private final int NEW_DOMAIN_NAME_REQUEST_CODE = 0;
    private Spinner dropdown;
    private List<String> items;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domains_activity);

        dropdown = (Spinner) findViewById(R.id.domains_dropdown);
        items = new ArrayList<>();
        // to be removed
        items.add("kogo");
        items.add("mosa");
        items.add("amr");
        items.add("druid");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }


    public void addDomainButton(View view) {
        Intent intent = new Intent(this, AddDomainActivity.class);
        startActivityForResult(intent, NEW_DOMAIN_NAME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_DOMAIN_NAME_REQUEST_CODE && resultCode == RESULT_OK) {
            items.add(data.getStringExtra("newDomainName"));
            Toast.makeText(this.getApplicationContext(), "Domain Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectDomain(View view) {

        /**
         * TODO
         * get the selected domain and send it through network
         * to fetch the needed data
         * || select it directly from the local recognizer
         * */
    }

}
