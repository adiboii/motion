package com.example.motion;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.motion.helpers.vision.posedetector.Calculations;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalculateUnitTest {
    Calculations solve = new Calculations();

    @Test
    public void testCalculateAngles_isCorrect() {
        //Arrange
//        double fpX = 166.49829;
//        double fpY = 69.24819;
//        double mpX =188.899;
//        double mpY=93.46699;
//        double lpX = 193.56766;
//        double lpY =  166.90042;
        double fpX = 166.50;
        double fpY = 69.25;
        double mpX =188.90;
        double mpY=93.47;
        double lpX = 193.57;
        double lpY =  166.90;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(140.87,result,0.01);
    }

    @Test
    public void testCalculateAngles2_isCorrect(){
        //Arrange
//        double fpX = 180.5192;
//        double fpY = 120.349625;
//        double mpX =193.67575;
//        double mpY= 252.74133;
//        double lpX = 155.28427;
//        double lpY = 344.19778;
        double fpX = 180.52;
        double fpY = 120.35;
        double mpX =193.68;
        double mpY= 252.74;
        double lpX = 155.28;
        double lpY = 344.20;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(151.55,result,0.01);
    }

    @Test
    public void testCalculateAngles3_isWrong(){
        //Arrange
        double fpX = 0;
        double fpY = 0;
        double mpX = 0;
        double mpY= 0;
        double lpX = 0;
        double lpY = 0;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(0,result,0.001);
    }

    @Test
    public void testCalculateAngles4_isWrong(){
        //Arrange
        double fpX = 120.55;
        double fpY = 252.93;
        double mpX = 118.74;
        double mpY= 353.87;
        double lpX = 113.30;
        double lpY = -244;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(1.54,result,0.01);
    }
}