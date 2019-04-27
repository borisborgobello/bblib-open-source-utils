/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.handler;

import com.borisborgobello.jfx.utils.BBLog;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;

/**
 * 
 * @author borisborgobello
 * 
 * AtomicInteger i2 = new AtomicInteger(0);
        
    ISHandler.TaskStackHandler tsh = new ISHandler.TaskStackHandler("Test") {
        @Override
        public void onTaskStackFinished() {
            ISLog.s("Done with result " + i2.get());
        }
    };

    for (int i = 0; i < 1000; i++) {
        final int tmp = i;
        tsh.addTask(new ISHandler.Task("Task " + tmp) {
            @Override
            public void runNoEx() throws Exception {
                i2.addAndGet(tmp);
                //log(""+tmp*tmp);
                boolean success = Math.random() > 0.6;
                if (success) doneSuccess();
                else doneFailed(null);
            }
        });
    }

    tsh.execute(1);
 */
public abstract class BBTaskStackHandler {
    public boolean S_TSH_VERBOSE = true;
    public final String title;
    public final ArrayList<BBTask> tasksAL = new ArrayList<>();

    boolean started = false; // to improve with atomic boolean

    public BBTaskStackHandler(String title) { this.title = title; }
    public void addTask(BBTask t) { if (!started) tasksAL.add(t); }

    private final ConcurrentLinkedDeque<BBTask> tasks = new ConcurrentLinkedDeque<>();
    public int NB_TASKS = -1;
    public int NB_THREADS = 1;
    public AtomicInteger currentTaskIdx = new AtomicInteger(1);
    public AtomicInteger currentWorkingThreads = new AtomicInteger(1);

    public final ConcurrentLinkedDeque<BBTask> successTasks = new ConcurrentLinkedDeque<>();
    public final ConcurrentLinkedDeque<BBTask> failedTasks = new ConcurrentLinkedDeque<>();

    public void execute(int nbThreads) {
        if (started) return;
        started = true;

        tasks.addAll(tasksAL);
        NB_TASKS = tasks.size(); 
        NB_THREADS = nbThreads;
        currentTaskIdx.set(1);
        currentWorkingThreads.set(NB_THREADS);

        if (S_TSH_VERBOSE) { BBLog.s(String.format("[%s] TaskStack exec %d tasks", title, NB_TASKS)); }

        if (NB_THREADS <= 1) execNext();
        else {
            for (int i = 0; i < nbThreads; i++) {
                new Thread(() -> {
                    execNext();
                }).start();
            }
        }
    }

    private void execNext() {
        BBTask t = null;
        int id = -1;
        synchronized(tasks) {
            if (!tasks.isEmpty()) {
                t = tasks.pollFirst();
                id = currentTaskIdx.getAndIncrement();
            } else {
                int wt = currentWorkingThreads.decrementAndGet();
                if (wt != 0) return; // no the last thread
            }
            final double progress = 1.0*tasks.size()/NB_TASKS;
            Platform.runLater(() -> {
                onProgress(progress);
            });
        }
        if (t != null) {
            t.onTaskBeforeExecute(this, id, NB_TASKS);
            t.execute(this, id, NB_TASKS);
        } else {
            onTaskStackFinishedPriv();
        }
    }

    protected void onProgress(double progress) {}

    protected final void onTaskFinishedPriv(BBTask t, boolean success) {
        if (S_TSH_VERBOSE) { t.log("Task ended"); }
        if (success) successTasks.add(t);
        else failedTasks.add(t);
        onTaskFinished(t);
        execNext();
    }

    public void onTaskFinished(BBTask t) {}

    protected final void onTaskStackFinishedPriv() {
        Platform.runLater(() -> {
            if (S_TSH_VERBOSE) { BBLog.s(String.format("[%s] TaskStack finished %d tasks, %d success, %d failed", title, NB_TASKS, successTasks.size(), failedTasks.size())); }
            onTaskStackFinished();
        });
    }

    // Called on UI thread always
    public abstract void onTaskStackFinished();
}
