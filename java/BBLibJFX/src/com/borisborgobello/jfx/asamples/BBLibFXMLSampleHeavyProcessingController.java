/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.img.processing.BBImageApacheMLBridge;
import com.borisborgobello.jfx.img.processing.BBImageSplitter;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleHeavyProcessingController extends BBSuperController {

    @Override
    public void initialize(URL url, ResourceBundle rb) {}   
    
    private static class PixelGroup extends ArrayList<Pixel> {
        int id;
    }
    private static class Pixel {
        int x;
        int y;
        int value;
        PixelGroup group = null;
        Pixel(int x, int y, int value) { this.x = x; this.y = y; this.value = value; }
    }
    
    void apacheMLTest() {
        final BBImageApacheMLBridge.BBFilterBase filter = new BBImageApacheMLBridge.FKmeansAp();
        final BBImageApacheMLBridge.BBDistances dist = BBImageApacheMLBridge.BBDistances.DIST_EUCLI;
        final int w = 119, h = 319;
        
        final int K = 15; // desired nb clusters (K means)
        final int fuzzy = 0;
        final int attempts = 3;
        
        ArrayList<Pixel> points = new ArrayList<>(h*w);
        
        // Typical case of an image
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                points.add(new Pixel(i, j, (int) (Math.random()*255)));
            }
        }
        
        ArrayList<BBImageApacheMLBridge.BBFilterGroup<Pixel>> result = filter.doFilter(points, dist, new BBImageApacheMLBridge.PixelConverter<Pixel>() {
            @Override public int x(Pixel t) { return t.x; }
            @Override public int y(Pixel t) { return t.y; }
            @Override public boolean shouldCloneData() { return true; }
            @Override
            public double[] getDistanceComponents(Pixel t) {
                Color c = new Color(t.value);
                return new double[] { c.getRed()/255.0, c.getGreen()/255.0, c.getBlue()/255.0 };
            }
        }, K, fuzzy, attempts);
        
        // All pixels have now been put in K = 15 groups based on euclidean distance of their colors
    }
    
    // Image Splitter
    
    void bbImageSplitter() {
        BBImageSplitter.segmentateByTransparency(new File("./temp"), 0.5, 10);
    }
    
}
