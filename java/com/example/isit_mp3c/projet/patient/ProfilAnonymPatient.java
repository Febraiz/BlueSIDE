package com.example.isit_mp3c.projet.patient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.util.ArrayList;
import java.util.List;

public class ProfilAnonymPatient extends AppCompatActivity {

    private TextView idPatient,sex, height, weight, hemoglobin,
            vgm, tcmh, idr_cv, hypo, ret_he, platelet, ferritin,
            transferrin, serum_iron, cst, fibrinogen, crp, other, imc, deficiency;
    private List<User> users;
    private int id ;
    SQLiteDBHelper dbHelper = SQLiteDBHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_anonym_patient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getInt("last_ID");
        Log.i("Profil Last ID", "AnonymProfilPatient_java, Get the last ID pleaaase = " + id);

        //set toolbar title
        getSupportActionBar().setTitle("Patient " + id);

        users = getPatient();
        getProfil(id);

        Button okBtn = (Button)findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent okIntent = new Intent(ProfilAnonymPatient.this, MainActivity.class);
                startActivity(okIntent);
            }
        });

        Button editBtn = (Button)findViewById(R.id.edit_button);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(ProfilAnonymPatient.this, EditAnonymPatient.class);
                editIntent.putExtra("ID",id);
                startActivity(editIntent);
            }
        });
    }

    //get all patients
    public List<User> getPatient() {
        List<User> users = new ArrayList<>();

        /*try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            dbHelper.close();
            throw new Error("unable to create database");
        }*/
        if(dbHelper.openDatabase()){
            users = dbHelper.getPatient();
        }
        dbHelper.close();
        return users;
    }

    public void getProfil(int id){

        idPatient = (TextView)findViewById(R.id.id_patient);
        height = (TextView)findViewById(R.id.height_patient);
        weight = (TextView)findViewById(R.id.weight_patient);
        imc = (TextView)findViewById(R.id.imc);
        hemoglobin = (TextView)findViewById(R.id.hb);
        vgm = (TextView)findViewById(R.id.vgm);
        tcmh = (TextView)findViewById(R.id.tcmh);
        idr_cv = (TextView)findViewById(R.id.idr_cv);
        hypo = (TextView)findViewById(R.id.hypo);
        ret_he = (TextView)findViewById(R.id.ret_he);
        platelet = (TextView)findViewById(R.id.platelet);
        ferritin = (TextView)findViewById(R.id.ferritin);
        transferrin = (TextView)findViewById(R.id.transferrin);
        serum_iron = (TextView)findViewById(R.id.srum_iron);
        cst = (TextView)findViewById(R.id.cst);
        fibrinogen = (TextView)findViewById(R.id.fibrinogen);
        crp = (TextView)findViewById(R.id.crp);
        other = (TextView)findViewById(R.id.other);
        sex = (TextView)findViewById(R.id.sexe_patient);
        deficiency = (TextView)findViewById(R.id.textViewDeficiency);

        //String idText = String.valueOf(id);

        try {

            //idPatient.setText(idText);
            idPatient.setText(users.get(id-1).getPseudo());
            height.setText(users.get(id-1).getHeight().toString());
            weight.setText(users.get(id-1).getWeight().toString());
            imc.setText(users.get(id-1).getImc());
            hemoglobin.setText(users.get(id-1).getHb());
            vgm.setText(users.get(id-1).getVgm());
            tcmh.setText(users.get(id-1).gettcmh());
            idr_cv.setText(users.get(id-1).getIdr_cv());
            hypo.setText(users.get(id-1).getHypo());
            ret_he.setText(users.get(id-1).getRet_he());
            platelet.setText(users.get(id-1).getPlatelet());
            ferritin.setText(users.get(id-1).getFerritin());
            transferrin.setText(users.get(id - 1).getTransferrin());
            //String ironValue = users.get(id-1).getSerum_iron()+
            // users.get(id-1).getSerum_iron_unit();
            String ironValue = users.get(id-1).getSerum_iron();
            String ironUnit = users.get(id-1).getSerum_iron_unit();
            Log.i("Serum iron value", "The serum iron value is : " + ironValue +
                    " ,The serum iron unit is : " + ironUnit);
            if(!ironValue.equals("")) {
                serum_iron.append(ironValue + " " + ironUnit);
            }
            cst.setText(users.get(id-1).getCst());
            fibrinogen.setText(users.get(id-1).getFibrinogen());
            crp.setText(users.get(id-1).getCrp());
            other.setText(users.get(id-1).getOther());
            sex.setText(users.get(id-1).getSexe());

            //Mise en place du bon radioButton
            String carence = users.get(id - 1).getDeficiency();

            switch(carence) {
                case "Carence certaine":
                    deficiency.setText("Carence certaine");
                    break;
                case "Absence de carence":
                    deficiency.setText("Absence de carence");
                    break;
                case "Carence incertaine":
                    deficiency.setText("Carence incertaine");
                    break;
                case "":
                    deficiency.setText("-");
                    break;
            }

        } catch (Exception e) {
            Log.e("DB error", "ProfilPatient_java, It did not read the ID value");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profil_patient, menu);
/*        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(true);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, ListProfile.class));
                return true;
            case R.id.delete_patient:
                deleteDialog(new View(getBaseContext()));
                return true;
            case R.id.edit:
                Intent editIntent = new Intent(ProfilAnonymPatient.this, EditAnonymPatient.class);
                editIntent.putExtra("ID",id);
                startActivity(editIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_patient)
                .setMessage(R.string.delete_patient_msg)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("row ID deleted",
                                "ProfilPatient_java, the row ID wich will be deleted is " + id);
                        if (deletePatient()) {
                            Toast.makeText(ProfilAnonymPatient
                                            .this, R.string.patient_deleted,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfilAnonymPatient.this, R.string.patient_not_deleted,
                                    Toast.LENGTH_LONG).show();
                        }
                        Intent profilToList = new Intent(ProfilAnonymPatient.this,ListProfile.class);
                        /*profilToList.putExtra("deletedID", id);
                        Log.i("deletedID", "ProfilAnonymPatient," +
                                " the patient id to delete is :" + id);*/
                        startActivity(profilToList);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        startActivity(getIntent());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //delete patient
    public boolean deletePatient(){
        boolean isDeleted;
        final int ID;
        ID = users.get(id-1).getUserID();
        Log.i("deletePatient", "the ID is : " + ID);
        /*try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            dbHelper.close();
            throw new Error("unable to create database");
        }*/
        if(dbHelper.openDatabase()){
            dbHelper.deletePatient(ID);
            isDeleted = true;
        } else{
            isDeleted = false;
        }
        dbHelper.close();
        return isDeleted ? true:false;
    }
}
