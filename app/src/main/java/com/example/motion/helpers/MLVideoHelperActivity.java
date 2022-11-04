package com.example.motion.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.motion.R;
import com.example.motion.helpers.vision.CameraSource;
import com.example.motion.helpers.vision.CameraSourcePreview;
import com.example.motion.helpers.vision.GraphicOverlay;

import java.io.IOException;

public abstract class MLVideoHelperActivity extends AppCompatActivity{
    // Attributes with predefined classes
    private GraphicOverlay graphicOverlay;
    protected CameraSource cameraSource;

    // Attributes with user-defined classes
    private static final int REQUEST_CAMERA = 1001;
    private static final String TAG = "MLVideoHelperActivity";
    private CameraSourcePreview cameraScreen;
    private boolean doingSelectedPose = false;
    private CountDownTimer timerController;
    public String pose;
    private boolean countingDown = false;
    private ImageButton selectedButton = null;

    // Views
    private TextView textPrompt;
    private ImageView imagePromptIcon;
    private TextView textRecordingTimer;
    private TextView textCountdownTimer;
    private ImageButton buttonWarriorTwo;
    private ImageButton buttonGoddess;
    private ImageButton buttonTree;

    // Objects
    public MotionProcessor motionProcessor;

    // Activity's Life Cycle Functions
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_helper);
        connectToXML();
        initializeDisplay();
        instantiateMotionProcessor();
        requestCameraPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSource();
            startCameraSource();
        }
    }

    // User-defined Functions
    public void connectToXML() {
        cameraScreen = findViewById(R.id.camera_screen);
        graphicOverlay = findViewById(R.id.graphic_overlay);
        textPrompt = findViewById(R.id.text_prompt);
        imagePromptIcon = findViewById(R.id.image_prompt_icon);
        textRecordingTimer = findViewById(R.id.text_recording_timer);
        textCountdownTimer = findViewById(R.id.text_countdown_timer);
        buttonWarriorTwo = findViewById(R.id.button_warrior_two);
        buttonGoddess = findViewById(R.id.button_goddess);
        buttonTree = findViewById(R.id.button_tree);
    }

    public void initializeDisplay() {
        textCountdownTimer.setVisibility(View.INVISIBLE);
        buttonWarriorTwo.setBackground(ContextCompat.getDrawable(this, R.drawable.button_normal));
        buttonGoddess.setBackground(ContextCompat.getDrawable(this, R.drawable.button_normal));
        buttonTree.setBackground(ContextCompat.getDrawable(this, R.drawable.button_normal));
    }

    public void instantiateMotionProcessor() {
        motionProcessor = new MotionProcessor(new MotionListener() {
            @Override
            public void verifyUserPose(boolean isDoingSelectedPose) {
                if(isDoingSelectedPose && !countingDown){
                    startCountdown();
                }
            }
        });
    }

    public void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            initSource();
            startCameraSource();
            clickPoseBtn();
        }
    }

    public String selectedButtonToString() {
        if(selectedButton == buttonWarriorTwo) {
            return "Warrior II Pose";
        } else if (selectedButton == buttonGoddess) {
            return "Goddess Pose";
        } else if (selectedButton == buttonTree) {
            return "Tree Pose";
        } else {
            return null;
        }
    }

    public void updatePromptWidget(int icon, String text) {
        imagePromptIcon.setImageResource(icon);
        textPrompt.setText(text);
    } // No more changes until here

    public void selectButton(ImageButton button) {
        if(selectedButton != null) {
            selectedButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_normal));
        }
        selectedButton = button;
        updatePromptWidget(R.drawable.warning_icon, "Perform the " + selectedButtonToString());
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_pressed));

        if(timerController != null) {
            countingDown = false;
            textRecordingTimer.setVisibility(View.INVISIBLE);
            timerController.cancel();
        }
    }

    public void unselectButton(){
        motionProcessor.setSelectedPose("");
        textRecordingTimer.setVisibility(View.INVISIBLE);
        updatePromptWidget(R.drawable.warning_icon, "Select a pose");
        selectedButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_normal));
        selectedButton = null;
    }

    public void showPoseDetectedPrompt() {
        imagePromptIcon.setImageResource(R.drawable.success_icon);
        textPrompt.setText(selectedButtonToString() + "Detected");
    }

    public void startRecordingCountdown() {
        updatePromptWidget(R.drawable.record_icon, "Hold the pose for 15 seconds");
        textRecordingTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                textRecordingTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {}
        }.start();
    }

    public void startCountdown(){
        textCountdownTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(4000, 1000){
            public void onTick(long millisUntilFinished) {
                textCountdownTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                showPoseDetectedPrompt();
                textCountdownTimer.setVisibility(View.INVISIBLE);
                startRecordingCountdown();
                countingDown = true;
            }
        }.start();
    }

    //Actions to do for the buttons
    private void clickPoseBtn() {
        buttonWarriorTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countingDown = false;
                if(selectedButton == buttonWarriorTwo){
                    unselectButton();
                }else{
                    motionProcessor.setSelectedPose("warrior2");
                    selectButton(buttonWarriorTwo);
                }
            }
        });

        buttonGoddess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countingDown = false;
                if(selectedButton == buttonGoddess){
                    unselectButton();
                }else{
                    motionProcessor.setSelectedPose("goddess");
                    selectButton(buttonGoddess);
                }
            }
        });

        buttonTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countingDown = false;
                if(selectedButton == buttonTree){
                    unselectButton();
                }else{
                    motionProcessor.setSelectedPose("tree");
                    selectButton(buttonTree);
                }
            }
        });
    }

    // Pre-defined Camera Functions
    private void initSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        setProcessor();
    }

    protected abstract void setProcessor();

    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (cameraScreen == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                cameraScreen.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}
