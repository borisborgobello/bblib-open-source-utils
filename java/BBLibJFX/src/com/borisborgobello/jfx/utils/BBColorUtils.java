/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import java.awt.Color;
import java.util.Comparator;

/**
 *
 * @author borisborgobello
 */
public class BBColorUtils {

    // RANDOM COLORS
    private static final javafx.scene.paint.Color[] RAND_COLORS = new javafx.scene.paint.Color[]{
        javafx.scene.paint.Color.WHITE, javafx.scene.paint.Color.BLACK, javafx.scene.paint.Color.BLUE, javafx.scene.paint.Color.RED,
        javafx.scene.paint.Color.GREEN, javafx.scene.paint.Color.YELLOW, javafx.scene.paint.Color.PURPLE, javafx.scene.paint.Color.ORANGE,
        javafx.scene.paint.Color.CYAN, javafx.scene.paint.Color.DARKGREEN, javafx.scene.paint.Color.DARKGRAY, javafx.scene.paint.Color.DARKCYAN, javafx.scene.paint.Color.HOTPINK,
        javafx.scene.paint.Color.KHAKI, javafx.scene.paint.Color.INDIGO, javafx.scene.paint.Color.LIGHTCORAL, javafx.scene.paint.Color.SIENNA
    };

    public static final Comparator<String> COLOR_COMPARATOR = (String o1, String o2) -> {
        if (!o1.startsWith("#")) {
            o1 = "#" + o1;
        }
        if (!o2.startsWith("#")) {
            o2 = "#" + o2;
        }
        javafx.scene.paint.Color c1 = javafx.scene.paint.Color.web(o1);
        javafx.scene.paint.Color c2 = javafx.scene.paint.Color.web(o2);
        double d1 = c1.getRed() * c1.getRed() + c1.getBlue() * c1.getBlue() + c1.getGreen() * c1.getGreen();
        double d2 = c2.getRed() * c2.getRed() + c2.getBlue() * c2.getBlue() + c2.getGreen() * c2.getGreen();
        if (d1 == d2) {
            return 0;
        }
        return d1 < d2 ? 1 : -1;
    };

    public static Color getGrayedWeigh(Color color) {
        int c = (int) ((color.getRed() * 0.2989 + color.getGreen() * 0.5870 + color.getBlue() * 0.1140));
        return new Color(c, c, c, color.getAlpha());
    }

    public static String getCssColorForARGB(String argb) {
        return getCssColorForColor(getColorForARGB(argb));
    }

    public static Color getColorForARGBAWT(String argb) {
        return getAWTColor(getColorForARGB(argb));
    }

    public static javafx.scene.paint.Color getColorForARGB(String argb) {
        if (argb.startsWith("#")) {
            if (argb.length() != 9) {
                return javafx.scene.paint.Color.web(argb);
            } else {
                argb = argb.substring(1);
            }
        } else if (argb.length() != 8) {
            return javafx.scene.paint.Color.web(argb);
        }
        long opacity = Long.decode("#" + argb.substring(0, 2));
        //java.awt.Color.
        return javafx.scene.paint.Color.web("#" + argb.substring(2), opacity / 255.0);
    }

    public static int getMergedColorWithTransparentLayer(int layer, int underSolid) {
        java.awt.Color cOver = new java.awt.Color(layer, true);
        java.awt.Color cUnder = new java.awt.Color(underSolid, true);

        int r1 = cOver.getRed(), g1 = cOver.getGreen(), b1 = cOver.getBlue();
        int r2 = cUnder.getRed(), g2 = cUnder.getGreen(), b2 = cUnder.getBlue();//, a2 = cUnder.getAlpha();
        double a1 = cOver.getAlpha() / 255.0;

        int r3 = (int) (r2 + (r1 - r2) * a1);
        int g3 = (int) (g2 + (g1 - g2) * a1);
        int b3 = (int) (b2 + (b1 - b2) * a1);
        return new java.awt.Color(r3, g3, b3).getRGB();
    }

