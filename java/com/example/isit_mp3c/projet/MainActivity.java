package com.example.isit_mp3c.projet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.isit_mp3c.projet.camera.CameraActivity;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;
import com.example.isit_mp3c.projet.exportdb.ExportDBActivity;
import com.example.isit_mp3c.projet.patient.AddPatientActivity;
import com.example.isit_mp3c.projet.patient.AddPatientAnonym;
import com.example.isit_mp3c.projet.patient.ListProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jcraft.jsch.*;

import android.provider.Settings.Secure;

public class MainActivity extends AppCompatActivity {

    private Button addPatientBtn, searchBtn, photoBtn, exportBtn;
    private String android_id;
    List<User> users = new ArrayList<>();
    ExportDBActivity exportDBActivity;
    SQLiteDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        dbHelper = SQLiteDBHelper.getInstance(this);

        if (isOnline()) {
            Toast.makeText(this, "Connecté", Toast.LENGTH_LONG).show();
            sendData();
        } else
            Toast.makeText(this, "Non connecté", Toast.LENGTH_LONG).show();

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

        photoBtn = (Button) findViewById(R.id.picture_button);
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

    }

    public void setLanguage(String lang) {
        String languageToLoad = lang;
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
        String lang = null;

        switch (id) {
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

    public void chooseDialog(View view) {
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
                        if (intent[0] != null)
                            startActivity(intent[0]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(getIntent());
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void sendData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String directory = "/home/comptemicroct/blueside";

                try {
                    JSch ssh = new JSch();
                    Session session = ssh.getSession("comptemicroct", "193.49.166.86", 22);
                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.setPassword("MachinaeRising@2015");
                    session.connect();
                    Channel channel = session.openChannel("sftp");
                    channel.connect();

                    // Test if your device is connected to the sftp server
                    /*boolean connect = session.isConnected();
                    if (connect)
                        Log.i("connect ", "true");
                    else
                        Log.i("connect ", "false");*/

                    ChannelSftp sftp = (ChannelSftp) channel;

                    String dir = android_id;
                    SftpATTRS attrs = null;
                    try {
                        attrs = sftp.stat(directory+"/"+dir);
                    } catch (Exception e) {
                        Log.i("a",directory+"/"+dir+" not found");
                    }

                    if (attrs != null) {
                        Log.i("a","Directory exists IsDir="+attrs.isDir());
                    } else {
                        Log.i("a","Creating dir "+dir);
                        sftp.mkdir(directory+"/"+dir);
                    }

                    sftp.cd(directory+"/"+dir);

                    FileInputStream fis = createFile(getApplicationContext());
                    sftp.put(fis, "blueSIDE.csv");

                    channel.disconnect();
                    session.disconnect();
                } catch (JSchException e) {
                    System.out.println(e.getMessage().toString());
                    e.printStackTrace();
                }  catch (SftpException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public FileInputStream createFile(Context context) throws IOException {
        users = dbHelper.getPatient();
        String fileName = "blueSIDE.csv";
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println("sep=;");
            printWriter.println("ID; NAME; FIRST_NAME; BIRTH_DATE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO; RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO ");

            for (int i = 0; i < users.size(); i++) {
                try {
                    int id = users.get(i).getUserID();
                    String secured = users.get(i).getSecured();
                    Log.i("secured", "ExportDB, secured : " + secured);
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
                    Log.i("IMC", "ExportDB, the value of IMC is : " + imc);
                    String hb = users.get(i).getHb();
                    Log.i("HB", "ExportDB, the value of hb is " + hb);
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

                    String record = id + ";" + name + ";" + firstName + ";" + birthDate + ";" + adress
                            + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                            + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                            + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                            + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                            + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                            + ";" + pseudo;
                    printWriter.println(record);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExportDB", "Error in for : " + e.getMessage());
                }
            }
            dbHelper.close();
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openFileInput(fileName);
    }

}




