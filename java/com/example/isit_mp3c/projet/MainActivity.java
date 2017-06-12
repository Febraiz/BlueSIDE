package com.example.isit_mp3c.projet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.isit_mp3c.projet.camera.CameraActivity;
import com.example.isit_mp3c.projet.database.Acquisition;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;
import com.example.isit_mp3c.projet.exportdb.ExportDBActivity;
import com.example.isit_mp3c.projet.fileBrowser.FileBrowser;
import com.example.isit_mp3c.projet.patient.AddPatientActivity;
import com.example.isit_mp3c.projet.patient.AddPatientAnonym;
import com.example.isit_mp3c.projet.patient.ListProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.isit_mp3c.projet.patient.AutresOptions;
import com.jcraft.jsch.*;

import android.provider.Settings.Secure;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

public class MainActivity extends AppCompatActivity {

    private Toast mToast = null;
    private Button addPatientBtn, searchBtn, exportFtpBtn, autresBtn;
    private FloatingActionButton fileExplorerBtn;
    private static final int REQUEST_READ_STORAGE_RESULT = 1;

    private String android_id;
    List<User> users = new ArrayList<>();
    ExportDBActivity exportDBActivity;
    SQLiteDBHelper dbHelper;
    String directory = "/home/comptemicroct/blueside";
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

        dbHelper = SQLiteDBHelper.getInstance(this);

