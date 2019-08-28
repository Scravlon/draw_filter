package com.scravlon.mobilevision1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.scravlon.mobilevision1.constantClass.HEADSHARE;
import static com.scravlon.mobilevision1.constantClass.SHAREDSTRING;
import static com.scravlon.mobilevision1.constantClass.arrayExtra;
import static com.scravlon.mobilevision1.constantClass.typeExtra;

public class drwaingActivity extends AppCompatActivity {
    Button but_cancel;
    Button but_save;
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
        drawView = findViewById(R.id.drawView);
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
                ArrayList<Bitmap> all = arrayListPulling(sharedStringAdding);
                arrayListAdding(all,sharedStringAdding,b);
                finish();
                //arrayListAdding(all, HEADSHARE,c);
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
    public void arrayListAdding(ArrayList<Bitmap> target, String targetString, Bitmap adding){
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
    public ArrayList<Bitmap> arrayListPulling(String targetString){
        SharedPreferences prefs = getSharedPreferences(SHAREDSTRING,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(targetString, null);
        if(json==null){
            return (new ArrayList<Bitmap>());
        }
        Type type = new TypeToken<ArrayList<Bitmap>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( 1, 1, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }


}
