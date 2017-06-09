package com.example.isit_mp3c.projet.patient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListProfile extends AppCompatActivity {

    //adapter to display the list's data
    ArrayAdapter<String> mAdapter;

    //Patient rows that will be retrieved
    private ArrayList<String> values = new ArrayList<String>();

    private List<User> users;
    private ListView listProfile;

    SQLiteDBHelper dbH = SQLiteDBHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listProfile = (ListView) findViewById(R.id.list);

        //Create an empty adapter that will be used to display the loaded data

        //The adapter is created in the "onResume", to load new data when adding one to the database

        listProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("last_ID", "ListProfile : the id putted in extra is : "
                        + Integer.valueOf(position + 1));
                String secured;
                try {
                    secured = users.get(position).getSecured();
                } catch (Exception e) {
                    e.printStackTrace();
                    secured = "null";
                    Log.i("secured", "ListProfil, the system was enable to read the " +
                            "value of secured");
                }
                if (secured == null) {
                    secured = "null";
                }
                try {

                    switch (secured) {
                        case "FALSE":
                            Intent profilIntent = new Intent(ListProfile.this, ProfilPatient.class);
                            profilIntent.putExtra("last_ID", position + 1);
                            startActivity(profilIntent);
                            break;
                        case "TRUE":
                            Intent anonymProfilIntent = new Intent(ListProfile.this,
                                    ProfilAnonymPatient.class);
                            anonymProfilIntent.putExtra("last_ID", position + 1);
                            startActivity(anonymProfilIntent);
                            break;
                        case "null":
                            Intent caseNullIntent = new Intent(ListProfile.this, ProfilPatient.class);
                            caseNullIntent.putExtra("last_ID", position + 1);
                            startActivity(caseNullIntent);
                            break;
                        default:
                            Intent defaultIntent = new Intent(ListProfile.this, ProfilPatient.class);
                            defaultIntent.putExtra("last_ID", position + 1);
                            startActivity(defaultIntent);
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("switch type", "erreur lors de la lecture du type de profil");
                }
            }
        });
    }

    private ArrayList<String> addValues() {
        ArrayList<String> values = new ArrayList<String>();
        users = getPatient();
        for (int i = 1; i <= users.size(); i++) {
            if(users.get(i-1).getName() !=  null)
                values.add("Patient n°" + i + " : " + users.get(i-1).getName() + " " + users.get(i-1).getFirstName());
            else
                values.add("Patient n°" + i + " : " + users.get(i-1).getPseudo() + " (anonyme)");
        }
        return values;
    }

    public List<User> getPatient() {
        List<User> users = new ArrayList<>();

        if (dbH.openDatabase()) {
            users = dbH.getPatient();
        }
        dbH.close();
        return users;
    }

    @Override
    public void onResume() {
        super.onResume();

        //add item to the list
        values = addValues();

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);

        listProfile.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        //Liaison de l'activité avec le menu
        inflater.inflate(R.menu.menu_list_patient, menu);

        menu.getItem(0).setEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.newPatient:
                chooseDialog(new View(getBaseContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chooseDialog(View view) {
        AlertDialog alertDialog = null;
        final Intent[] intent = new Intent[1];

        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {getString(R.string.choose_protocol_anonym),
                getString(R.string.choose_protocol_not_anonym)};
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ListProfile.this);
        builder.setTitle(R.string.choose_protocol_title)
                //.setMessage(R.string.choose_protocol_message)
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        switch (item) {
                            case 0:
                                intent[0] = new Intent(ListProfile.this,
                                        AddPatientAnonym.class);
                                break;
                            case 1:
                                intent[0] = new Intent(ListProfile.this,
                                        AddPatientActivity.class);
                                break;
                        }
                    }
                })
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(intent[0] != null)
                            startActivity(intent[0]);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

}