        if (isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.senddata)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sendData();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create();
            builder.show();
        }

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

        autresBtn = (Button) findViewById(R.id.option_button);
        autresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AutresOptions.class);
                startActivity(intent);
            }
        });

        exportFtpBtn = (Button) findViewById(R.id.export_ftp_button);
        exportFtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(R.string.senddata)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sendData();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create();
                    builder.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage(getString(R.string.no_connection))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create();
                    builder.show();
                }

            }
        });

        fileExplorerBtn = (FloatingActionButton) findViewById(R.id.FBSearch);
        fileExplorerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                https://www.google.fr/search?q=android+how+to+open+file+manager+app&oq=android+how+to+open+file+manager+app&aqs=chrome..69i57.5063j0j7&sourceid=chrome&ie=UTF-8#q=android+open+6.0+file+explorer+intent

                http://forum.codecall.net/topic/79689-creating-a-file-browser-in-android/
                */

                //Check if any acquisitions as been done
                if(SQLiteDBHelper.getInstance(MainActivity.this).getCountAcquisition() == 0)
                {

                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(MainActivity.this, "Aucune acquisition disponible", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                else {

                    //Check permissions
                    if (Build.VERSION.SDK_INT < 23) {
                        openFileEx();
                    } else {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                            openFileEx();

                        } else {

                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                if (mToast != null) mToast.cancel();
                                mToast = Toast.makeText(MainActivity.this, "No Permission to read the external storage", Toast.LENGTH_SHORT);
                                mToast.show();
                            }
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_RESULT);

                        }
                    }

                }

            }
        });

    }

    //Open the file explorer
    public void openFileEx() {
        Intent intent = new Intent(MainActivity.this, FileBrowser.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_READ_STORAGE_RESULT:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(this, "Read external storage permission have not been granted", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                else {
                    // If authorized
                    try {
                        openFileEx();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
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
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.sending_data), "", true);
                    }
                });
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
                        Log.i("a","Creating dir "+directory+"/"+ dir);
                        sftp.mkdir(directory+"/"+dir);
                    }

                    sftp.cd(directory+"/"+dir);

                    FileInputStream fis = createFile(getApplicationContext());
                    sftp.put(fis, "blueSIDE.csv");

                    sftp.cd(directory);
                    File localSrc = getExternalFilesDir("");
                    upload(localSrc,sftp,directory);

                    channel.disconnect();
                    session.disconnect();
                } catch (JSchException e) {
                    System.out.println(e.getMessage().toString());
                    e.printStackTrace();
                }  catch (SftpException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
        thread.start();
    }

    //new
    public void upload(File src, ChannelSftp sftp, String dir) throws IOException, SftpException {
        if (src.isDirectory()) {
            SftpATTRS attrs = null;
            try {
                attrs = sftp.stat(dir + "/" + src.getName());
            } catch (Exception e) {
                Log.i("a",dir + "/" + src.getName()+" not found");
            }

            if (attrs != null) {
                Log.i("a","Directory exists IsDir="+attrs.isDir());
            } else {
                Log.i("a","Creating dir "+dir);
                sftp.mkdir(dir + "/" + src.getName());
            }

            if(src.getName().contains("patient") && src.getName().contains("_acq")){
                String string = src.getName();
                String[] parts = string.split("_");
                String id = parts[1];
                String[] parts1 = string.split("_acq");
                String acquisition_number = parts1[1];

                User user = dbHelper.getPatientWithId(Integer.parseInt(id));
                Acquisition  acq = dbHelper.getAcquisition(Integer.parseInt(id),Integer.parseInt(acquisition_number));

                //suppression fichier data.csv if exist
                try {
                    sftp.rm(dir + "/" + src.getName() + "/" + "data.csv");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FileInputStream dataFile = createdataFile(getApplicationContext(),user,acq);

                sftp.put(dataFile,dir + "/" + src.getName() + "/" + "data.csv");


                File fileToDelete = new File(getApplicationContext().getCacheDir()+
                        File.separator + "data.csv");
                Log.i("delete cache", "data.csv cache file deleted : " + fileToDelete.delete());

            }

            sftp.cd(dir + "/" + src.getName());

            for (File file : src.listFiles()) {
                upload(file, sftp,dir+"/"+src.getName());
            }
            sftp.cd(directory);
        }
        else {
            InputStream srcStream = null;
            try {
                try {
                    sftp.lstat(src.getName());
                } catch (SftpException e){
                    if(e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE){
                        // file doesn't exist
                        srcStream = src.toURI().toURL().openStream();
                        sftp.put(srcStream,src.getName());
                        Log.i("src",src.getName() + " add on ftp");
                    } else {
                        // something else went wrong
                        throw e;
                    }
                }
            }
            finally {
                IOUtils.closeQuietly(srcStream);
            }
        }
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
            printWriter.append("sep=;" + "\n");
            printWriter.append("ID; NAME; FIRST_NAME; BIRTH_DATE; AGE; ADDRESS; MAIL; PHONE; SEX;" +
                    " HEIGHT; WEIGHT; IMC; HB; VGM; TCMH; IDR_CV; HYPO; RET_HE; PLATELET;" +
                    " FERRITINE; TRANSFERRIN; SERUM_IRON; CST; FIBRINOGEN; CRP; NOTES; SECURED;" +
                    " PSEUDO; DEFICIENCY" + "\n");

            for (int i = 0; i < users.size(); i++) {
                try {
                    int id = users.get(i).getUserID();
                    String secured = users.get(i).getSecured();
                    Log.i("secured", "ExportDB, secured : " + secured);
                    String name = users.get(i).getName();
                    String firstName = users.get(i).getFirstName();
                    String birthDate = users.get(i).getDateBirth();
                    String age = users.get(i).getAge();
                    String adress = users.get(i).getAddress();
                    String mail = users.get(i).getMail();
                    String phone = users.get(i).getPhone();
                    String sex = users.get(i).getSexe();
                    String height = users.get(i).getHeight();
                    String weight = users.get(i).getWeight();
                    String imc = users.get(i).getImc();
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
                    String carence = users.get(i).getDeficiency();

                    String record = id + ";" + name + ";" + firstName + ";" + birthDate + ";" + age + ";" + adress
                            + ";" + mail + ";" + phone + ";" + sex + ";" + height + ";"
                            + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                            + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                            + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                            + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                            + ";" + pseudo + ";" + carence;
                    printWriter.append(record + "\n");
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

    //new
    public FileInputStream createdataFile(Context context, User user, Acquisition acquisition) throws IOException {
        users = dbHelper.getPatient();
        String fileName = "data.csv";
        File cacheFile = new File(context.getCacheDir() + File.separator + fileName);
        cacheFile.createNewFile();

        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.append("sep=;" + "/n");
            printWriter.append("ID;NAME;FIRST_NAME;BIRTH_DATE;AGE;ADDRESS;MAIL;PHONE;DATE;ACQ_IDX;SEX;" +
                    "HEIGHT;WEIGHT;IMC;HB;VGM;TCMH;IDR_CV;HYPO;RET_HE;PLATELET;" +
                    "FERRITINE;TRANSFERRIN;SERUM_IRON;CST;FIBRINOGEN;CRP;NOTES;SECURED;" +
                    "PSEUDO;DEFICIENCY" + "/n");

                try {
                    int id = user.getUserID();
                    String secured = user.getSecured();
                    Log.i("secured", "ExportDB, secured : " + secured);
                    String name = user.getName();
                    String firstName = user.getFirstName();
                    String birthDate = user.getDateBirth();
                    String age = user.getAge();
                    String adress = user.getAddress();
                    String mail = user.getMail();
                    String phone = user.getPhone();
                    String date = acquisition.getDate_acquisition();
                    String acq_idx = Integer.toString(acquisition.getAcquisition_number());
                    String sex = user.getSexe();
                    String height = user.getHeight();
                    String weight = user.getWeight();
                    String imc = user.getImc();
                    Log.i("IMC", "ExportDB, the value of IMC is : " + imc);
                    String hb = user.getHb();
                    Log.i("HB", "ExportDB, the value of hb is " + hb);
                    String vgm = user.getVgm();
                    String tcmh = user.gettcmh();
                    String idr_cv = user.getIdr_cv();
                    String hypo = user.getHypo();
                    String ret_he = user.getRet_he();
                    String platelet = user.getPlatelet();
                    String ferritin = user.getFerritin();
                    String transferrin = user.getTransferrin();
                    String serum_iron_value = user.getSerum_iron();
                    String serum_iron_unit = user.getSerum_iron_unit();
                    String serum_iron = "";
                    if (!serum_iron_unit.equalsIgnoreCase("(unité)") && !serum_iron_unit.equalsIgnoreCase("(unit)")) {
                        serum_iron = serum_iron_value + " " + serum_iron_unit;
                    }
                    String cst = user.getCst();
                    String fibrinogen = user.getFibrinogen();
                    String crp = user.getCrp();
                    String notes = user.getOther();
                    String pseudo = user.getPseudo();
                    String carence = user.getDeficiency();

                    String record = id + ";" + name + ";" + firstName + ";" + birthDate + ";" + age + ";" + adress
                            + ";" + mail + ";" + phone + ";"+date+";"+acq_idx+";" + sex + ";" + height + ";"
                            + weight + ";" + imc + ";" + hb + ";" + vgm + ";" + tcmh
                            + ";" + idr_cv + ";" + hypo + ";" + ret_he + ";" + platelet
                            + ";" + ferritin + ";" + transferrin + ";" + serum_iron + ";"
                            + cst + ";" + fibrinogen + ";" + crp + ";" + notes + ";" + secured
                            + ";" + pseudo + ";" + carence;
                    printWriter.append(record + "/n");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExportDB", "Error in for : " + e.getMessage());
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




