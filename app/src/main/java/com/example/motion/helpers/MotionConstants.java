package com.example.motion.helpers;

import java.util.HashMap;
import java.util.Map;

public class MotionConstants {

    // 0 left elbow - 1 right elbow - 2 left shoulder - 3 right shoulder
    // 4 left hip - 5 right hip - 6 left knee -7 right knee

    private static final int[] treeIdealAngles = {165,165,165,165,175,15,175,25};
    private static final int[] goddessIdealAngles = {175,175,95,95,75,135,90,170};
    private static final int[] warrior2IdealAngles = {100,100,100,100,100,100,100,100};
    public static final  Map<String, int[]> idealAnglesMap = new HashMap<>();

    static{
        idealAnglesMap.put("tree", treeIdealAngles);
        idealAnglesMap.put("goddess", goddessIdealAngles);
        idealAnglesMap.put("warrior2", warrior2IdealAngles);
    }

}
