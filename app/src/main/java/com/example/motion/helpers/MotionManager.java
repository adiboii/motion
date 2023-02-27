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

import com.example.motion.DBHelper;
import com.example.motion.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView resultsMessage;
    private ImageView resultsIcon;
    private ProgressBar consistencyProgressBar;
    private ProgressBar accuracyProgressBar;
    private TextView accuracy;
    private TextView consistency;

    private CountDownTimer timerController;
    public String pose;
    public boolean stopCountdown = false;
    private ImageButton selectedButton = null;

    DBHelper dbHelper;


    MotionManager(Activity activity, MotionProcessor motionProcessor){
        this.activity = activity;
        this.motionProcessor = motionProcessor;
        connectToXML();
        initializeDisplay();
        clickPoseBtn();
        dbHelper = new DBHelper(activity);
    }

    // Public functions

    public void startCountdown(){
        motionProcessor.isCountingDown = true;
        textCountdownTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(4000, 1000){
            public void onTick(long millisUntilFinished) {
                textCountdownTimer.setText("" + millisUntilFinished / 1000);

            }
            public void onFinish() {
                textCountdownTimer.setVisibility(View.INVISIBLE);
                startRecordingCountdown();

            }
        }.start();
    }

    private void startRecordingCountdown() {
        updatePromptWidget(R.drawable.record_icon, "Hold the pose for 15 seconds");
        textCountdownTimer.setVisibility(View.VISIBLE);
        timerController = new CountDownTimer(16000, 1000) {
            public void onTick(long millisUntilFinished) {
                textCountdownTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                motionProcessor.isCompleted = true;
                motionProcessor.isCountingDown = false;
                motionProcessor.gradeUserPerformance();
                updatePromptWidget(R.drawable.warning_icon, "Calculating Angles");
                timerController.cancel();
                textCountdownTimer.setVisibility(View.INVISIBLE);
                showResults();
            }
        }.start();
    }

    public void stopCountdown(boolean isVisible){
        stopCountdown = true;
        timerController.cancel();
        textCountdownTimer.setVisibility(View.INVISIBLE);
        textRecordingTimer.setVisibility(View.INVISIBLE);
        motionProcessor.isCountingDown = false;
        motionProcessor.clearArrays();
        checkLandmarksPrompt(isVisible);
        stopCountdown = false;
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
        resultsIcon = activity.findViewById(R.id.results_icon);
        resultsMessage = activity.findViewById(R.id.results_message);
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
        textPrompt.setVisibility(View.INVISIBLE);
        imagePromptIcon.setVisibility(View.INVISIBLE);
        resultsModal.setVisibility(View.VISIBLE);
        textRecordingTimer.setVisibility(View.INVISIBLE);
        setResultMessage();
        int scoreUserAccuracy = getScoreUserAccuracy();
        int scoreUserConsistency = getScoreUserConsistency();
        accuracyProgressBar.setProgress(scoreUserAccuracy);
        consistencyProgressBar.setProgress(scoreUserConsistency);
        accuracy.setVisibility(View.VISIBLE);
        consistency.setVisibility(View.VISIBLE);
        accuracy.setText("" + scoreUserAccuracy);
        consistency.setText("" + scoreUserConsistency);
        System.out.println("selected: " + selectedButtonToString());
        InsertData(selectedButtonToString(),scoreUserAccuracy,scoreUserConsistency);
    }

    private int getScoreUserConsistency() {
        int scoreUserConsistency = 0;
        if(motionProcessor.userConsistency > 0 || !Double.isNaN(motionProcessor.userConsistency))
            scoreUserConsistency = (int) motionProcessor.userConsistency;
        return scoreUserConsistency;
    }

    private int getScoreUserAccuracy() {
        int scoreUserAccuracy = 0;
        if(motionProcessor.userAccuracy > 0 || !Double.isNaN(motionProcessor.userAccuracy))
            scoreUserAccuracy = (int) motionProcessor.userAccuracy;
        return scoreUserAccuracy;
    }

    private void setResultMessage() {
        String message = "";
        double resultScore = (motionProcessor.userAccuracy + motionProcessor.userConsistency)/2;
        int icon = R.drawable.trophy;
        if(resultScore < 25){
            message = "Getting started!";
            icon = R.drawable.fire;
        } else if(resultScore < 50){
            message = "On a roll!";
            icon = R.drawable.dice;
        } else if (resultScore < 75) {
            message= "True Yoga Champ!";
            icon = R.drawable.prize;
        } else {
             message = "Yoga Master!";
        }

        resultsIcon.setImageResource(icon);
        resultsMessage.setText(message);
    }

    private void retry(){
        motionProcessor.clearArrays();
        resultsModal.setVisibility(View.INVISIBLE);
        accuracyProgressBar.setProgress(0);
        consistencyProgressBar.setProgress(0);
        motionProcessor.isCompleted = false;
        textPrompt.setVisibility(View.VISIBLE);
        imagePromptIcon.setVisibility(View.VISIBLE);
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
            motionProcessor.isCountingDown = false;
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

    public void showPoseDetectedPrompt() {
        imagePromptIcon.setImageResource(R.drawable.success_icon);
        textPrompt.setText(selectedButtonToString() + " Detected");
    }



    // Actions to do for the buttons
    private void clickPoseBtn() {
        buttonWarriorTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopCountdown = false;
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
                motionProcessor.isCountingDown = false;
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
                motionProcessor.isCountingDown = false;
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


}
