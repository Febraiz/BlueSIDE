package com.example.isit_mp3c.projet.patient;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPatient extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private EditText name, first_Name, date_Birth,
            address, mail, phone, height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin,
            transferrin, serum_iron, cst, fibrinogen, crp, other;
    private Spinner genderSpinner, ironSpinner;
    private TextView idPatient;
    private List<User> users;
    private int id;

    private ArrayAdapter<CharSequence> genderSpinnerAdapter, ironSpinnerAdapter;

    private boolean isMailValid = true;
    private boolean isDateValid = true;
    private boolean isPhoneValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //idPatient = (TextView) findViewById(R.id.id_patient);
        name = (EditText) findViewById(R.id.name_patient);
        first_Name = (EditText) findViewById(R.id.first_name_patient);
        date_Birth = (EditText) findViewById(R.id.patient_birth);
        address = (EditText) findViewById(R.id.adress_patient);
        mail = (EditText) findViewById(R.id.mail_patient);
        phone = (EditText) findViewById(R.id.phone_patient);
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
        //sex = (EditText) findViewById(R.id.sexe_patient);

        genderSpinner = (Spinner) findViewById(R.id.sexe_patient);
        // Create an ArrayAdapter using the string array and a default spinner layout
        genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.setGender, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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


        date_Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePicker = new DatePickerDialog(EditPatient.this, dateD,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
            }
        });

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("ID");
        Log.i("Profil Last ID", "lest ID in activity EditPatient = " + id);

        users = getPatient();
        getProfil();

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
                if (!validPhone(s)) {
                    isPhoneValid = false;
                } else {
                    isPhoneValid = true;
                }
            }
        });

        Button saveBtn = (Button)findViewById(R.id.update_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePatient();
/*                Snackbar.make(v, R.string.update, Snackbar.LENGTH_LONG)
                        .setAction(R.string.home, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent homeIntent = new Intent(EditPatient.this, MainActivity.class);
                                startActivity(homeIntent);
                            }
                        })
                        .show();*/
                Toast.makeText(EditPatient.this, R.string.update, Toast.LENGTH_SHORT).show();
                Intent profilIntent = new Intent(EditPatient.this,ProfilPatient.class);
                profilIntent.putExtra("last_ID",id);
                startActivity(profilIntent);


            }
        });

        Button cancelBtn = (Button)findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToMainIntent = new Intent(EditPatient.this, MainActivity.class);
                startActivity(returnToMainIntent);
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
            if(phone.length() < 9 || phone.length() > 13 ){
                isValid = false;
            }else{
                isValid = Patterns.PHONE.matcher(phone).matches();
            }
        }else if(TextUtils.isEmpty(phone)){
            isValid = true;
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

    //get all patients
    public List<User> getPatient() {
        List<User> users = new ArrayList<>();

        SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());

        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            dbHelper.close();
            throw new Error("unable to create database");
        }
        if(dbHelper.openDatabase()){
            // users = db.getPatient();
            users = dbHelper.getPatient();
        }
        dbHelper.close();
        return users;
    }

    //get the patient's data
    public void getProfil() {

        try {
            //idPatient.setText(String.valueOf(users.get(id).getUserID()));
            //idPatient.setText(String.valueOf(id));
            name.setText(users.get(id - 1).getName());
            first_Name.setText(users.get(id - 1).getFirstName());
            date_Birth.setText(users.get(id - 1).getDateBirth());
            address.setText(users.get(id - 1).getAddress());
            mail.setText(users.get(id - 1).getMail());
            phone.setText(users.get(id - 1).getPhone());
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
            // sex.setText(users.get(id - 1).getSexe());

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
        } catch (Exception e) {
            Log.e("DB error", "It did not read the ID value");
        }
    }

    //Update patient data
    public void updatePatient(){
        try {
            SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);

            String NAME = name.getText().toString();
            String FIRST_NAME = first_Name.getText().toString();
            String DATE_BIRTH = date_Birth.getText().toString();
            String ADDRESS = address.getText().toString();
            String MAIL = mail.getText().toString();
            String PHONE = phone.getText().toString();
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

            String SECURED = users.get(id-1).getSecured();
            String PSEUDO = users.get(id-1).getPseudo();

            String GENDER = String.valueOf(genderSpinner.getSelectedItem());
            String UNIT = String.valueOf(ironSpinner.getSelectedItem());

            int ID = users.get(id-1).getUserID();

            dbHelper.updatePatient(new User(NAME, FIRST_NAME, DATE_BIRTH, MAIL,
                    ADDRESS, PHONE, GENDER, HEIGHT, WEIGHT, HEMOGLOBIN,
                    VGM, TCMH, IDR_CV, HYPO, RET_HE, PLATELET, FERRITIN,
                    TRANSFERRIN, SERUM_IRON, UNIT, CST, FIBRINOGEN, CRP, OTHER,
                    SECURED, PSEUDO), ID);
            dbHelper.close();
        }catch(Exception e){
            e.printStackTrace();
            Log.e("Update ERROR", "erreur lors de la mise à jour du profil");
        }
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

        String myFormat = "yyyy-MM-dd"; //ISO8601
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        date_Birth.setText(sdf.format(myCalendar.getTime()));
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
            case R.id.save:
                if(isInputValid()) {
                    updatePatient();
                    Toast.makeText(EditPatient.this, R.string.update, Toast.LENGTH_SHORT).show();
                    Intent profilIntent = new Intent(EditPatient.this,ProfilPatient.class);
                    profilIntent.putExtra("last_ID", id);
                    startActivity(profilIntent);
                } else {
                    Toast.makeText(EditPatient.this, "Error",
                            Toast.LENGTH_LONG);
                }
                break;
            case android.R.id.home:
                //NavUtils.navigateUpTo(this, new Intent(EditPatient.this, MainActivity.class));
                Intent returnIntent = new Intent(EditPatient.this, ProfilPatient.class);
                returnIntent.putExtra("last_ID", id);
                startActivity(returnIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
