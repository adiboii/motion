package com.example.motion.helpers;

public class PoseClassificationChecker {
    String selectedPose;
    boolean isSelectedPose;
    public PoseClassificationChecker(){}

    public boolean isSelectedPose(String pose){

        if(pose.equals(selectedPose))
            isSelectedPose = true;
        else
            isSelectedPose = false;

        return isSelectedPose;
    }

    public void setSelectedPose(String selectedPose){
        this.selectedPose = selectedPose;
    }

    public String getPose(){
        return selectedPose;
    }

    public boolean startRecord(){
        return isSelectedPose;
    }
}
