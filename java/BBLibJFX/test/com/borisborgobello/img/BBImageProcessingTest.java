/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.img;

import com.borisborgobello.img.processing.BBImageSplitter;
import com.borisborgobello.utils.BBColor;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author borisborgobello
 */
public class BBImageProcessingTest {
    
    public BBImageProcessingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toUID method, of class BBImageProcessing.
     */
    
    @Test
    public void testToUID() {
        System.out.println("toUID");
        
        assertEquals(BBImageSplitter.toUID(0,0,0), (Long) 0L);
        assertEquals(BBImageSplitter.toUID(10,0,10), (Long) 10L);
        assertEquals(BBImageSplitter.toUID(10,10,10), (Long) 110L);
        assertEquals(BBImageSplitter.toUID(1,3,10), (Long) 31L);
        assertEquals(BBImageSplitter.toUID(7,99,3), (Long) 304L);
    }

    /**
     * Test of segmentateWithTransparency2 method, of class BBImageProcessing.
     */
    @Test
    public void testSegmentateWithTransparency2() {
        System.out.println("segmentateWithTransparency2");
        
        final int colTransparent = BBColor.I_TRANSPARENT;
        final int colTransp30p = new Color(255, 255, 255, (int) (70.0/100*255)).getRGB();
        final int colTransp70p = new Color(255, 255, 255, (int) (30.0/100*255)).getRGB();
        final int colOpaque = new Color(255, 255, 255, 255).getRGB();
        
        // Case 1 - aims at testing pure segmentation
        
        long counter = 0;
        long step1 = 153;
        long step2 = 356;
        long step3 = 652;
        BufferedImage img = new BufferedImage(101, 73, BufferedImage.TYPE_INT_ARGB);
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                //long uid = BBImageProcessing.toUID(i, j, img.getWidth());
                if (counter < step1) img.setRGB(i, j, colTransp30p);
                else if (counter < step2) img.setRGB(i, j, colTransparent);
                else if (counter < step3) img.setRGB(i, j, colTransp30p);
                else img.setRGB(i, j, colTransparent);
                counter++;
            }
        }
        
        double transparencyTolerance = 0.5;
        ArrayList<BBImageSplitter.PixelGroup> result = BBImageSplitter.segmentateWithTransparency2(transparencyTolerance, img);
        assertEquals(result.size(), 2);
        assertEquals(step1, result.get(0).ps.size());
        assertEquals(step3-step2, result.get(1).ps.size());
        
        BBImageSplitter.StructSlice ss = result.get(0).toSlice();
        assertEquals(img.getWidth(), ss.w());
        assertEquals(2, ss.h());
        
        HashMap<Long,BBImageSplitter.Pixel> map = new HashMap<>();
        for (BBImageSplitter.PixelGroup pg : result) {
            long key;
            assertTrue(pg.groupNumber != -1);
            for (BBImageSplitter.Pixel p : pg.ps) {
                assertTrue(p.group != -1);
                key = BBImageSplitter.toUID(p.x, p.y, img.getWidth());
                assertTrue(!map.containsKey(key));
                map.put(key, p);
            }
        }
        
        // Case 2 - aims at testing transparency threshold
        
        transparencyTolerance = 0.69;
        result = BBImageSplitter.segmentateWithTransparency2(transparencyTolerance, img);
        assertEquals(result.size(), 2);
        assertEquals(step1, result.get(0).ps.size());
        assertEquals(step3-step2, result.get(1).ps.size());
        
        ss = result.get(0).toSlice();
        assertEquals(img.getWidth(), ss.w());
        assertEquals(2, ss.h());
        
        // Case 3 - aims at testing transparency threshold
        
        transparencyTolerance = 0.71;
        result = BBImageSplitter.segmentateWithTransparency2(transparencyTolerance, img);
        assertEquals(result.size(), 0);
    } 
}


// TEST
        /*BufferedImage test = BBImgUtils.createBI(img.getWidth(), img.getHeight(), true);
        int black = Color.BLACK.getRGB();
        int white = Color.WHITE.getRGB();
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                test.setRGB(i, j, matrix[j][i] == null ? black : white);
            }
        }
        try { ImageIO.write(test, "png", BBFileInout.getTemporaryFile("test.png")); } catch (Exception e) {}*/
        // TEST
        
        // TEST
        /*BufferedImage test2 = BBImgUtils.createBI(img.getWidth(), img.getHeight(), true);
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                test2.setRGB(i, j, matrix[j][i] == null ? black : 
                        new Color(1.0f*matrix[j][i].group/allGroups.size(), 0.0f,0.0f).getRGB());
            }
        }
        try { ImageIO.write(test2, "png", BBFileInout.getTemporaryFile("test2.png")); } catch (Exception e) {}*/
        // TEST