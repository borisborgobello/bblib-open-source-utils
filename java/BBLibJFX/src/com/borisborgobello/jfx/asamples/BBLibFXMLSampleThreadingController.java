/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.ui.dialogs.FXMLProgressDialogController;
import com.borisborgobello.jfx.threading.BBTask;
import com.borisborgobello.jfx.threading.BBTaskStackHandler;
import com.borisborgobello.jfx.threading.Handler;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.utils.BBLog;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleThreadingController extends BBSuperController {
    @Override public void initialize(URL url, ResourceBundle rb) {}  
    
    // TaskStack

    @FXML TextField tfCalcIntensiveness;
    @FXML CheckBox cbPrintToSTD;
    @FXML void clkStackHandler1T() {parallelizeWithTaskStack(1);}
    @FXML void clkStackHandler5T() {parallelizeWithTaskStack(5);}
    
    void parallelizeWithTaskStack(int nbThreads) {
        parallelizeWithTaskStack(nbThreads, Long.parseLong(tfCalcIntensiveness.getText()), cbPrintToSTD.isSelected());
    }
    
    void parallelizeWithTaskStack(int nbThreads, long calculationIntensiveness, boolean printToSTD) {
        final long finalCalculationIntensiveness = Math.min(calculationIntensiveness, 30000);
        
        // Declare the handler
        BBTaskStackHandler stack = new BBTaskStackHandler("PARALLEL TASK") {
            @Override
            public void onTaskStackFinished() {
                notifyInfo(String.format("All done : success ratio = %d/%d, and took %d ms", successTasks.size(), NB_TASKS, (stopns-startns)/1000000));
            }
        };
        if (!printToSTD) stack.S_TSH_VERBOSE = false;
        
        // Create the tasks
        for (int i = 1; i <= 20; i++) {
            final Integer myTaskData = i;
            stack.addTask(new BBTask("Task"+myTaskData) {
                @Override
                public void run() {
                    BBLog.s("Working task : "+myTaskData);
                    // waste time
                    for (int i = 0; i < finalCalculationIntensiveness*finalCalculationIntensiveness; i++) { int b = i*i; b++; }
                    
                    if ((int)(Math.random()+0.5) == 0) doneSuccess(); // doneSuccess or doneFailed must be called exactly one time per task
                    else doneFailed(new Exception("Bad luck"));
                }
            });
        }
        
        // Execute tasks on a specific amount of threads
        stack.execute(nbThreads);
    }
    
    // Android handler system
    
    @FXML void clkHandlerWithLoopingThread() {
        showProgress("Looping on main thread", 0);
        
        // Delay on main thread
        Handler.mainHandler.postDelayed(()->{showProgress("After 2 secs, still here ?", 0.4);}, 2000);
        Handler.mainHandler.postDelayed(()->{showProgress("After 4 secs, nearlyDone ?", 0.8);}, 4000);
        
        // On background thread
        long barrier = System.currentTimeMillis() + 8000;
        Handler h2 = Handler.newHandler(false, "Background handler");
        final Runnable lambda = new Runnable() {
            @Override
            public void run() {
                long remainingTimeMs = barrier - System.currentTimeMillis();
                if (remainingTimeMs < 0) { Handler.mainHandler.postDelayed(()->{hideProgress();}, 0); return; }

                double progress = 1.0 - (remainingTimeMs/4000.0);
                showProgress("Just kidding, not yet !", progress); // progress update is thread safe from controller
                h2.postDelayed(this, 20);
            }
        };
        h2.postDelayed(lambda, 4500);
    }
}
