/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Locale;

/**
 *
 * @author borisborgobello
 */
public class ISGlobal {
    
    public static final SimpleDateFormat appleFormatAlt = new SimpleDateFormat(
		    "yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat iso8601near = new SimpleDateFormat(
		    "yyyy-MM-dd'T'HH:mm:ssZZZZZZ");
    
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat(
		    "HH:mm:ss.SSS");
    
    public static final SimpleDateFormat logFormat = new SimpleDateFormat(
		    "yyyy-MM-dd'_'HH-mm-ss");
    
    public static final SimpleDateFormat timeTrack = new SimpleDateFormat(
		    "dd/MM/yy HH:mm:ss");
    public static final SimpleDateFormat timeTrackPause = new SimpleDateFormat(
		    "HH:mm:ss");
    
    static final StringBuilder mFormatBuilder;
    static final Formatter mFormatter;
    static {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }
    
    public static String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;
        
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = totalSeconds / 3600;
        
        
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    
    public static String stringForTime2(long timeMs) {
        long totalSeconds = timeMs / 1000;
        
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = (totalSeconds / 3600) %24;
        long days   = totalSeconds / (3600*24);
        
        
        mFormatBuilder.setLength(0);
        if (days > 0) {
            return mFormatter.format("%dd %dh %dm", days, hours, minutes).toString();
        }
        else if (hours > 0) {
            return mFormatter.format("%dh %dm", hours, minutes).toString();
        } else {
            return mFormatter.format("%dm", minutes).toString();
        }
    }
    
    public static String stringForTime3(long timeMs) {
        long totalSeconds = timeMs / 1000;
        
        //long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = (totalSeconds / 3600) %24;
        long days   = totalSeconds / (3600*24);
        
        
        mFormatBuilder.setLength(0);
        if (days > 0) {
            return mFormatter.format("%dd%dh%dm", days, hours, minutes).toString();
        }
        else if (hours > 0) {
            return mFormatter.format("%dh%dm", hours, minutes).toString();
        } else {
            return mFormatter.format("%dm", minutes).toString();
        }
    }
    
    public static void init() {}
}
