/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.borisborgobello.jfx.io.BBFileInout;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.util.Pair;
import resources.ResRoot;

/**
 *
 * @author borisborgobello
 * 
 * Convenient methods for using resources
 */
public class BBRes {
    
    public static ResourceBundle DEFAULT_BUNDLE = getResBundleForLanguage(null);
    
    public static class DefaultAssets {
        public static final String S_ICON_BALL_INFO = "borisborgobello/ic_ball_info.png";
        public static final String S_ICON_BALL_HELP = "borisborgobello/ic_ball_help.png";
        public static final String S_ICON_BALL_KO = "borisborgobello/ic_ball_ko.png";
        public static final String S_ICON_BALL_OK = "borisborgobello/ic_ball_ok.png";
        public static final String S_ICON_BALL_PACMAN = "borisborgobello/ic_ball_pacman.png";
        public static final String S_ICON_BALL_SEARCH = "borisborgobello/ic_ball_search.png";
        public static final String S_ICON_BALL_STANDBY = "borisborgobello/ic_ball_standby.png";
        public static final String S_ICON_BALL_SHUTDOWN = "borisborgobello/ic_ball_shutdown.png";
        public static final String S_ICON_BALL_TIME = "borisborgobello/ic_ball_time.png";
        public static final String S_ICON_BALL_FAVORITE = "borisborgobello/ic_ball_favorite.png";
        public static final String S_ICON_BALL_LOGOFF = "borisborgobello/ic_ball_logoff.png";
        
        public static final String S_MEDIA_PAUSE = "borisborgobello/ic_media_pause.png";
        public static final String S_MEDIA_PLAY = "borisborgobello/ic_media_play.png";
        public static final String S_MEDIA_STOP = "borisborgobello/ic_media_stop.png";
        public static final String S_MEDIA_RESUME = "borisborgobello/ic_media_resume.png";
        
        public static final String S_ICON_PRINT = "borisborgobello/ic_print.png";
        public static final String S_ICON_EYE = "borisborgobello/see_lowres.png";
    }
    
    public static String getRes(String ... components) {
        return getRes(true, components);
    }
    
    public static String getRes(boolean inResDir, String ... components) {
        if (components == null || components.length == 0) return null;
        
        StringBuilder sb = new StringBuilder();
        for (String s : components) {
            if (s.startsWith("/")) s = s.substring(1);
            if (s.endsWith("/")) s = s.substring(0, s.length()-1);
            
            sb.append(s).append("/");
        }
        sb.deleteCharAt(sb.length()-1);
        String res = sb.toString();
        if (inResDir && !res.startsWith("resources")) res = "resources/" + res; 
        return res;
    }

    protected static Font registerFont(String font) {
        InputStream is = ResRoot.class.getResourceAsStream(String.format("fonts/%s", font));
        Font f = Font.loadFont(is , 0);
        //Font f = Font.loadFont("resources/fonts/"+font , 0);
        BBLog.s("Registered font : " + f.toString());
        return f;
    }
    
    public static byte[] getResAsBytes(String resfullpath) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resfullpath);
        return BBFileInout.readAllDataFromInputStream(is);
        //com.sun.javafx.tk.quantum.QuantumToolkit
    }
    
    public static Image getResAfterResDir(String path) {
        return new Image("resources/" + path);
    }
    public static String getResUrlInImageDir(String imgName) {
        return "resources/imgs/" + imgName;
    }
    public static Image getResInImageDir(String path) {
        return new Image(getResUrlInImageDir(path));
    }
    public static String getResUrlInIconDir(String imgName) {
        return "resources/icons/" + imgName;
    }
    public static Image getResInIconDir(String path) {
        return new Image(getResUrlInIconDir(path));
    }
    
    public static Pair<Set<String>, Set<String>> listResources(Class<Application> clazz, String path) {
        HashMap<String, String> dirs = new HashMap<>();
        HashMap<String, String> files = new HashMap<>();
        
        ArrayList<String> allFiles = new ArrayList<>();
        
        try {
        final File jarFile = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {  // Run with JAR file
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
            while(entries.hasMoreElements()) {
                final String name = entries.nextElement().getName();
                if (name.startsWith(path + "/")) { //filter according to the path
                    allFiles.add(name);
                }
            }
            jar.close();
        } else { // Run with IDE
            final URL url = clazz.getResource("/" + path);
            if (url != null) {
                try {
                    final File apps = new File(url.toURI());
                    for (File app : apps.listFiles()) {
                        allFiles.add(app.getCanonicalPath());
                    }
                } catch (URISyntaxException ex) {
                    // never happens
                }
            }
        }
        } catch (Exception ex) { Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex); }
        
        Pattern pDir = Pattern.compile(path + "/([^/]+)/");
        Pattern pFile = Pattern.compile(path + "/([^/]+)");
        for (String f : new ArrayList<>(allFiles)) {
            Matcher m = pDir.matcher(f);
            if (m.find()) {
                dirs.put(m.group(1), m.group(1));
            } else {
                m = pFile.matcher(f);
                m.find();
                try { files.put(m.group(1), m.group(1)); } catch (Exception e) {}
            }
        }
        return new Pair(dirs.keySet(), files.keySet());
    }
    
    // language is a 2 letter language : vi = vietnamese, en = english, fr = french
    public static final ResourceBundle getResBundleForLanguage(String language) {
        return language == null ? 
                ResourceBundle.getBundle("resources.UIResources")
                : ResourceBundle.getBundle("resources/UIResources", new Locale(language));
    } 
    
    public static final String getS(String language, String key) {
        return getResBundleForLanguage(language).getString(key);
    }
    
    public static final String getS(ResourceBundle bundle, String key) {
        return bundle.getString(key);
    }
    
    public static final String getS(String key) {
        return getS(DEFAULT_BUNDLE, key);
    }
}
