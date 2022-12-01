package com.example.motion.helpers;
import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.motion.R;

public class MotionManager {

    private Activity activity;
    private MotionProcessor motionProcessor;

    // Views
    private TextView textPrompt;
    private ImageView imagePromptIcon;
    private TextView textRecordingTimer;
    private TextView textCountdownTimer;
    private ImageButton buttonWarriorTwo;
    private ImageButton buttonGoddess;
    private ImageButton buttonTree;

    private CountDownTimer timerController;
    public String pose;
    private boolean countingDown = false;
    private ImageButton selectedButton = null;

    MotionManager(Activity activity, MotionProcessor motionProcessor){
        this.activity = activity;
        this.motionProcessor = motionProcessor;
        connectToXML();
        initializeDisplay();
        clickPoseBtn();
    }

    // Public functions

    public void startCountdown(){
        showPoseDetectedPrompt();
        textCountdownTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(4000, 1000){
            public void onTick(long millisUntilFinished) {
                textCountdownTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {

                textCountdownTimer.setVisibility(View.INVISIBLE);
                startRecordingCountdown();
                countingDown = true;
            }
        }.start();
    }

    public boolean getIsCountingDown(){
        return countingDown;
    }

    public void checkLandmarksPrompt(boolean isVisible){
        if(!isVisible){
            updatePromptWidget(R.drawable.warning_icon, "Ensure all body parts are seen");
        }else{
            performPrompt();
        }
    }

    // Initialization
    private void connectToXML() {
        textPrompt = activity.findViewById(R.id.text_prompt);
        imagePromptIcon = activity.findViewById(R.id.image_prompt_icon);
        textRecordingTimer = activity.findViewById(R.id.text_recording_timer);
        textCountdownTimer = activity.findViewById(R.id.text_countdown_timer);
        buttonWarriorTwo = activity.findViewById(R.id.button_warrior_two);
        buttonGoddess = activity.findViewById(R.id.button_goddess);
        buttonTree = activity.findViewById(R.id.button_tree);
    }

    public void initializeDisplay() {
        textCountdownTimer.setVisibility(View.INVISIBLE);
        buttonWarriorTwo.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        buttonGoddess.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        buttonTree.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
    }


    // Helper Functions
    private String selectedButtonToString() {
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

    private void updatePromptWidget(int icon, String text) {
        imagePromptIcon.setImageResource(icon);
        textPrompt.setText(text);
    } // No more changes until here

    public void performPrompt() {
        updatePromptWidget(R.drawable.warning_icon, "Perform the " + selectedButtonToString());
    }

    private void selectButton(ImageButton button) {
        if(selectedButton != null) {
            selectedButton.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        }
        selectedButton = button;

        button.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_pressed));

        if(timerController != null) {
            countingDown = false;
            textRecordingTimer.setVisibility(View.INVISIBLE);
            timerController.cancel();
        }
    }

    private void unselectButton(){
        motionProcessor.setSelectedPose("");
        textRecordingTimer.setVisibility(View.INVISIBLE);
        updatePromptWidget(R.drawable.warning_icon, "Select a pose");
        selectedButton.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        selectedButton = null;
    }

    private void showPoseDetectedPrompt() {
        imagePromptIcon.setImageResource(R.drawable.success_icon);
        textPrompt.setText(selectedButtonToString() + "Detected");
    }



    private void startRecordingCountdown() {
        updatePromptWidget(R.drawable.record_icon, "Hold the pose for 15 seconds");
        textRecordingTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                textRecordingTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {}
        }.start();
    }

    // Actions to do for the buttons
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
                    performPrompt();
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
                    //performPrompt();
                }
            }
        });
    }


}
