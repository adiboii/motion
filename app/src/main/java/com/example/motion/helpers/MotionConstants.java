package com.example.motion.helpers;

import java.util.HashMap;
import java.util.Map;

public class MotionConstants {

    // 0 left elbow - 1 right elbow - 2 left shoulder - 3 right shoulder
    // 4 left hip - 5 right hip - 6 left knee -7 right knee

    public int[] treeIdealAngles = {165,165,165,165,175,15,175,25};
    public int[] goddessIdealAngles = {175,175,95,95,75,135,90,170};
    public int[] warrior2IdealAngles = {100,100,100,100,100,100,100,100};
    public Map<String, int[]> idealAnglesMap = new HashMap<>();


    MotionConstants(){
        idealAnglesMap.put("tree", treeIdealAngles);
        idealAnglesMap.put("goddess", goddessIdealAngles);
        idealAnglesMap.put("warrior2", warrior2IdealAngles);
    }

}
