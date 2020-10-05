package com.ahagari.dialogtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button searchButton;
    Spinner spinner;
    Spinner spinner2;

    HashMap<Integer, ArrayList<String>> testMap;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextStation);
        editText.setCursorVisible(true);
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        setMap();
    }

    private void setMap() {
        testMap = new HashMap<Integer, ArrayList<String>>();
        for(int i =0; i < 3; i++){
            ArrayList<String> arrayList = new ArrayList<String>();
            for(int j = 0; j < 5; j++){
                arrayList.add("Select:" + i + "[" + j + "]");
            }
        }
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();

        if(buttonId == searchButton.getId()){
            showStationDialog();
        } else {

        }
    }


    private void setRailsSpinner(int which) {
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spinner.setAdapter(arrayAdapter);
    }
    private void showStationDialog(){
        TextView titleView = new TextView(this);
        titleView.setText("駅名を選択してください");
        titleView.setTextSize(24);
        titleView.setPadding(20, 20, 20, 20);
        titleView.setTextColor(Color.rgb(0, 191, 255));

        int BORDER_WEIGHT = 2;


// 太さ2pxのボーダーをつける
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(BORDER_WEIGHT, Color.rgb(0, 191, 255));

// LayerDrawableにボーダーを付けたDrawableをセット
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
// ボーダーがいらない辺にオフセット（負値）をつける
        layerDrawable.setLayerInset(0, -BORDER_WEIGHT, -BORDER_WEIGHT, -BORDER_WEIGHT, 0);

// ボーダーを付けたいViewにセットする
        titleView.setBackground(layerDrawable);

        final String[] items = {"item_0", "item_1", "item_2"};
        new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item_which pressed
                        setRailsSpinner(which);
                        editText.setText(items[which]);
                    }
                })
                .show();
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(R.string.pick_color)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }
}
