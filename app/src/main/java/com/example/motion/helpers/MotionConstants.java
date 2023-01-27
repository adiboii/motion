package com.example.motion.helpers;

import java.util.HashMap;
import java.util.Map;

public class MotionConstants {

    // 0 left elbow - 1 right elbow - 2 left shoulder - 3 right shoulder
    // 4 left hip - 5 right hip - 6 left knee -7 right knee

    private static final int[] treeLeftIdealAngles = {145,147,172,172,121,176,31,175};
    private static final int[] treeRightIdealAngles = {145,144,172,171,176,122,175,33};
    private static final int[] goddessIdealAngles = {98,98,93,95,105,106,107,109};
    private static final int[] warrior2LeftIdealAngles = {175,175,103,94,96,127,104,176};
    private static final int[] warrior2RightIdealAngles = {175,175,94,102,127,97,176,106};

    public static final  Map<String, int[]> idealAnglesMap = new HashMap<>();

    static{
        idealAnglesMap.put("tree-left", treeLeftIdealAngles);
        idealAnglesMap.put("tree-right",treeRightIdealAngles);
        idealAnglesMap.put("goddess", goddessIdealAngles);
        idealAnglesMap.put("warrior2-left", warrior2LeftIdealAngles);
        idealAnglesMap.put("warrior2-right",warrior2RightIdealAngles);
    }

}
