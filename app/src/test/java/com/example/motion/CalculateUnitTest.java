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
    @Test
    public void testCalculateAngles_isCorrect() {
        //Arrange
        Calculations solve = new Calculations();
        double fpX = 166.49829;
        double fpY = 69.24819;
        double mpX =188.899;
        double mpY=93.46699;
        double lpX = 193.56766;
        double lpY =  166.90042;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(140.87109077541606,result,0.001);
    }

    @Test
    public void testCalculateAngles2_isCorrect(){
        //Arrange
        Calculations solve = new Calculations();
        double fpX = 180.5192;
        double fpY = 120.349625;
        double mpX =193.67575;
        double mpY= 252.74133;
        double lpX = 155.28427;
        double lpY = 344.19778;

        //Act
        double result = solve.calculateAngles(fpX,fpY,mpX,mpY,lpX,lpY);

        //Assert
        assertEquals(151.55317749710645,result,0.001);
    }
}