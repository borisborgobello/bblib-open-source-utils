/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import com.borisborgobello.BBLibInit;
import com.borisborgobello.handler.Handler;
import com.borisborgobello.ui.controllers.BBSuperController;
import com.borisborgobello.dialogs.BBDialogs;
import com.borisborgobello.ui.BBUIHelper;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author borisborgobello
 */
public class BBApplication {

    public static void postMainDelayed(Runnable runnable, long delay) {
        Handler.postMainDelayed(runnable, delay);
    }
    
    public static void postBack(Runnable back, Runnable execMain) { 
        Handler.postBack(back, execMain);
    }
    
    public static interface BBApplicationInterface {
        public Image getAppIcon();
        public BBBuildSettings getBuildSettings();
    }

    public static Application MAIN_APPLICATION;
    public static BBApplicationInterface MAIN_APPLICATION_INTERFACE;
    public static Stage MAIN_STAGE;
    public static BBSuperController MAIN_CONTROLLER = null;
    
    public static void terminate() {
        BBUIHelper.quitApplication();
    }
    
    public static void init(Application a, BBApplicationInterface i, Stage stage) {
        MAIN_APPLICATION = a;
        MAIN_APPLICATION_INTERFACE = i;
        MAIN_STAGE = stage;
        BBLibInit.init();
        BBBuildSettings.bbs = i.getBuildSettings();
        
        stage.getIcons().add(i.getAppIcon());
        stage.setOnCloseRequest((WindowEvent event) -> {
            if (BBBuildSettings.bbs.APP_BYPASS_EXIT()) return;
            
            if (BBDialogs.questionQuickB(MAIN_CONTROLLER, "Quit application ?")) {
                BBUIHelper.quitApplication();
            }
            else event.consume();
        });
        
        BBLog.s("Welcome BBApplication !!!");
        
        /*Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "App crash :(( restart the app and notify Boris\nMessage : " + e.toString()).show();
                Platform.exit();
            }
        });*/
    }
    
}
