package com.example.motion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motion.audio.AudioClassificationActivity;
import com.example.motion.audio.BirdSoundDetectorActivity;
import com.example.motion.image.FlowerIdentificationActivity;
import com.example.motion.image.ImageClassificationActivity;
import com.example.motion.object.DriverDrowsinessDetectionActivity;
import com.example.motion.object.FaceDetectionActivity;
import com.example.motion.object.ObjectDetectionActivity;
import com.example.motion.object.PoseDetectionActivity;
import com.example.motion.text.SpamTextDetectionActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, PoseDetectionActivity.class);
        startActivity(intent);
    }
}