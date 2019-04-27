/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author borisborgobello
 *
 * This class contains static data specifically for IStitch It is here for
 * educational purpose only Basically for IStitch we use only a specified set of
 * colors that are distant enough from each other Pack one are colors very
 * distant from each other Pack two adds colors in the middle making distances
 * smaller Pack three even more colors Then a static map of each distance
 * combination is created for perfomance reason due to LAB calculations This
 * could be improved with int and TREEMAP complex o(n) ...
 *
 */
public class BBColorsForStitching {

    public static class IStitch {

        public static final String RED_DARK = "73270d";
        public static final String YELLOW_DARK = "676822";
        public static final String GREEN_DARK = "1e660d";

        public static final String IS_BLUE_DARK = "005f66";
        public static final String IS_BLUE_2 = "008993";
        public static final String IS_BLUE = "019faa";
        public static final String IS_BLUE_LIGHT = "00d8e7";
        public static final String IS_BLUE_LIGHT_LIGHT = "00d8e7";
    }

    public static final Color FAB_BG = new Color(255, 255, 254);
    public static final int FAB_BG_I = FAB_BG.getRGB();

    public static final ObservableList<Color> STITCH_COLOR_PACK1 = FXCollections.observableArrayList(); // 12
    public static final ObservableList<Color> STITCH_COLOR_PACK2 = FXCollections.observableArrayList(); // 19
    public static final ObservableList<Color> STITCH_COLOR_PACK3 = FXCollections.observableArrayList(); // 11

    public static final ObservableList<Color> STITCH_COLOR_FBOSS_SYMB = FXCollections.observableArrayList();
    public static final ObservableList<Color> STITCH_COLOR_FBOSS_BG_NO_SYMB = FXCollections.observableArrayList();
    public static final Color STITCH_COLOR_FBOSS_BG_YELLOW = BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB("FFFF00"));
    public static final ObservableList<Color> STITCH_COLOR_FBOSS_SYMB_BG_YELLOW = FXCollections.observableArrayList();

    public static final HashMap<Color, HashMap<Color, Double>> DISTANCES = new HashMap();
    public static final double MAX_DISTANCE, MIN_DISTANCE;

    static {
        // ISTITCH
        final String[] rainColorsStr = new String[]{
            "ff0000", "ff4300", "ff7400", "ff8e00", "ffa904", "ffc504",
            "ffe204", "ffff00", "00ff00", "04ff8e", "00ffc5", "00ffff",
            "00e2ff", "00c5ff", "008fff", "005cff", "0000ff", "c504ff",
            "ff04ff", "ff00c5", "ff008e", "ff005b"
        };

        final String[] addOn1LightDarkColorsStr = new String[]{
            "f59478", "00ab91", "cbdc7c", "f18eb8", "6180be", "76c290",
            "fdc781", "282b87", "981f83", "d0c464", "8a8101", "029b46",
            "8ec135", "fff598", "515151", "919191", "cacaca", "b01d16",
            "efbbe8"
        };

        STITCH_COLOR_PACK1.add(Color.BLACK);

        for (int i = 0; i < rainColorsStr.length; i++) {
            if (i % 2 == 0) {
                STITCH_COLOR_PACK1.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(rainColorsStr[i])));
            } else {
                STITCH_COLOR_PACK3.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(rainColorsStr[i])));
            }
        }
        for (String addOn1LightDarkColorsStr1 : addOn1LightDarkColorsStr) {
            STITCH_COLOR_PACK2.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(addOn1LightDarkColorsStr1)));
        }

        // FABBOSS
        String[] fabbossBgNoSymb = new String[]{
            "FFEF00", "6EB82B", "009FE7", "E3007D", "F29600", "7CCCF2",
            "EF9DC0", "7A59A1", "948c85"
        };
        for (String fabbossBgNoSymb1 : fabbossBgNoSymb) {
            STITCH_COLOR_FBOSS_BG_NO_SYMB.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(fabbossBgNoSymb1)));
        }

        String[] fabbossSymbCol = new String[]{
            "009FE7", "E3007D", "00883F", "F29600", "7A59A1", "000000", "FF0000"
        };
        for (String fabbossSymbCol1 : fabbossSymbCol) {
            STITCH_COLOR_FBOSS_SYMB.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(fabbossSymbCol1)));
        }

        String[] fabbossSymbColYellowBg = new String[]{
            "FF0000", "007C00"
        };
        for (String fabbossSymbColYellowBg1 : fabbossSymbColYellowBg) {
            STITCH_COLOR_FBOSS_SYMB_BG_YELLOW.add(BBColorUtils.getAWTColor(BBColorUtils.getColorForARGB(fabbossSymbColYellowBg1)));
        }

        // Compute distances
        ArrayList<Color> allColors = new ArrayList<>(STITCH_COLOR_PACK1);
        allColors.addAll(STITCH_COLOR_PACK2);
        allColors.addAll(STITCH_COLOR_PACK3);

        double min = -1, max = -1;
        double diff;
        for (Color c : allColors) {
            HashMap<Color, Double> distances = new HashMap<>();
            DISTANCES.put(c, distances);
            for (Color c2 : allColors) {
                if (c == c2) {
                    continue;
                }
                diff = BBColorUtils.getColorDistanceLAB(c.getRGB(), c2.getRGB());
                distances.put(c2, diff);
                if (min == -1 || diff < min) {
                    min = diff;
                }
                if (max == -1 || diff > max) {
                    max = diff;
                }
            }
        }
        MIN_DISTANCE = min;
        MAX_DISTANCE = max;
    }

    public static final ArrayList<Color> getStitchColors(int requiredColors) {
        ArrayList<Color> l = new ArrayList<>(STITCH_COLOR_PACK1);

        if (requiredColors > l.size()) {
            l.addAll(STITCH_COLOR_PACK2);
        }
        if (requiredColors > l.size()) {
            l.addAll(STITCH_COLOR_PACK3);
        }
        return l;
    }
}
