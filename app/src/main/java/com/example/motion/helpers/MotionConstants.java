package com.example.motion.helpers;

import java.util.HashMap;
import java.util.Map;

public class MotionConstants {

    // 0 left elbow - 1 right elbow - 2 left shoulder - 3 right shoulder
    // 4 left hip - 5 right hip - 6 left knee -7 right knee

    private static final int[] treeIdealAngles = {143,143,172,170,171,126,165,45};
    private static final int[] goddessIdealAngles = {99,99,94,96,104,105,106,107};
    private static final int[] warrior2IdealAngles = {174,175,95,101,126,101,170,112};
    public static final  Map<String, int[]> idealAnglesMap = new HashMap<>();

    static{
        idealAnglesMap.put("tree", treeIdealAngles);
        idealAnglesMap.put("goddess", goddessIdealAngles);
        idealAnglesMap.put("warrior2", warrior2IdealAngles);
    }

}
