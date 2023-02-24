package com.example.motion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class History extends AppCompatActivity {

    DBHelper dbHelper;
    ImageButton btnWarriorTwo;
    ImageButton btnGoddess;
    ImageButton btnTree;
    TextView txtNoRecords;
    TextView txtPoseName;
    TextView txtSelectPose;
    TextView txtPerfMade;
    TextView txtHighAccuracy;
    TextView txtHighConsistency;
    TextView txtAveAccuracy;
    TextView txtAveConsistency;
    TableLayout tblSummary;
    TableLayout tblTable;
    List<UserData> listUserData = new ArrayList<>();
    List<UserData> listUserWarriorTwo = new ArrayList<>();
    List<UserData> listUserTree = new ArrayList<>();
    List<UserData> listUserGoddess = new ArrayList<>();

    int maxAccuracy = 0;
    int aveAccuracy = 0;
    int maxConsistency = 0;
    int aveConsistency = 0;

    int countTree = 0;
    int countGoddess = 0;
    int countWarrior = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DBHelper(this);

        ConnectToXML();

        try {
            listUserData = dbHelper.GetData();
            if(listUserData!=null){
                FetchData();
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnWarriorTwo.setOnClickListener(v -> {
            tblTable.removeViews(1, Math.max(0, tblTable.getChildCount() - 1));
            txtSelectPose.setVisibility(View.INVISIBLE);
            txtPoseName.setText("Warrior 2");
            if(listUserWarriorTwo == null || listUserWarriorTwo.isEmpty()){
                Reset();
                txtNoRecords.setVisibility(View.VISIBLE);
            } else {
                Collections.sort(listUserWarriorTwo, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData o1, UserData o2) {
                        return o2.getDateTime().compareTo(o1.getDateTime());
                    }
                });
               DisplayRecords(listUserWarriorTwo);
               txtPerfMade.setText(listUserWarriorTwo.size() + "");
            }
        });

        btnTree.setOnClickListener(v -> {
            tblTable.removeViews(1, Math.max(0, tblTable.getChildCount() - 1));
            txtSelectPose.setVisibility(View.INVISIBLE);
            txtPoseName.setText("Tree 2");
            if(listUserTree == null || listUserTree.isEmpty()){
                Reset();
                txtNoRecords.setVisibility(View.VISIBLE);
            } else {
                Collections.sort(listUserTree, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData o1, UserData o2) {
                        return o2.getDateTime().compareTo(o1.getDateTime());
                    }
                });
                DisplayRecords(listUserTree);
                txtPerfMade.setText(listUserTree.size() + "");
            }
        });

        btnGoddess.setOnClickListener(v -> {
            tblTable.removeViews(1, Math.max(0, tblTable.getChildCount() - 1));
            txtSelectPose.setVisibility(View.INVISIBLE);
            txtPoseName.setText("Goddess");
            if(listUserGoddess == null || listUserGoddess.isEmpty()){
                Reset();
                txtNoRecords.setVisibility(View.VISIBLE);
            } else {
                Collections.sort(listUserGoddess, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData o1, UserData o2) {
                        return o2.getDateTime().compareTo(o1.getDateTime());
                    }
                });
                DisplayRecords(listUserGoddess);
                txtPerfMade.setText(listUserGoddess.size() + "");
            }
        });
    }

    private void Reset() {
        maxAccuracy =0;
        maxConsistency = 0;
        aveAccuracy = 0;
        aveConsistency = 0;
        tblSummary.setVisibility(View.INVISIBLE);
        tblTable.setVisibility(View.INVISIBLE);
        txtPerfMade.setText("0");
    }


    private void DisplayRecords(List<UserData> list){
        CreateTable(list);
        CreateSummary(list);
        txtNoRecords.setVisibility(View.INVISIBLE);
        tblSummary.setVisibility(View.VISIBLE);
        tblTable.setVisibility(View.VISIBLE);
    }

    private void FetchData(){
        for(UserData user: listUserData){
            if(user.getPose().equalsIgnoreCase("Warrior II Pose")){
                countWarrior++;
                listUserWarriorTwo.add(user);
            } else if(user.getPose().equalsIgnoreCase("Goddess Pose")){
                countGoddess++;
                listUserGoddess.add(user);
            } else if(user.getPose().equalsIgnoreCase("Tree Pose")){
                countTree++;
                listUserTree.add(user);
            }
        }

    }

    private void InsertData(String pose, int accuracy, int consistency){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datetime = dateFormat.format(date);
        Boolean inserted = null;

        try {
            inserted = dbHelper.Insert(pose, datetime, accuracy, consistency);
            if (inserted) {
                System.out.println("Data is inserted");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("SetTextI18n")
    private void CreateTable(List<UserData> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy\nHH:mm:ss");
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f);
        layoutParams.setMargins(0, 0, 0, 0);

        for (UserData data : list) {
            int i = 0;
            TableRow row = new TableRow(this);
            TextView dateTimeTextView =  new TextView(this);
            TextView accuracyTextView = new TextView(this);
            TextView consistencyTextView = new TextView(this);
            ImageView consistencyChange = new ImageView(this);
            ImageView accuracyChange = new ImageView(this);
            LinearLayout accuracyLayout = new LinearLayout(this);
            LinearLayout consistencyLayout = new LinearLayout(this);

            if (list.size() > 1) {
                UserData recent = list.get(i);
                UserData previous = list.get(i + 1);
                setImageResource(consistencyChange, recent.getConsistency(), previous.getConsistency());
                setImageResource(accuracyChange, recent.getAccuracy(), previous.getAccuracy());
            }


            dateTimeTextView.setText(dateFormat.format(data.getDateTime()));
            setTextView(layoutParams, dateTimeTextView, dateFormat.format(data.getDateTime()));
            row.addView(dateTimeTextView);

            setTextView(layoutParams, accuracyTextView, String.valueOf(data.getAccuracy()) + "%");
            setTextView(layoutParams, consistencyTextView, String.valueOf(data.getConsistency()) + "%");

            if (list.indexOf(data) == 0) {
                setLinearLayoutView(accuracyLayout, layoutParams, accuracyTextView, accuracyChange);
                row.addView(accuracyLayout);
            } else {
                row.addView(accuracyTextView);
            }

            if (list.indexOf(data) == 0) {
                setLinearLayoutView(consistencyLayout, layoutParams, consistencyTextView, consistencyChange);
                row.addView(consistencyLayout);
            } else {
                row.addView(consistencyTextView);
            }

            tblTable.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void setImageResource(ImageView imageView, int currentValue, int previousValue) {
        if (currentValue > previousValue) {
            imageView.setImageResource(R.drawable.arrow_up);
        } else if (currentValue < previousValue) {
            imageView.setImageResource(R.drawable.arrow_down);
        } else {
            imageView.setImageResource(R.drawable.equal);
        }
    }

    private void setTextView(TableRow.LayoutParams layoutParams, TextView textView, String text) {
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setPadding(5, 5, 5, 5);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private void setLinearLayoutView(LinearLayout layout, TableRow.LayoutParams layoutParams, TextView textView, ImageView imageView){
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 0, 0, 0);
        layout.setGravity(Gravity.CENTER);
        layout.setClipChildren(false);
        layout.setClipToPadding(false);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        layout.addView(textView);
        layout.addView(imageView, new LinearLayout.LayoutParams(dpToPx(16), dpToPx(16)));
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void CreateSummary(List<UserData> list){
        GetAverage(list);
        GetHighest(list);
        txtAveAccuracy.setText(aveAccuracy + "%");
        txtAveConsistency.setText(aveConsistency + "%");
        txtHighConsistency.setText(maxConsistency + "%");
        txtHighAccuracy.setText(maxAccuracy + "%");
    }

    private void GetHighest(List<UserData> list){
       maxAccuracy = 0;
       maxConsistency=0;
        for (UserData user: list){
            if(user.getConsistency() >= maxConsistency)
                maxConsistency = user.getConsistency();
            if(user.getAccuracy() >= maxAccuracy)
                maxAccuracy = user.getAccuracy();
       }
    }

    private void GetAverage(List<UserData> list){

        aveAccuracy = 0;
        aveConsistency = 0;
        for(UserData user: list){
            aveAccuracy += user.getAccuracy();
            aveConsistency += user.getConsistency();
        }
        aveAccuracy= (int) aveAccuracy / list.size();
        aveConsistency/=list.size();
    }

    private void ConnectToXML() {
        txtNoRecords = findViewById(R.id.no_records);
        tblSummary = (TableLayout) findViewById(R.id.table_summary);
        tblTable = (TableLayout) findViewById(R.id.table_table);
        btnWarriorTwo = findViewById(R.id.button_warrior_two);
        btnGoddess = findViewById(R.id.button_goddess);
        btnTree = findViewById(R.id.button_tree);
        txtPoseName = findViewById(R.id.pose_name);
        txtPerfMade = findViewById(R.id.no_perf_made);
        txtSelectPose = findViewById(R.id.select_pose);
        txtSelectPose.setVisibility(View.VISIBLE);
        txtAveAccuracy = findViewById(R.id.txtAveAccu);
        txtAveConsistency = findViewById(R.id.txtAveCon);
        txtHighAccuracy = findViewById(R.id.txtHighAccu);
        txtHighConsistency = findViewById(R.id.txtHighCon);
    }
}