package com.example.isit_mp3c.projet.database;

/**
 * Created by ISIT on 21/07/2016.
 */
public class Photo {
    private int imageID;
    private String imageRef;
    private String imageTitle;
    private String imageDate;
    private Boolean flash;
    private float exposureTime;
    private String imageResult;

    public int getImageID(){
        return imageID;
    }

    public String getImageRef(){
        return imageRef;
    }

    public void setImageRef(String image_Ref){
        this.imageRef = image_Ref;
    }

    public String getImageTitle(){
        return imageTitle;
    }

    public void setImageTitle(String image_Title){
        this.imageTitle = image_Title;
    }

    public String getImageDate(){
        return imageDate;
    }

    public void setImageDate(String image_Date){
        this.imageDate = image_Date;
    }

    public boolean getFlash(){
        return flash;
    }

    public void setFlash(boolean flash){
        this.flash = flash;
    }

    public float getExposureTime(){
        return exposureTime;
    }

    public void setExposureTime(float exposure_Time){
        this.exposureTime = exposure_Time;
    }

    public String getImageResult(){
        return imageResult;
    }

    public void setImageResult(String image_Result){
        this.imageResult = image_Result;
    }

    public Photo(int imageID, String imageRef, String imageTitle){
        this.imageID = imageID;
        this.imageRef = imageRef;
        this.imageTitle = imageTitle;
    }
}
