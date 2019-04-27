/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img.processing;

import com.borisborgobello.jfx.img.BBImgUtils;
import com.borisborgobello.jfx.io.BBFileInout;
import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBGeometry;
import com.borisborgobello.jfx.utils.BBGeometry.BaseBProjection;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBImageSplitter {
    
    // A simple pixel with x,y coordinates and a group it belongs to
    public static class Pixel {
        public Pixel(int x, int y) { this.x = x; this.y = y; }
        public int x, y;
        public int group = -1;
    }
    
    // Contains a group of pixels
    public static final class PixelGroup {
        public ArrayList<Pixel> ps = new ArrayList<>();
        public int groupNumber = 0;
        public int centerX, centerY;
        
        public int minX = -1, minY, maxX, maxY;
        public void generateCenter() {
            for (Pixel p3 : ps) {
                if (minX == -1) {minX = p3.x; maxX = p3.x+1; minY = p3.y; maxY = p3.y+1; }
                minX = Math.min(minX, p3.x);
                minY = Math.min(minY, p3.y);
                maxX = Math.max(maxX, p3.x+1);
                maxY = Math.max(maxY, p3.y+1);
            }
            centerX = minX + (maxX-minX)/2;
            centerY = minY + (maxY-minY)/2;
        }
        
        public StructSlice toSlice() {
            generateCenter();
            StructSlice ss = new StructSlice();
            ss.x = minX;
            ss.y = minY;
            ss.x2 = maxX;
            ss.y2 = maxY;
            return ss;
        }
    }
    
    // A rectangle defined by 2 points (4 absolute coordinates)
    public static class StructSlice {
        public int x, x2, y, y2;
        public int number;

        public StructSlice() {}
        private StructSlice(StructSlice ss1) {
            number = ss1.number;
            x = ss1.x;
            x2 = ss1.x2;
            y = ss1.y;
            y2 = ss1.y2;
        }
        
        public Point getCenter() {
            return new Point(x+(x2-x)/2, y+(y2-y)/2);
        }

        private StructSlice scaledUp(int scale) {
            StructSlice ss = new StructSlice(this);
            ss.scaleUp(scale);
            return ss;
        }
        private void scaleUp(int scale) {
            x*=scale; y*=scale; x2 *=scale; y2*=scale;
        }
        public int w() { return x2-x; }
        public int h() { return y2-y; }

        private StructSlice changeToBase(BaseBProjection b) {
            StructSlice ss = new StructSlice();
            ss.number = number;
            ss.x = b.toBaseBX(x);
            ss.y = b.toBaseBX(y);
            ss.x2 = b.toBaseBY(x2);
            ss.y2 = b.toBaseBY(y2);
            return ss;
        }
        private StructSlice revertFromBase(BaseBProjection b) {
            StructSlice ss = new StructSlice();
            ss.number = number;
            ss.x = b.toBaseAX(x);
            ss.y = b.toBaseAY(y);
            ss.x2 = b.toBaseAX(x2);
            ss.y2 = b.toBaseAY(y2);
            return ss;
        }

        private int distance(StructSlice ss2) { // in  orthonorme classical (not img)
            boolean left = ss2.x2 < x;
            boolean right = x2 < ss2.x;
            boolean bottom = ss2.y2 < y;
            boolean top = y2 < ss2.y;
            
            if (top && left)
                return BBGeometry.distInt(x, y2, ss2.x2, ss2.y); // distance between corners
            else if (left && bottom)
                return BBGeometry.distInt(x, y, ss2.x2, ss2.y2);
            else if (bottom && right)
                return BBGeometry.distInt(x2, y, ss2.x, ss2.y2);
            else if (right && top)
                return BBGeometry.distInt(x2, y2, ss2.x, ss2.y);
            else if (left)
                return x - ss2.x2; // edge distances
            else if (right)
                return ss2.x - x2;
            else if (bottom)
                return y - ss2.y2;
            else if (top)
                return ss2.y - y2;
            else { //            # rectangles intersect
                return 0;
            }
        }
        
        private Boolean isAlignedVertical(StructSlice ss2) { // in  orthonorme classical (not img)
            boolean left = ss2.x2 <= x;
            boolean right = x2 <= ss2.x;
            boolean bottom = ss2.y2 <= y;
            boolean top = y2 <= ss2.y;
            
            if (top && left)
                return null;
            else if (left && bottom)
                return null;
            else if (bottom && right)
                return null;
            else if (right && top)
                return null;
            return top || bottom;
        }

        private void absorb(StructSlice ss2) {
            x = Math.min(x, ss2.x);
            y = Math.min(y, ss2.y);
            x2 = Math.max(x2, ss2.x2);
            y2 = Math.max(y2, ss2.y2);
        }
    }
    
    public static Long toUID(int x, int y, int totalWidth) {
        return x + ((long)y)*totalWidth;
    }
    
    /**
     * Segmentation of all PNG files contained inside inputFolder based on transparency
     * threshold. Each image will be splited into smaller isolated pieces (that
     * had transparency in between). Groups of pixels with size less or equald to
     * minObjectPixels will be ignored
     * Synchronous = Monothread
     * Accepted input files are .png, 
     * 
     * @param inputFolder all PNGs inside this folder are inputs
     * @param transparencyThreshold [0.0->1.0], 0.3 -> pixels with alpha 0 -> 0.3
     * will be considered transparent
     * @param minObjectSizePixels pixel groups smaller than this are ignored
     */
    public static void segmentateByTransparency(
            File inputFolder,
            double transparencyThreshold, 
            long minObjectSizePixels
    )
    {
        if (inputFolder == null || !inputFolder.exists()) return;
        
        final File outputFolder = Paths.get(inputFolder.getParentFile().getAbsolutePath(), inputFolder.getName() + "_gen").toFile();
        outputFolder.mkdir();
        
        for (File fin : inputFolder.listFiles()) { try {
            
            String ext = BBFileInout.getExtensionWithoutDot(fin.getAbsolutePath());
            if (!ext.equalsIgnoreCase("png")) continue;
            
            BufferedImage a1 = ImageIO.read(fin);
            
            int objectNb = 1;
            for (PixelGroup pg : segmentateWithTransparency2(transparencyThreshold, a1)) {
                if (pg.ps.size() < minObjectSizePixels) continue;
                
                File fout = Paths.get(outputFolder.getAbsolutePath(), 
                        String.format("%s-%d.jpg",BBFileInout.extractFilename(fin.getAbsolutePath(), false), objectNb++)).toFile();
                StructSlice ss = pg.toSlice();
                
                ImageIO.write(BBImgUtils.toRGBForJpgSafe(a1.getSubimage(ss.x, ss.y, ss.w(), ss.h()), 
                        javafx.scene.paint.Color.WHITE), "jpg", fout);
            }
        } catch (Exception e) {}}
    }
    
    
    /**
     * Segmentates an image based on transparency threshold
     * @param transparencyTolerance [0.0->1.0], 0.3 -> pixels with alpha 0 -> 0.3
     * will be considered transparent
     * 
     * @param img
     * @return 
     */
    public static ArrayList<PixelGroup> segmentateWithTransparency2(
            double transparencyTolerance, 
            BufferedImage img) {
        
        final int transparency = (int) (transparencyTolerance*255);
        
        // Thresholding
        Pixel[][] matrix = new Pixel[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                int c = img.getRGB(i, j);
                Color cT = new Color(c, true);
                if (cT.getAlpha() <= transparency) {
                    matrix[j][i] = null;
                } else {
                    matrix[j][i] = new Pixel(i, j);
                }
            }
        }
        
        ArrayList<PixelGroup> allGroups = new ArrayList<>();
        int groupNumber = 1;
        
        for (int j = 0; j < img.getHeight(); j++) {
            //ISLog.s(String.format("%d%%", 100*j/img.getHeight()));
            for (int i = 0; i < img.getWidth(); i++) {
                Pixel p = matrix[j][i];
                if (p == null) continue;
                //long uid = toUID(i, j, img.getWidth());
                if (p.group != -1) continue;
                PixelGroup pg = new PixelGroup();
                pg.groupNumber = groupNumber++;
                allGroups.add(pg);
                /*try {
                    findNotTransparentGroup(p, pg, matrix);
                } catch (StackOverflowError e) {*/
                    //ISLog.s("Overflown => fallback on stacked");
                    //pg.ps.clear();
                    //for (Pixel p5 : pg.ps) p5.group = -1;
                    findNotTransparentGroupStacked(p, pg, matrix);
                //}
                
                //for (Pixel p2 : pg.ps) { allAffectedPixels.put(toUID(p2.x, p2.y, img.getWidth()), p2); }
            }
        }
        
        return allGroups;
    }
    
    
    /**
     * Given a matrix, a current group and a starting pixel, recursively discovers
     * new pixels linked to this group
     * This function is VULNERABLE to stackoverflows on big matrix with big objects
     * @param currentPixel
     * @param pg
     * @param matrix 
     */
    public static void findNotTransparentGroup(Pixel currentPixel, PixelGroup pg, Pixel[][] matrix) {
        pg.ps.add(currentPixel);
        currentPixel.group = pg.groupNumber;
        for (int k= currentPixel.y-1; k <= currentPixel.y+1; k++) {
            if (!BBCollections.isInsideY(matrix, k)) continue;
            Pixel[] row2 = matrix[k];
            for (int l = currentPixel.x-1; l <= currentPixel.x+1; l++) {
                if (!BBCollections.isInsideX(matrix, l)) continue;
                Pixel p2 = row2[l];
                if (p2 == null || p2 == currentPixel || p2.group != -1) continue;
                findNotTransparentGroup(p2, pg, matrix);
            }
        }
    }
    
    /**
     * Given a matrix and a pixel group, check currentPixel's neighbours for
     * non empty pixels. If found, repeat the process.
     * This function uses a Deque as custom stack to avoid stack overflow
     * exception. This doesn't protect against OutOfMemory errors ...
     * @param currentPixel
     * @param pg
     * @param matrix 
     */
    public static void findNotTransparentGroupStacked(Pixel currentPixel, PixelGroup pg, Pixel[][] matrix) {
        Deque<Runnable> stack = new LinkedList<>();
        findNotTransparentGroupStacked(stack, currentPixel, pg, matrix);
        while (!stack.isEmpty()) {
            stack.pollLast().run();
        }
    }
    
    
    /**
     * Given a custom stack, a currentPixel, a pixelgroup, a global matrix
     * checks each neighbour for non-emptiness and repeat this function on these
     * non-empty pixels
     * @param stack
     * @param currentPixel
     * @param pg
     * @param matrix 
     */
    public static void findNotTransparentGroupStacked(Deque<Runnable> stack, Pixel currentPixel, PixelGroup pg, Pixel[][] matrix) {
        if (currentPixel.group != -1) return;
        pg.ps.add(currentPixel);
        currentPixel.group = pg.groupNumber;
        for (int k= currentPixel.y-1; k <= currentPixel.y+1; k++) {
            if (!BBCollections.isInsideY(matrix, k)) continue;
            Pixel[] row2 = matrix[k];
            for (int l = currentPixel.x-1; l <= currentPixel.x+1; l++) {
                if (!BBCollections.isInsideX(matrix, l)) continue;
                Pixel p2 = row2[l];
                if (p2 == null || p2 == currentPixel || p2.group != -1) continue;
                stack.add((Runnable) () -> {
                    findNotTransparentGroupStacked(stack, p2, pg, matrix);
                });
            }
        }
    }
}
