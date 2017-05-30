package com.example.isit_mp3c.projet.exportdb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class FileProvider extends ContentProvider {

    private static final String CLASS_NAME = "FileProvider";

    //Authority : symbolic name for the provider class
    public static final String AUTHORITY = "com.example.isit_mp3c.project.exportdb.provider";

    //UriMatcher : used to match against incoming requests
    private UriMatcher uriMatcher;

    private static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>();
    private String fileName = "blueSIDE.csv";

    public FileProvider() {
    }

    static{
        MIME_TYPES.put(".csv", "application/csv");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        //throw new UnsupportedOperationException("Not yet implemented");

        String path = uri.toString();

        for(String extension : MIME_TYPES.keySet()){
            if(path.endsWith(extension)){
                return(MIME_TYPES.get(extension));
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
    /*    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add a URI to the matcher which will match against the form
        // 'content://com.isit.testapplication.exportdb.fileprovider*//*'
        // and return 1 in the case that the incoming Uri matches this pattern
        uriMatcher.addURI(AUTHORITY, "*", 1);
        return false;*/

        /*File file = new File(getContext().getFilesDir(), fileName);
        if(!file.exists()){
            AssetManager assetManager = getContext().getResources().getAssets();
            try{
                copy(assetManager.open(fileName), file);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("File Provider", "Exception copying from assets", e);
                return false;
            }
        }*/
        return true;
    }

    private void copy(InputStream inputStream, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

/*    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        String LOG_TAG = CLASS_NAME + " - openFile";

        Log.v(LOG_TAG, "Called with uri: '" + uri + "'." + uri.getLastPathSegment());

        //check incoming Uri against the mathcer
        switch (uriMatcher.match(uri)){
            //if it return 1 : it matchs the Uri defined in onCreate
            case 1:
                // The desired file name is specified by the last segment of the
                // path
                // E.g.
                // 'content://com.example.isit.testapplication.exportdb.provider/Test.txt'
                // Take this and build the path to the file in the cache
                String fileLocation = getContext().getCacheDir() + File.separator
                        + uri.getLastPathSegment();

                // Create & return a ParcelFileDescriptor pointing to the file
                // Note: I don't care what mode they ask for - they're only getting
                // read only
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(fileLocation),
                        ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;

            default:
                Log.v(LOG_TAG,"Unsupported uri : '" + uri + "'.");
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }*/

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        File file = new File(getContext().getFilesDir(), uri.getPath());
        if(file.exists()){
            return(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
        }
        throw new FileNotFoundException(uri.getPath());
    }
}
