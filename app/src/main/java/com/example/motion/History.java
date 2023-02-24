package com.example.motion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
        boolean isFirst = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy\nHH:mm:ss");
        TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                4f
        );
        TableRow.LayoutParams layoutParams2= new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                4f
        );
        TableRow.LayoutParams layoutParams3 = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                4f
        );
        for(UserData user: list){
            TableRow row = new TableRow(this);

            TextView dateTimeTextView = new TextView(this);
            dateTimeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
            dateTimeTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            dateTimeTextView.setPadding(5, 5, 5, 5);
            dateTimeTextView.setText(dateFormat.format(user.getDateTime()));
            dateTimeTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
            row.addView(dateTimeTextView);

            TextView accuracyTextView = new TextView(this);
            accuracyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
            accuracyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            accuracyTextView.setPadding(5, 5, 5, 5);
            accuracyTextView.setText(String.valueOf(user.getAccuracy()) + "%");
            accuracyTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
            row.addView(accuracyTextView);

            TextView consistencyTextView = new TextView(this);
            consistencyTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
            consistencyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            consistencyTextView.setPadding(5, 5, 5, 5);
            consistencyTextView.setText(String.valueOf(user.getConsistency()) + "%");
            consistencyTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
            row.addView(consistencyTextView);

            tblTable.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
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