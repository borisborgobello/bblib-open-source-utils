/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.threading;

import com.borisborgobello.jfx.utils.BBLog;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;

/**
 * 
 * @author borisborgobello
 * 
 * A class responsible for creating and feeding threads for a stack of tasks
 * to do. Cannot be stopped once started. If executed over one thread, execution
 * is synchronous (careful with main thread).
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
    
    public long startns;
    public long stopns;

    public void execute(int nbThreads) {
        if (started) return;
        started = true;

        tasks.addAll(tasksAL);
        NB_TASKS = tasks.size(); 
        NB_THREADS = nbThreads;
        currentTaskIdx.set(1);
        currentWorkingThreads.set(NB_THREADS);

        if (S_TSH_VERBOSE) { BBLog.s(String.format("[%s] TaskStack exec %d tasks", title, NB_TASKS)); }

        startns = System.nanoTime();
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
        stopns = System.nanoTime();
        Platform.runLater(() -> {
            if (S_TSH_VERBOSE) { 
                BBLog.s(String.format("[%s] TaskStack finished %d tasks, %d success, %d failed", title, NB_TASKS, successTasks.size(), failedTasks.size())); 
                BBLog.s(String.format("[%s] TaskStack finished in %d ms", title, (stopns-startns)/1000000)); 
            }
            onTaskStackFinished();
        });
    }

    // Called on UI thread always
    public abstract void onTaskStackFinished();
}
