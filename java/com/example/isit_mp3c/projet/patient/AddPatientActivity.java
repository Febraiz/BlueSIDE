package com.example.isit_mp3c.projet.patient;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPatientActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private EditText name, first_Name, date_Birth, address, mail, phone, height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin, transferrin, serum_iron, cst,
            fibrinogen, crp, other;
    private TextView age_patient;
    private Spinner genderSpinner, ironSpinner;
    private RadioButton rbCertain, rbAbsence, rbIncertain;
    private List<User> patientsList;
    private boolean isMailValid = true;
    private boolean isDateValid = true;
    private boolean isPhoneValid = true;
    SQLiteDBHelper dbH = SQLiteDBHelper.getInstance(this);
    User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText)findViewById(R.id.name_patient);
        mail = (EditText)findViewById(R.id.mail_patient);
        date_Birth = (EditText)findViewById(R.id.patient_birth);
        phone = (EditText)findViewById(R.id.phone_patient);
        age_patient = (TextView)findViewById(R.id.age_patient);

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

        //set the date of birth
        date_Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePicker = new DatePickerDialog(AddPatientActivity.this,
                        dateD,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //datePicker.getDatePicker().setMaxDate(new Date().getTime());
                datePicker.show();
            }
        });

        date_Birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newUser.setDateBirth(date_Birth.getText().toString());
                age_patient.setText(newUser.calculAge());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newUser.setDateBirth(date_Birth.getText().toString());
                age_patient.setText(newUser.calculAge());
            }
        });
        //email check
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!validEmail(s)) {
                    isMailValid = false;
                    //mail.setError(getString(R.string.condition_mail));
                } else {
                    isMailValid = true;
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing to do
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!validEmail(s)) {
                    isMailValid = false;
                    //mail.setError(getString(R.string.condition_mail));
                } else {
                    isMailValid = true;
                }
            }
        });

        //Birth date check
        date_Birth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(!validDate(s)){
                    isDateValid = false;
                } else {
                    isDateValid = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!validDate(s)){
                    isDateValid = false;
                } else {
                    isDateValid = true;
                }
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!validPhone(s)){
                    isPhoneValid = false;
                } else {
                    isPhoneValid = true;
                }
            }
        });

        Button cancel =(Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Récupère la liste des patients en base de données
        patientsList = getPatient();

        final long lastID;

        if(patientsList.size() != -1) {
            lastID = patientsList.size()+1;
        } else {
            lastID = 0;
        }

        Button save = (Button)findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener(){
            //long lastID;
            @Override
            public void onClick(View v) {
                Log.i("input value", "is input value valid? " + isInputValid());
                if(isInputValid()) {

                    addNewPatient();
                    saveDialog(new View(getBaseContext()), lastID);
                } else {
                    Toast.makeText(AddPatientActivity.this, "Error",
                            Toast.LENGTH_LONG);
                }
            }
        });
    }

    public boolean validEmail(CharSequence mail){
        //return !TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        if(!TextUtils.isEmpty(mail)){
            return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        }else{
            return true;
        }
    }

    //check for french mobile format
    public boolean validPhone(CharSequence phone){
        boolean isValid = true;
        if(!TextUtils.isEmpty(phone)){
            //Log.i("phone check", "phone number length : "+ phone.length());
                if(phone.length() < 9 || phone.length() > 13 ){
                isValid = false;
                //Log.i("validate phone", "Phone Format is not valid");
            }else{
                isValid = Patterns.PHONE.matcher(phone).matches();
               // Log.i("validate phone", "Phone number is getting checked");
            }
        }else if(TextUtils.isEmpty(phone)){
            isValid = true;
           // Log.i("validate phone", "Phone number empty");
        }

        return isValid ? true : false;
    }

    public boolean validDate(CharSequence date){
        boolean isValid;
        String input =  String.valueOf(date);
        //hard coding
        if(input.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
            isValid = true;
        }else{
            isValid = false;
        }

        return isValid ? true : false;
    }


    //condition for the input
    public boolean isInputValid(){
        boolean[] test = new boolean[4];
        boolean isValid = true;
        if(!name.getText().toString().isEmpty()){
            test[0] = true;
        }else{
            test[0] = false;
            name.setError(getString(R.string.condition_name));
        }

        if(isMailValid){
            test[1] = true;
        }else {
            test[1] = false;
            mail.setError(getString(R.string.condition_mail));
        }

        if(isDateValid){
            test[2] = true;
        } else{
            test[2] = false;
            date_Birth.setError(getString(R.string.condition_date));
        }

        if(isPhoneValid){
            test[3] = true;
        } else{
            test[3] = false;
            phone.setError(getString(R.string.condition_phone));
        }

        int i =0;
        while(i < test.length){
            if(!test[i]) isValid = false;
            i++;
        }

        return isValid ? true : false;
    }

    //Add a new Patient
    public long addNewPatient(){
        long lastID =0;
        int ID;

        name = (EditText)findViewById(R.id.name_patient);
        first_Name = (EditText)findViewById(R.id.first_name_patient);
        //date_Birth = (EditText)findViewById(R.id.patient_birth);
        address = (EditText)findViewById(R.id.adress_patient);
        //mail = (EditText)findViewById(R.id.mail_patient);
        //phone = (EditText)findViewById(R.id.phone_patient);
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

        rbCertain = (RadioButton) findViewById(R.id.radioDeficiencyClear);
        rbAbsence = (RadioButton) findViewById(R.id.radioNoDeficiency);
        rbIncertain = (RadioButton) findViewById(R.id.radioDeficiencyUnclear);

        String NAME = name.getText().toString();
        String FIRST_NAME = first_Name.getText().toString();
        String DATE_BIRTH = date_Birth.getText().toString();
        String ADDRESS = address.getText().toString();
        String MAIL = mail.getText().toString();
        String PHONE =  phone.getText().toString();
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

        String GENDER = String.valueOf(genderSpinner.getSelectedItem());
        String UNIT = String.valueOf(ironSpinner.getSelectedItem());

        // Récupération de la carence
        String DEFICIENCY = getDeficiencyType();

        if (patientsList.size() == 0)
            ID = 1;
        else
            ID = patientsList.get(patientsList.size()-1).getUserID()+1;

        String PSEUDO = NAME + "_" + FIRST_NAME + "_" + ID;

        if(!HEIGHT.isEmpty()) {
            if (Float.parseFloat(HEIGHT) > 100) {
                HEIGHT = HEIGHT.substring(0, 1) + "." + HEIGHT.substring(1);
            }
        }

        if(dbH.openDatabase()) {
            lastID = dbH.addPatient(new User(NAME, FIRST_NAME, DATE_BIRTH, MAIL,
                    ADDRESS, PHONE, GENDER, HEIGHT, WEIGHT, HEMOGLOBIN,
                    VGM, TCMH, IDR_CV, HYPO, RET_HE, PLATELET, FERRITIN,
                    TRANSFERRIN, SERUM_IRON, UNIT, CST, FIBRINOGEN, CRP, OTHER, "FALSE", PSEUDO, DEFICIENCY));
        }

        Log.i("last ID is ", "AddPatientActivity_java, Last ID set is =" + lastID);
        dbH.close();
        return lastID;
    }

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateD = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        //set the date format according to the language :
        // not really possible, because even country which speak the same language
        // have different date format.

        String myFormat = "yyyy-MM-dd"; //ISO8601
        //SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat);
        date_Birth.setText(simpleDateFormat.format(myCalendar.getTime()));
        // }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void saveDialog(View view, final long lastID){
        final long id = lastID; // Not necessary. Could be deleted.
        final int ID; // Not necessary. could be deleted.

        ID = Integer.parseInt(String.valueOf(id));
        Log.i("return id", "AddPatientActivity_java, retuuuuurn extra id " + id);
        Log.i("return id", "AddPatientActivity_java, retuuuuurn extra id "
                + Integer.parseInt(String.valueOf(lastID)));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);

        builder.setTitle(R.string.save_dialog_title)
                .setMessage(R.string.save_dialog_question)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent profil = new Intent(AddPatientActivity.this,ProfilPatient.class);
                        profil.putExtra("last_ID", Integer.parseInt(String.valueOf(lastID)));
                        finish();
                        startActivity(profil);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    //get all patients
    public List<User> getPatient() {

        List<User> users = new ArrayList<>();

        if(dbH.openDatabase()){
            users = dbH.getPatient();
        }

        dbH.close();
        return users;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient, menu);
        menu.getItem(0).setEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save:
                if(isInputValid()) {
                    patientsList = getPatient();
                    final long lastID2;
                    if(patientsList.size() != -1) {
                        lastID2 = patientsList.size()+1;
                    } else {
                        lastID2 = 0;
                    }
                    Log.i("last id", "AddPatient, onItemSemected, last id is :" + lastID2);
                    addNewPatient();
                    saveDialog(new View(getBaseContext()), lastID2);
                } else {
                    Toast.makeText(AddPatientActivity.this, "Error",
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

    public String getDeficiencyType()
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
