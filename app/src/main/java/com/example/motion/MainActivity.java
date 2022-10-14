package com.example.motion;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motion.object.PoseDetectionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Automatically redirects to Camera Screen
        Intent intent = new Intent(this, PoseDetectionActivity.class);
        startActivity(intent);
    }
}