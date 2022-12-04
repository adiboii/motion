package com.example.motion.helpers;

import com.example.motion.helpers.vision.posedetector.Calculations;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;

public class MotionProcessor{
    final public MotionListener listener;
    public boolean isCompleted = false;
    String selectedPose = "";
    boolean isSelectedPose = false;
    public boolean isCountingDown = false;
    public double userAccuracy = 0;
    private double userConsistency = 0;
    Calculations calculations = new Calculations();
    private ArrayList<Pose> poses = new ArrayList<Pose>();
    final private ArrayList<ArrayList<Double>> anglesList = new ArrayList<ArrayList<Double>>();
    final private ArrayList<Double> leftElbowAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightElbowAngles = new ArrayList<Double>();
    final private ArrayList<Double> leftShoulderAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightShouldAngles = new ArrayList<Double>();
    final private ArrayList<Double> leftHipAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightHipAngles = new ArrayList<Double>();
    final private ArrayList<Double> leftKneeAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightKneeAngles = new ArrayList<Double>();

    // Constructor
    public MotionProcessor(MotionListener listener){
        this.listener = listener;
    }


    // Methods
    public boolean isDoingSelectedPose(String pose, double confidenceLevel){

        if(selectedPose.equals(pose)){
            switch(pose){
                case "warrior2" : isSelectedPose = checkPerformance(0.9999, confidenceLevel); break;
                case "goddess" : isSelectedPose = checkPerformance(0.85, confidenceLevel); break;
                case "tree" : isSelectedPose = checkPerformance(0.96, confidenceLevel); break;
                default: isSelectedPose = false;
            }
        }
        return isSelectedPose;
    }

    public void setSelectedPose(String selectedPose){
        this.selectedPose = selectedPose;
    }

    public String getSelectedPose(){
        return selectedPose;
    }


    private boolean checkPerformance(double threshold, double confidenceLevel){
        if(confidenceLevel >= threshold) return true;
        return false;
    }

    // Method to add poses from PoseDetectorProcessor
    // Getting the poses so calculations will only be done after
    // countdown to avoid performance issues
    public void addPoseToList(Pose pose){ poses.add(pose); }

    // After countdown, we get calculated angles and add them
    // to their respective arraylists
    // Once added, call Calculations.calcaulateAccuracy for ArrayList

    public void calculateAccuracy(){
        pushToAnglesArray();
        userAccuracy = calculations.calculateAccuracy(leftElbowAngles);
    }

    // Supposed to be, e add tanan to a single ArrayList
    // para dali nalang ang pag get2
    public void addToArrayList(ArrayList<Double> arrayList){
        // 0 left elbow - 1 right elbow - 2 left shoulder - 3 right shoulder
        // 4 left hip - 5 right hip - 6 left knee -7 right knee
        anglesList.add(arrayList);
    }
    //TODO: CLEAN!
    private void pushToAnglesArray(){

        // Get single pose in Pose List
        // calculate angles for each joint
        for(Pose pose: poses){
            leftElbowAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y));

            rightElbowAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y));

            leftShoulderAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y));

            rightShouldAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y));

            leftHipAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y));

            rightHipAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y));

            leftKneeAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HEEL).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.LEFT_HEEL).getPosition().y));

            rightKneeAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).getPosition().y,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL).getPosition().x,
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL).getPosition().y));
        }
    }
}
