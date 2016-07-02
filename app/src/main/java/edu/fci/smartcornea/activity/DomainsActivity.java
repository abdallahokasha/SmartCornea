package edu.fci.smartcornea.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.fci.smartcornea.R;
import edu.fci.smartcornea.core.OpenCVEngine;
import edu.fci.smartcornea.model.Domain;

public class DomainsActivity extends Activity {

    private final int NEW_DOMAIN_NAME_REQUEST_CODE = 0;
    private Spinner dropdown;
    private List<Domain> domains;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domains);
        dropdown = (Spinner) findViewById(R.id.domains_dropdown);
        loadDomains();
    }

    public void addDomainButton(View view) {
        Intent intent = new Intent(this, AddDomainActivity.class);
        intent.putExtra("currentDomains", getDomainsNames());
        startActivityForResult(intent, NEW_DOMAIN_NAME_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_DOMAIN_NAME_REQUEST_CODE && resultCode == RESULT_OK) {
            String domainName = data.getStringExtra("newDomainName");
            domains.add(new Domain(domains.size(), domainName));
            updateDropDownList();
            Toast.makeText(getApplicationContext(), "Domain Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectDomain(View view) {
        int index = dropdown.getSelectedItemPosition();
        if(index == -1) {
            Toast.makeText(DomainsActivity.this, "You must select a domain (or create one if you haven't)", Toast.LENGTH_SHORT).show();
        }else {
            displaySpinner();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    removeSpinner();
                    updateRecognizer("");
                    Intent intent = new Intent(DomainsActivity.this, MainCameraActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }
    }

    private void updateDropDownList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getDomainsNames());
        dropdown.setAdapter(adapter);
    }

    private void loadDomains() {
        displaySpinner();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                removeSpinner();
                domains = new ArrayList<>();
                for(int i = 1; i <= 3; ++i) {
                    domains.add(new Domain(i, "Test " + i));
                }
                updateDropDownList();
            }
        }, 1000);
    }

    private ArrayList<String> getDomainsNames() {
        ArrayList<String> items = new ArrayList<>();
        if(domains != null) {
            for (Domain domain : domains) {
                items.add(domain.getName());
            }
        }
        return items;
    }

    private void updateRecognizer(String body) {
        OpenCVEngine mOpenCVEngine = OpenCVEngine.getInstance();
        if(!body.isEmpty()) {
            File tempDir = getDir("smartCorneaXML", Context.MODE_PRIVATE);
            File xml = new File(tempDir, "domain.xml");
            try {
                FileOutputStream os = new FileOutputStream(xml);
                os.write(Base64.decode(body, Base64.DEFAULT));
                os.close();
                mOpenCVEngine.loadRecognizer(xml.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            // load training images from assets
            AssetManager assetManager = getAssets();
            ArrayList<Mat> faces = new ArrayList<>();
            ArrayList<Integer> labels = new ArrayList<>();
            try {
                File tempDir = getDir("tempDir", Context.MODE_PRIVATE);
                InputStream is = assetManager.open("unknown.pgm");
                File mImageFile = new File(tempDir, "unknown.pgm");
                FileOutputStream os = new FileOutputStream(mImageFile);
                writeFromFile(is, os);
                Mat faceMat = Imgcodecs.imread(mImageFile.getAbsolutePath());
                Imgproc.cvtColor(faceMat, faceMat, Imgproc.COLOR_RGBA2GRAY);
                faces.add(faceMat);
                labels.add(0);
                mOpenCVEngine.trainRecognizer(faces, labels);
                mOpenCVEngine.setLabelInfo(0, "Subject");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeFromFile(InputStream is, FileOutputStream os) {
        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displaySpinner() {
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void removeSpinner() {
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
