package com.example.motion.helpers;

public class MotionProcessor {
    final public MotionListener listener;
    String selectedPose = "";
    boolean isSelectedPose = false;

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

}
