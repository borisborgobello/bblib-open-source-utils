/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.borisborgobello.jfx.io.BBDualPrintStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author borisborgobello
 * A quick logging lib. Upon init it may replace STDOUT and STDERR and write into local logs
 * This is good for a log that lasts for the whole app
 */
public class BBLog {
    
    public static final String LOG_DIR = new File("./logs").getAbsolutePath(); // fix for apple
    public static final String LOG_EXT = ".log";
    public static final String LOG_ERR_PREF = "err_";
    public static final String LOG_OUT_PREF = "out_";
    public static final String LOG_IDENTIFIER = BBDateUtils.FDATE_LOGSTYLE.format(new Date());
    
    public static final String LOG_ERR() {
        return LOG_DIR+"/"+LOG_ERR_PREF+ LOG_IDENTIFIER+LOG_EXT;
    }
    public static final String LOG_OUT() {
        return LOG_DIR+"/"+LOG_OUT_PREF+ LOG_IDENTIFIER+LOG_EXT;
    }
    
    public static final void s(String s) {
        s("GLOBAL", s);
    }
    public static final void s(Object o, String s) {
        s(o.getClass().getSimpleName(), s);
    }
    public static final void s(String tag, String s) {
        System.out.println(String.format("%s--%s", BBDateUtils.FDATE_LOGSTYLE.format(new Date()), s));
    }

    public static void s(String tag, String message, RuntimeException e) {
        s(tag, message + e);
    }
    
    // Risk of OOM
    public static String getLogOutAsStr() {
        try {
            return new String(Files.readAllBytes(Paths.get(BBLog.LOG_OUT())), "UTF-8");
        } catch (Exception ex) {
            Logger.getLogger("Logs").log(Level.SEVERE, null, ex);
            return "LOG UNAVAILABLE";
        }
    }

    // Risk of OOM
    public static String getLogErrAsStr() {
        try {
            return new String(Files.readAllBytes(Paths.get(BBLog.LOG_ERR())), "UTF-8");
        } catch (Exception ex) {
            Logger.getLogger("Logs").log(Level.SEVERE, null, ex);
            return "LOG UNAVAILABLE";
        }
    }
    
    public static String getLastLogsAsStr(int limit, boolean errElseOutLogs) {
        //final AtomicInteger log = new AtomicInteger(0);
        final StringBuilder sb = new StringBuilder();
        BBCollections
            .newAL(new File(LOG_DIR).listFiles((dir, name) -> { 
                return name.startsWith(errElseOutLogs ? LOG_ERR_PREF : LOG_OUT_PREF); 
            }))
            .stream()
            .sorted(Collections.reverseOrder())
            .limit(limit)
            .forEachOrdered((t) -> { 
                try {
                    sb.append("###-> LOG - ").append(t.getName())
                    .append("\n")
                    .append(new String(Files.readAllBytes(Paths.get(t.getAbsolutePath())), "UTF-8"))
                    .append("\n");
                } catch (IOException ex) {
                    Logger.getLogger(BBLog.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }
            });
        return sb.toString();
    }
    
    private static boolean inited = false;
    public static synchronized void init(final boolean enableLogs) {
        if (inited) return;
        else inited = true;
        if (enableLogs) {
            try {
                new File(LOG_DIR).mkdirs();
                System.setOut(new BBDualPrintStream(LOG_OUT(),System.out));
                System.setErr(new BBDualPrintStream(LOG_ERR(),System.err));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(BBLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
