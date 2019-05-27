/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img.processing;

import com.borisborgobello.jfx.utils.BBCollections;
import static com.borisborgobello.jfx.utils.BBCollections.isInside;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author borisborgobello
 */
public class BBImageSegmentor {
    
    public static enum Mode { FAST_RECURSIVE, SAFE_STACKED }
    
    /**
     * Generic interface sourcing X,Y, unite based on T
     * @param <T> usually a Pixel having X,Y coordinates
     */
    public static interface Segmentor<T> {
        public abstract int x(T t);
        public abstract int y(T t);
        public abstract boolean shouldUnite(T s1, T s2);
    }
    
    /**
     * Segmentates a 2D matrix based on segmentor parameter
     * @param <T> usually a pixel
     * @param matrix matrix of T
     * @param seg returns X,Y and groupability of two T
     * @param mode recursive can be risky (stack overflow) on matrices with big potential groups (like full white)
     * @return list of list (groups) of pixels
     */
    public static <T> ArrayList<ArrayList<T>> segmentate(T[][] matrix, Segmentor seg, Mode mode) {
        //TreeMap<Long, T> allAffectedSquare = new TreeMap<>();
        Boolean[][] matrixAffected = new Boolean[matrix.length][matrix[0].length];
        BBCollections.initMatrixWith(matrixAffected, false);
        
        ArrayList<ArrayList<T>> allGroups = new ArrayList<>();
        for (int j = 0; j < matrix.length; j++) {
            for (int i = 0; i < matrix[0].length; i++) {
                if (!isInside(matrix, i,j)) continue;
                if (matrixAffected[j][i]) continue;

                T ms = matrix[j][i];
                if (ms == null) continue;
                if (seg.x(ms) != i || seg.y(ms) != j) throw new RuntimeException("Inconsistant data");                

                ArrayList<T> group = new ArrayList<>();
                switch (mode) {
                    case FAST_RECURSIVE: findGroupRec(ms, group, matrix, matrixAffected, seg);
                    case SAFE_STACKED: findGroupStack(ms, group, matrix, matrixAffected, seg);
                }
                allGroups.add(group);
                //for (T p2 : group) { allAffectedSquare.put(toUID(seg.x(p2), seg.y(p2), matrix[0].length), p2); }
            }
        }
        return allGroups;
    }
    
    
    private static <T> void findGroupRec(T currentPixel, ArrayList<T> pg, T[][] matrix, Boolean[][] matrixAffected, Segmentor seg) {
        {
            final int x = seg.x(currentPixel); 
            final int y = seg.y(currentPixel); 
            if (matrixAffected[y][x]) return;
            else { pg.add(currentPixel); matrixAffected[y][x] = true; }
        }
        for (int j= seg.y(currentPixel)-1; j <= seg.y(currentPixel)+1; j++) {
            //MatrixSquare[] row2 = matrix[k];
            for (int i = seg.x(currentPixel)-1; i <= seg.x(currentPixel)+1; i++) {
                if (!isInside(matrix, i, j)) continue;
                if (matrixAffected[j][i]) continue;
                
                T p2 = matrix[j][i];
                if (p2 == null || p2 == currentPixel || !seg.shouldUnite(currentPixel,p2)) continue;
                if (pg.contains(p2)) continue;
                //ISLog.s("");
                findGroupRec(p2, pg, matrix, matrixAffected, seg);
            }
        }
    }
    
    private static <T> void findGroupStack(T currentPixel, ArrayList<T> pg, T[][] matrix, Boolean[][] matrixAffected, Segmentor seg) {
        Deque<Runnable> stack = new LinkedList<>();
        findGroupStack(stack, currentPixel, pg, matrix, matrixAffected, seg);
        while (!stack.isEmpty()) {
            stack.pollLast().run();
        }
    }
    
    private static <T> void findGroupStack(Deque<Runnable> stack, T currentPixel, ArrayList<T> pg, T[][] matrix, Boolean[][] matrixAffected, Segmentor seg) {
        {
            final int x = seg.x(currentPixel); 
            final int y = seg.y(currentPixel); 
            if (matrixAffected[y][x]) return;
            else { pg.add(currentPixel); matrixAffected[y][x] = true; }
        }
        for (int j= seg.y(currentPixel)-1; j <= seg.y(currentPixel)+1; j++) {
            for (int i = seg.x(currentPixel)-1; i <= seg.x(currentPixel)+1; i++) {
                if (!isInside(matrix, i, j)) continue;
                if (matrixAffected[j][i]) continue;
                T p2 = matrix[j][i];
                if (p2 == null || p2 == currentPixel || !seg.shouldUnite(currentPixel,p2)) continue;
                if (pg.contains(p2)) continue;
                stack.add(() -> { findGroupStack(stack, p2, pg, matrix, matrixAffected, seg); });
            }
        }
    }
}
