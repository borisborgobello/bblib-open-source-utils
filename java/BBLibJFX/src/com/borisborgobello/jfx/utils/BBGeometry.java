/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Point;
import java.util.ArrayList;
import javax.vecmath.Vector2d;

/**
 *
 * @author borisborgobello
 */
public class BBGeometry {

    public static Vector2d newNormVect(Point p1, Point p2) {
        Vector2d v = new Vector2d(p2.x - p1.x, p2.y - p1.y);
        v.normalize();
        return v;
    }

    public static class BaseBTransformer {

        public static class BaseBProjectionBuilder {

            ArrayList<Object> operations = new ArrayList<>();

            public BaseBProjectionBuilder() {
            }

            public BaseBProjectionBuilder addScale(double a) {
                operations.add(a);
                return this;
            }

            public BaseBProjectionBuilder addTranslation(int x, int y) {
                operations.add(new Point(x, y));
                return this;
            }

            public BaseBTransformer build() {
                return new BaseBTransformer(operations);
            }
        }

        final ArrayList<Object> operations;

        public BaseBTransformer(ArrayList<Object> operations) {
            this.operations = operations;
        }

        int apply(boolean isX, int value, Object operation) {
            if (operation instanceof Point) {
                Point p = (Point) operation;
                return value += isX ? p.x : p.y;
            } else if (operation instanceof Double) {
                return value *= (Double) operation;
            }
            throw new RuntimeException("Unknown operation");
        }

        int apply(boolean isX, int value, boolean reverse) {
            if (reverse) {
                for (int i = operations.size() - 1; i >= 0; i--) {
                    value = apply(isX, value, operations.get(i));
                }
            } else {
                for (int i = 0; i < operations.size(); i++) {
                    value = apply(isX, value, operations.get(i));
                }
            }
            return value;
        }

        public int toBaseBX(int x) {
            return apply(true, x, false);
        }

        public int toBaseBY(int y) {
            return apply(false, y, false);
        }

        public int toBaseAX(int x) {
            return apply(true, x, true);
        }

        public int toBaseAY(int y) {
            return apply(false, y, true);
        }

        public Point toBaseB(Point pA) {
            return new Point(toBaseBX(pA.x), toBaseBY(pA.y));
        }

        public Point toBaseA(Point pB) {
            return new Point(toBaseAX(pB.x), toBaseAY(pB.y));
        }
    }

    public static class Vect2 {

        public Vect2() {
        }

        public Vect2(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @JsonProperty("x")
        public Double x;
        @JsonProperty("y")
        public Double y;

        public Vect2(Vector2d p) {
            x = p.x;
            y = p.y;
        }

        @JsonIgnore
        public Vector2d toVect() {
            return new Vector2d(x, y);
        }
    }

    public static class Point2 {

        public Point2() {
        }

        public Point2(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @JsonProperty("x")
        public Integer x;
        @JsonProperty("y")
        public Integer y;

        public Point2(Point p) {
            x = p.x;
            y = p.y;
        }

        public Point2(Point2 p) {
            x = p.x;
            y = p.y;
        }

        @JsonIgnore
        public Point toPoint() {
            return new Point(x, y);
        }

        @JsonIgnore
        public double dist(Point2 p2) {
            return Math.sqrt((p2.x - x) * (p2.x - x) + (p2.y - y) * (p2.y - y));
        }

        @JsonIgnore
        public Vector2d toVectorPP2(Point2 p2) {
            return new Vector2d(p2.x - x, p2.y - y);
        }

        @JsonIgnore
        public Vector2d toVector() {
            return new Vector2d(x, y);
        }

        @JsonIgnore
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj instanceof Point2) {
                Point2 p = (Point2) obj;
                return p.x.equals(x) && p.y.equals(y);
            }
            return super.equals(obj);
        }

    }

    public static Point2 applyRotation(Point2 p, Vector2d rotCenter, double angleRad, Vector2d newCenter) {
        Vector2d cp = p.toVector();
        cp.sub(rotCenter);
        double x2 = Math.cos(angleRad) * cp.x - Math.sin(angleRad) * cp.y;
        double y2 = Math.cos(angleRad) * cp.y + Math.sin(angleRad) * cp.x;
        Vector2d rotated = new Vector2d(x2, y2);
        return new Point2((int) (newCenter.x + x2), (int) (newCenter.y + y2));
    }

    public static class BaseBProjection {

        double scaleToBaseB;
        Vector2d originInBaseA;

        public BaseBProjection(double originX, double originY, double scale) {
            this.originInBaseA = new Vector2d(originX, originY);
            this.scaleToBaseB = scale;
        }

        public int toBaseBX(int x) {
            return (int) ((x - originInBaseA.x) * scaleToBaseB);
        }

        public int toBaseBY(int y) {
            return (int) ((y - originInBaseA.y) * scaleToBaseB);
        }

        public int toBaseAX(int x) {
            return (int) ((x / scaleToBaseB + originInBaseA.x));
        }

        public int toBaseAY(int y) {
            return (int) ((y / scaleToBaseB + originInBaseA.y));
        }

        public Point toBaseB(Point pA) {
            return new Point(toBaseBX(pA.x), toBaseBY(pA.y));
        }

        public Point toBaseA(Point pB) {
            return new Point(toBaseAX(pB.x), toBaseAY(pB.y));
        }
    }

    public static final double dist(int x1, int y1, int x2, int y2) {
        int w = x2 - x1;
        int h = y2 - y1;
        return Math.sqrt(w * w + h * h);
    }

    public static final int distInt(int x1, int y1, int x2, int y2) {
        return (int) dist(x1, y1, x2, y2);
    }
}
