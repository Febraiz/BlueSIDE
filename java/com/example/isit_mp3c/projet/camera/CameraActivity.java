package com.example.isit_mp3c.projet.camera;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.RggbChannelVector;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.isit_mp3c.projet.OrientationManager;
import com.example.isit_mp3c.projet.R;
import com.example.isit_mp3c.projet.database.Acquisition;
import com.example.isit_mp3c.projet.database.SQLiteDBHelper;
import com.example.isit_mp3c.projet.database.User;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.TextureView.*;

public class CameraActivity extends AppCompatActivity
        implements SurfaceTextureListener, AdapterView.OnItemSelectedListener, OrientationManager.OrientationListener {

    //minSDKversion = 21 to use android.hardware.camera2

    private static final String TAG = "CameraActivity";
    private static final int REQUEST_CAMERA_RESULT = 1;

    private CameraManager camManager;
    private CameraDevice camDevice;
    private String camId;
    private CameraCharacteristics camCharacteristics;
    private CaptureRequest.Builder captureRequestBuilder;
    private CaptureRequest.Builder viewRequestBuilder;
    private CameraCaptureSession captureSession;
    private boolean enableMenu = false;
    private boolean flashOn = false;
    private TotalCaptureResult imgResult;
    private TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private Surface surfaceView;
    private SurfaceTexture surfaceCapture;
    private Surface imageCapture;
    private TextureView textureCapture;
    private long exposureTimeAuto;
    private SeekBar exposureBar;
    private long expTimeMax;
    private long expTimeMin;
    private int isoMin;
    private int isoMax;
    private int progressExpTime;
    private List<Bitmap> bmplist;
    private List<CaptureRequest> list;
    private ProgressDialog progressDialog;
    private Image imgRaw;
    private ImageReader rawImageReader;
    private int cap = 0;
    private List<String> seqImages;
    private List<Float> expTimes;
    private List<Mat> seqMats;
    private int orientation;
    private long[] tabExp;
    private int[] tabTemp;
    private int x = -1;
    private int y = -1;
    private Matrix matrix;
    private ImageView imageView;
    private String directoryPatient;
    private String directoryFiles = "Images";
    private ImageView imageDisplay;
    private int idPatient;
    private ArrayList<String> nomsImages;

    private List<User> users;

    private SQLiteDBHelper dbHelper = SQLiteDBHelper.getInstance(this);

    //Résolution de la photo
    private int width = 960;
    private int height = 1280;

    static int heightEcran;
    static int widthEcran;

    private int widthCadre;
    private int heightCadre;

    private int position = 1;

    private static ImageView cadre_portrait, cadre_landscape, cadre_reverse_landscape;

    private BaseLoaderCallback openCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "LOADING SUCCESS");

                    System.loadLibrary("app-jni");
                }
                break;

                default: {
                    super.onManagerConnected(status);
                    Log.i(TAG, "ERROR LOADING");
                }
                break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, openCVLoaderCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        OrientationManager orientationManager = new OrientationManager(CameraActivity.this, SensorManager.SENSOR_DELAY_NORMAL, this);
        orientationManager.enable();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cadre_portrait = (ImageView) findViewById(R.id.cadre_display_portrait);
        cadre_landscape = (ImageView) findViewById(R.id.cadre_display_landscape);
        cadre_reverse_landscape = (ImageView) findViewById(R.id.cadre_display_reverse_landscape);

        heightEcran = getResources().getDisplayMetrics().heightPixels;
        widthEcran = getResources().getDisplayMetrics().widthPixels;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean gridIsDisplay = sharedPref.getBoolean(getString(R.string.gridDisplay), true);

        Log.i("grid is display", String.valueOf(gridIsDisplay));

        if(!gridIsDisplay) {
            cadre_portrait.setVisibility(View.INVISIBLE);
            cadre_reverse_landscape.setVisibility(View.INVISIBLE);
            cadre_landscape.setVisibility(View.INVISIBLE);
        }


        Bundle b = getIntent().getExtras();
        String name = ""; // or other values
        if(b != null)
            name = b.getString("pseudo");

        textureView = (TextureView) findViewById(R.id.textView);

        textureCapture = (TextureView) findViewById(R.id.textCapture);

        imageDisplay = (ImageView) findViewById(R.id.image_display);
        imageDisplay.setVisibility(View.INVISIBLE);

        textureView.setSurfaceTextureListener(this);
        textureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(TAG, "onTouch");
                    captureImageBurst();
                    return true;
                }

                return false;
            }
        });

        bmplist = new ArrayList<>();
        nomsImages = new ArrayList<>();

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //get patient ID for the directoryFiles
        if(name.equalsIgnoreCase(""))
            getNameFileDialog();
        else {
            directoryPatient = name;
            setDirectoryName();
        }

        Log.i("directory", name);

        Log.i(TAG, "onCreate");

    }

    private void setDirectoryName(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        idPatient = Integer.parseInt(directoryPatient.split("-")[0]);
        int nb_acq = dbHelper.getNextAcquisitionNumber(idPatient);
        String nameDirectory = "patient_" + idPatient + "_" + sdf.format(date) + "_acq" + nb_acq;
        if (directoryPatient != getResources().getString(R.string.none)) {
            directoryFiles = directoryPatient + "/";
            directoryFiles += nameDirectory;
        }
    }

    private void getNameFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);

        builder.setCancelable(false);

        users = getPatient();
        String[] listPatient = new String[users.size()];
        try {
            for (int i = 0; i < users.size(); i++) {
                listPatient[i] = users.get(i).getUserID() + "-" + users.get(i).getPseudo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CameraActivity.this,
                R.layout.custom_spinner, listPatient);
        final Spinner spinner = new Spinner(CameraActivity.this);


        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(CameraActivity.this);

        builder.setTitle(R.string.choose_patient)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        onBackPressed();
                    }
                })
                .setView(spinner)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        directoryPatient = String.valueOf(spinner.getSelectedItem());
                        dialog.dismiss();
                        setDirectoryName();
                    }
                });
        builder.create().show();
    }

    //get all patients
    private List<User> getPatient() {
        List<User> users = new ArrayList<>();

        if(dbHelper.openDatabase()){
            users = dbHelper.getPatient();
        }
        dbHelper.close();
        return users;
    }

    @Override
    protected void onPause() {

        Log.i(TAG, "onPause");
        try {
            closeCam();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        menu.getItem(0).setEnabled(false);
        menu.getItem(1).setEnabled(false);
        menu.getItem(3).setEnabled(false);
        menu.getItem(4).setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (enableMenu) {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(true);
            menu.getItem(3).setEnabled(true);
            menu.getItem(4).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.hdr) {
            progressDialog = ProgressDialog.show(CameraActivity.this, getResources().getString(R.string.process_hdr), "", true);
            nativeHDR();
            return true;
        }

        if (id == R.id.detect) {
            selectPoint();
            return true;
        }

        if (id == R.id.confirm) {
            showImage();
            return true;
        }

        if (id == R.id.analysisLDR) {
            //nativeAnalysisLDR();
            return true;
        }
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        if(id == R.id.grid){
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if(cadre_portrait.getVisibility() == View.VISIBLE || cadre_landscape.getVisibility() == View.VISIBLE || cadre_reverse_landscape.getVisibility() == View.VISIBLE) {

                cadre_portrait.setVisibility(View.INVISIBLE);
                cadre_landscape.setVisibility(View.INVISIBLE);
                cadre_reverse_landscape.setVisibility(View.INVISIBLE);
                editor.putBoolean(getString(R.string.gridDisplay), false);
            }
            else {
                switch (position) {
                    case 1 :
                        cadre_portrait.setVisibility(View.VISIBLE);
                        break;
                    case 2 :
                        cadre_landscape.setVisibility(View.VISIBLE);
                        break;
                    case 3 :
                        cadre_reverse_landscape.setVisibility(View.VISIBLE);
                        break;

                }
                editor.putBoolean(getString(R.string.gridDisplay), true);
            }

            editor.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture texture, int w, int h) {

        try {
            Log.i(TAG, "onSurfaceTextureAvailable");
            openCam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int w, int h) {
        Log.i(TAG, "onSurfaceTextureSizeChanged");
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
        Log.i(TAG, "onSurfaceTextureDestroyed");
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        //Log.i(TAG, "onSurfaceTextureUpdated");
    }

    public void onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow");
    }

    private final CameraDevice.StateCallback camDeviceCallback = new CameraDevice.StateCallback() {

        @Override
        public void onClosed(CameraDevice device) {

            if (camDevice != null) {
                Log.i(TAG, "camDeviceCallback onClosed");
                camDevice.close();
                camDevice = null;
            }
        }

        @Override
        public void onDisconnected(CameraDevice device) {

            if (camDevice != null) {
                Log.i(TAG, "camDeviceCallback onDisconnected");
                camDevice.close();
                camDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice device, int error) {

            if (camDevice != null) {
                Log.i(TAG, "camDeviceCallback onError");
                camDevice.close();
                camDevice = null;
            }
        }

        @Override
        public void onOpened(CameraDevice device) {

            Log.i(TAG, "camDeviceCallback onOpened");
            camDevice = device;

            initCameraView();
        }

    };

    private final CameraCaptureSession.StateCallback camSessionCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onActive(CameraCaptureSession session) {
            Log.i(TAG, "camSessionCallback onActive");
        }

        @Override
        public void onClosed(CameraCaptureSession session) {
            Log.i(TAG, "camSessionCallback onClosed");

        }

        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.i(TAG, "camSessionCallback onConfigured");
            captureSession = session;
            requestCameraView();

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.i(TAG, "camSessionCallback onConfiguredFailed");
        }

    };

    private final CameraCaptureSession.CaptureCallback camViewCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            //Log.i(TAG, "camViewCallback onCaptureCompleted");

            if (result.get(CaptureResult.SENSOR_EXPOSURE_TIME) != null) {
                exposureTimeAuto = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
                //Log.i(TAG, "EXPOSURE TIME AUTO: " + String.valueOf(exposureTimeAuto));
            }

        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult result) {
            //Log.i(TAG, "camViewCallback onCaptureProgressed");


        }


    };

    private final CameraCaptureSession.CaptureCallback camCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            //Log.i(TAG, "camCaptureCallback onCaptureCompleted");

            imgResult = result;

            //set visible during capture to save the image
            textureCapture.setVisibility(View.VISIBLE);

            if (cap != 1)
                expTimes.add((float) result.get(CaptureResult.SENSOR_EXPOSURE_TIME) / 1000000000);
            //Log.i(TAG, "exposure time captured: " + String.valueOf(result.get(CaptureResult.SENSOR_EXPOSURE_TIME)));
            Log.i(TAG, "exposure time captured (seconds): " + String.valueOf((float) result.get(CaptureResult.SENSOR_EXPOSURE_TIME) / 1000000000));

            //Bitmap bitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmplist.add(textureCapture.getBitmap(bitmap));

            //only one capture
            if (cap == 1) {
                progressDialog = ProgressDialog.show(CameraActivity.this, getResources().getString(R.string.saving_images), "", true);
                saveImage();
            } else {
                //save image on the phone when all captures are made
                if (bmplist.size() == list.size()) {
                    progressDialog = ProgressDialog.show(CameraActivity.this, getResources().getString(R.string.saving_images), "", true);
                    saveImages();
                    enableMenu = true;

                    invalidateOptionsMenu();
                }
            }
            //set back to invisible to see the camera preview
            textureCapture.setVisibility(View.INVISIBLE);


        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult result) {
            Log.i(TAG, "camCaptureCallback onCaptureProgressed");
        }

    };

    private void openCam() {

        Log.i(TAG, "openCam");

        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        
        cameraConfig(camManager);

        Log.i("camId", camId);

        if (camManager != null) {
            try {
                Log.i(TAG, "CameraManager.openCamera");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        camManager.openCamera(camId, camDeviceCallback, null);
                    } else {
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                            Toast.makeText(this, "No Permission to use the Camera", Toast.LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
                    }
                } else {
                    camManager.openCamera(camId, camDeviceCallback, null);
                }

            } catch (CameraAccessException e) {
                e.printStackTrace();

            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case  REQUEST_CAMERA_RESULT:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera service permission have not been granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    // If the camera is authorized
                    try {
                        camManager.openCamera(camId, camDeviceCallback, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void closeCam(){

        Log.i(TAG, "closeCam");

        if(captureSession!=null){
            Log.i("closeCam","CLOSE SESSION");
            captureSession.close();
            captureSession = null;
        }

        if (camDevice != null) {
            Log.i("closeCam", "CLOSE DEVICE");
            camDevice.close();
            camDevice = null;
        }


    }

    private void cameraConfig(CameraManager cManager){

        Log.i(TAG, "cameraConfig");

        try{

            String id = null;
            int facing;
            int orientation;

            for(int i=0; i<cManager.getCameraIdList().length; i++) {

                id = cManager.getCameraIdList()[i];
                camCharacteristics = cManager.getCameraCharacteristics(id);

                facing = camCharacteristics.get(CameraCharacteristics.LENS_FACING);

                //front camera
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {

                    Log.i(TAG, "LENS FACING FRONT");
                    //camId = id;

                    // hardware level
                    int level = camCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                        Log.i(TAG, "INFO_SUPPORTED_HARDWARE_LEVEL: LEGACY");
                    if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)
                        Log.i(TAG, "INFO_SUPPORTED_HARDWARE_LEVEL: FULL");
                    if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
                        Log.i(TAG, "INFO_SUPPORTED_HARDWARE_LEVEL: LIMITED");

                    //available capabilities (all of them if level=FULL)
                    int[] cap = camCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                    Log.i(TAG, "REQUEST_AVAILABLE_CAPABILITIES: ");
                    for (int c : cap) {
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE)
                            Log.i(TAG, " - BACKWARD COMPATIBLE ");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)
                            Log.i(TAG, " - MANUAL SENSOR ");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING)
                            Log.i(TAG, " - MANUAL POST PROCESSING ");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)
                            Log.i(TAG, " - RAW");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS)
                            Log.i(TAG, " - READ SENSOR SETTINGS");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE)
                            Log.i(TAG, " - BURST CAPTURE");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING)
                            Log.i(TAG, " - PRIVATE REPROCESSING");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING)
                            Log.i(TAG, " - YUV REPROCESSING");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)
                            Log.i(TAG, " - DEPTH OUTPUT");
                        if (c == CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO)
                            Log.i(TAG, " - CONSTRAINED HIGH SPEED VIDEO");
                    }

                    float focus = camCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                    Log.i(TAG, "LENS MINIMUM FOCUS DISTANCE: "+String.valueOf(focus));

                    //exposure time range
                    android.util.Range range = camCharacteristics.get( CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
                    Log.i(TAG, "EXPOSURE TIME RANGE: " + String.valueOf(range));
                    if(range!=null) {
                        expTimeMin = (long) range.getLower();
                        expTimeMax = (long) range.getUpper();
                    }

                    //auto exposure compensation range
                    android.util.Range rangeCompensation = camCharacteristics.get( CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
                    Log.i(TAG, "AUTO EXPOSURE COMPENSATION RANGE: " + String.valueOf(rangeCompensation));

                    //auto exposure compensation step
                    android.util.Rational step = camCharacteristics.get( CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
                    Log.i(TAG, "AUTO EXPOSURE COMPENSATION STEP " + String.valueOf(step));

                    //iso range
                    android.util.Range rangeIso = camCharacteristics.get( CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                    Log.i(TAG, "SENSITIVITY RANGE: " + String.valueOf(rangeIso));
                    if(rangeIso!=null) {
                        isoMin = (int) rangeIso.getLower();
                        isoMax = (int) rangeIso.getUpper();
                    }

                    //available awb modes
                    int[] awbMode = camCharacteristics.get( CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
                    Log.i(TAG,"AWB_AVAILABLE_MODES: ");
                    for(int awb : awbMode){
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_OFF)Log.i(TAG," - AWB MODE OFF ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_AUTO)Log.i(TAG," - AWB MODE AUTO ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT)Log.i(TAG," - AWB MODE FLUORESCENT ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT)Log.i(TAG," - AWB MODE DAYLIGHT ");
                        //all modes are available...
                    }

                    //orientation
                    //270 for nexus5X (instead of 90 for most phones)
                    orientation = camCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Log.i(TAG,"SENSOR ORIENTATION: "+ orientation);
                }

                //back camera
                if (facing == CameraCharacteristics.LENS_FACING_BACK) {

                    Log.i(TAG, "LENS FACING BACK");
                    camId = id;

                    // hardware level
                    int level = camCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if(level==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) Log.i(TAG,"INFO_SUPPORTED_HARDWARE_LEVEL: LEGACY");
                    if(level==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) Log.i(TAG,"INFO_SUPPORTED_HARDWARE_LEVEL: FULL");
                    if(level==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) Log.i(TAG,"INFO_SUPPORTED_HARDWARE_LEVEL: LIMITED");

                    //available capabilities (all of them if level=FULL)
                    int[] cap = camCharacteristics.get( CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
                    Log.i(TAG,"REQUEST_AVAILABLE_CAPABILITIES: ");
                    for(int c : cap){
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE)Log.i(TAG," - BACKWARD COMPATIBLE ");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)Log.i(TAG," - MANUAL SENSOR ");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING)Log.i(TAG," - MANUAL POST PROCESSING ");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)Log.i(TAG," - RAW");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS)Log.i(TAG," - READ SENSOR SETTINGS");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE)Log.i(TAG," - BURST CAPTURE");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING)Log.i(TAG," - PRIVATE REPROCESSING");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING)Log.i(TAG," - YUV REPROCESSING");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT)Log.i(TAG," - DEPTH OUTPUT");
                        if(c==CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO)Log.i(TAG," - CONSTRAINED HIGH SPEED VIDEO");
                    }

                    float focus = camCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
                    Log.i(TAG, "LENS MINIMUM FOCUS DISTANCE: "+String.valueOf(focus));

                    float zoom = camCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
                    Log.i(TAG, "MAX DIGITAL ZOOM: "+String.valueOf(zoom));

                    //exposure time range
                    android.util.Range range = camCharacteristics.get( CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);
                    Log.i(TAG, "EXPOSURE TIME RANGE: " + String.valueOf(range));
                    if(range!=null) {
                        expTimeMin = (long) range.getLower();
                        expTimeMax = (long) range.getUpper();
                    }

                    //auto exposure compensation range
                    android.util.Range rangeCompensation = camCharacteristics.get( CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
                    Log.i(TAG, "AUTO EXPOSURE COMPENSATION RANGE: " + String.valueOf(rangeCompensation));

                    //auto exposure compensation step
                    android.util.Rational step = camCharacteristics.get( CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP);
                    Log.i(TAG, "AUTO EXPOSURE COMPENSATION STEP " + String.valueOf(step));

                    //iso range
                    android.util.Range rangeIso = camCharacteristics.get( CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
                    Log.i(TAG, "SENSITIVITY RANGE: " + String.valueOf(rangeIso));
                    if(rangeIso!=null) {
                        isoMin = (int) rangeIso.getLower();
                        isoMax = (int) rangeIso.getUpper();
                    }

                    //available awb modes
                    int[] awbMode = camCharacteristics.get( CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
                    Log.i(TAG,"AWB_AVAILABLE_MODES: ");
                    for(int awb : awbMode){
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_OFF)Log.i(TAG," - AWB MODE OFF ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_AUTO)Log.i(TAG," - AWB MODE AUTO ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT)Log.i(TAG," - AWB MODE FLUORESCENT ");
                        if(awb==CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT)Log.i(TAG," - AWB MODE DAYLIGHT ");
                        //all modes are available...
                    }

                    boolean flash = camCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    if(flash)Log.i(TAG,"FLASH_INFO_AVAILABLE: Yes");
                    else  Log.i(TAG,"FLASH_INFO_AVAILABLE: No");

                    //orientation
                    //270 for nexus5X (instead of 90 for most phones)
                    orientation = camCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                    Log.i(TAG,"SENSOR ORIENTATION: "+orientation);

                }
            }

        } catch (CameraAccessException e) {
            e.printStackTrace();

        }
    }

    private void initCameraView(){

        Log.i(TAG,"initCameraView");

        surfaceTexture = textureView.getSurfaceTexture();
        surfaceView = new Surface(surfaceTexture);


        surfaceCapture = textureCapture.getSurfaceTexture();
        imageCapture = new Surface(surfaceCapture);
        textureCapture.setVisibility(View.INVISIBLE);

        try {
                List<Surface> surfaces = Arrays.asList(surfaceView,imageCapture);
                camDevice.createCaptureSession(surfaces, camSessionCallback, null);

        }catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void requestCameraView(){

        Log.i(TAG, "requestCameraView");

        try {

            //request to display camera view
            viewRequestBuilder = camDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            viewRequestBuilder.addTarget(surfaceView);

            //disable auto white balance
            viewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_OFF);

            viewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            //disable/enable auto exposure
            viewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CameraMetadata.CONTROL_AE_MODE_ON);

            captureSession.setRepeatingRequest(viewRequestBuilder.build(), camViewCallback, null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }


    //take only one capture
    private void captureImage(){

        Log.i(TAG,"captureImage");

        cap = 1;

        try {
            //request to capture image
            captureRequestBuilder = camDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageCapture);

            //disable auto white balance
            captureRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_OFF);

            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CameraMetadata.CONTROL_AE_MODE_OFF);

            //set exposure time
            //captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, expTimeMax / 8);

            captureSession.capture(captureRequestBuilder.build(), camCaptureCallback, null);


        }catch (CameraAccessException e){
            e.printStackTrace();
        }


    }


    //take captures with different settings (exposure time, white balance, iso)
    private void captureImageBurst(){

        Log.i(TAG, "captureImageBurst");

        cap = 0;

        try {
            captureRequestBuilder = camDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageCapture);
            list = new ArrayList<>();
            seqImages = new ArrayList<>();
            expTimes = new ArrayList<>();
            seqMats = new ArrayList<>();

            int cptExp = 0;
            int cptTemp = 0;

            // Temps d'exposition
            //tabExp = new long[]{16, 8, 4, 2, 1,-2,-4,-8};//,-16,-32,-64};
            tabExp = new long[]{2, 1,-2,-4,-8, 16, 8, 4};

            //tabTemp = new int[]{0,1000,2000,3000,4000,5000,6000,7000,8000,9000,10000};
            tabTemp = new int[]{0};

            int nbCapture = tabTemp.length*tabExp.length;

            Log.i(TAG, "exposureTimeAuto: " + String.valueOf(exposureTimeAuto));

            for(int i=0;i<nbCapture;i++){

                Log.i(TAG,"tabExp: "+String.valueOf(tabExp[cptExp]));

                //Exposure Time
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_OFF);

                if(tabExp[cptExp]>0) captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTimeAuto*tabExp[cptExp]);
                else captureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTimeAuto/(-tabExp[cptExp]));

                //iso
                captureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, isoMin * 2);

                //White balance, temperature
                captureRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_OFF);

                if(tabTemp[cptTemp]!=0) {
                    captureRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CameraMetadata.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX);
                    captureRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, setColorTemp(tabTemp[cptTemp]));
                }

                if(cptExp==(tabExp.length-1)){
                    cptExp = 0;
                    cptTemp++;
                }
                else {
                    cptExp++;
                }

                /*
                if(flashOn) {
                    captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_TORCH);
                }*/

                list.add(captureRequestBuilder.build());
            }

            captureSession.captureBurst(list, camCaptureCallback, null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }


    //to convert temperature to rggbchannelvector
    private double getRGBToDouble(int color) {
        double t = color;
        t = t * 3 *2;
        t = t / (255);
        t = t / 3;
        t += 1;

        return t;
    }

    private RggbChannelVector setColorTemp(float temp){

        float r,g,b;
        int[] rgb = ColorTemp(temp);

        r = (float)getRGBToDouble(rgb[0]);
        g = (float)getRGBToDouble(rgb[1])/2;
        b = (float)getRGBToDouble(rgb[2]);
        r = r/g;
        b = b/g;
        g = 1;

        RggbChannelVector v = new RggbChannelVector(r,g,g,b);

        //Log.i(TAG,String.valueOf(v));

        return v;
    }

    private int[] ColorTemp(float temp){

        float temperature = temp/100;
        float t;
        float r;
        float g;
        float b;

        //red
        if(temperature <= 66) r = 255;
        else{
            t = temperature - 60;
            t = (float) (329.698727446*(Math.pow((double)t,-0.1332047592)));
            r = t;
            if(r<0) r=0;
            if(r>255) r=255;
        }

        //green
        if(temperature <= 66){
            t = temperature;
            t = (float)(99.4708025861*Math.log(t)-161.1195681661);
            g = t;
            if(g<0) g = 0;
            if(g>255) g = 255;
        }
        else{
            t = temperature - 60;
            t = (float)(288.1221695283*(Math.pow((double)t,-0.0755148492)));
            g = t;
            if(g<0) g = 0;
            if(g>255) g = 255;
        }

        //blue
        if(temperature >= 66) b = 255;
        else{
            if(temperature <=19)b = 0;
            else{
                t = temperature -10;
                t = (float)(138.5177312231*Math.log(t)-305.0447927307);
                b = t;
                if(b<0)b = 0;
                if(b>255)b=255;
            }
        }

        int[] v = new int[]{(int)r,(int)g,(int)b};

        //Log.i(TAG, "r: "+String.valueOf(v[0])+" g: "+String.valueOf(v[1])+" b: "+String.valueOf(v[2]));

        return v;
    }

    //thread to save images
    private void saveImages(){

        new Thread(new Runnable(){

            @Override
            public void run(){

                Log.i(TAG, "Saving images...");
                int cpt = 0;
                int t = 0;

                for(int i=0;i<bmplist.size();i++) {

                    File file;

                    if(i<10) file = new File(getExternalFilesDir(directoryFiles), directoryPatient+"_image0" + i+"_"+tabExp[cpt]+".png");
                    else file = new File(getExternalFilesDir(directoryFiles), directoryPatient+"_image" + i +"_"+tabExp[cpt]+".png");

                    nomsImages.add(file.getName());

                    cpt++;
                    if(cpt==tabExp.length){
                        t++;
                        cpt = 0;
                    }

                    seqImages.add(file.getAbsolutePath());

                    FileOutputStream fos;

                    try {
                        fos = new FileOutputStream(file);
                        bmplist.get(i).compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        bmplist.get(i).recycle();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                //Add acquisition in DB
                SQLiteDBHelper dbHelper = new SQLiteDBHelper(getApplicationContext());
                idPatient = Integer.parseInt(directoryPatient.split("-")[0]);
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Acquisition acq = new Acquisition(idPatient,dbHelper.getNextAcquisitionNumber(idPatient), sdf.format(date));
                dbHelper.addAcquisition(acq);
                dbHelper.close();

                createTimesFile();

                Log.i("Acquisition", acq.getAcquisitionID() + " - " + acq.getPatientID() + " - " + acq.getAcquisition_number() + " - " + acq.getDate_acquisition());
                Log.i(TAG, "Images Saved");
                progressDialog.dismiss();

            }
        }).start();
    }

    private void saveImage(){

        new Thread(new Runnable(){

            @Override
            public void run(){

                Log.i(TAG, "Saving image...");

                File file = new File(getExternalFilesDir(directoryFiles), "image.png");
                nomsImages.add(file.getName());

                FileOutputStream fos;

                try {
                    fos = new FileOutputStream(file);
                    bmplist.get(0).compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    bmplist.get(0).recycle();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                createTimesFile();

                Log.i(TAG, "Image Saved");
                progressDialog.dismiss();
            }
        }).start();
    }

    //native functions
    public native int imageHDR(String dir, String[] images, float[] times);
    public native int eyeDetection(String name, String dir, int x, int y);
    public native int analysisLDR(String name1, int i1, int j1, String name2, int i2, int j2, int size, String dir);

    public void nativeHDR(){

        new Thread(new Runnable() {

            @Override
            public void run() {

            long start, end;
            double time;

            String dir = getExternalFilesDir(directoryFiles).getAbsolutePath();

            Log.i(TAG, "start imageHDR native");
            start = System.currentTimeMillis();

            float[] tempTime = new float[expTimes.size()];
            String[] tempImg = new String[seqImages.size()];
            for (int i = 0; i < expTimes.size(); i++) {
                tempImg[i] = seqImages.get(i);
                tempTime[i] = expTimes.get(i);
            }

            if ((!expTimes.isEmpty()) && (!seqImages.isEmpty())) {

                //call native function imageHDR
                imageHDR(dir, tempImg, tempTime);

                Log.i(TAG, "end imageHDR native");
                end = System.currentTimeMillis();
                time = (end - start) / 1000.000;
                Log.i(TAG, "time: " + String.valueOf(time) + " seconds");

            }

            progressDialog.dismiss();
        }

        }).start();
    }

    private void showImage(){

        File file_preview = new File(getExternalFilesDir(directoryFiles), "sclera.png");
        Log.i("path",file_preview.getAbsolutePath());

        Bitmap img_preview = BitmapFactory.decodeFile(file_preview.getAbsolutePath());

        AlertDialog.Builder builder =  new AlertDialog.Builder(CameraActivity.this);
        LayoutInflater inflater = CameraActivity.this.getLayoutInflater();
        View dialog_view = inflater.inflate(R.layout.custom_dialog,null);
        imageView = (ImageView) dialog_view.findViewById(R.id.image_preview);
        imageView.setImageBitmap(img_preview);

        builder.setView(dialog_view)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    dialog.dismiss();
                }
            })
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    dialog.dismiss();
                    imageDisplay.setVisibility(View.INVISIBLE);
                }
            })
            .create();

        builder.show();

    }

    //select seed point and readjust it to correspond to the size of the image
    private void selectPoint(){

        File file_preview = new File(getExternalFilesDir(directoryFiles), nomsImages.get(2)); //nomsImages.get(4));
        Bitmap img = BitmapFactory.decodeFile(file_preview.getAbsolutePath());

        imageDisplay.setImageBitmap(img);
        imageDisplay.setVisibility(View.VISIBLE);

        imageDisplay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    Log.i(TAG, "onTouchDetect");

                    int textureHeight = textureCapture.getMeasuredHeight();
                    int textureWidth = textureCapture.getMeasuredWidth();
                    x = (width * (int) event.getX()) / textureWidth;
                    y = (height * (int) event.getY()) / textureHeight;

                    Log.i(TAG, "resize x: " + String.valueOf(x) + " resize y: " + String.valueOf(y));
                    progressDialog = ProgressDialog.show(CameraActivity.this, "Starting detection...", "", true);
                    nativeDetection();

                    return true;
                }

                return false;
            }
        });

    }

    public void nativeDetection() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                long start, end;
                double time;

                String dir = getExternalFilesDir(directoryFiles).getAbsolutePath();
                String name = "/"+nomsImages.get(2);//(4);

                Log.i("name", name);

                Log.i(TAG, "start detection native");
                start = System.currentTimeMillis();

                //call native function
                eyeDetection(name, dir, x, y);

                Log.i(TAG, "end detecti" +
                        "on native");

                end = System.currentTimeMillis();
                time = (end - start) / 1000.000;
                Log.i(TAG, "time: " + String.valueOf(time) + " seconds");

                progressDialog.dismiss();

            }

        }).start();
    }

    public void nativeAnalysisLDR(){

        new Thread(new Runnable() {

            @Override
            public void run() {

                String dir = getExternalFilesDir(directoryFiles).getAbsolutePath();
                String nameSclera = "/" + nomsImages.get(3);
                String nameRef = "/" + nomsImages.get(2);//(4);
                Log.i("nameSclera", nameSclera);
                Log.i("nameRef", nameRef);
                int size_window = 12;
                int iSclera = 100;
                int jSclera = 100;
                int iRef = 200;
                int jRef = 200;

                Log.i(TAG, "start analysis LDR");

                analysisLDR(nameSclera, iSclera, jSclera, nameRef, iRef, jRef, size_window, dir);

                Log.i(TAG, "end analysis LDR");

            }

        }).start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void createTimesFile(){
        //VERSION 2
        //save exposure times to a file .csv
        File timesFile = new File(getExternalFilesDir(directoryFiles), "times.csv");

        try {
            timesFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(timesFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            //myOutWriter.append("sep=;" + "\n");
            myOutWriter.append("IMAGE; TIMES" + "\n");
            for (int i = 0; i < expTimes.size(); i++) {
                myOutWriter.append(nomsImages.get(i).toString() + "; " + expTimes.get(i).toString());
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onOrientationChange(OrientationManager.ScreenOrientation screenOrientation) {

        int percentHeight;
        int percentWidth;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean gridIsDisplay = sharedPref.getBoolean(getString(R.string.gridDisplay), true);

        if(gridIsDisplay) {
            switch (screenOrientation) {
                case PORTRAIT:
                    position = 1;
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cadre_portrait.getLayoutParams();
                    percentHeight = (int) Math.round(heightEcran * 0.40);
                    percentWidth = (int) Math.round(widthEcran * 0.40);

                    /*lp.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);*/

                    lp.height = percentHeight;
                    lp.width = percentWidth;
                    /*lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);*/
                    //cadre_portrait.setLayoutParams(lp);

                    cadre_portrait.setVisibility(View.VISIBLE);
                    cadre_landscape.setVisibility(View.INVISIBLE);
                    cadre_reverse_landscape.setVisibility(View.INVISIBLE);
                    break;
                case REVERSED_PORTRAIT:
                    break;
                case REVERSED_LANDSCAPE:
                    position = 3;
                    RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) cadre_reverse_landscape.getLayoutParams();
                    percentHeight = (int) Math.round(heightEcran * 0.60);
                    percentWidth = (int) Math.round(widthEcran * 0.60);

                    /*lp2.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp2.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp2.removeRule(RelativeLayout.CENTER_VERTICAL);
                    lp2.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);*/

                    lp2.height = percentHeight;
                    lp2.width = percentWidth;
                    /*lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);*/
                    //cadre_reverse_landscape.setLayoutParams(lp2);

                    cadre_reverse_landscape.setVisibility(View.VISIBLE);
                    cadre_landscape.setVisibility(View.INVISIBLE);
                    cadre_portrait.setVisibility(View.INVISIBLE);
                    break;
                case LANDSCAPE:
                    position = 2;
                    RelativeLayout.LayoutParams lp3 = (RelativeLayout.LayoutParams) cadre_landscape.getLayoutParams();
                    percentHeight = (int) Math.round(heightEcran * 0.60);
                    percentWidth = (int) Math.round(widthEcran * 0.60);

                    /*lp3.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    lp3.removeRule(RelativeLayout.CENTER_VERTICAL);
                    lp3.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp3.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);*/

                    lp3.height = percentHeight;
                    lp3.width = percentWidth;
                    /*lp3.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP);*/
                    //cadre_landscape.setLayoutParams(lp3);

                    cadre_landscape.setVisibility(View.VISIBLE);
                    cadre_portrait.setVisibility(View.INVISIBLE);
                    cadre_reverse_landscape.setVisibility(View.INVISIBLE);

                    break;
            }
        }
    }
}





