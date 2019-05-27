/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img;

import com.borisborgobello.jfx.img.processing.BBImageNeighbourReporting;
import com.borisborgobello.jfx.img.processing.BBImageSegmentor;
import com.borisborgobello.jfx.img.processing.BBImageSplitter;
import com.borisborgobello.jfx.img.processing.BBImageSplitter.Pixel;
import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBColor;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
        
        assertEquals(BBCollections.toUID(0,0,0), (Long) 0L);
        assertEquals(BBCollections.toUID(10,0,10), (Long) 10L);
        assertEquals(BBCollections.toUID(10,10,10), (Long) 110L);
        assertEquals(BBCollections.toUID(1,3,10), (Long) 31L);
        assertEquals(BBCollections.toUID(7,99,3), (Long) 304L);
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
        ArrayList<BBImageSplitter.PixelGroup> result = BBImageSplitter.segmentateWithTransparency(transparencyTolerance, img, BBImageSegmentor.Mode.SAFE_STACKED);
        assertEquals(2, result.size());
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
                key = BBCollections.toUID(p.x, p.y, img.getWidth());
                assertTrue(!map.containsKey(key));
                map.put(key, p);
            }
        }
        
        // Case 2 - aims at testing transparency threshold
        
        transparencyTolerance = 0.69;
        result = BBImageSplitter.segmentateWithTransparency(transparencyTolerance, img, BBImageSegmentor.Mode.SAFE_STACKED);
        assertEquals(result.size(), 2);
        assertEquals(step1, result.get(0).ps.size());
        assertEquals(step3-step2, result.get(1).ps.size());
        
        ss = result.get(0).toSlice();
        assertEquals(img.getWidth(), ss.w());
        assertEquals(2, ss.h());
        
        // Case 3 - aims at testing transparency threshold
        
        transparencyTolerance = 0.71;
        result = BBImageSplitter.segmentateWithTransparency(transparencyTolerance, img, BBImageSegmentor.Mode.SAFE_STACKED);
        assertEquals(result.size(), 0);
    } 
    
    private static class Pix { 
        final int x, y; 
        final Color c;
        final int group;
        public Pix(int x, int y, Color c) {
            this(x,y,c, -1);
        }
        public Pix(int x, int y, Color c, int group) {
            this.x = x;
            this.y = y;
            this.c = c;
            this.group = group;
        }
 }
    
    @Test
    public void testSegmentor() {
        System.out.println("testSegmentor");
        final int w = 113;
        final int h = 201;
        final Pix[][] matrix = new Pix[h][w];
        final BBImageSegmentor.Segmentor<Pix> seg = new BBImageSegmentor.Segmentor<Pix>() {
            @Override public int x(Pix t) { return t.x; }
            @Override public int y(Pix t) { return t.y; }
            @Override public boolean shouldUnite(Pix s1, Pix s2) {
                if (s1 != null) { return s1.c == s2.c; } 
                else return s1 == s2;
            }
        };
        
        ArrayList<ArrayList<Pix>> result1, result2;
        
        // random doting
        for (int i = 0; i < 4; i++) {
            AtomicInteger nbDots = new AtomicInteger(0);
            BBCollections.forEach(matrix, w, h, (Integer x, Integer y, Pix unused) -> {
                boolean candidate = (int) (Math.random() + 0.5) == 1;
                if (candidate) {
                    nbDots.incrementAndGet();
                    matrix[y][x] = new Pix(x,y,Color.WHITE);
                } else matrix[y][x] = null;
            });
            
            result1 = BBImageSegmentor.segmentate(matrix, seg, BBImageSegmentor.Mode.SAFE_STACKED);
            //result2 = BBImageSegmentor.segmentate(matrix, seg, BBImageSegmentor.Mode.FAST_RECURSIVE);
            
            int result1Qty = 0;
            //int result2Qty = 0;
            for (ArrayList<Pix> group : result1) { result1Qty+= group.size(); }
            //for (ArrayList<Pix> group : result2) { result2Qty+= group.size(); }
            
            assertEquals(nbDots.get(), result1Qty);
            //assertEquals(nbDots, result2Qty);
            //assertEquals(result1.size(), result2.size());
        }
        
        // prepared case
        long counter = 0;
        long step1 = 153;
        long step2 = 356;
        long step3 = 652;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                //long uid = BBImageProcessing.toUID(i, j, img.getWidth());
                if (counter < step1) matrix[j][i] = new Pix(i,j,Color.WHITE);
                else if (counter < step2) matrix[j][i] = new Pix(i,j,Color.BLACK);
                else if (counter < step3) matrix[j][i] = new Pix(i,j,Color.BLUE);
                else matrix[j][i] = null;
                counter++;
            }
        }
        
        result1 = BBImageSegmentor.segmentate(matrix, seg, BBImageSegmentor.Mode.SAFE_STACKED);
        result2 = BBImageSegmentor.segmentate(matrix, seg, BBImageSegmentor.Mode.FAST_RECURSIVE);

        int result1Qty = 0;
        int result2Qty = 0;
        for (ArrayList<Pix> group : result1) { result1Qty+= group.size(); }
        for (ArrayList<Pix> group : result2) { result2Qty+= group.size(); }

        assertEquals(step3, result1Qty);
        assertEquals(step3, result2Qty);
        assertEquals(3, result1.size());
        assertEquals(result1.size(), result2.size());
    }
    
    
    @Test
    public void testNeighbourReporting() {
        System.out.println("testNeigh");
        final int w = 20, h = 30;
        final Pix[][] matrix = new Pix[h][w];
        
        BBCollections.clear(matrix);
        
        matrix[2][3] = new Pix(3, 2, Color.WHITE, 1);
        matrix[3][3] = new Pix(3, 3, Color.WHITE, 1);
        matrix[3][4] = new Pix(4, 3, Color.WHITE, 1);
        matrix[4][4] = new Pix(4, 4, Color.BLACK, 2);
        matrix[4][2] = new Pix(2, 4, Color.BLACK, 3);
        matrix[4][3] = new Pix(3, 4, Color.GREEN, 2);
        matrix[5][1] = new Pix(1, 5, Color.GREEN, 2);
        
        BBImageNeighbourReporting.BBIndexValueExtractor extractor = new BBImageNeighbourReporting.BBIndexValueExtractor<Pix,Integer>() {
            @Override public int x(Pix t) { return t.x; }
            @Override public int y(Pix t) { return t.y; }
            @Override public boolean areIdentical(Pix t1, Pix t2) {
                if (t1 != null && t2 != null) { return t1.c == t2.c; }
                else if (t1 == null && t2 == null) return true;
                return false;
            }
            @Override public Integer value(Pix t) { return t.c.getRGB(); }
            @Override public Integer group(Pix t) { return t.group; }
        };
        
        BBImageNeighbourReporting.BBNeighbourReportGeneric report = 
                BBImageNeighbourReporting.findIdenticalNeighboursGeneric(matrix, matrix[3][3], extractor, null);
        
        assertEquals(2, report.identical);
        assertEquals(3, report.different);
        assertEquals(false, report.cornerIdent);
        assertEquals(2, (int)report.getOverwhelmingGroup(1));
        assertEquals(1, (int)report.getOverwhelmingGroup(-1));
        assertEquals(Color.BLACK.getRGB(), (int)report.getOverwhelmingDifferentDMC());
        assertEquals(3, report.groups.size());
        assertEquals(2, report.histo.size());
        
        report = BBImageNeighbourReporting.findIdenticalNeighboursGeneric(matrix, matrix[4][2], extractor, report);
        
        assertEquals(0, report.identical);
        assertEquals(3, report.different);
        assertEquals(false, report.cornerIdent);
        assertEquals(2, (int)report.getOverwhelmingGroup(3));
        assertEquals(Color.GREEN.getRGB(), (int)report.getOverwhelmingDifferentDMC());
        assertEquals(2, report.groups.size());
        assertEquals(2, report.histo.size());
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