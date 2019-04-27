package com.borisborgobello.jfx.asamples;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.borisborgobello.jfx.utils.BBLog;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.utils.BBApplication;
import com.borisborgobello.jfx.utils.BBBuildSettings;
import com.borisborgobello.jfx.utils.BBRes;
import com.borisborgobello.jfx.ui.BBUIHelper;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 *
 * @author borisborgobello
 */
public class RunApplication extends Application implements BBApplication.BBApplicationInterface {
    
    public static RunApplication getApp() { return (RunApplication) BBApplication.MAIN_APPLICATION; }
    
    @Override
    public BBBuildSettings getBuildSettings() { 
        return new BBBuildSettings() {
            @Override public boolean APP_IS_PROD_VERSION() {return true;}
            @Override public String S_MODULE_NAME() {return "BBLIB JFX";}
            @Override public String S_MODULE_VERSION() {return "1.0";}
            @Override public String LAST_VERSION_UPDATE_NOTES() {return "";}
            @Override public String S_WS_SERVER_DN_DEV() {return "localhost";}
            @Override public String S_WS_SERVER_DN_PROD() {return "localhost";}
            @Override public boolean APP_BYPASS_EXIT() {return true;}
            @Override public BBBuildSettings.AppLanguage APP_LANGUAGE() { return AppLanguage.en;}
        };
    }
    @Override
    public Image getAppIcon() { return new Image(BBRes.getRes(BBRes.DefaultAssets.S_ICON_BALL_PACMAN)); }
    
    
    public static BBFXMLMainController MAIN_CONTROLLER;
    
    @Override
    public void start(Stage stage) throws Exception {
        BBApplication.init(this, this, stage);
        
        String language = BBBuildSettings.APP_LANGUAGE_S() == BBBuildSettings.AppLanguage.vi 
                ? BBBuildSettings.AppLanguage.vi.toString() : null;
        BBUIHelper.setDefaultBundleAndTheme(BBRes.getResBundleForLanguage(language), "resources/css/modena_dark.css");
        
        BBLog.s("Welcome to BBLib !!!");
        
        load(BBFXMLMainController.class, "Welcome to BBLIB");
    }
    
    private void load(Class<? extends BBSuperController> clazz, String title) {
        try {
            FXMLLoader fxmlLoader = BBSuperController.loaderForClass(clazz);
            Parent root = fxmlLoader.load();
            
            MAIN_CONTROLLER = fxmlLoader.getController();
            BBApplication.MAIN_CONTROLLER = MAIN_CONTROLLER;
            MAIN_CONTROLLER.stage = new WeakReference<>(BBApplication.MAIN_STAGE);
            BBApplication.MAIN_STAGE.setTitle(title);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("resources/css/modena_dark.css");
            BBApplication.MAIN_STAGE.setScene(scene);
            
            BBApplication.MAIN_STAGE.show();
            
            BBApplication.MAIN_STAGE.setOnCloseRequest((_n) -> {BBUIHelper.quitApplication(); });
        } catch (IOException ex) {
            Logger.getLogger(RunApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
