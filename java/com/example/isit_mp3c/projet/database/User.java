package com.example.isit_mp3c.projet.database;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ISIT on 21/07/2016.
 */
public class User {
    private int userID;
    private  String name;
    private String firstName;
    private String password;
    private String dateBirth;
    private String mail;
    private String address;
    private String username;
    private String sexe;
    private String phone;
    private String height;
    private String weight;
    private String imc;
    private String hb;
    private String vgm;
    private String tcmh;
    private String idr_cv;
    private String hypo;
    private String ret_he;
    private String platelet;
    private String ferritin;
    private String transferrin;
    private String serum_iron;
    private String cst;
    private String fibrinogen;
    private String crp;
    private String other;
    private String serum_iron_unit;
    private String secured;
    private String pseudo;


    public int getUserID(){
        return userID;
    }
    public void setUserID(int id){this.userID = id; }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getFirstName(){
        return firstName;
    }
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getDateBirth(){
        return dateBirth;
    }
    public void setDateBirth(String dateBirth){
        this.dateBirth = dateBirth;
    }

    public String getMail(){
        return mail;
    }
    public void setMail(String mail){
        this.mail = mail;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }

    public String getSexe(){
        return sexe;
    }
    public void setSexe(String sexe){
        this.sexe = sexe;
    }

    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getOther(){
        return other;
    }
    public void setOther(String other){
        this.other = other;
    }

    public String getHeight(){return height;}
    public void setHeight(String height){
        this.height = height;
    }

    public String getWeight(){return weight;}
    public void setWeight(String weight){
        this.weight = weight;
    }

    public String getImc(){
        String IMC = "";
        try {
            Float float_height = Float.parseFloat(height);
            Float float_weight = Float.parseFloat(weight);
            //Calculate IMC
            Float float_imc = float_weight / (float_height * float_height);

            //Convert float to 2 number after the dot
            // must use the English locale format to get a number with "dot" not "comma"
            // so we could export the DB correctly
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
            numberFormat.setMaximumFractionDigits(2);
            IMC = numberFormat.format(float_imc);

        }catch (Exception e){
            Log.e("Convert Float", "Cannot convert the value of HEIGHT and WEIGHT, User_java");
        }


        return IMC;
    }
    public void setImc(String imc ){
        this.imc = imc;
    }

    public String getHb(){return hb;}
    public void setHb(String hb){
        this.hb = hb;
    }

    public String getVgm(){return vgm;}
    public void setVgm(String vgm){
        this.vgm = vgm;
    }

    public String gettcmh(){return tcmh;}
    public void settcmh(String tcmh){
        this.tcmh = tcmh;
    }

    public String getIdr_cv(){return idr_cv;}
    public void setIdr_cv(String idr_cv){
        this.idr_cv = idr_cv;
    }

    public String getHypo(){return hypo;}
    public void setHypo(String hypo){
        this.hypo = hypo;
    }

    public String getRet_he(){return ret_he;}
    public void setRet_he(String ret_he){
        this.ret_he = ret_he;
    }

    public String getPlatelet(){return platelet;}
    public void setPlatelet(String platelet){
        this.platelet = platelet;
    }

    public String getFerritin(){return ferritin;}
    public void setFerritin(String ferritin){
        this.ferritin = ferritin;
    }

    public String getTransferrin(){return transferrin;}
    public void setTransferrin(String transferrin){
        this.transferrin = transferrin;
    }

    public String getSerum_iron(){return serum_iron;}
    public void setSerum_iron(String serum_iron){
        this.serum_iron = serum_iron;
    }

    public String getCst(){return cst;}
    public void setCst(String cst){
        this.cst = cst;
    }

    public String getFibrinogen(){return fibrinogen;}
    public void setFibrinogen(String fibrinogen){
        this.fibrinogen = fibrinogen;
    }

    public String getCrp(){return crp;}
    public void setCrp(String crp){
        this.crp = crp;
    }

    public String getSerum_iron_unit(){return serum_iron_unit;}
    public void setSerum_iron_unit(String serum_iron_unit){
        this.serum_iron_unit = serum_iron_unit;
    }

