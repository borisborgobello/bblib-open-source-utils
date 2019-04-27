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
public abstract class BBBuildSettings {
    public static enum Phase { alpha, beta, stable; }

    public static enum AppLanguage { en, vi }
    
    public static BBBuildSettings bbs = null; //new BBBuildSettings() {};
    
    public static long S_MODULE_BUILD() { // Max 3 numbers separated by dots, numbers are 2 digits max
        String[] vs = S_MODULE_VERSION_S().split("\\.");
        try {
            long build = 0;
            for (int i = 0; i < vs.length; i++) {
                build += (long) (Long.parseLong(vs[i]) * Math.pow(10, 4-2*i));
            }
            return build;
        } catch (Exception e) { return -1; }
    }
    public static boolean APP_IS_PROD_VERSION_S() { return bbs.APP_IS_PROD_VERSION(); }
    public static String S_MODULE_NAME_S() { return bbs.S_MODULE_NAME(); }
    public static String S_MODULE_VERSION_S() { return bbs.S_MODULE_VERSION(); }
    public static String LAST_VERSION_UPDATE_NOTES_S() { return bbs.LAST_VERSION_UPDATE_NOTES(); }
    public static String S_WS_SERVER_DN_DEV_S() { return bbs.S_WS_SERVER_DN_DEV(); }
    public static String S_WS_SERVER_DN_PROD_S() { return bbs.S_WS_SERVER_DN_PROD(); }
    public static boolean APP_BYPASS_EXIT_S() { return bbs.APP_BYPASS_EXIT(); }
    public static AppLanguage APP_LANGUAGE_S() { return bbs.APP_LANGUAGE(); }
    
    public abstract boolean APP_IS_PROD_VERSION();
    public abstract String S_MODULE_NAME();
    public abstract String S_MODULE_VERSION();
    public abstract String LAST_VERSION_UPDATE_NOTES();
    public abstract String S_WS_SERVER_DN_DEV();
    public abstract String S_WS_SERVER_DN_PROD();
    public abstract boolean APP_BYPASS_EXIT();
    public abstract AppLanguage APP_LANGUAGE();
    
    protected String makeVersionNotes(String ... ss) { 
        if (BBCollections.isEmpty(ss)) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : ss) { sb.append(s).append("\n"); }
        sb.append("END");
        return sb.toString(); 
    }
 
}
