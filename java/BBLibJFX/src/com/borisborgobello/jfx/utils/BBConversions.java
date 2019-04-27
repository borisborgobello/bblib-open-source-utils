/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

/**
 *
 * @author borisborgobello
 */
public class BBConversions {

    // Metrics
    private static final double ONE_INCH_TO_CM = 2.54;
    private static final double ONE_CM_TO_INCH = 1 / ONE_INCH_TO_CM;

    public static double inchToCm(double inch) {
        return inch * ONE_INCH_TO_CM;
    }

    public static double cmToInch(double cm) {
        return cm * ONE_CM_TO_INCH;
    }

    public static double inchToMeter(double inch) {
        return inchToCm(inch) / 100.0;
    }

    // Currencies
    public static double RMB_TO_USD = 0.15;
    public static double RMB_TO_VND = 3383;
}
