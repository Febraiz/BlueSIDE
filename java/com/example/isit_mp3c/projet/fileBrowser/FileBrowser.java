package com.example.isit_mp3c.projet.fileBrowser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.isit_mp3c.projet.R;

/**
 * Created by techmed on 07/06/2017.
 */

public class FileBrowser extends AppCompatActivity {
    private UiView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mainfilebrowser);

        mView = (UiView) getFragmentManager().findFragmentById(R.id.file_list);

        //Activate top actionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        mView.onBackPressedOverrided();
    }

}
