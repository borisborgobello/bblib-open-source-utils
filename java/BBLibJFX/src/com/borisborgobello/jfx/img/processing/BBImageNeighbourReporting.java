/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img.processing;

import com.borisborgobello.jfx.utils.BBCollections;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author borisborgobello
 */
public class BBImageNeighbourReporting {
    
    public interface BBIndexValueExtractor<T,V> {
        public abstract int x(T t);
        public abstract int y(T t);
        public abstract boolean areIdentical(T t1, T t2);
        public abstract V value(T t);
        public abstract Integer group(T t);
    }
    
    public static class BBNeighbourReportGeneric<T,V> {
        public T pl;
        public int identical=0;
        public int different=0;
        public final HashMap<V, Integer> histo = new HashMap(); // 
        public final HashMap<Integer, Integer> groups = new HashMap();
        //public DMC tl=null,t=null,tr=null,l=null,r=null,bl=null,b=null,br=null;
        public boolean cornerIdent = false;

        public BBNeighbourReportGeneric(T pl) { this.pl = pl; }
        public BBNeighbourReportGeneric() { this.pl = null; }

        public Integer getOverwhelmingGroup(int excludeGroup) {
            if (groups.isEmpty()) return null;
            ArrayList<Integer> n = new ArrayList<>(groups.keySet());
            n.sort((Integer o1, Integer o2) -> BBCollections.COMPARATOR_INT_REVERSED.compare(groups.get(o1), groups.get(o2)));
            Integer winner = n.get(0);
            if (winner == excludeGroup) {
                if (groups.size() > 1) winner = n.get(1);
                else return null;
            }
            return winner;
        }
        public V getOverwhelmingDifferentDMC() {
            if (histo.isEmpty()) return null;
            ArrayList<V> n = new ArrayList<>(histo.keySet());
            n.sort((V o1, V o2) -> BBCollections.COMPARATOR_INT_REVERSED.compare(histo.get(o1), histo.get(o2)));
            V winner = n.get(0);
            return winner;
        }

        private void clearFor(T pl) {
            this.pl = pl;
            identical = 0;
            different = 0;
            histo.clear();
            groups.clear();
            cornerIdent = false;
        }
    }
    
    /**
     * Analyzes the neighbouring points of a pixel. Count identical values
     * and different value, generate an histogram for values and support
     * an additional group parameter specified in the extractor
     * @param <T> usually a pixel
     * @param <V> the value type on which pixels are compared
     * @param matrix matrix of pixels
     * @param currentPixel the pixel being analyzed
     * @param extractor bridges/sources between BBLib and projects data
     * @param recycledReport if null, a new report is created, else it is cleared
     * @return a report containing counters and histograms
     */
    public static <T,V> BBNeighbourReportGeneric<T,V> findIdenticalNeighboursGeneric(T[][] matrix, T currentPixel, BBIndexValueExtractor<T,V> extractor, BBNeighbourReportGeneric<T,V> recycledReport) {
        //DMC d = currentPixel.mDMC;
        if (recycledReport == null) recycledReport = new BBNeighbourReportGeneric(currentPixel);
        else recycledReport.clearFor(currentPixel);
        for (int j= extractor.y(currentPixel)-1; j <= extractor.y(currentPixel)+1; j++) {
            //MatrixSquare[] row2 = matrix[k];
            for (int i = extractor.x(currentPixel)-1; i <= extractor.x(currentPixel)+1; i++) {
                if (!BBCollections.isInside(matrix, i, j)) continue;
                T p2 = matrix[j][i];
                if (p2 == null || p2 == currentPixel) continue;
                
                if (extractor.areIdentical(p2, currentPixel)) {
                    recycledReport.identical++;
                    if (Math.abs(j-extractor.y(currentPixel)) == 1 && Math.abs(i-extractor.x(currentPixel)) == 1) {
                        recycledReport.cornerIdent = true;
                    }
                } else {
                    BBCollections.addInMap(recycledReport.histo, extractor.value(p2), 1);
                    recycledReport.different++;
                }
                BBCollections.addInMap(recycledReport.groups, extractor.group(p2), 1);
            }
        }
        return recycledReport;
    }
}
