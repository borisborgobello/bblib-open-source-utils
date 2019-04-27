/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img.processing;

import com.borisborgobello.jfx.utils.BBLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.FuzzyKMeansClusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.clustering.MultiKMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.CanberraDistance;
import org.apache.commons.math3.ml.distance.ChebyshevDistance;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

/**
 *
 * @author borisborgobello
 * 
 * 
 */
public class BBImageApacheMLBridge {

    public static class BBDistances {

        @Override
        public String toString() {
            return name;
        }
        final public String name;
        final public DistanceMeasure dm;

        public BBDistances(String name, DistanceMeasure dm) {
            this.name = name;
            this.dm = dm;
        }

        public static BBDistances DIST_CANBERRA = new BBDistances("CanberraDistance", new CanberraDistance());
        public static BBDistances DIST_CHEBY = new BBDistances("ChebyshevDistance", new ChebyshevDistance());
        public static BBDistances DIST_EUCLI = new BBDistances("EuclideanDistance", new EuclideanDistance());
        public static BBDistances DIST_MANHATTAN = new BBDistances("ManhattanDistance", new ManhattanDistance());
        public static BBDistances DIST_EARTH_MOVER = new BBDistances("EarthMoversDistance", new EarthMoversDistance());
    }

    public static interface PixelConverter<T> {
        public abstract int x(T t);
        public abstract int y(T t);
        public abstract double[] getDistanceComponents(T t);
        public boolean shouldCloneData();
    }

    public static class ISFilterGroup<T> {
        public double[] center;
        public ArrayList<T> points = new ArrayList<>();
    }

    public static abstract class ISFilterBase {
        @Override
        public String toString() {
            return getName();
        }
        public int getParamCount() {
            int i = -1;
            while (getParamName(++i) != null) {}
            return i;
        }
        public abstract String getName();
        public abstract String getParamName(int position);
        public final <T> ArrayList<ISFilterGroup<T>> filter(ArrayList<T> p, BBDistances d, PixelConverter<T> c, Object... params) {
            StringBuilder sb = new StringBuilder();
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    Object o = params[i];
                    sb.append(getParamName(i)).append("=").append(o).append(" ");
                }
            }
            BBLog.s(String.format("Starting filter %s using distance %s and with params %s", getName(), d.name, sb.toString()));
            return doFilter(p, d, c, params);
        }
        public abstract <T> ArrayList<ISFilterGroup<T>> doFilter(ArrayList<T> p, BBDistances d, PixelConverter<T> c, Object... params);
    }

    public static class FKmeansAp extends ISFilterBase {

        @Override
        public String getName() {
            return "FKMeansAp";
        }

        @Override
        public String getParamName(int position) {
            switch (position) {
                case 0:
                    return "DMC";
                case 1:
                    return "Fuzzy";
                case 2:
                    return "Attempts";
            }
            return null;
        }

        @Override
        public <T> ArrayList<ISFilterGroup<T>> doFilter(ArrayList<T> p, BBDistances d, PixelConverter<T> conv, Object... params) {
            if (params.length != getParamCount()) {
                throw new RuntimeException("Wrongs args");
            }

            int dmcs = ((int) params[0]);
            int fuzzy = ((int) params[1]);
            int attempts = ((int) params[2]);

            HashMap<Clusterable, T> map = new HashMap();

            for (T t : p) {
                Clusterable c = new Clusterable() {
                    private double[] fastP = null;

                    @Override
                    public double[] getPoint() {
                        if (fastP == null) {
                            fastP = conv.getDistanceComponents(t);
                        }
                        return fastP;
                    }
                };
                map.put(c, t);
            }

            ArrayList<Clusterable> data = new ArrayList<>(map.keySet());
            List<CentroidCluster<Clusterable>> clusterResults = null;
            long start = System.currentTimeMillis();
            if (fuzzy > 0) {
                FuzzyKMeansClusterer<Clusterable> clusterer = new FuzzyKMeansClusterer<>(dmcs, fuzzy, 10000, d.dm);
                clusterResults = clusterer.cluster(data);
            } else {
                KMeansPlusPlusClusterer<Clusterable> clusterer = new KMeansPlusPlusClusterer(dmcs, 10000, d.dm);
                if (attempts > 1) {
                    MultiKMeansPlusPlusClusterer mmppc = new MultiKMeansPlusPlusClusterer(clusterer, attempts);
                    clusterResults = mmppc.cluster(data);
                } else {
                    clusterResults = clusterer.cluster(data);
                }
            }
            BBLog.s("Clustering time (s) -> " + (System.currentTimeMillis() - start) / 1000);

            ArrayList<ISFilterGroup<T>> result = new ArrayList<>(clusterResults.size());
            for (int i = 0; i < clusterResults.size(); i++) {
                ISFilterGroup<T> fg = new ISFilterGroup();
                result.add(fg);
                CentroidCluster<Clusterable> cc = clusterResults.get(i);
                fg.center = cc.getCenter().getPoint();
                for (Clusterable l : cc.getPoints()) {
                    fg.points.add(map.get(l));
                }
            }
            return result;
        }
    }
}

