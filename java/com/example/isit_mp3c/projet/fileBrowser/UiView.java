package com.example.isit_mp3c.projet.fileBrowser;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.patient.EditPatient;

/**
 * Created by techmed on 07/06/2017.
 */

public class UiView extends ListFragment {
    //This is a passive view, so my presenter handles all of the updating, etc.
    private Presenter presenter;

    //This is a good place to do final initialization as the Fragment is finished initializing itself.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set the presenter
        setPresenter(new Presenter(this));
    }

    public void setPresenter(Presenter p) {
        presenter = p;

        /*I am not using this, but I like to enable it just in case I want to populate the overflow menu
        with menu options
         */
        setHasOptionsMenu(true);
    }

    //Return the view to the Activity for display.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listfragment_main, container, false);
    }

    //When we intercept a click, call through to the appropriate method in the presenter.
    @Override
    public void onListItemClick(ListView listView, android.view.View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        presenter.listItemClicked(listView, view, position, id);
    }

    /* Populate options menu and or action bar with menu from res/menu/menu_browser.xml*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_browser, menu);
    }

    //Called when an item in the menu, or the home button (if enabled) is selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                presenter.homePressed();
                break;

            case R.id.exit:
                finish();
                break;
            //case R.id.settings:
            //    presenter.settings();
        }

        return super.onOptionsItemSelected(item);
    }

    public void finish(){
        getActivity().finish();
    }

    public void onBackPressedOverrided()
    {
        presenter.homePressed();
    }

}
