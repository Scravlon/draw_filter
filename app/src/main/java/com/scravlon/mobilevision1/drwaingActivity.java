package com.scravlon.mobilevision1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.scravlon.mobilevision1.constantClass.SHAREDSTRING;
import static com.scravlon.mobilevision1.constantClass.typeExtra;

public class drwaingActivity extends AppCompatActivity {
    Button but_cancel;
    Button but_save;
    Button but_undo;
    drawView drawView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drwaing);
        sharedPreferences = getSharedPreferences(SHAREDSTRING,MODE_PRIVATE);

        Intent intent = getIntent();
        final String sharedStringAdding = intent.getStringExtra(typeExtra);
        but_save = findViewById(R.id.but_save);
        but_cancel = findViewById(R.id.but_cancel);
        but_undo = findViewById(R.id.but_undo);
        drawView = findViewById(R.id.drawView);
        but_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.undoPaint();
            }
        });
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //TODO ArrayList reading then add to target
        but_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap b = loadBitmapFromView(drawView);
                ArrayList<String> all = arrayListPulling(sharedStringAdding);
                arrayListAdding(all,sharedStringAdding,encodeTobase64(b));
                finish();
            }
        });
        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                drawView.clonePaint();
                drawView.updateSize(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        RadioButton radioRed = findViewById(R.id.radiored);
        RadioButton radioorange = findViewById(R.id.radioorange);
        RadioButton radioyellow = findViewById(R.id.radioyellow);
        RadioButton radiogreen = findViewById(R.id.radiogreen);
        RadioButton radioblue = findViewById(R.id.radioblue);
        RadioButton radioindigo = findViewById(R.id.radioindigo);
        RadioButton radioviolet = findViewById(R.id.radioviolet);
        RadioButton radiowhite = findViewById(R.id.radiowhite);
        RadioButton radiodef = findViewById(R.id.radiodef);
        radioRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.RED);
            }
        });radioorange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(255,127,0));
            }
        });radioyellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(255,255,0));
            }
        });radiogreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(0,255,0));
            }
        });radioblue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(0,0,255));
            }
        });radioindigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(39,0,51));
            }
        });radioviolet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(139,0,255));
            }
        });radiowhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(255,255,255));
            }
        });radiodef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.clonePaint();
                drawView.updateColor(Color.rgb(250,250,250));
            }
        });
    }



    /**
     * Add the drew Bitmap to the storage
     *
     * @param target ArrayList to be added
     * @param targetString SharedPreference target string
     * @param adding Canvas tobe added
     */
    public void arrayListAdding(ArrayList<String> target, String targetString, String adding){
        target.add(adding);
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
     * Load the painting to Bitmap from view
     * @param v Painting View
     * @return Bitmap of the painting
     */
    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    /**
     * Encode bitmap to Base64 easy storage
     * @param image Bitmap to be encoded
     * @return Base64 string bitmap
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }


}
