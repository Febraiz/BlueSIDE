package com.example.isit_mp3c.projet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.isit_mp3c.projet.R.drawable.db2;

/**
 * Created by ISIT on 21/07/2016.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper implements DatabaseConstants {

    private static String dbPath = "";

    private SQLiteDatabase db;
    public static Context mycontext;
    private static int DB_VERSION = 1;



    //To avoid getting error : to verify
    private static SQLiteDBHelper singleton;

    public static SQLiteDBHelper getInstance(final Context context) {
        if(singleton == null){
            singleton = new SQLiteDBHelper(context);
        }
        return singleton;
    }


    //Takes and keeps a reference of the passed context in order to access to the application assets and resources.
    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);


        dbPath = "/data/data/" + context.getPackageName() + "/databases/";

        this.mycontext = context;

        this.db = this.getWritableDatabase();
        createDB(db);
    }

    public boolean openDatabase() {
        try {
            String path = dbPath + DB_NAME;

            db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);

        } catch (SQLiteException sqle){
            db = null;
        }
        return db != null ? true : false;
    }


    public boolean openDatabaseReading() {
        try {
            String path = dbPath + DB_NAME;

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

    public void createDB(SQLiteDatabase db) {
        String create_table_user =
                "create table " + TABLE_USER + " ("
                        + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + USER_NAME + " TEXT,"
                        + USER_PASSWORD + " TEXT,"
                        + USER_FIRST_NAME + " TEXT,"
                        + USER_DATE_OF_BIRTH + " NUMERIC,"
                        + USER_AGE + " NUMERIC,"
                        + USER_MAIL + " TEXT,"
                        + USER_ADDRESS + " TEXT,"
                        + USER_USERNAME + " TEXT,"
                        + USER_SEXE + " TEXT,"
                        + USER_HEIGHT + " REAL,"
                        + USER_WEIGHT + " REAL,"
                        + USER_IMC + " REAL,"
                        + USER_HB + " REAL,"
                        + USER_VGM + " REAL,"
                        + USER_TCMH + " REAL,"
                        + USER_IDR_CV + " REAL,"
                        + USER_HYPO + " REAL,"
                        + USER_RET_HE + " REAL,"
                        + USER_PLATELET + " REAL,"
                        + USER_FERRITIN + " REAL,"
                        + USER_TRANSFERRIN + " REAL,"
                        + USER_SERUM_IRON + " REAL,"
                        + USER_CST + " REAL,"
                        + USER_FIBRINOGEN + " REAL,"
                        + USER_CRP + " REAL,"
                        + USER_OTHERS + " TEXT,"
                        + USER_PHONE + " TEXT,"
                        + USER_SERUM_IRON_UNIT + " TEXT,"
                        + USER_SECURED + " TEXT,"
                        + USER_PSEUDO + " TEXT,"
                        + USER_CARENCE + " TEXT" + ");";

        String create_table_acquisition =
                "create table " + TABLE_ACQUISITION + " ("
                        + ACQUISITION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + ACQUISITION_ID_PATIENT + " INTEGER NOT NULL,"
                        + ACQUISITION_NUMBER + " INTEGER NOT NULL,"
                        + ACQUISITION_DATE + " DATE NOT NULL,"
                        + "FOREIGN KEY(" + ACQUISITION_ID_PATIENT + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "));";

        String create_table_image =
                "create table " + TABLE_IMAGE + " ("
                        + IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + IMAGE_REF_IMAGE + " TEXT NOT NULL,"
                        + IMAGE_TITLE + " TEXT NOT NULL,"
                        + IMAGE_DATE + " NUMERIC,"
                        + IMAGE_FLASH + " NUMERIC,"
                        + IMAGE_EXPOSURE_TIME + " INTEGER,"
                        + IMAGE_RESULT + " TEXT" + ");";

        String create_table_type =
                "create table " + TABLE_TYPE + " ("
                        + TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + TYPE_NAME + " TEXT NOT NULL,"
                        + TYPE_DESC + " TEXT NOT NULL,"
                        + TYPE_SECURED + " BOOLEAN NOT NULL DEFAULT TRUE" + ");";

        String create_table_tag =
                "create table " + TABLE_TAG + " ("
                        + TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                        + TAG_VALUE + " TEXT NOT NULL,"
                        + TAG_ID_TYPE + " INTEGER NOT NULL,"
                        + TAG_IDTAGPARENT + " INTEGER NOT NULL,"
                        + "FOREIGN KEY(" + TAG_ID_TYPE + ") REFERENCES " + TABLE_TYPE + "(" + TYPE_ID + "),"
                        + "FOREIGN KEY(" + TAG_IDTAGPARENT + ") REFERENCES " + TABLE_TAG + "(" + TAG_ID + "));";

        String create_table_taguser =
                "create table " + TABLE_TAGUSER + " ("
                        + TAGUSER_ID_TAG + " INTEGER NOT NULL,"
                        + TAGUSER_ID_USER + " INTEGER NOT NULL,"
                        + "PRIMARY KEY(" + TAGUSER_ID_TAG + "," + TAGUSER_ID_USER + "),"
                        + "FOREIGN KEY(" + TAGUSER_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + TAG_ID + "),"
                        + "FOREIGN KEY(" + TAGUSER_ID_USER + ") REFERENCES " + TABLE_USER + "(" + USER_ID + "));";

        String create_table_datatag =
                "create table " + TABLE_DATATAG + " ("
                        + DATATAG_ID_TAG + " INTEGER NOT NULL,"
                        + DATATAG_ID_DATA + " INTEGER NOT NULL,"
                        + "PRIMARY KEY(" + DATATAG_ID_TAG + "," + DATATAG_ID_DATA + "),"
                        + "FOREIGN KEY(" + DATATAG_ID_TAG + ") REFERENCES " + TABLE_TAG + "(" + TAG_ID + "),"
                        + "FOREIGN KEY(" + DATATAG_ID_DATA + ") REFERENCES " + TABLE_IMAGE + "(" + IMAGE_ID + "));";


        try {
            db.execSQL(create_table_user);
            db.execSQL(create_table_image);
            db.execSQL(create_table_type);
            db.execSQL(create_table_tag);
            db.execSQL(create_table_taguser);
            db.execSQL(create_table_datatag);
            db.execSQL(create_table_acquisition);
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACQUISITION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATATAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGUSER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        onCreate(db);
    }


    //Get all the images
    public List<Photo> getImages(){
        List<Photo> photos = new ArrayList<>();
        try{
            //get all row from Image table
            String query = "SELECT * FROM " + TABLE_IMAGE;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null
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
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int patientID = Integer.parseInt(cursor.getString(0));
                String patientName = cursor.getString(1);
                String patientFirstName = cursor.getString(3);
                String patientBirth = cursor.getString(4);
                String patientAge = cursor.getString(5);
                String patientmail = cursor.getString(6);
                String patientadress = cursor.getString(7);
                String patientGender = cursor.getString(9);
                String patientHeight =cursor.getString(10);
                String patientWeight = cursor.getString(11);
                String patientIMC = cursor.getString(12);
                String patientHB = cursor.getString(13);
                String patientVGM = cursor.getString(14);
                String patientTCMH = cursor.getString(15);
                String patientIDR_CV = cursor.getString(16);
                String patientHypo = cursor.getString(17);
                String patientRet_He =cursor.getString(18);
                String patientPlatelet = cursor.getString(19);
                String patientFerritin = cursor.getString(20);
                String patientTransferrin = cursor.getString(21);
                String patientSerum_iron = cursor.getString(22);
                String patientCST = cursor.getString(23);
                String patientFibrinogen = cursor.getString(24);
                String patientCRP = cursor.getString(25);
                String patientOthers = cursor.getString(26);
                String patientPhone = cursor.getString(27);
                String Serum_iron_unit = cursor.getString(28);
                String secured = cursor.getString(29);
                String pseudo = cursor.getString(30);
                String carence = cursor.getString(31);

                // float patientCRP = Float.parseFloat(cursor.getString(24));

                User patient = new User(patientID, patientName, patientFirstName,
                        patientBirth, patientAge, patientmail, patientadress, patientPhone,
                        patientGender, patientHeight, patientWeight, patientIMC,
                        patientHB, patientVGM, patientTCMH, patientIDR_CV, patientHypo,
                        patientRet_He, patientPlatelet, patientFerritin,
                        patientTransferrin, patientSerum_iron, Serum_iron_unit, patientCST,
                        patientFibrinogen, patientCRP, patientOthers, secured, pseudo, carence);

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
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null
                    , SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

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
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null
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
        values.put(USER_NAME, patient.getName());
        values.put(USER_FIRST_NAME, patient.getFirstName());
        values.put(USER_DATE_OF_BIRTH, patient.getDateBirth());
        values.put(USER_AGE, patient.getAge());
        values.put(USER_MAIL,patient.getMail());
        values.put(USER_ADDRESS,patient.getAddress());
        values.put(USER_SEXE,patient.getSexe());
        values.put(USER_HEIGHT,patient.getHeight());
        values.put(USER_WEIGHT,patient.getWeight());
        values.put(USER_IMC,patient.getImc());
        values.put(USER_HB,patient.getHb());
        values.put(USER_VGM,patient.getVgm());
        values.put(USER_TCMH,patient.gettcmh());
        values.put(USER_IDR_CV,patient.getIdr_cv());
        values.put(USER_HYPO,patient.getHypo());
        values.put(USER_RET_HE,patient.getRet_he());
        values.put(USER_PLATELET,patient.getPlatelet());
        values.put(USER_PLATELET,patient.getFerritin());
        values.put(USER_TRANSFERRIN,patient.getTransferrin());
        values.put(USER_SERUM_IRON,patient.getSerum_iron());
        values.put(USER_CST,patient.getCst());
        values.put(USER_FIBRINOGEN,patient.getFibrinogen());
        values.put(USER_CRP, patient.getCrp());
        values.put(USER_OTHERS, patient.getOther());
        values.put(USER_PHONE, patient.getPhone());
        values.put(USER_SERUM_IRON_UNIT, patient.getSerum_iron_unit());
        values.put(USER_SECURED, patient.getSecured());
        values.put(USER_PSEUDO, patient.getPseudo());
        values.put(USER_CARENCE, patient.getDeficiency());

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

        values.put(USER_NAME, patient.getName());
        values.put(USER_FIRST_NAME, patient.getFirstName());
        values.put(USER_DATE_OF_BIRTH, patient.getDateBirth());
        values.put(USER_AGE, patient.getAge());
        values.put(USER_MAIL,patient.getMail());
        values.put(USER_ADDRESS,patient.getAddress());
        values.put(USER_SEXE,patient.getSexe());
        values.put(USER_HEIGHT,patient.getHeight());
        values.put(USER_WEIGHT,patient.getWeight());
        values.put(USER_IMC,patient.getImc());
        values.put(USER_HB,patient.getHb());
        values.put(USER_VGM,patient.getVgm());
        values.put(USER_TCMH,patient.gettcmh());
        values.put(USER_IDR_CV,patient.getIdr_cv());
        values.put(USER_HYPO,patient.getHypo());
        values.put(USER_RET_HE,patient.getRet_he());
        values.put(USER_PLATELET,patient.getPlatelet());
        values.put(USER_PLATELET,patient.getFerritin());
        values.put(USER_TRANSFERRIN,patient.getTransferrin());
        values.put(USER_SERUM_IRON,patient.getSerum_iron());
        values.put(USER_CST,patient.getCst());
        values.put(USER_FIBRINOGEN,patient.getFibrinogen());
        values.put(USER_CRP, patient.getCrp());
        values.put(USER_OTHERS, patient.getOther());
        values.put(USER_PHONE, patient.getPhone());
        values.put(USER_SERUM_IRON_UNIT, patient.getSerum_iron_unit());
        values.put(USER_SECURED, patient.getSecured());
        values.put(USER_PSEUDO, patient.getPseudo());
        Log.i("AVANT MISE A JOUR", patient.getDeficiency());
        values.put(USER_CARENCE, patient.getDeficiency());

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
        values.put(TAG_VALUE, tag.getValue());
        values.put(TAG_ID_TYPE, tag.getIdType());
        values.put(TAG_IDTAGPARENT, tag.getIdParent());

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

    //Adding a new acquisition
    public long addAcquisition(Acquisition acquisition){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACQUISITION_ID_PATIENT, acquisition.getPatientID());
        values.put(ACQUISITION_NUMBER, acquisition.getAcquisition_number());
        values.put(ACQUISITION_DATE,acquisition.getDate_acquisition());

        //insert row
        // long is the return type of the method: insert(String table,String nullColumnHack,ContentValues values)) == it returns the last ID/row inserted
        long lastID = db.insert(TABLE_ACQUISITION,"nullColumnHack",values);
        db.close();
        return lastID;
    }

    public int getNextAcquisitionNumber(int patientID){
        int  number = 0;
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath + DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ACQUISITION + " WHERE IDPatient = " + patientID, null);
            while (cursor.moveToNext()) {
                number = Integer.parseInt(cursor.getString(0)) + 1;
            }
            cursor.close();
        } catch (Exception e) {
            Log.d("database ", e.getMessage());
        }
        db.close();
        Log.i("acq","Numéro acquisition :" + number);
        return number;
    }

    public User getPatientWithId(int id){
        User patient = new User();
        try{
            String query = "SELECT * FROM " + TABLE_USER + " WHERE " + USER_ID + "=" + id;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int patientID = Integer.parseInt(cursor.getString(0));
                String patientName = cursor.getString(1);
                String patientFirstName = cursor.getString(3);
                String patientBirth = cursor.getString(4);
                String patientAge = cursor.getString(5);
                String patientmail = cursor.getString(6);
                String patientadress = cursor.getString(7);
                String patientGender = cursor.getString(9);
                String patientHeight =cursor.getString(10);
                String patientWeight = cursor.getString(11);
                String patientIMC = cursor.getString(12);
                String patientHB = cursor.getString(13);
                String patientVGM = cursor.getString(14);
                String patientTCMH = cursor.getString(15);
                String patientIDR_CV = cursor.getString(16);
                String patientHypo = cursor.getString(17);
                String patientRet_He =cursor.getString(18);
                String patientPlatelet = cursor.getString(19);
                String patientFerritin = cursor.getString(20);
                String patientTransferrin = cursor.getString(21);
                String patientSerum_iron = cursor.getString(22);
                String patientCST = cursor.getString(23);
                String patientFibrinogen = cursor.getString(24);
                String patientCRP = cursor.getString(25);
                String patientOthers = cursor.getString(26);
                String patientPhone = cursor.getString(27);
                String Serum_iron_unit = cursor.getString(28);
                String secured = cursor.getString(29);
                String pseudo = cursor.getString(30);
                String carence = cursor.getString(31);

                // float patientCRP = Float.parseFloat(cursor.getString(24));

                patient = new User(patientID, patientName, patientFirstName,
                        patientBirth, patientAge, patientmail, patientadress, patientPhone,
                        patientGender, patientHeight, patientWeight, patientIMC,
                        patientHB, patientVGM, patientTCMH, patientIDR_CV, patientHypo,
                        patientRet_He, patientPlatelet, patientFerritin,
                        patientTransferrin, patientSerum_iron, Serum_iron_unit, patientCST,
                        patientFibrinogen, patientCRP, patientOthers, secured, pseudo, carence);
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return patient;
    }

    public Acquisition getAcquisition(int UserId,int numAcquisition){
        Acquisition acquisition = new Acquisition();
        try{
            String query = "SELECT * FROM " + TABLE_ACQUISITION + " WHERE " + ACQUISITION_ID_PATIENT + "=" + UserId + " AND " + ACQUISITION_NUMBER + "=" + numAcquisition;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                int acquisitionID = Integer.parseInt(cursor.getString(0));
                int patientID = Integer.parseInt(cursor.getString(1));
                int acquisition_number = Integer.parseInt(cursor.getString(2));
                String date_acquisition = cursor.getString(3);

                acquisition = new Acquisition(acquisitionID,patientID,acquisition_number,date_acquisition);
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return acquisition;
    }

    public int getCountAcquisition(){

        int cmpt = 0;

        try{
            String query = "SELECT COUNT(*) FROM " + TABLE_ACQUISITION;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                cmpt = Integer.parseInt(cursor.getString(0));
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return cmpt;

    }

    public void deleteUserAcquisition(int id){
        // Table acquisition avant suppression

        /*try{
            String query = "SELECT * FROM " + TABLE_ACQUISITION;
            SQLiteDatabase db3 = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db3.rawQuery(query, null);

            while (cursor.moveToNext()) {
                String s = cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3);
                Log.i("sql",s);
            }
            cursor.close();

            db3.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }*/

        SQLiteDatabase db = this.getWritableDatabase();

        String where = ACQUISITION_ID_PATIENT + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        db.delete(TABLE_ACQUISITION, where, whereArgs);
        db.close();


        // Table acquisition après suppression

        /*try{
            String query = "SELECT * FROM " + TABLE_ACQUISITION;
            SQLiteDatabase db2 = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db2.rawQuery(query, null);

            while (cursor.moveToNext()) {
                String s = cursor.getString(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3);
                Log.i("sql",s);
            }
            cursor.close();

            db2.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }*/
    }

    public int getCountPatient()
    {

        int cmpt = 0;

        try{
            String query = "SELECT COUNT(*) FROM " + TABLE_USER;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                cmpt = Integer.parseInt(cursor.getString(0));
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return cmpt;

    }

    public boolean userExist(int id){
        boolean exist = false;
        try{
            String query = "SELECT COUNT(*) FROM " + TABLE_USER + " WHERE " + USER_ID + "=" + id;
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath+DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.rawQuery(query, null);

            while (cursor.moveToNext()) {
                if(Integer.parseInt(cursor.getString(0)) == 1){
                    exist = true;
                }
            }
            cursor.close();
        }catch (Exception e){
            Log.d("database ", e.getMessage());
        }

        db.close();
        return exist;
    }


}
