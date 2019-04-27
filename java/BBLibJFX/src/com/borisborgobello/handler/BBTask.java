/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.handler;

import com.borisborgobello.utils.BBLog;
import com.borisborgobello.utils.Callb;
import com.borisborgobello.utils.Callb2;

/**
 *
 * @author borisborgobello
 */
public abstract class BBTask implements Runnable {
        
    public static boolean PRINT_EXCEPTIONS = false;
    
    public final String title;
    public BBTask(String title) { this.title = title; }
    public BBTask(String title, Callb<BBTask> doneRun) { this(title); doneRunnable = doneRun; }
    public BBTaskStackHandler handler = null;
    public int taskIdx = -1, nbTasks = -1;

    public Object data;

    public boolean hasHandler() { return handler != null; }
    
    public void onTaskBeforeExecute(BBTaskStackHandler h, int taskIdx, int nbTasks) {}

    protected final void execute(BBTaskStackHandler h, int taskIdx, int nbTasks) {
        this.handler = h;
        this.taskIdx = taskIdx;
        this.nbTasks = nbTasks;
        if (h.S_TSH_VERBOSE) { log("Task start"); }
        run();
    }

    protected final void log(String s) {
        if (hasHandler()) BBLog.s(String.format("[%s][%d/%d](%s) -> %s", handler.title, taskIdx, nbTasks, title, s));
        else BBLog.s(String.format("%s %s", title, s));
    }

    public Callb<BBTask> doneRunnable = null;
    public Callb2<BBTask, Exception> failedRunnable = null;

    protected void doneSuccess() { 
        if (doneRunnable != null) doneRunnable.run(this);
        if (hasHandler()) handler.onTaskFinishedPriv(this, true); 
    }

    protected void doneFailed(Exception e) { 
        if (failedRunnable != null) failedRunnable.run(this, e);
        if (hasHandler()) handler.onTaskFinishedPriv(this, false); 
    }

    protected final BBTask getT() { return this; }

    @Override
    public void run() {
        try {
            runNoEx();
        } catch (Exception e) {
            log(String.format("Exception => ", e.toString()));
            if (PRINT_EXCEPTIONS) e.printStackTrace();
            doneFailed(e);
        }
    }

    public void runNoEx() throws Exception {}

}