    public String getSecured(){return secured;}
    public void setSecured(String secured){
        this.secured = secured;
    }

    public String getPseudo(){return pseudo;}
    public void setPseudo(String pseudo){
        this.pseudo = pseudo;
    }


    //Constructor for getting patient profil
    public User(int userID, String name, String firstName, String dateBirth,
                String mail, String address, String phone, String sexe,
                String height, String weight, String imc, String hb,
                String vgm, String tcmh, String idr_cv, String hypo,
                String ret_he, String platelet, String ferritin,
                String transferrin, String serum_iron, String serum_iron_unit, String cst,
                String fibrinogen, String crp, String other, String secured, String pseudo){
        this.userID = userID;
        this.name = name;
        this.firstName = firstName;
        this.dateBirth = dateBirth;
        this.mail = mail;
        this.address = address;
        this.phone = phone;
        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        this.imc = imc;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
        this.secured = secured;
        this.pseudo = pseudo;
    }



    //Constructor for Add Patient
    public User( String name, String firstName, String dateBirth,
                 String mail, String adress, String phone, String sexe,
                 String height, String weight, String hb,
                 String vgm, String tcmh, String idr_cv, String hypo,
                 String ret_he, String platelet, String ferritin,
                 String transferrin, String serum_iron, String serum_iron_unit,
                 String cst, String fibrinogen, String crp, String other,
                 String secured, String pseudo){
        this.name = name;
        this.firstName = firstName;
        this.dateBirth = dateBirth;
        this.mail = mail;
        this.address = adress;
        this.phone = phone;
        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
        this.secured = secured;
        this.pseudo = pseudo;
        this.pseudo = pseudo;
    }

    //Constructor for Add Patient
    public User( String name, String firstName, String dateBirth,
                 String mail, String address, String phone, String sexe,
                 String height, String weight, String hb,
                 String vgm, String tcmh, String idr_cv, String hypo,
                 String ret_he, String platelet, String ferritin,
                 String transferrin, String serum_iron, String serum_iron_unit,
                 String cst, String fibrinogen, String crp, String other){
        this.name = name;
        this.firstName = firstName;
        this.dateBirth = dateBirth;
        this.mail = mail;
        this.address = address;
        this.phone = phone;
        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        //this.imc = imc;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
    }


    //Constructor for Add Patient
    public User( String sexe, String height, String weight, String hb,
                 String vgm, String tcmh, String idr_cv, String hypo,
                 String ret_he, String platelet, String ferritin,
                 String transferrin, String serum_iron, String serum_iron_unit,
                 String cst, String fibrinogen, String crp, String other,
                 String secured, String pseudo){

        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
        this.secured = secured;
        this.pseudo = pseudo;
    }

    //Constructor for Add Patient
    public User( String sexe, String height, String weight, String hb,
                 String vgm, String tcmh, String idr_cv, String hypo,
                 String ret_he, String platelet, String ferritin,
                 String transferrin, String serum_iron, String serum_iron_unit,
                 String cst, String fibrinogen, String crp, String other, String pseudo){

        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
        this.pseudo = pseudo;
    }

    //Constructor for Add Patient
    public User( String sexe, String height, String weight, String hb,
                 String vgm, String tcmh, String idr_cv, String hypo,
                 String ret_he, String platelet, String ferritin,
                 String transferrin, String serum_iron, String serum_iron_unit,
                 String cst, String fibrinogen, String crp, String other){

        this.sexe = sexe;
        this.height = height;
        this.weight = weight;
        this.hb = hb;
        this.vgm = vgm;
        this.tcmh = tcmh;
        this.idr_cv = idr_cv;
        this.hypo = hypo;
        this.ret_he = ret_he;
        this.platelet = platelet;
        this.ferritin = ferritin;
        this.transferrin = transferrin;
        this.serum_iron = serum_iron;
        this.serum_iron_unit = serum_iron_unit;
        this.cst = cst;
        this.fibrinogen = fibrinogen;
        this.crp = crp;
        this.other = other;
    }

}
