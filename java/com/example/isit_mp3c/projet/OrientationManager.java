package com.example.isit_mp3c.projet;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Created by techmed on 05/07/2017.
 */

public class OrientationManager extends OrientationEventListener {

    public enum ScreenOrientation {
        REVERSED_LANDSCAPE, LANDSCAPE, PORTRAIT, REVERSED_PORTRAIT
    }

    public ScreenOrientation screenOrientation;
    private OrientationListener listener;

    public OrientationManager(Context context, int rate, OrientationListener listener) {
        super(context, rate);
        setListener(listener);
    }

    public OrientationManager(Context context, int rate) {
        super(context, rate);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == -1){
            return;
        }
        ScreenOrientation newOrientation;
        if (orientation >= 60 && orientation <= 140){
            newOrientation = ScreenOrientation.REVERSED_LANDSCAPE;
        } else if (orientation >= 140 && orientation <= 220) {
            newOrientation = ScreenOrientation.REVERSED_PORTRAIT;
        } else if (orientation >= 220 && orientation <= 300) {
            newOrientation = ScreenOrientation.LANDSCAPE;
        } else {
            newOrientation = ScreenOrientation.PORTRAIT;
        }
        if(newOrientation != screenOrientation){
            screenOrientation = newOrientation;
            if(listener != null){
                listener.onOrientationChange(screenOrientation);
            }
        }
    }

    public OrientationManager(Context context) {
        super(context);
    }

    public void setListener(OrientationListener listener){
        this.listener = listener;
    }

    public ScreenOrientation getScreenOrientation(){
        return screenOrientation;
    }

    public interface OrientationListener {

        public void onOrientationChange(ScreenOrientation screenOrientation);
    }
}