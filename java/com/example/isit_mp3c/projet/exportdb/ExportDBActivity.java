package com.example.isit_mp3c.projet.exportdb;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;
import com.example.isit_mp3c.projet.email.GMailSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.example.isit_mp3c.projet.R.string.mail;

public class ExportDBActivity extends AppCompatActivity {

    private List<User> users;
    private String fileName = "blueSIDE.csv";
    private String anonymFileName = "BlueSIDE_anonym.csv";
    private String nonAnonymFileName = "BlueSIDE_non_anonym.csv";
    private EditText mailAddress, mailSubject, mailBody;
    private String adress, subject, body;
    private int choice =0;
    private List<String> filesPaths;
    private Boolean get_anonym = false;
    private Boolean get_non_anonym = false;
    private Boolean get_all_data = false;
    private SQLiteDBHelper dbHelper = SQLiteDBHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_db);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mailAddress = (EditText)findViewById(R.id.mail_address);
        mailSubject = (EditText)findViewById(R.id.mail_subject);
        mailBody = (EditText)findViewById(R.id.mail_body);

        Button sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> filesName = new ArrayList<String>();
                adress = String.valueOf(mailAddress.getText());
                subject = String.valueOf(mailSubject.getText());
                body = String.valueOf(mailBody.getText());

                if(get_anonym){
                    try {
                        createAnonymFile(getApplicationContext(), anonymFileName);
                        filesName.add(anonymFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(get_non_anonym){
                    try {
                        createNonAnonymFile(getApplicationContext(), nonAnonymFileName);
                        filesName.add(nonAnonymFileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(get_all_data) {
                    try {
                        createFile(getApplicationContext(), fileName);
                        filesName.add(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // if champs  tous rempli
               Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            GMailSender sender = new GMailSender("blueside.project@gmail.com", "blueside123");
                            ArrayList<File> files = new ArrayList<File>();
                            try {
                                for(int i = 0; i < filesName.size(); i++) {
                                    files.add(new File(getApplicationContext().getCacheDir(), filesName.get(i)));
                                }
                                sender.sendMail(mailSubject.getText().toString(),
                                        mailBody.getText().toString()
                                        , "blueside.project@gmail.com", mailAddress.getText().toString(),files);

                                for(int j = 0; j < filesName.size(); j++){
                                    File fileToDelete = new File(getApplicationContext().getCacheDir()+
                                            File.separator + filesName.get(j));
                                    Log.i("delete cache", filesName.get(j) + "cache file deleted : " + fileToDelete.delete());
                                }
                            } catch (Exception e) {
                                for(int j = 0; j < filesName.size(); j++){
                                    File fileToDelete = new File(getApplicationContext().getCacheDir()+
                                            File.separator + filesName.get(j));
                                    Log.i("delete cache", filesName.get(j) + "cache file deleted : " + fileToDelete.delete());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Toast.makeText(ExportDBActivity.this, "Mail envoyé", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAnonymFile(Context context, String fileName) throws IOException {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);

        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());
            users = getPatient();
            printWriter.println("sep=;");
            printWriter.println("NAME; FIRST_NAME; BIRTH_DATE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO; RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO; DEFICIENCY ");

            for(int i =0; i<users.size(); i++) {
                try {
                    String secured = users.get(i).getSecured();
                    Log.i("secured", "ExportDB, secured : "+ secured);
                    if(secured.matches("TRUE")){
                        String name = users.get(i).getName();
                        String firstName = users.get(i).getFirstName();
                        String birthDate = users.get(i).getDateBirth();
                        String adress = users.get(i).getAddress();
                        String mail = users.get(i).getMail();
                        String phone = users.get(i).getPhone();
                        String sex = users.get(i).getSexe();
                        String height = users.get(i).getHeight();
                        String weight = users.get(i).getWeight();
                        String imc = users.get(i).getImc().toString();
                        String hb = users.get(i).getHb();
                        String vgm = users.get(i).getVgm();
                        String tcmh = users.get(i).gettcmh();
                        String idr_cv = users.get(i).getIdr_cv();
                        String hypo = users.get(i).getHypo();
                        String ret_he = users.get(i).getRet_he();
                        String platelet = users.get(i).getPlatelet();
                        String ferritin = users.get(i).getFerritin();
                        String transferrin = users.get(i).getTransferrin();
                        String serum_iron_value = users.get(i).getSerum_iron();
                        String serum_iron_unit = users.get(i).getSerum_iron_unit();
                        String serum_iron = "";
                        if (!serum_iron_unit.equalsIgnoreCase("(unité)") && !serum_iron_unit.equalsIgnoreCase("(unit)")) {
                            serum_iron = serum_iron_value + " " + serum_iron_unit;
                        }
                        String cst = users.get(i).getCst();
                        String fibrinogen = users.get(i).getFibrinogen();
                        String crp = users.get(i).getCrp();
                        String notes = users.get(i).getOther();
                        String pseudo = users.get(i).getPseudo();
                        String carence = users.get(i).getDeficiency();

                        String record = name + ";" + firstName + ";" + birthDate + ";" + adress
                                + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                                + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                                + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                                + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                                + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                                + ";" + pseudo + ";" + carence;
                        printWriter.println(record);
                    }else {
                        Log.i("export db","row not secured");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExportDB", "Error in for : " + e.getMessage());
                }
            }

            dbHelper.close();

            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createNonAnonymFile(Context context, String fileName) throws IOException {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());
            users = getPatient();
            printWriter.println("sep=;");
            printWriter.println("NAME; FIRST_NAME; BIRTH_DATE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO; RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO; DEFICIENCY ");

            for(int i =0; i<users.size(); i++) {
                try {
                    String secured = users.get(i).getSecured();
                    Log.i("secured", "ExportDB, secured : "+ secured);
                    if(secured.matches("FALSE")){
                        String name = users.get(i).getName();
                        String firstName = users.get(i).getFirstName();
                        String birthDate = users.get(i).getDateBirth();
                        String adress = users.get(i).getAddress();
                        String mail = users.get(i).getMail();
                        String phone = users.get(i).getPhone();
                        String sex = users.get(i).getSexe();
                        String height = users.get(i).getHeight();
                        String weight = users.get(i).getWeight();
                        String imc = users.get(i).getImc().toString();
                        String hb = users.get(i).getHb();
                        String vgm = users.get(i).getVgm();
                        String tcmh = users.get(i).gettcmh();
                        String idr_cv = users.get(i).getIdr_cv();
                        String hypo = users.get(i).getHypo();
                        String ret_he = users.get(i).getRet_he();
                        String platelet = users.get(i).getPlatelet();
                        String ferritin = users.get(i).getFerritin();
                        String transferrin = users.get(i).getTransferrin();
                        String serum_iron_value = users.get(i).getSerum_iron();
                        String serum_iron_unit = users.get(i).getSerum_iron_unit();
                        String serum_iron = "";
                        if (!serum_iron_unit.equalsIgnoreCase("(unité)") && !serum_iron_unit.equalsIgnoreCase("(unit)")) {
                            serum_iron = serum_iron_value + " " + serum_iron_unit;
                        }
                        String cst = users.get(i).getCst();
                        String fibrinogen = users.get(i).getFibrinogen();
                        String crp = users.get(i).getCrp();
                        String notes = users.get(i).getOther();
                        String pseudo = users.get(i).getPseudo();
                        String carence = users.get(i).getDeficiency();

                        String record = name + ";" + firstName + ";" + birthDate + ";" + adress
                                + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                                + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                                + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                                + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                                + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                                + ";" + pseudo + ";" + carence;
                        printWriter.println(record);
                    }else {
                        Log.i("export db","row not secured");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExportDB", "Error in for : " + e.getMessage());
                }
            }
            dbHelper.close();
            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void createFile(Context context, String fileName) throws IOException {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());
            users = getPatient();
            printWriter.println("sep=;");
            printWriter.println("ID; NAME; FIRST_NAME; BIRTH_DATE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO; RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO; DEFICIENCY ");

            for(int i =0; i<users.size(); i++) {
                try {
                    int id = users.get(i).getUserID();
                    String secured = users.get(i).getSecured();
                    Log.i("secured", "ExportDB, secured : "+ secured);
                    String name = users.get(i).getName();
                    String firstName = users.get(i).getFirstName();
                    String birthDate = users.get(i).getDateBirth();
                    String adress = users.get(i).getAddress();
                    String mail = users.get(i).getMail();
                    String phone = users.get(i).getPhone();
                    String sex = users.get(i).getSexe();
                    String height = users.get(i).getHeight();
                    String weight = users.get(i).getWeight();
                    String imc = users.get(i).getImc().toString();
                    Log.i("IMC", "ExportDB, the value of IMC is : "+ imc);
                    String hb = users.get(i).getHb();
                    Log.i("HB", "ExportDB, the value of hb is "+ hb);
                    String vgm = users.get(i).getVgm();
                    String tcmh = users.get(i).gettcmh();
                    String idr_cv = users.get(i).getIdr_cv();
                    String hypo = users.get(i).getHypo();
                    String ret_he = users.get(i).getRet_he();
                    String platelet = users.get(i).getPlatelet();
                    String ferritin = users.get(i).getFerritin();
                    String transferrin = users.get(i).getTransferrin();
                    String serum_iron_value = users.get(i).getSerum_iron();
                    String serum_iron_unit = users.get(i).getSerum_iron_unit();
                    String serum_iron = "";
                    if (!serum_iron_unit.equalsIgnoreCase("(unité)") && !serum_iron_unit.equalsIgnoreCase("(unit)")) {
                        serum_iron = serum_iron_value + " " + serum_iron_unit;
                    }
                    String cst = users.get(i).getCst();
                    String fibrinogen = users.get(i).getFibrinogen();
                    String crp = users.get(i).getCrp();
                    String notes = users.get(i).getOther();
                    String pseudo = users.get(i).getPseudo();
                    String carence = users.get(i).getDeficiency();

                    String record =id + ";" +  name + ";" + firstName + ";" + birthDate + ";" + adress
                            + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                            + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                            + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                            + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                            + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                            + ";" + pseudo + ";" + carence;
                    printWriter.println(record);
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExportDB", "Error in for : " + e.getMessage());
                }
            }
            dbHelper.close();
            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //get all patients
    public List<User> getPatient() {
        List<User> users = new ArrayList<>();

        if(dbHelper.openDatabase()){
            users = dbHelper.getPatient();
        }
        dbHelper.close();
        return users;
    }


    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBox_anonym:
                if (checked){
                    get_anonym = true;
                }else{
                    get_anonym = false;
                }
                break;
            case R.id.checkBox_non_anonym:
                if (checked){
                    get_non_anonym = true;
                }else{
                    get_non_anonym = false;
                }
                break;
            case R.id.checkBox_all_db:
                if(checked){
                    get_all_data = true;
                }else{
                    get_all_data = false;
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
