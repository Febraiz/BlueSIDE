package com.example.isit_mp3c.projet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ISIT on 21/07/2016.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static String dbPath = "";
    private static String dbName = "bluesidedb";
    private static String TABLE_IMAGE = "image";
    private static String TABLE_USER = "user";
    private static String TABLE_TAG = "tag";
    private static String TABLE_TYPE = "type";
    private SQLiteDatabase db;
    private static Context mycontext;
    private static int DB_VERSION = 1;


    //To avoid getting error : to verify
    private static SQLiteDBHelper singleton;

    public static SQLiteDBHelper getInstance(final Context context){
        if(singleton == null){
            singleton = new SQLiteDBHelper(context);
        }
        return singleton;
    }

    //Takes and keeps a reference of the passed context in order to access to the application assets and resources.
    public SQLiteDBHelper(Context context) {
        super(context, dbName, null, DB_VERSION);


        // this code gives error : do not use it !

/*        if (android.os.Build.VERSION.SDK_INT >= 17) {
            dbPath = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            dbPath = "/data/data/" + context.getPackageName() + "/databases/";
        }*/


        //Hard Coding PATH : not the best solution but at least it works !
        dbPath = "/data/data/" + context.getPackageName() + "/databases/"; //this code does not work in every situation
        //dbPath = "/data/data/com.example.isit.testapplication/databases/"; //this code works in every situation : but remember to change the package name!

        //emplacement non securise, possibilite de recuperer le fichier via le Nexus,
        // utilise seulement pour le test.
        //dbPath = context.getExternalFilesDir("bluesideDB") + File.separator;

        this.mycontext = context;
        //Does not work for  Nexus !
       /* String fileDir = mycontext.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        dbPath = fileDir.substring(0, fileDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/*/
    }

    // Creates a empty database on the system and rewrites it with your own database.
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if(dbExist){
            //do nothing - database already exist
        }else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database
            this.getReadableDatabase();
            try {
                copyDatabase();
            }catch (IOException e){
                throw new Error("Error copying database");
            }
        }

    }

    // Check if the database exist to avoid re-copy the data
    private boolean checkDatabase(){
        SQLiteDatabase checkDB = null;
        try{
            String path = dbPath + dbName;

            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        }catch (SQLiteException e){
            //throw new Error("db does not exist");
            e.printStackTrace();
        }

        if(checkDB != null){
            checkDB.close();
        }

     /*   if(checkDB != null) {
            return true;
        }
        else {
            return false;
        }
        The same meaning below*/
        return checkDB != null ? true : false;
    }

    private  void copyDatabase() throws IOException {
        //Open the local db as the input stream
        InputStream input = mycontext.getAssets().open(dbName);
        // path to the created empty db
        String outFileName = dbPath + dbName;
        //open the empty db as output file
        OutputStream output = new FileOutputStream(outFileName);

        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length=input.read(buffer)) > 0){
            output.write(buffer, 0, length);
        }

        //close the streams
        output.flush();
        output.close();
        input.close();
    }

    public boolean openDatabase() {
        try {
            String path = dbPath + dbName;

            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException sqle){
            db = null;
        }
        return db != null ? true : false;
    }


    public boolean openDatabaseReading() {
        try {
            String path = dbPath + dbName;

            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException sqle){
            db = null;
        }
        return db != null ? true : false;
    }

    public synchronized void close(){
        if(db != null)
            db.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //Get all the images
    public List<Photo> getImages(){
        List<Photo> photos = new ArrayList<>();
        try{
            //get all row from Image table
            String query = "SELECT * FROM " + TABLE_IMAGE;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+dbName, null
                    , SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int imageID = Integer.parseInt(cursor.getString(0));
                String imageName = cursor.getString(1);
                String imageRef = cursor.getString(2);

                Photo photo = new Photo(imageID, imageName, imageRef);
                photos.add(photo);
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }
        return photos;
    }

    //Getting all the users
    public List<User> getPatient(){
        List<User> patients = new ArrayList<>();
        try{
            String query = "SELECT * FROM " + TABLE_USER;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+dbName, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int patientID = Integer.parseInt(cursor.getString(0));
                String patientName = cursor.getString(1);
                String patientFirstName = cursor.getString(3);
                String patientBirth = cursor.getString(4);
                String patientmail = cursor.getString(5);
                String patientadress = cursor.getString(6);
                String patientGender = cursor.getString(8);
                String patientHeight =cursor.getString(9);
                String patientWeight = cursor.getString(10);
                String patientIMC = cursor.getString(11);
                String patientHB = cursor.getString(12);
                String patientVGM = cursor.getString(13);
                String patientTCMH = cursor.getString(14);
                String patientIDR_CV = cursor.getString(15);
                String patientHypo = cursor.getString(16);
                String patientRet_He =cursor.getString(17);
                String patientPlatelet = cursor.getString(18);
                String patientFerritin = cursor.getString(19);
                String patientTransferrin = cursor.getString(20);
                String patientSerum_iron = cursor.getString(21);
                String patientCST = cursor.getString(22);
                String patientFibrinogen = cursor.getString(23);
                String patientCRP = cursor.getString(24);
                String patientOthers = cursor.getString(25);
                String patientPhone = cursor.getString(26);
                String Serum_iron_unit = cursor.getString(27);
                String secured = cursor.getString(28);
                String pseudo = cursor.getString(29);

                // float patientCRP = Float.parseFloat(cursor.getString(24));

                User patient = new User(patientID, patientName, patientFirstName,
                        patientBirth, patientmail, patientadress, patientPhone,
                        patientGender, patientHeight, patientWeight, patientIMC,
                        patientHB, patientVGM, patientTCMH, patientIDR_CV, patientHypo,
                        patientRet_He, patientPlatelet, patientFerritin,
                        patientTransferrin, patientSerum_iron, Serum_iron_unit, patientCST,
                        patientFibrinogen, patientCRP, patientOthers, secured, pseudo);

                patients.add(patient);
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return patients;
    }

    //Get all the tag
    public List<Tag> getTag(){
        List<Tag> tags = new ArrayList<>();
        try{
            //get all row from Tag table
            String query = "SELECT * FROM " + TABLE_TAG;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+dbName, null
                    , SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

/*            while (cursor.moveToNext()) {
                int tagID = Integer.parseInt(cursor.getString(0));
                String value = cursor.getString(1);
                int typeID = Integer.parseInt(cursor.getString(2));
                int parentID = Integer.parseInt(cursor.getString(3));*/

            if(cursor.moveToFirst()){
                do{
                    int tagID = Integer.parseInt(cursor.getString(0));
                    String value = cursor.getString(1);
                    String typeID = cursor.getString(2);
                    String parentID = cursor.getString(3);

                    Tag tag = new Tag(tagID, value, typeID, parentID);
                    tags.add(tag);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return tags;
    }


    public List<Type> getType(){
        List<Type> types = new ArrayList<>();
        try{
            //get all row from Image table
            String query = "SELECT * FROM " + TABLE_TYPE;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+dbName, null
                    , SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int idType = Integer.parseInt(cursor.getString(0));
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                String secured = cursor.getString(3);

                Type type = new Type(idType, name, description, secured);
                types.add(type);
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }
        return types;
    }




    //Adding a new patient
    public long addPatient(User patient){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", patient.getName());
        values.put("first_name", patient.getFirstName());
        values.put("date_of_birth", patient.getDateBirth());
        values.put("mail",patient.getMail());
        values.put("address",patient.getAddress());
        values.put("sexe",patient.getSexe());
        values.put("height",patient.getHeight());
        values.put("weight",patient.getWeight());
        values.put("IMC",patient.getImc());
        values.put("HB",patient.getHb());
        values.put("VGM",patient.getVgm());
        values.put("TCMH",patient.gettcmh());
        values.put("IDR_CV",patient.getIdr_cv());
        values.put("Hypo",patient.getHypo());
        values.put("Ret_He",patient.getRet_he());
        values.put("Platelet",patient.getPlatelet());
        values.put("Ferritin",patient.getFerritin());
        values.put("Transferrin",patient.getTransferrin());
        values.put("Serum_iron",patient.getSerum_iron());
        values.put("CST",patient.getCst());
        values.put("Fibrinogen",patient.getFibrinogen());
        values.put("CRP", patient.getCrp());
        values.put("others", patient.getOther());
        values.put("phone", patient.getPhone());
        values.put("Serum_iron_unit", patient.getSerum_iron_unit());
        values.put("secured", patient.getSecured());
        values.put("pseudo", patient.getPseudo());

        //insert row
        // long is the return type of the method: insert(String table,String nullColumnHack,ContentValues values)) == it returns the last ID/row inserted
        long lastID = db.insert(TABLE_USER,"nullColumnHack",values);
        db.close();
        return lastID;
    }

    //update patient
    public void updatePatient(User patient, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", patient.getName());
        values.put("first_name", patient.getFirstName());
        values.put("date_of_birth", patient.getDateBirth());
        values.put("mail",patient.getMail());
        values.put("address",patient.getAddress());
        values.put("sexe",patient.getSexe());
        values.put("height",patient.getHeight());
        values.put("weight",patient.getWeight());
        values.put("IMC",patient.getImc());
        values.put("HB",patient.getHb());
        values.put("VGM",patient.getVgm());
        values.put("TCMH",patient.gettcmh());
        values.put("IDR_CV",patient.getIdr_cv());
        values.put("Hypo",patient.getHypo());
        values.put("Ret_He",patient.getRet_he());
        values.put("Platelet",patient.getPlatelet());
        values.put("Ferritin",patient.getFerritin());
        values.put("Transferrin",patient.getTransferrin());
        values.put("Serum_iron",patient.getSerum_iron());
        values.put("CST",patient.getCst());
        values.put("Fibrinogen",patient.getFibrinogen());
        values.put("CRP", patient.getCrp());
        values.put("others", patient.getOther());
        values.put("phone", patient.getPhone());
        values.put("Serum_iron_unit", patient.getSerum_iron_unit());
        values.put("secured", patient.getSecured());
        values.put("pseudo", patient.getPseudo());

        String where = "IDuser=?";
        //String[] whereArgs = new String[] {String.valueOf(patient.getUserID())};
        String[] whereArgs = new String[] {String.valueOf(id)};

        db.update(TABLE_USER,values,where,whereArgs);
        db.close();
    }

    //delete row from table user
    public void deletePatient(int id){

        String query = "SELECT * FROM " + TABLE_USER + " where user.IDuser=" + id;
        Cursor cursor = db.rawQuery(query, null);

        SQLiteDatabase db = this.getWritableDatabase();

        String where = "IDuser=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.delete(TABLE_USER, where, whereArgs);
        db.close();
    }

    //Adding a new tag
    public long addTag(Tag tag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put("IDtag", tag.getIdTag());
        values.put("value", tag.getValue());
        values.put("IDtype", tag.getIdType());
        values.put("idtagparent", tag.getIdParent());

        //insert row
        db.insert(TABLE_TAG, "nullColumnHack", values);
        db.close();
        return 0;
    }



    public void checkTag(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM tag", null);
        if(cursor != null){
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0){
                Log.i("SQLiteDBHelper", "zero count means empty table");
            }else{
                Log.i("SQLiteDBHelper", "DB is not empty");
            }

        }
        db.close();
    }

}
