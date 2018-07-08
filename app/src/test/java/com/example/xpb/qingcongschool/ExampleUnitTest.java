package com.example.xpb.qingcongschool;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format( new Date());
        System.out.println("当前时间："+current);
        assertEquals(4, 2 + 2);
    }

}