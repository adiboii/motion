package com.example.motion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motion.object.PoseDetectionActivity;

public class MainActivity extends AppCompatActivity {
    Button btnStart;
    Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openPoseDetectionActivity();
            }
        });
        btnHistory = findViewById(R.id.btn_history);

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistoryActivity();
            }
        });
    }

    public void openPoseDetectionActivity(){
        // Automatically redirects to Camera Screen
        Intent intent = new Intent(this, PoseDetectionActivity.class);
        startActivity(intent);
    }

    public void openHistoryActivity(){
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }

}