    public static int getMergedColor(int over, int under) {
        java.awt.Color cA = new java.awt.Color(over, true);
        java.awt.Color cB = new java.awt.Color(under, true);
        int rOut, gOut, bOut, aOut;
        int rA = cA.getRed(), gA = cA.getGreen(), bA = cA.getBlue(), aA = cA.getAlpha();
        int rB = cB.getRed(), gB = cB.getGreen(), bB = cB.getBlue(), aB = cB.getAlpha();
        rOut = (rA * aA / 255) + (rB * aB * (255 - aA) / (255 * 255));
        gOut = (gA * aA / 255) + (gB * aB * (255 - aA) / (255 * 255));
        bOut = (bA * aA / 255) + (bB * aB * (255 - aA) / (255 * 255));
        aOut = aA + (aB * (255 - aA) / 255);
        return new java.awt.Color(rOut, gOut, bOut, aOut).getRGB();
    }

    /* factor between 0 and 1, 1 is same, 0 is white*/
    public static Color getBrighterColor(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int alpha = c.getAlpha();

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new java.awt.Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }

        return new java.awt.Color(Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255),
                alpha);
    }

    /* factor between 0 and 1, 1 is same, 0 is black*/
    public static java.awt.Color getDarkerColor(java.awt.Color c, double factor) {
        return new java.awt.Color(Math.max((int) (c.getRed() * factor), 0),
                Math.max((int) (c.getGreen() * factor), 0),
                Math.max((int) (c.getBlue() * factor), 0),
                c.getAlpha());
    }

    public static java.awt.Color getAWTColor(javafx.scene.paint.Color c) {
        return new java.awt.Color((float) c.getRed(), (float) c.getGreen(), (float) c.getBlue(), (float) c.getOpacity());
    }

    public static javafx.scene.paint.Color getFXColor(java.awt.Color awtColor) {
        return new javafx.scene.paint.Color(awtColor.getRed() / 255.0, awtColor.getGreen() / 255.0, awtColor.getBlue() / 255.0, awtColor.getAlpha() / 255.0);
    }

    public static javafx.scene.paint.Color getFXColor(int color) {
        return getFXColor(new Color(color));
    }

    public static javafx.scene.paint.Color getRandomColor() {
        return RAND_COLORS[(int) (Math.random() * RAND_COLORS.length)];
    }

    public static String getCssColorForColor(javafx.scene.paint.Color c) {
        return String.format(" rgba(%d, %d, %d, %f);", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255), c.getOpacity());
    }

    public static String getCssColorForColor(Color c) {
        return String.format(" rgba(%d, %d, %d, %f);", c.getRed(), c.getGreen(), c.getBlue(), (c.getAlpha() + .5f) / 255);
    }

    public static String getHexString(Color c) {
        if (c == null) {
            return null;
        }
        return String.format("%02x%02x%02x%02x", c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
    }

    public static String getRandomColorForCss() {
        return getCssColorForColor(getRandomColor());
    }

    public static Color getColorWithAlpha(int rgb, double alpha) {
        Color c = new Color(rgb);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (alpha * 255));
    }

    public static int argb(int R, int G, int B) {
        return argb(Byte.MAX_VALUE, R, G, B);
    }

    public static int argb(int A, int R, int G, int B) {
        byte[] colorByteArr = {(byte) A, (byte) R, (byte) G, (byte) B};
        return byteArrToInt(colorByteArr);
    }

    public static int[] rgb(int argb) {
        return new int[]{(argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF};
    }

    public static int byteArrToInt(byte[] colorByteArr) {
        return (colorByteArr[0] << 24) + ((colorByteArr[1] & 0xFF) << 16)
                + ((colorByteArr[2] & 0xFF) << 8) + (colorByteArr[3] & 0xFF);
    }

    public static int[] rgb2lab(int R, int G, int B) {
        //http://www.brucelindbloom.com

        float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
        float Ls, as, bs;
        float eps = 216.f / 24389.f;
        float k = 24389.f / 27.f;

        float Xr = 0.964221f;  // reference white D50
        float Yr = 1.0f;
        float Zr = 0.825211f;

        // RGB to XYZ
        r = R / 255.f; //R 0..1
        g = G / 255.f; //G 0..1
        b = B / 255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045) {
            r = r / 12;
        } else {
            r = (float) Math.pow((r + 0.055) / 1.055, 2.4);
        }

        if (g <= 0.04045) {
            g = g / 12;
        } else {
            g = (float) Math.pow((g + 0.055) / 1.055, 2.4);
        }

        if (b <= 0.04045) {
            b = b / 12;
        } else {
            b = (float) Math.pow((b + 0.055) / 1.055, 2.4);
        }

        X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
        Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
        Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

        // XYZ to Lab
        xr = X / Xr;
        yr = Y / Yr;
        zr = Z / Zr;

        if (xr > eps) {
            fx = (float) Math.pow(xr, 1 / 3.);
        } else {
            fx = (float) ((k * xr + 16.) / 116.);
        }

        if (yr > eps) {
            fy = (float) Math.pow(yr, 1 / 3.);
        } else {
            fy = (float) ((k * yr + 16.) / 116.);
        }

        if (zr > eps) {
            fz = (float) Math.pow(zr, 1 / 3.);
        } else {
            fz = (float) ((k * zr + 16.) / 116);
        }

        Ls = (116 * fy) - 16;
        as = 500 * (fx - fy);
        bs = 200 * (fy - fz);

        int[] lab = new int[3];
        lab[0] = (int) (2.55 * Ls + .5);
        lab[1] = (int) (as + .5);
        lab[2] = (int) (bs + .5);
        return lab;
    }

    public static double getColorDistanceLAB(Color a, Color b) {
        return getColorDistanceLAB(a.getRGB(), b.getRGB());
    }

    public static final int S_255x255 = 255 * 255;
    public static final int S_3x255x255 = 255 * 255;

    // Cartesian distance square, returns 0->1.0
    public static double getColorDistanceRGBp2(Color a, Color b) {
        return (Math.pow(a.getRed() - b.getRed(), 2)
                + Math.pow(a.getGreen() - b.getGreen(), 2)
                + Math.pow(a.getBlue() - b.getBlue(), 2)) / S_3x255x255;
    }

    public static double getColorDistanceRGBp2(int a, int b) {
        return getColorDistanceRGBp2(new BBColor(a, true), new BBColor(b, true));
    }

    /**
     * Computes the difference between two RGB colors by converting them to the
     * L*a*b scale and comparing them using the CIE76 algorithm {
     * http://en.wikipedia.org/wiki/Color_difference#CIE76}
     */
    public static double getColorDistanceLAB(int a, int b) {
        Color c1 = new Color(a);
        Color c2 = new Color(b);
        int r1, g1, b1, r2, g2, b2;
        r1 = c1.getRed();
        g1 = c1.getGreen();
        b1 = c1.getBlue();
        r2 = c2.getRed();
        g2 = c2.getGreen();
        b2 = c2.getBlue();
        int[] lab1 = rgb2lab(r1, g1, b1);
        int[] lab2 = rgb2lab(r2, g2, b2);
        return Math.sqrt(Math.pow(lab2[0] - lab1[0], 2) + Math.pow(lab2[1] - lab1[1], 2) + Math.pow(lab2[2] - lab1[2], 2));
    }

    public static String getStyleForProgress(double progress) {
        return "-fx-accent: " + getColorForProgress(progress);
    }

    public static String getColorForProgress(double progress) {
        if (progress < 0.5) {
            String hex = Integer.toHexString((int) (progress * 2 * 255));
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            return "#FF" + hex + "00";
        } else {
            String hex = Integer.toHexString((int) ((1 - progress) * 2 * 255));
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            return "#" + hex + "FF00";
        } // 1 - 2progress +1 = 2(1 - progress)*255
    }
}
