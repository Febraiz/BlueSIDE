package com.example.isit_mp3c.projet.patient;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPatient extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    private Toast mToast = null;
    private Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener dateD = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private EditText name, first_Name, date_Birth,
            address, mail, phone, height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin,
            transferrin, serum_iron, cst, fibrinogen, crp, other;
    private RadioButton rbCertain, rbAbsence, rbIncertain;
    private Spinner genderSpinner, ironSpinner;
    private List<User> users;
    private int id;

    private ArrayAdapter<CharSequence> genderSpinnerAdapter, ironSpinnerAdapter;

    private boolean isMailValid = true;
    private boolean isDateValid = true;
    private boolean isPhoneValid = true;

    private SQLiteDBHelper dbHelper = SQLiteDBHelper.getInstance(this);

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

        // Disable those 2 editTexts
        name.setInputType(0);
        name.setTextIsSelectable(false);
        first_Name.setInputType(0);
        first_Name.setTextIsSelectable(false);

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
        rbCertain = (RadioButton) findViewById(R.id.radioDeficiencyClear);
        rbAbsence = (RadioButton) findViewById(R.id.radioNoDeficiency);
        rbIncertain = (RadioButton) findViewById(R.id.radioDeficiencyUnclear);

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
        ironSpinner.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                serum_iron.requestFocus();
                return false;
            }
        });


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

        users = getPatient();
        getProfil();

        //email check
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!validEmail(s)) {
                    isMailValid = false;
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
                if (isInputValid()){
                    updatePatient();

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(EditPatient.this, R.string.update, Toast.LENGTH_SHORT);
                    mToast.show();

                    finish();
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

    private boolean validEmail(CharSequence mail){
        if(!TextUtils.isEmpty(mail)){
            return Patterns.EMAIL_ADDRESS.matcher(mail).matches();
        }else{
            return true;
        }
    }

    //check for french mobile format
    private boolean validPhone(CharSequence phone){
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

    private boolean validDate(CharSequence date){
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
    private boolean isInputValid(){
        ArrayList<Boolean> test = new ArrayList<>();

        boolean isValid = true;

        String tmp_height = height.getText().toString();
        String tmp_weight = weight.getText().toString();
        String tmp_idr_cv = idr_cv.getText().toString();
        String tmp_hypo = hypo.getText().toString();
        String tmp_transferrin = transferrin.getText().toString();
        String tmp_cst = cst.getText().toString();

        if (isMailValid) {
            test.add(true);
        } else {
            test.add(false);
            mail.setError(getString(R.string.condition_mail));
        }

        if (isDateValid) {
            test.add(true);
        } else {
            test.add(false);
            date_Birth.setError(getString(R.string.condition_date));
        }

        if (isPhoneValid) {
            test.add(true);
        } else {
            test.add(false);
            phone.setError(getString(R.string.condition_phone));
        }

        if (tmp_height.isEmpty()) {
            test.add(true);
        }
        else{
            if (Float.parseFloat(tmp_height) > 100) {
                tmp_height = tmp_height.substring(0, 1) + "." + tmp_height.substring(1);
            }
            if (Float.parseFloat(tmp_height) > 2.3) {
                test.add(false);
                height.setError(getString(R.string.condition_height));
            } else {
                test.add(true);
            }
        }

        if (tmp_weight.isEmpty()) {
            test.add(true);
        }
        else {
            if ((Float.parseFloat(tmp_weight) > 400 || Float.parseFloat(tmp_weight) < 20)) {
                test.add(false);
                weight.setError(getString(R.string.condition_weight));
            } else {
                test.add(true);
            }
        }

        if (tmp_idr_cv.isEmpty()) {
            test.add(true);
        }
        else {
            if (Float.parseFloat(tmp_idr_cv) > 100) {
                test.add(false);
                idr_cv.setError(getString(R.string.condition_idr_cv));
            } else {
                test.add(true);
            }
        }

        if (tmp_cst.isEmpty()) {
            test.add(true);
        }
        else {
            if (Float.parseFloat(tmp_cst) > 100) {
                test.add(false);
                cst.setError(getString(R.string.condition_cst));
            } else {
                test.add(true);
            }
        }

        if (tmp_hypo.isEmpty()) {
            test.add(true);
        } else {
            if (Float.parseFloat(tmp_hypo) > 100) {
                test.add(false);
                hypo.setError(getString(R.string.condition_hypo));
            } else {
                test.add(true);
            }
        }

        for (Boolean iter : test) {
            if(!iter){
                isValid=false;

                if (mToast != null) mToast.cancel();
                mToast = Toast.makeText(EditPatient.this, getString(R.string.error), Toast.LENGTH_SHORT);
                mToast.show();

                break;
            }
        }

        return isValid ? true : false;
    }

    //get all patients
    private List<User> getPatient() {
        List<User> users = new ArrayList<>();

        if(dbHelper.openDatabase()){
            users = dbHelper.getPatient();
        }
        dbHelper.close();
        return users;
    }

    //get the patient's data
    private void getProfil() {

        try {
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

    //Update patient data
    private void updatePatient(){
        try {
            String NAME = name.getText().toString().replace(" ","");
            String FIRST_NAME = first_Name.getText().toString().replace(" ","");
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

            String DEFICIENCY = getDeficiencyType();

            if(!HEIGHT.isEmpty()) {
                if (Float.parseFloat(HEIGHT) > 100) {
                    HEIGHT = HEIGHT.substring(0, 1) + "." + HEIGHT.substring(1);
                }
            }

            int ID = users.get(id-1).getUserID();

            dbHelper.updatePatient(new User(NAME, FIRST_NAME, DATE_BIRTH, MAIL,
                    ADDRESS, PHONE, GENDER, HEIGHT, WEIGHT, HEMOGLOBIN,
                    VGM, TCMH, IDR_CV, HYPO, RET_HE, PLATELET, FERRITIN,
                    TRANSFERRIN, SERUM_IRON, UNIT, CST, FIBRINOGEN, CRP, OTHER,
                    SECURED, PSEUDO, DEFICIENCY), ID);
            dbHelper.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

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

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(EditPatient.this, R.string.update, Toast.LENGTH_SHORT);
                    mToast.show();

                    finish();
                    return true;
                }
                break;
            case android.R.id.home:
                onBackPressed();
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
