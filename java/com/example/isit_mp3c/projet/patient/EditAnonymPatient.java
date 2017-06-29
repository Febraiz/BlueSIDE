package com.example.isit_mp3c.projet.patient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.util.ArrayList;
import java.util.List;

public class EditAnonymPatient extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private EditText height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin,
            transferrin, serum_iron, cst, fibrinogen, crp, other, age, idPatient;
    private Spinner genderSpinner, ironSpinner;
    private RadioButton rbCertain, rbAbsence, rbIncertain;
    private List<User> users;
    private int id;
    private ArrayAdapter<CharSequence> genderSpinnerAdapter, ironSpinnerAdapter;
    private SQLiteDBHelper dbH = SQLiteDBHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_anonym_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idPatient = (EditText) findViewById(R.id.id_patient);

        // Disable the editText
        idPatient.setInputType(0);
        idPatient.setTextIsSelectable(false);

        age = (EditText) findViewById(R.id.age_patient);
        height = (EditText) findViewById(R.id.height_patient);
        weight = (EditText) findViewById(R.id.weight_patient);
        hemoglobin = (EditText) findViewById(R.id.hb);
        vgm = (EditText) findViewById(R.id.vgm);
        tcmh = (EditText) findViewById(R.id.tcmh);
        idr_cv = (EditText) findViewById(R.id.idr_cv);
        hypo = (EditText) findViewById(R.id.hypo);
        ret_he = (EditText) findViewById(R.id.ret_he);
        platelet = (EditText) findViewById(R.id.platelet);
        ferritin = (EditText) findViewById(R.id.ferritin);
        transferrin = (EditText) findViewById(R.id.transferrin);
        serum_iron = (EditText) findViewById(R.id.srum_iron);
        cst = (EditText) findViewById(R.id.cst);
        fibrinogen = (EditText) findViewById(R.id.fibrinogen);
        crp = (EditText) findViewById(R.id.crp);
        other = (EditText) findViewById(R.id.other);
        rbCertain = (RadioButton) findViewById(R.id.radioDeficiencyClear);
        rbAbsence = (RadioButton) findViewById(R.id.radioNoDeficiency);
        rbIncertain = (RadioButton) findViewById(R.id.radioDeficiencyUnclear);
        //sex = (EditText) findViewById(R.id.sexe_patient);

        genderSpinner = (Spinner) findViewById(R.id.sexe_patient);
        // Create an ArrayAdapter using the string array and a default spinner layout
        genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.setGender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genderSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        genderSpinner.setAdapter(genderSpinnerAdapter);
        genderSpinner.setOnItemSelectedListener(this);


        ironSpinner =(Spinner)findViewById(R.id.iron_unit);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ironSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.IronUnit, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        ironSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ironSpinner.setAdapter(ironSpinnerAdapter);
        ironSpinner.setOnItemSelectedListener(this);
        ironSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                serum_iron.requestFocus();
                return false;
            }
        });

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("ID");

        users = getPatient();
        getProfil();

        Button saveBtn = (Button)findViewById(R.id.update_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()){
                    updatePatient();
                    Toast.makeText(EditAnonymPatient.this, R.string.update, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAnonymPatient.this, "Error",
                            Toast.LENGTH_LONG);
                }
            }
        });

        Button cancelBtn = (Button)findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //get all patients
    private List<User> getPatient() {
        List<User> users = new ArrayList<>();

        if(dbH.openDatabase()){
            users = dbH.getPatient();
        }
        dbH.close();

        return users;
    }

    //get the patient's data
    private void getProfil() {

        try {
            idPatient.setText(users.get(id - 1).getPseudo());
            height.setText(users.get(id - 1).getHeight().toString());
            weight.setText(users.get(id - 1).getWeight().toString());
            hemoglobin.setText(users.get(id - 1).getHb());
            vgm.setText(users.get(id - 1).getVgm());
            tcmh.setText(users.get(id - 1).gettcmh());
            idr_cv.setText(users.get(id - 1).getIdr_cv());
            hypo.setText(users.get(id - 1).getHypo());
            ret_he.setText(users.get(id - 1).getRet_he());
            platelet.setText(users.get(id - 1).getPlatelet());
            ferritin.setText(users.get(id - 1).getFerritin());
            transferrin.setText(users.get(id - 1).getTransferrin());
            serum_iron.setText(users.get(id-1).getSerum_iron());
            cst.setText(users.get(id - 1).getCst());
            fibrinogen.setText(users.get(id - 1).getFibrinogen());
            crp.setText(users.get(id - 1).getCrp());
            other.setText(users.get(id - 1).getOther());
            age.setText(users.get(id - 1).getAge());

            String sex = users.get(id - 1).getSexe();
            if(!sex.equals(null)){
                int genderSpinnerPosition = genderSpinnerAdapter.getPosition(sex);
                genderSpinner.setSelection(genderSpinnerPosition);
            }

            String serum_iron_unit = users.get(id - 1).getSerum_iron_unit();
            if(!serum_iron_unit.equals(null)){
                int ironUnitPosition = ironSpinnerAdapter.getPosition(serum_iron_unit);
                ironSpinner.setSelection(ironUnitPosition);
            }

            //Mise en place du bon radioButton
            String carence = users.get(id - 1).getDeficiency();
            switch(carence) {
                case "Carence certaine":
                    rbCertain.toggle();
                    break;
                case "Absence de carence":
                    rbAbsence.toggle();
                    break;
                case "Carence incertaine":
                    rbIncertain.toggle();
                    break;
            }

        } catch (Exception e) {
        }
    }

    private void updatePatient() {
        try {
            String PSEUDO = idPatient.getText().toString().replace(" ","");
            String HEIGHT = height.getText().toString();
            String WEIGHT = weight.getText().toString();
            String HEMOGLOBIN = hemoglobin.getText().toString();
            Log.i("hemoglobin", "EditAnonymPatient, the value of hb is : " + HEMOGLOBIN);
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
            String AGE = age.getText().toString();

            String SECURED = users.get(id-1).getSecured();

            String GENDER = String.valueOf(genderSpinner.getSelectedItem());
            String UNIT = String.valueOf(ironSpinner.getSelectedItem());

            String DEFICIENCY = getDeficiencyType();

            if(!HEIGHT.isEmpty()) {
                if (Float.parseFloat(HEIGHT) > 100) {
                    HEIGHT = HEIGHT.substring(0, 1) + "." + HEIGHT.substring(1);
                }
            }

            int ID = users.get(id - 1).getUserID();
            dbH.updatePatient(new User(GENDER, HEIGHT, WEIGHT, HEMOGLOBIN,
                    VGM, TCMH, IDR_CV, HYPO, RET_HE, PLATELET, FERRITIN,
                    TRANSFERRIN, SERUM_IRON, UNIT, CST, FIBRINOGEN, CRP, OTHER,
                    SECURED, PSEUDO, DEFICIENCY, AGE), ID);
            dbH.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isInputValid() {
        boolean[] test = new boolean[5];
        boolean isValid = true;

        String tmp_height = height.getText().toString();
        String tmp_weight = weight.getText().toString();
        String tmp_idr_cv = idr_cv.getText().toString();
        String tmp_hypo = hypo.getText().toString();
        String tmp_transferrin = transferrin.getText().toString();

        if (tmp_height.isEmpty()) {
            test[0] = true;
        }
        else{
            if (Float.parseFloat(tmp_height) > 100) {
                tmp_height = tmp_height.substring(0, 1) + "." + tmp_height.substring(1);
            }
            if (Float.parseFloat(tmp_height) > 2.3) {
                test[0] = false;
                height.setError(getString(R.string.condition_height));
            } else {
                test[0] = true;
            }
        }

        if (tmp_weight.isEmpty()) {
            test[1] = true;
        }
        else {
            if ((Integer.parseInt(tmp_weight) > 400 || Integer.parseInt(tmp_weight) < 20)) {
                test[1] = false;
                weight.setError(getString(R.string.condition_weight));
            } else {
                test[1] = true;
            }
        }

        if (tmp_idr_cv.isEmpty()) {
            test[2] = true;
        }
        else {
            if (Integer.parseInt(tmp_idr_cv) > 100) {
                test[2] = false;
                idr_cv.setError(getString(R.string.condition_idr_cv));
            } else {
                test[2] = true;
            }
        }

        if (tmp_hypo.isEmpty()) {
            test[3] = true;
        } else {
            if (Integer.parseInt(tmp_hypo) > 100) {
                test[3] = false;
                hypo.setError(getString(R.string.condition_hypo));
            } else {
                test[3] = true;
            }
        }

        if (tmp_transferrin.isEmpty()) {
            test[4] = true;
        } else {
            if (Integer.parseInt(tmp_transferrin) > 100) {
                test[4] = false;
                transferrin.setError(getString(R.string.condition_transferrin));
            } else {
                test[4] = true;
            }
        }

        int i = 0;
        while (i < test.length) {
            if (!test[i])
                isValid = false;
            i++;
        }

        return isValid ? true : false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                onBackPressed();
                return true;
            case R.id.save:
                if (isInputValid()){
                    updatePatient();
                    Toast.makeText(EditAnonymPatient.this, R.string.update, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAnonymPatient.this, "Error",
                            Toast.LENGTH_LONG);
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode nécessaire au bon fonctionnement des radioButtons
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

    private String getDeficiencyType()
    {
        if (rbCertain.isChecked())
            return "Carence certaine";
        else if(rbAbsence.isChecked())
            return "Absence de carence";
        else if(rbIncertain.isChecked())
            return "Carence incertaine";
        else
            return "";
    }
}
