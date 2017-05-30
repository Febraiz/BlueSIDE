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

        Button exportBtn = (Button)findViewById(R.id.send_button);
        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] filesName = new String[3];
                Log.i("filename 1", "ExportDB, fileNames contains : "
                        + filesName[0] + " ; " + filesName[1] + " ; " + filesName[2]);
                adress = String.valueOf(mailAddress.getText());
                subject = String.valueOf(mailSubject.getText());
                body = String.valueOf(mailBody.getText());

            if(get_anonym){
                try {
                    createAnonymFile(getApplicationContext(), anonymFileName);
                    filesName[0] = anonymFileName;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(get_non_anonym){
                try {
                    createNonAnonymFile(getApplicationContext(), nonAnonymFileName);
                    filesName[1] = nonAnonymFileName;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(get_all_data){
                try {
                    createFile(getApplicationContext(), fileName);
                    filesName[2] = fileName;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                //createCacheFile(ExportDBActivity.this, fileName, "this is a test");
                //the activity's launch to send that file via gmail
                startActivity(Intent.createChooser(getSendMailIntent(
                        adress,
                        // "blueside.project@gmail.com",
                        subject,
                        //"test",
                        body,
                        //"Export DB test",
                        filesName), "Send mail..."));
            }  catch (ActivityNotFoundException e) {
                Toast.makeText(ExportDBActivity.this,
                        "Gmail is not available on this devise",
                        Toast.LENGTH_LONG).show();
            }

            Log.i("filename 2", "ExportDB, fileNames contains : "+filesName[0]
                    +" ; "+filesName[1]+" ; "+filesName[2]);

            if(filesName[0] != null){
                File anonymFile = new File(getApplicationContext().getCacheDir()+
                        File.separator + filesName[0]);
                Log.i("delete cache", "anonym cache file deleted : " + anonymFile.delete());
            }

            if(filesName[1] != null){
                File nonAnonymFile = new File(getApplicationContext().getCacheDir()+
                        File.separator + filesName[1]);
                Log.i("delete cache", "non anonym cache file deleted : "
                        + nonAnonymFile.delete());
            }

            if(filesName[2] != null){
                File allDataFile = new File(getApplicationContext().getCacheDir()+
                        File.separator + filesName[2]);
                Log.i("delete cache", "all cache file deleted : "
                        + allDataFile.delete());
            }

        }
    });
}

    public static Intent getSendMailIntent( String email, String subject,
                                           String body, String[] filesName){
        String[] fileName = filesName;
        //final Intent mailIntent = new Intent(Intent.ACTION_SEND);
        final Intent mailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

/*        //only use Gmail to send
        mailIntent.setClassName("com.google.android.gm",
                "com.google.android.gm.ComposeActivityGmail");*/

        //send only email
        mailIntent.setData(Uri.parse("mailto:"));

        //mailIntent.setType("text/plain");
        mailIntent.setType("text/csv");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if(fileName != null){
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for(int i=0; i < fileName.length; i++) {
                if (fileName[i] != null) {
                    Uri uri = Uri.parse("content://" + FileProvider.AUTHORITY + "/" + fileName[i]);
                   /* File file = new File(fileName[i]);
                    Uri uri = Uri.fromFile(file);*/
                    uris.add(uri);
                }
            }
            if(!uris.isEmpty()){
                mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
        } else {
            Log.i("MAIL", "no attachment found");
        }

        return mailIntent;
    }

    //test method
    public String getFileData(Context context){
        String msg = "";
        try{
            InputStream inputStream = openFileInput(fileName);
            if( inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receivedString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receivedString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receivedString);
                }
                inputStream.close();
                msg = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void createAnonymFile(Context context, String fileName) throws IOException {
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
        //FileOutputStream fileOutputStream;

        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());
            users = getPatient();
            printWriter.println("sep=;");
            printWriter.println("NAME; FIRST_NAME; BIRTH_DATE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO, RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO ");

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
                        String imc = users.get(i).getImc().toString(); //les chiffres après la virgule se déplacent ds la colonne de Hb.
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
                        if (serum_iron_unit != "(unité)" && serum_iron_unit != "(unit)") {
                            serum_iron = serum_iron_value + " " + serum_iron_unit;
                        }
                        String cst = users.get(i).getCst();
                        String fibrinogen = users.get(i).getFibrinogen();
                        String crp = users.get(i).getCrp();
                        String notes = users.get(i).getOther();
                        String pseudo = users.get(i).getPseudo();

                        String record = name + ";" + firstName + ";" + birthDate + ";" + adress
                                + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                                + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                                + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                                + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                                + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                                + ";" + pseudo;
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
                    " PSEUDO");

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
                        String imc = users.get(i).getImc().toString(); //les chiffres après la virgule se déplacent ds la colonne de Hb.
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
                        if (serum_iron_unit != "(unité)" && serum_iron_unit != "(unit)") {
                            serum_iron = serum_iron_value + " " + serum_iron_unit;
                        }
                        String cst = users.get(i).getCst();
                        String fibrinogen = users.get(i).getFibrinogen();
                        String crp = users.get(i).getCrp();
                        String notes = users.get(i).getOther();
                        String pseudo = users.get(i).getPseudo();

                        String record = name + ";" + firstName + ";" + birthDate + ";" + adress
                                + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                                + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                                + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                                + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                                + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                                + ";" + pseudo;
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
                    " PSEUDO ");

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
                    String imc = users.get(i).getImc().toString(); //les chiffres après la virgule se déplacent ds la colonne de Hb.
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

                    String record =id + ";" +  name + ";" + firstName + ";" + birthDate + ";" + adress
                            + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                            + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                            + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                            + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                            + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                            + ";" + pseudo;
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
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
