package com.ahagari.howmanyminutesleft;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeTableListActivity extends AppCompatActivity implements View.OnClickListener {


    int intBackGroundColor;

    // View
    TextView  textView2, textView3;
    Button backButton;
    ListView listView;
    ConstraintLayout constraintLayout;

    // インテント
    Intent intent;

    String filename;

    InputStream inputStream;
    String lineBuffer;
    List<String> timeTableAfterList;
    List<String> timeTableBeforeList;
    List<String> timeTableList;

    ArrayList<CustomListData> afterViewList;
    ArrayList<CustomListData> beforeViewList;
    List<CustomListData> viewItems;

    Timer timer; // タイマー処理をする
    final Handler handler = new Handler();
    private TimerTask mTask;
    ArrayList<String> fileList;
    boolean displayedFlag = false; //リストを１度でも表示したらtrue
    int pos;
    //ArrayAdapter<String> adapter; // listViewとデータをつなぐアダプター
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_list);

        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        backButton = findViewById(R.id.button2);
        listView = findViewById(R.id.listView);
        constraintLayout = findViewById(R.id.constraintLayout);
        intBackGroundColor = R.color.color_black;
        constraintLayout.setBackgroundResource(intBackGroundColor);

        intent = getIntent();
        Bundle bundle = intent.getExtras();

        // 時刻取得
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        long nowJikoku = hour * 3600 + minutes * 60 + second;
        if(hour < 3)
            nowJikoku = nowJikoku + 0x15180L;

        filename = bundle.getString(getString(R.string.timetable_list_file));
        String titleArray[] = filename.replace(".txt", "").split("-");
        textView2.setText(titleArray[0] + " " + titleArray[1] + "駅");
        textView2.setTextColor(Color.WHITE);
        textView3.setText(titleArray[2] + "方面");
        textView3.setTextColor(Color.WHITE);

        fileList = new ArrayList<String>();
        try {
            inputStream = openFileInput(filename);

            BufferedReader reader= new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

            while( (lineBuffer = reader.readLine()) != null ){
                System.out.println(lineBuffer);
                fileList.add(lineBuffer);

//                String[] lineArray = lineBuffer.split(",");
//                timeTableList.add(String.format("%s時%s分\n%s %s",
//                        lineArray[0], lineArray[1], lineArray[3].equals("null") ? "" : lineArray[3] , lineArray[4]));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer = new Timer(true);
        /**
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayTimeTableList();
                    }
                });
            }
        };
         **/

        mTask = new TimerTask() {

            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        displayTimeTableList();
                    }
                });
            }
        };

        timer.schedule(mTask, 0, 1000);
        //timer.schedule(timerTask,0, 1000);
        //displayTimeTableList();
        backButton.setOnClickListener(this);
        //listView.setEnabled(false);
    }

    private void setTimer(){
        mTask = new TimerTask() {

            public void run() {
                handler.post(new Runnable() {

                    public void run() {
                        displayTimeTableList();
                    }
                });
            }
        };
        timer.schedule(mTask, 0, 1000);
    }
    @Override
    public void onPause()
    {
        if(mTask != null)
        {
            mTask.cancel();
            timer.purge();
            mTask = null;
        }
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //getAlarm();
        setTimer();
    }

    private void displayTimeTableList() {

        int startIndex =0;
        int j=0;
        timeTableAfterList = new ArrayList<String>();
        afterViewList = new ArrayList<CustomListData>();

        // 時刻取得
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        long nowJikoku = hour * 3600 + minutes * 60 + second;

        for( int i=0; i < fileList.size(); i++){
            String strTmp = fileList.get(i);
            String[] lineArray = strTmp.split(",");

            int stationTime = Integer.parseInt(lineArray[2]);
            if(nowJikoku > stationTime){
                continue;
            }
            if( j==0) {
                startIndex = i - 1;
            }

            if(j > 9) {
                break;
            }
            long  longTmp = stationTime - nowJikoku;
            long dispHour = longTmp / 60L / 60L;
            long dispMinute = (longTmp % 3600L) / 60L;
            long dispSecond = longTmp % 60L;
            String dispLeftTime;
            if(dispHour > 0) {
                dispLeftTime = String.format("あと%s時間%s分", String.valueOf(dispHour), String.valueOf(dispMinute));
            } else if(dispMinute > 0){
                dispLeftTime = String.format("あと%s分", String.valueOf(dispMinute));
            } else {
                dispLeftTime = String.format("あと%s秒", String.valueOf(dispSecond));

            }
            timeTableAfterList.add(String.format("%s時%s分\n%s\n%s %s",
                    lineArray[0], lineArray[1], dispLeftTime, lineArray[3].equals("null") ? "" : lineArray[3] , lineArray[4]));

            // tData.hour, tData.minute, tData.jikoku, tData.type, tData.destination);
            CustomListData customItem = new CustomListData();
            customItem.setListTextColor(createTimeLeftColor(longTmp));
            customItem.setListTextIndex(getTextIndex(j));
            customItem.setListTextTime(String.format("%s時%02d分",lineArray[0], Integer.valueOf(lineArray[1])));
            customItem.setListTextTrainType(lineArray[3].equals("null") ? "" : lineArray[3]);
            customItem.setListTextDest(lineArray[4] + "行き");
            customItem.setListTextCountTime(dispLeftTime);
            afterViewList.add(customItem);
            j++;
        }



        // 表示する時刻が10件未満の場合、翌日の時刻を表示
        if(timeTableAfterList.size() < 10) {
            addTomorrowTimeTable(fileList, nowJikoku);
        }

        ArrayList<String> beforeList = new ArrayList<String>();
        if(startIndex > 0) {
            int i = startIndex;
            int count =0;
            while(i >= 0) {
                if(count > 9) {
                    break;
                }
                String strTmp = fileList.get(i);
                beforeList.add(strTmp);
                i--;
                count++;
            }
        }

        timeTableBeforeList = new ArrayList<String>();
        beforeViewList = new ArrayList<CustomListData>();
        Collections.reverse(beforeList);
        for(int i=0; i < beforeList.size(); i++){
            String strTmp = beforeList.get(i);
            String[] lineArray = strTmp.split(",");
            int stationTime = Integer.parseInt(lineArray[2]);
            long longTmp = nowJikoku -stationTime;
            long dispHour = longTmp / 60 / 60;
            long dispMinute = (longTmp % 3600) / 60;
            String dispPastTime;
            if(dispHour > 0) {
                dispPastTime = String.format("%s時間%s分前", String.valueOf(dispHour), String.valueOf(dispMinute));
            } else {
                dispPastTime = String.format("%s分過ぎ", String.valueOf(dispMinute));
            }

            timeTableBeforeList.add(String.format("%s時%s分\n%s\n%s %s",
                    lineArray[0], lineArray[1], dispPastTime, lineArray[3].equals("null") ? "" : lineArray[3] , lineArray[4]));

            CustomListData customItem = new CustomListData();
            customItem.setListTextIndex("出発済み");
            customItem.setListTextColor(Color.rgb(128, 40, 192));
            customItem.setListTextTime(String.format("%s時%02d分",lineArray[0], Integer.valueOf(lineArray[1])));
            // tData.hour, tData.minute, tData.jikoku, tData.type, tData.destination);
            customItem.setListTextTrainType(lineArray[3].equals("null") ? "" : lineArray[3]);
            customItem.setListTextDest(lineArray[4] + "行き");
            customItem.setListTextCountTime(dispPastTime);
            beforeViewList.add(customItem);
        }

        // TODO 表示方法変更したので、デバッグ後削除する
        timeTableList = new ArrayList<String>();
        timeTableList.addAll(timeTableBeforeList);
        timeTableList.addAll(timeTableAfterList);

        viewItems = new ArrayList<CustomListData>();
        viewItems.addAll(beforeViewList);
        viewItems.addAll(afterViewList);


        if(displayedFlag == false) {
            MyListArrayAdapter customAdapter = new MyListArrayAdapter(this, R.layout.list, viewItems);
            //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timeTableList);
            listView.setAdapter(customAdapter);
        } else {
            MyListArrayAdapter customAdapter = (MyListArrayAdapter)listView.getAdapter();
            customAdapter.clear();
            customAdapter.addAll(viewItems);
            customAdapter.notifyDataSetChanged();
        }


        if(displayedFlag == false) {
            if (beforeViewList != null || beforeViewList.size() > 1) {
                listView.setSelection(beforeViewList.size());
            }
            displayedFlag = true;
        }

    }
    private String getTextIndex(int j){
        String[] indexString = new String[]{"先発", "次発", "次々発"};
        if(j < 3) {
            return indexString[j];
        }

        return j + "番目";
    }

    private void addTomorrowTimeTable(ArrayList<String> fileList, long nowJikoku) {
//        int timeTableListIndex = 0;
//        if(timeTableAfterList != null && timeTableAfterList.size() > 0) {
//            timeTableListIndex =timeTableAfterList.size();
//        }
        int countIndex = fileList.size() - 1;
        for(int i=0; i < 10; i++ ){
            if(i > fileList.size() - 1){
                break;
            }
            String strTmp = fileList.get(i);
            String[] lineArray = strTmp.split(",");
            int stationTime = Integer.parseInt(lineArray[2]);
            long  longTmp = stationTime + 86400 - nowJikoku;
            long dispHour = longTmp  / 60 / 60;
            long dispMinute = (longTmp % 3600) / 60;
            String dispLeftTime;
            if(dispHour > 0) {
                dispLeftTime = String.format("あと%s時間%s分", String.valueOf(dispHour), String.valueOf(dispMinute));
            } else {
                dispLeftTime = String.format("あと%s分", String.valueOf(dispMinute));
            }
            timeTableAfterList.add(String.format("%s時%s分\n%s\n%s %s",
                    lineArray[0], lineArray[1], dispLeftTime, lineArray[3].equals("null") ? "" : lineArray[3] , lineArray[4]));
            CustomListData customItem = new CustomListData();
            customItem.setListTextColor(createTimeLeftColor(longTmp));
            customItem.setListTextIndex(getTextIndex(countIndex));
            customItem.setListTextTime(String.format("%s時%02d分",lineArray[0], Integer.valueOf(lineArray[1])));
            // tData.hour, tData.minute, tData.jikoku, tData.type, tData.destination);
            customItem.setListTextTrainType(lineArray[3].equals("null") ? "" : lineArray[3]);
            customItem.setListTextDest(lineArray[4] + "行き");
            customItem.setListTextCountTime(dispLeftTime);
            afterViewList.add(customItem);
            countIndex++;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button2) {
            finish();
        }
    }
    private int createTimeLeftColor(long l)
    {
        if(l < 0L)
            return Color.rgb(128, 40, 192);
        if(l < 600L)
        {
            int j1 = (int)l;
            int k = 512 - j1;
            int i = k;
            if(k > 255)
                i = 255;
            k = i;
            if(i < 0)
                k = 0;
            i = j1;
            if(j1 > 255)
                i = 255;
            j1 = i;
            if(i < 0)
                j1 = 0;
            return Color.rgb(k, j1, 0);
        }
        if(l > 7200L)
        {
            int k1 = (int)(l - 7200L);
            int i1 = 256 - k1;
            int j = i1;
            if(i1 > 255)
                j = 255;
            i1 = j;
            if(j < 0)
                i1 = 0;
            j = k1;
            if(k1 > 255)
                j = 255;
            k1 = j;
            if(j < 0)
                k1 = 0;
            return Color.rgb(0, i1, k1);
        } else
        {
            return 0xff00ff00;
        }
    }
}
