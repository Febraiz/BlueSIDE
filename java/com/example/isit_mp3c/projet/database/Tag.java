package com.example.isit_mp3c.projet.database;

/**
 * Created by ISIT on 21/07/2016.
 */
public class Tag {

    private int idTag;
    private String idType;
    private String idParent;
    private String value;

    public int getIdTag() {
        return idTag;
    }

    ;

    public String getIdType() {
        return idType;
    }

    ;

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    //Constructor for getting Tag
    public Tag(int idTag, String value, String idType, String idParent) {
        this.idTag = idTag;
        this.value = value;
        this.idType = idType;
        this.idParent = idParent;
    }

    //Constructor for setting a tag
    public Tag(String value, String idType, String idParent) {
        this.value = value;
        this.idType = idType;
        this.idParent = idParent;
    }
}
