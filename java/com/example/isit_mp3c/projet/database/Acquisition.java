package com.example.isit_mp3c.projet.database;

/**
 * Created by techmed on 24/05/2017.
 */

public class Acquisition {
    private int acquisitionID;
    private int patientID;
    private int acquisition_number;
    private String date_acquisition;

    public Acquisition(int acquisitionID, int patientID, int acquisition_number, String date_acquisition) {
        this.acquisitionID = acquisitionID;
        this.patientID = patientID;
        this.acquisition_number = acquisition_number;
        this.date_acquisition = date_acquisition;
    }

    public Acquisition(int patientID, int acquisition_number, String date_acquisition) {
        this.patientID = patientID;
        this.acquisition_number = acquisition_number;
        this.date_acquisition = date_acquisition;
    }

    public int getAcquisitionID() {
        return acquisitionID;
    }

    public void setAcquisitionID(int acquisitionID) {
        this.acquisitionID = acquisitionID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getAcquisition_number() {
        return acquisition_number;
    }

    public void setAcquisition_number(int acquisition_number) {
        this.acquisition_number = acquisition_number;
    }

    public String getDate_acquisition() {
        return date_acquisition;
    }

    public void setDate_acquisition(String date_acquisition) {
        this.date_acquisition = date_acquisition;
    }
}
