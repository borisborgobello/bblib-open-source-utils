/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import java.awt.Color;
import java.awt.color.ColorSpace;

/**
 *
 * @author borisborgobello 
 * Extends awt Color
 */
public class BBColor extends Color {

    public final static javafx.scene.paint.Color TRANSPARENT_FX = javafx.scene.paint.Color.TRANSPARENT;
    public final static Color TRANSPARENT = BBColorUtils.getAWTColor(javafx.scene.paint.Color.TRANSPARENT);
    public final static int I_TRANSPARENT = TRANSPARENT.getRGB();

    private static class RGB {

        int r, g, b;
    }

    public static BBColor from(int r, int g, int b) {
        return new BBColor(r, g, b);
    }

    public BBColor(int r, int g, int b) {
        super(r, g, b);
    }

    public BBColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public BBColor(int rgb) {
        super(rgb);
    }

    public BBColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public BBColor(float r, float g, float b) {
        super(r, g, b);
    }

    public BBColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public BBColor(double r, double g, double b) {
        super((float) r, (float) g, (float) b);
    }

    public BBColor(double r, double g, double b, double a) {
        super((float) r, (float) g, (float) b, (float) a);
    }

    public BBColor(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    double ColourDistance(RGB e1, RGB e2) {
        long rmean = ((long) e1.r + (long) e2.r) / 2;
        long r = (long) e1.r - (long) e2.r;
        long g = (long) e1.g - (long) e2.g;
        long b = (long) e1.b - (long) e2.b;
        return Math.sqrt((((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8));
    }
}
