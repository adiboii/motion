package com.example.motion.helpers;

import com.example.motion.helpers.vision.posedetector.Calculations;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;

public class MotionProcessor{
    String selectedPose = "null";
    final public MotionListener listener;
    public boolean isCompleted = false;
    public boolean isSelectedPose = false;
    public boolean isCountingDown = false;

    public double userAccuracy = 0;
    public double userConsistency = 0;
    final Calculations calculations = new Calculations();

    private ArrayList<Pose> poses = new ArrayList<Pose>();
    public ArrayList<Double> landmarkAccuracies = new ArrayList<Double>();
    public ArrayList<Double> landmarkConsistencies = new ArrayList<>();
    private ArrayList<Double> leftElbowAngles = new ArrayList<Double>();
    private ArrayList<Double> rightElbowAngles = new ArrayList<Double>();
    private ArrayList<Double> leftShoulderAngles = new ArrayList<Double>();
    private ArrayList<Double> rightShoulderAngles = new ArrayList<Double>();
    private ArrayList<Double> leftHipAngles = new ArrayList<Double>();
    private ArrayList<Double> rightHipAngles = new ArrayList<Double>();
    private ArrayList<Double> leftKneeAngles = new ArrayList<Double>();
    private ArrayList<Double> rightKneeAngles = new ArrayList<Double>();

    private String detectedPose;

    // Constructor
    public MotionProcessor(MotionListener listener){
        this.listener = listener;
    }

    // Methods

    // checks whether provided pose is
    // the same as the selected pose
    public boolean isDoingSelectedPose(String pose, double confidenceLevel){

        detectedPose = pose;

        if(pose.equals("warrior2-left") || pose.equals("warrior2-right")){
            pose = "warrior2";
        }

        if(pose.equals("tree-left") || pose.equals("tree-right")){
            pose = "tree";
        }

        if(selectedPose.equals(pose)){
            switch(pose){
                case "warrior2" : isSelectedPose = checkPerformance(0.9999, confidenceLevel); break;
                case "goddess" : isSelectedPose = checkPerformance(0.85, confidenceLevel); break;
                case "tree" : isSelectedPose = checkPerformance(0.90, confidenceLevel); break;
                default: isSelectedPose = false;
            }
        }
        return isSelectedPose;
    }

    public void setSelectedPose(String selectedPose){
        this.selectedPose = selectedPose;
    }

    // checks if confidence level is within threshold
    // to make sure user is at least trying properly doing the pose
    private boolean checkPerformance(double threshold, double confidenceLevel){
        if(confidenceLevel >= threshold) return true;
        return false;
    }

    // Method to add poses from PoseDetectorProcessor
    // Getting the poses so calculations will only be done after
    // countdown to avoid performance issues
    public void addPoseToList(Pose pose){ poses.add(pose); }

    public void clearArrays(){
        userAccuracy = 0;
        userConsistency = 0;
        poses.clear();
        landmarkAccuracies.clear();
        landmarkConsistencies.clear();
        leftElbowAngles.clear();
        rightElbowAngles.clear();
        leftShoulderAngles.clear();
        rightShoulderAngles.clear();
        leftHipAngles.clear();
        rightHipAngles.clear();
        leftKneeAngles.clear();
        rightKneeAngles.clear();
    }

    private void calculateLandmarkAngles(){
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

    public void gradeUserPerformance(){
        calculateLandmarkAngles();
        // accuracy grading for individual angles
        landmarkAccuracies.add(calculations.calculateAccuracy(rightElbowAngles, MotionConstants.idealAnglesMap.get(detectedPose)[1]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftShoulderAngles, MotionConstants.idealAnglesMap.get(detectedPose)[2]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightShoulderAngles, MotionConstants.idealAnglesMap.get(detectedPose)[3]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftHipAngles, MotionConstants.idealAnglesMap.get(detectedPose)[4]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightHipAngles, MotionConstants.idealAnglesMap.get(detectedPose)[5]));
        landmarkAccuracies.add(calculations.calculateAccuracy(leftKneeAngles, MotionConstants.idealAnglesMap.get(detectedPose)[6]));
        landmarkAccuracies.add(calculations.calculateAccuracy(rightKneeAngles, MotionConstants.idealAnglesMap.get(detectedPose)[7]));

        // consistency grading for individual angles
        landmarkConsistencies.add(calculations.calculateConsistency(leftElbowAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(rightElbowAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(leftShoulderAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(rightShoulderAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(leftHipAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(rightHipAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(leftKneeAngles));
        landmarkConsistencies.add(calculations.calculateConsistency(rightKneeAngles));
        totalAccuracy();
        totalConsistency();
    }

    public void totalAccuracy(){
        userAccuracy = calculations.totalAccuracy(landmarkAccuracies);
    }

    public void totalConsistency(){ userConsistency = calculations.totalConsistency(landmarkConsistencies); }

}
