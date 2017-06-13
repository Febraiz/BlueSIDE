package com.example.isit_mp3c.projet.patient;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.isit_mp3c.projet.MainActivity;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.camera.CameraActivity;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.exportdb.ExportDBActivity;

/**
 * Created by Techmed on 08/06/2017.
 */

public class AutresOptions extends AppCompatActivity {

    Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_autres_options);
        Button pictureBtn = (Button) findViewById(R.id.picture_button);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pictureBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(SQLiteDBHelper.getInstance(AutresOptions.this).getCountPatient() == 0)
                {
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(AutresOptions.this, "Aucun patient enregistré", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                else{
                    Intent intent = new Intent(AutresOptions.this, CameraActivity.class);
                    startActivity(intent);
                }

            }
        });


        Button mButton = (Button) findViewById(R.id.export_button);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //public void open(View v){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AutresOptions.this);
                    alertDialogBuilder.setMessage(getString(R.string.mail_request));
                    alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent myIntent = new Intent(AutresOptions.this, ExportDBActivity.class);
                                startActivity(myIntent);
                            }
                    });

                    alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }


            });

        };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


