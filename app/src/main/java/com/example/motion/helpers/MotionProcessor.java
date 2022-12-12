package com.example.motion.helpers;

import com.example.motion.helpers.vision.posedetector.Calculations;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;

public class MotionProcessor{
    final public MotionListener listener;

    public boolean isCompleted = false;
    String selectedPose = "";
    boolean isSelectedPose = false;
    public boolean isCountingDown = false;
    public ArrayList<Double> landmarkAccuracies = new ArrayList<Double>();
    public ArrayList<Double> landmarkConsistencies = new ArrayList<>();
    public double userAccuracy = 0;
    public double userConsistency = 0;
    Calculations calculations = new Calculations();
    final private MotionConstants motionConstants = new MotionConstants();
    private ArrayList<Pose> poses = new ArrayList<Pose>();
    final private ArrayList<Double> leftElbowAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightElbowAngles = new ArrayList<Double>();
    final private ArrayList<Double> leftShoulderAngles = new ArrayList<Double>();
    final private ArrayList<Double> rightShoulderAngles = new ArrayList<Double>();
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
    // Once added, call Calculations.calculateAccuracy for ArrayList

    public void calculateAccuracy(){
        pushToAnglesArray();
        landmarkAccuracies.add(calculations.calculateAccuracy(leftElbowAngles, motionConstants.idealAnglesMap.get(selectedPose)[0]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightElbowAngles, motionConstants.idealAnglesMap.get(selectedPose)[1]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftShoulderAngles, motionConstants.idealAnglesMap.get(selectedPose)[2]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightShoulderAngles, motionConstants.idealAnglesMap.get(selectedPose)[3]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftHipAngles, motionConstants.idealAnglesMap.get(selectedPose)[4]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightHipAngles, motionConstants.idealAnglesMap.get(selectedPose)[5]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftKneeAngles, motionConstants.idealAnglesMap.get(selectedPose)[6]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightKneeAngles, motionConstants.idealAnglesMap.get(selectedPose)[7]));

        landmarkConsistencies.add(calculations.calculateAngleConsistency(leftElbowAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(rightElbowAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(leftShoulderAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(rightShoulderAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(leftHipAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(rightHipAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(leftKneeAngles));
        landmarkConsistencies.add(calculations.calculateAngleConsistency(rightKneeAngles));
    }

    public void totalAccuracy(){
        userAccuracy = calculations.totalAccuracy(landmarkAccuracies);
    }

    public void totalConsistency(){
        userConsistency = calculations.totalConsistency(landmarkConsistencies);
    }

    //TODO: CLEAN!
    private void pushToAnglesArray(){
        // Get single pose in Pose List
        // calculate angles for each joint
        for(Pose pose: poses){
            leftElbowAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST),
                    pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)));

            rightElbowAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)));

            leftShoulderAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP)));

            rightShoulderAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)));

            leftHipAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)));

            rightHipAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HIP),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)));

            leftKneeAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    pose.getPoseLandmark(PoseLandmark.LEFT_KNEE),
                    pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)));

            rightKneeAngles.add(calculations.calculateAngles(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE),
                    pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
           ));
        }
    }
}
