package com.example.isit_mp3c.projet.database;

/**
 * Created by ISIT on 21/07/2016.
 */
public class Type {
    private int idType;
    private String name;
    private String description;
    private String secured;

    public int getIdType(){return idType;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getSecured(){return secured;}
    public void setSecured(String secured){this.secured = secured;}

    //Constructor for getting the Type
    public Type(int idType, String name, String description, String secured){
        this.idType = idType;
        this.name = name;
        this.description = description;
        this.secured = secured;
    }

    //Constructor for setting a new Type
    public Type(String name, String description, String secured){
        this.name = name;
        this.description = description;
        this.secured = secured;
    }
}
