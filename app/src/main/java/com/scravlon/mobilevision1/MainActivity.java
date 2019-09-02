package com.scravlon.mobilevision1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scravlon.mobilevision1.camera.CameraSourcePreview;
import com.scravlon.mobilevision1.camera.GraphicOverlay;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import static com.scravlon.mobilevision1.constantClass.HEADSHARE;
import static com.scravlon.mobilevision1.constantClass.MUSTSHARE;
import static com.scravlon.mobilevision1.constantClass.NOSESHARE;
import static com.scravlon.mobilevision1.constantClass.SHAREDSTRING;
import static com.scravlon.mobilevision1.constantClass.typeExtra;

public class MainActivity extends AppCompatActivity {
    public FaceGraphic mFaceGraphic;
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;

    private static final int BUTMUSTCODE = 7;
    private static final int BUTMNOSECODE = 8;
    private static final int BUTHEADCODE = 9;

    private String chead="";
    private String cnose="";
    private String cmust="";



    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    SharedPreferences sharedPreferences;
    ImageButton but_head;
    ImageButton but_nose;
    ImageButton but_must;
    LinearLayout ll_head;
    LinearLayout ll_nose;
    LinearLayout ll_must;
    ArrayList<String> arrayHead;
    ArrayList<String> arrayNose;
    ArrayList<String> arrayMust;

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        but_head = findViewById(R.id.but_addHead);
        but_nose = findViewById(R.id.but_addNose);
        but_must = findViewById(R.id.but_addMust);
        ll_head = findViewById(R.id.ll_head);
        ll_nose = findViewById(R.id.ll_nose);
        ll_must = findViewById(R.id.ll_must);

        sharedPreferences = getSharedPreferences(SHAREDSTRING,MODE_PRIVATE);
        arrayHead = arrayListPulling(HEADSHARE);
        arrayNose = arrayListPulling(NOSESHARE);
        arrayMust = arrayListPulling(MUSTSHARE);

        //TODO pass useful information
        //TODO Activity for result
       but_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, drwaingActivity.class);
                intent.putExtra(typeExtra,HEADSHARE);
                startActivity(intent);
            }
        });
       but_nose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, drwaingActivity.class);
                intent.putExtra(typeExtra,NOSESHARE);
                startActivity(intent);
            }
        });
        but_must.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, drwaingActivity.class);
                intent.putExtra(typeExtra,MUSTSHARE);
                startActivityForResult(intent,BUTMUSTCODE);
            }
        });

        Toast.makeText(this, "SIZE "+ arrayMust.size(), Toast.LENGTH_SHORT).show();
        refreshView();

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Populate the user select ListView
     * @param arrayList ArrayList to be used in population
     * @param llView Specified ListView to be populate
     * @param arraySharedPreference SharedPreference reference
     */
    private void populateListView(final ArrayList<String> arrayList, final LinearLayout llView, final String arraySharedPreference) {
        LinearLayout.LayoutParams aa = (LinearLayout.LayoutParams) but_head.getLayoutParams();
        for(final String c : arrayList){
            final ImageButton imageButton = new ImageButton(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(aa.width, aa.height);
            imageButton.setBackground(getDrawable(R.drawable.imgagebutton));
            imageButton.setLayoutParams(layoutParams);
            if(c.equals(chead)||c.equals(cmust)||c.equals(cnose)){
                imageButton.setBackgroundColor(Color.GRAY);
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  view.setBackgroundColor(Color.GRAY);
                    switch (arraySharedPreference){
                        case HEADSHARE:
                            if(chead.equals(c)){
                                chead="";
                                mFaceGraphic.headBit = null;
                            } else{
                                chead = c;
                                mFaceGraphic.headBit = decodeBase64(c);
                            }
                            break;
                        case MUSTSHARE:
                            if(cmust.equals(c)){
                                cmust="";
                                mFaceGraphic.mustBit = null;
                            } else{
                                cmust = c;
                                mFaceGraphic.mustBit = decodeBase64(c);
                            }
                            break;
                        case NOSESHARE:
                            if(cnose.equals(c)){
                                cnose="";
                                mFaceGraphic.noseBit = null;
                            } else{
                                cnose = c;
                                mFaceGraphic.noseBit = decodeBase64(c);
                            }
                            break;
                        default:
                            break;
                    }
                    resetListView(llView);
                    populateListView(arrayList,llView,arraySharedPreference);
                }
            });
            imageButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    arrayList.remove(c);
                    arrayListAdding(arrayList,arraySharedPreference);
                    refreshView();
                    return true;                }
            });
            imageButton.setImageBitmap(decodeBase64(c));
            imageButton.setAdjustViewBounds(true);
            llView.addView(imageButton);
        }
    }

    /**
     * Remove all view in a Layout
     * @param ll LinearLayour to be cleared
     */
    private void resetListView(LinearLayout ll){
        ll.removeAllViews();
    }

    /**
     * Reset all the view with new views
     */
    private void refreshView(){
        resetListView(ll_head);
        resetListView(ll_must);
        resetListView(ll_nose);
        populateListView(arrayHead,ll_head,HEADSHARE);
        populateListView(arrayMust,ll_must,MUSTSHARE);
        populateListView(arrayNose,ll_nose,NOSESHARE);

    }
    /**
     * Add the drew Bitmap to the storage
     *
     * @param target ArrayList to be added
     * @param targetString SharedPreference target string
     */
    public void arrayListAdding(ArrayList<String> target, String targetString){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(target);
        editor.putString(targetString, json);
        editor.apply();
    }

    /**
     * Pull canvas data to populate art selection
     * @param targetString SharedPreference string
     * @return ArrayList of Canvas
     */
    public ArrayList<String> arrayListPulling(String targetString){
        SharedPreferences prefs = getSharedPreferences(SHAREDSTRING,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(targetString, null);
        if(json==null){
            return (new ArrayList<String>());
        }
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {

            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
//                .setRequestedPreviewSize(1080, 720)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Restarts the camera. Update the ArrayList
     */
    @Override
    protected void onResume() {
        arrayHead = arrayListPulling(HEADSHARE);
        arrayNose = arrayListPulling(NOSESHARE);
        arrayMust = arrayListPulling(MUSTSHARE);
        refreshView();
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay,getApplicationContext());
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }


    }
}
