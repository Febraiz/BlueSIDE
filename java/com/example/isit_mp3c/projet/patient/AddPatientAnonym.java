package com.example.isit_mp3c.projet.patient;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.RadioButton;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPatientAnonym extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private EditText height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin, transferrin, serum_iron, cst,
            fibrinogen, crp, other, pseudo;
    private Spinner genderSpinner, ironSpinner;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient_anonym);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pseudo = (EditText)findViewById(R.id.pseudo);
        genderSpinner = (Spinner) findViewById(R.id.sexe_patient);
        ironSpinner =(Spinner)findViewById(R.id.iron_unit);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.setGender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderSpinnerAdapter);
        genderSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> ironSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.IronUnit, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        ironSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ironSpinner.setAdapter(ironSpinnerAdapter);
        ironSpinner.setOnItemSelectedListener(this);

        Button cancel =(Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        users = getPatient();
        final long lastID2;
        if(users.size() != -1) {
            lastID2 = users.size()+1;
        } else {
            lastID2 = 0;
        }

        Button save = (Button)findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener(){
            long lastID;
            @Override
            public void onClick(View v) {
                // if(!name.getText().toString().isEmpty()) {
                //lastID = addNewPatient();
                addNewPatient();
                Log.i("last Id ",
                        "AddPatientActivity_java, Laaaaaaaaaaaaaast Id issss === " + lastID2);
                saveDialog(new View(getBaseContext()), lastID2);
                //  } else {
                //     Toast.makeText(AddPatientActivity.this,
                // R.string.empty_fields, Toast.LENGTH_LONG).show();
                // }
            }
        });
    }

    //get all patients
    public List<User> getPatient() {
        List<User> users = new ArrayList<>();

        SQLiteDBHelper dbH = new SQLiteDBHelper(getApplicationContext());
        try {
            dbH.createDatabase();
        } catch (IOException e) {
            dbH.close();
            throw new Error("unable to create database");
        }
        if(dbH.openDatabase()){
            // users = db.getPatient();
            users = dbH.getPatient();
        }
        dbH.close();
        return users;
    }

    private void addNewPatient() {
        long lastID =0;

        height = (EditText)findViewById(R.id.height_patient);
        weight = (EditText)findViewById(R.id.weight_patient);
        hemoglobin = (EditText)findViewById(R.id.hb);
        vgm = (EditText)findViewById(R.id.vgm);
        tcmh = (EditText)findViewById(R.id.tcmh);
        idr_cv = (EditText)findViewById(R.id.idr_cv);
        hypo = (EditText)findViewById(R.id.hypo);
        ret_he = (EditText)findViewById(R.id.ret_he);
        platelet = (EditText)findViewById(R.id.platelet);
        ferritin = (EditText)findViewById(R.id.ferritin);
        transferrin = (EditText)findViewById(R.id.transferrin);
        serum_iron = (EditText)findViewById(R.id.srum_iron);
        cst = (EditText)findViewById(R.id.cst);
        fibrinogen = (EditText)findViewById(R.id.fibrinogen);
        crp = (EditText)findViewById(R.id.crp);
        other = (EditText)findViewById(R.id.other);
        //pseudo = (EditText)findViewById(R.id.pseudo);

        String HEIGHT = height.getText().toString();
        String WEIGHT = weight.getText().toString();
        String HEMOGLOBIN = hemoglobin.getText().toString();
        String VGM = vgm.getText().toString();
        String TCMH = tcmh.getText().toString();
        String IDR_CV = idr_cv.getText().toString();
        String HYPO = hypo.getText().toString();
        String RET_HE = ret_he.getText().toString();
        String PLATELET = platelet.getText().toString();
        String FERRITIN = ferritin.getText().toString();
        String TRANSFERRIN = transferrin.getText().toString();
        String SERUM_IRON = serum_iron.getText().toString();
        String CST = cst.getText().toString();
        String FIBRINOGEN = fibrinogen.getText().toString();
        String CRP = crp.getText().toString();
        String OTHER = other.getText().toString();
        String PSEUDO = pseudo.getText().toString();

        String GENDER = String.valueOf(genderSpinner.getSelectedItem());
        String UNIT = String.valueOf(ironSpinner.getSelectedItem());

        SQLiteDBHelper dbH = new SQLiteDBHelper(this);
        try {
            dbH.createDatabase();
        } catch (IOException e) {
            dbH.close();
            throw new Error("unable to create database");
        }
        if(dbH.openDatabase()) {
            lastID = dbH.addPatient(new User( GENDER, HEIGHT, WEIGHT, HEMOGLOBIN,
                    VGM, TCMH, IDR_CV, HYPO, RET_HE, PLATELET, FERRITIN,
                    TRANSFERRIN, SERUM_IRON, UNIT, CST, FIBRINOGEN, CRP, OTHER, "TRUE", PSEUDO));
        }

        Log.i("last ID is ", "AddNonAnonymPatientActivity_java, Last ID set is =" + lastID);
        dbH.close();
    }

    public void saveDialog(View view, final long lastID){
        final long id = lastID; // Not necessary. Could be deleted.

        Log.i("return id", "AddPatientActivity_java, retuuuuurn extra id " + id);
        Log.i("return id", "AddPatientActivity_java, retuuuuurn extra id " +
                Integer.parseInt(String.valueOf(lastID)));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.save_dialog_title)
                .setMessage(R.string.save_dialog_question)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent profil = new Intent(AddPatientAnonym.this,ProfilAnonymPatient.class);
                        profil.putExtra("last_ID", Integer.parseInt(String.valueOf(lastID)));
                        startActivity(profil);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        startActivity(getIntent());
                    }
                });
/*                .setNeutralButton(R.string.add_patient,
                        new DialogInterface.OnClickListener() {  //Another button on the dialog for adding new patient. It is not necessary. It takes a lot of space.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(getIntent());
                    }
                });*/

        AlertDialog dialog = builder.create();

        dialog.show();
        //return builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_patient, menu);
        menu.getItem(0).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
            case R.id.save:
                if(!pseudo.getText().toString().isEmpty()) {
                    users = getPatient();
                    final long lastID2;
                    if(users.size() != -1) {
                        lastID2 = users.size()+1;
                    } else {
                        lastID2 = 0;
                    }
                    addNewPatient();
                    saveDialog(new View(getBaseContext()), lastID2);
                } else {
                    pseudo.setError(getString(R.string.condition_pseudo));
                    Toast.makeText(AddPatientAnonym.this, "Error",
                            Toast.LENGTH_LONG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioDeficiencyClear:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radioNoDeficiency:
                if (checked)
                    // Ninjas rule
                    break;
            case R.id.radioDeficiencyUnclear:
                if (checked)
                    // AH
                    break;
        }
    }
}
