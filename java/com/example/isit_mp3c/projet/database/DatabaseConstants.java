package com.example.isit_mp3c.projet.database;

/**
 * Created by techmed on 29/05/2017.
 */

public interface DatabaseConstants {
    String DB_NAME = "bluesidedb";
    int DB_VERSION = 1;


    //Table Image
    String TABLE_IMAGE = "image";
    String IMAGE_ID = "IDdata";
    String IMAGE_REF_IMAGE = "ref_image";
    String IMAGE_TITLE = "title";
    String IMAGE_DATE = "Date";
    String IMAGE_FLASH = "Flasg";
    String IMAGE_EXPOSURE_TIME = "exposure_time";
    String IMAGE_RESULT = "Result";

    //Table User / Patient
    String TABLE_USER = "user";
    String USER_ID = "IDuser";
    String USER_NAME = "name";
    String USER_PASSWORD = "password";
    String USER_FIRST_NAME = "first_name";
    String USER_DATE_OF_BIRTH = "date_of_birth";
    String USER_MAIL = "mail";
    String USER_ADDRESS = "address";
    String USER_USERNAME = "username";
    String USER_SEXE = "sexe";
    String USER_HEIGHT = "height";
    String USER_WEIGHT = "weight";
    String USER_IMC = "IMC";
    String USER_HB = "HB";
    String USER_VGM = "VGM";
    String USER_TCMH = "TCMH";
    String USER_IDR_CV = "IDR_CV";
    String USER_HYPO = "Hypo";
    String USER_RET_HE = "Ret_He";
    String USER_PLATELET = "Platelet";
    String USER_FERRITIN = "Ferritin";
    String USER_TRANSFERRIN = "Transferrin";
    String USER_SERUM_IRON = "Serum_iron";
    String USER_CST = "CST";
    String USER_FIBRINOGEN = "Fibrinogen";
    String USER_CRP = "CRP";
    String USER_OTHERS = "others";
    String USER_PHONE = "phone";
    String USER_SERUM_IRON_UNIT = "Serum_iron_unit";
    String USER_SECURED = "secured";
    String USER_PSEUDO = "pseudo";
    String USER_CARENCE = "carence";

    //Table Tag
    String TABLE_TAG = "tag";
    String TAG_ID = "IDtag";
    String TAG_VALUE = "value";
    String TAG_ID_TYPE = "IDtype";
    String TAG_IDTAGPARENT = "idtagparent";

    //Table Type
    String TABLE_TYPE = "type";
    String TYPE_ID = "IDtype";
    String TYPE_NAME = "name";
    String TYPE_DESC = "desc";
    String TYPE_SECURED = "secured";

    //Table Acquisition
    String TABLE_ACQUISITION = "acquisition";
    String ACQUISITION_ID = "IDacquisiton";
    String ACQUISITION_ID_PATIENT = "IDpatient";
    String ACQUISITION_NUMBER = "acquisition_number";
    String ACQUISITION_DATE = "data_acquisition";

    //Table Datatag
    String TABLE_DATATAG = "datatag";
    String DATATAG_ID_TAG = "IDtag";
    String DATATAG_ID_DATA = "IDdata";

    //Table Taguser
    String TABLE_TAGUSER = "taguser";
    String TAGUSER_ID_TAG = "IDtag";
    String TAGUSER_ID_USER = "IDuser";
}
