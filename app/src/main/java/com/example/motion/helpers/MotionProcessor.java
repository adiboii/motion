package com.example.motion.helpers;

public class MotionProcessor {



    String selectedPose;
    boolean isSelectedPose;
    final public MotionListener listener;
    public MotionProcessor(MotionListener listener){
        this.listener = listener;
    }

    // Methods
    public boolean doingSelectedPose(String pose){

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

}
