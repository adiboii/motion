package com.example.motion.helpers;
import android.app.Activity;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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


    private Button buttonRetry;
    private LinearLayout resultsModal;
    private ProgressBar consistencyProgressBar;
    private ProgressBar accuracyProgressBar;
    private TextView accuracy;
    private TextView consistency;

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
                //TODO: use only one countingDown variable
                countingDown = true;
                motionProcessor.isCountingDown = true;
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
        buttonRetry = activity.findViewById(R.id.button_retry);
        resultsModal = activity.findViewById(R.id.results_modal);
        consistencyProgressBar = activity.findViewById(R.id.consistency_progress_bar);
        accuracyProgressBar = activity.findViewById(R.id.accuracy_progress_bar);
        accuracy = activity.findViewById(R.id.accuracy_text);
        consistency = activity.findViewById(R.id.consistency_text);
    }

    public void initializeDisplay() {
        textCountdownTimer.setVisibility(View.INVISIBLE);
        buttonWarriorTwo.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        buttonGoddess.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        buttonTree.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
    }


    // Helper Functions

    private void showResults(){
        resultsModal.setVisibility(View.VISIBLE);
        accuracyProgressBar.setProgress((int) motionProcessor.userAccuracy);
        consistencyProgressBar.setProgress((int) motionProcessor.userConsistency);
        accuracy.setVisibility(View.VISIBLE);
        consistency.setVisibility(View.VISIBLE);
        accuracy.setText(String.format("%.2f", Double.isNaN(motionProcessor.userAccuracy) ? 0 : motionProcessor.userAccuracy));
        consistency.setText(String.format("%.2f", Double.isNaN(motionProcessor.userConsistency) ? 0 : motionProcessor.userConsistency));
    }

    private void retry(){
        motionProcessor.clearArrays();
        resultsModal.setVisibility(View.INVISIBLE);
        accuracyProgressBar.setProgress(0);
        consistencyProgressBar.setProgress(0);
        motionProcessor.isCompleted = false;
        unselectButton();
    }

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
        motionProcessor.setSelectedPose("null");
        textRecordingTimer.setVisibility(View.INVISIBLE);
        updatePromptWidget(R.drawable.warning_icon, "Select a pose");
        selectedButton.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_normal));
        motionProcessor.selectedPose = "null";
        selectedButton = null;
    }

    private void showPoseDetectedPrompt() {
        imagePromptIcon.setImageResource(R.drawable.success_icon);
        textPrompt.setText(selectedButtonToString() + " Detected");
    }

    private void startRecordingCountdown() {
        updatePromptWidget(R.drawable.record_icon, "Hold the pose for 15 seconds");
        textRecordingTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                textRecordingTimer.setText("" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                motionProcessor.isCompleted = true;
                motionProcessor.isCountingDown = false;
                motionProcessor.gradeUserPerformance();
                updatePromptWidget(R.drawable.warning_icon, "Calculating Angles");
                timerController.cancel();
                showResults();
            }
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

        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    retry();
                }
            });
    }


}
