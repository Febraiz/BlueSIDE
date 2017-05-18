package com.example.isit_mp3c.projet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.isit_mp3c.projet.camera.CameraActivity;
import com.example.isit_mp3c.projet.exportdb.ExportDBActivity;
import com.example.isit_mp3c.projet.patient.AddPatientActivity;
import com.example.isit_mp3c.projet.patient.AddPatientAnonym;
import com.example.isit_mp3c.projet.patient.ListProfile;

import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private Button  addPatientBtn, searchBtn, photoBtn, exportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


/*        FloatingActionButton buttonCamera = (FloatingActionButton) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call CameraActivity
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });*/

        addPatientBtn = (Button) findViewById(R.id.add_patient_button);
        addPatientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseDialog(new View(getBaseContext()));
            }
        });

        searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTest = new Intent(MainActivity.this, ListProfile.class);
                startActivity(newTest);
            }
        });

        exportBtn = (Button) findViewById(R.id.export_button);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exportIntent = new Intent(MainActivity.this, ExportDBActivity.class);
                startActivity(exportIntent);
            }
        });

        photoBtn = (Button)findViewById(R.id.picture_button);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }

    public void setLanguage(String lang){
        String languageToLoad =lang;
        //Current local application
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        //context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String lang=null;

        switch(id) {
            case R.id.item1:
                lang = "en";
                setLanguage(lang);
                break;
            case R.id.item2:
                lang = "fr";
                setLanguage(lang);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void chooseDialog(View view){
        AlertDialog alertDialog = null;
        final Intent[] intent = new Intent[1];

        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {getString(R.string.choose_protocol_anonym),
                getString(R.string.choose_protocol_not_anonym)};
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_protocol_title)
                //.setMessage(R.string.choose_protocol_message)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                intent[0] = new Intent(MainActivity.this,
                                        AddPatientAnonym.class);
                                break;
                            case 1:
                                intent[0] = new Intent(MainActivity.this,
                                        AddPatientActivity.class);
                                break;
                        }
                    }
                })
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent[0]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(getIntent());
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }
}
