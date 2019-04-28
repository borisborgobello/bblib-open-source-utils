/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;

/**
 *
 * @author borisborgobello
 */
public abstract class ISSuperController implements Initializable {
    
    
    public static final ResourceBundle RESOURCE_BUNDLE;
    static {
        if (BBJarUpdater.APP_LANGUAGE == BBJarUpdater.AppLanguage.vi) {
             RESOURCE_BUNDLE = ResourceBundle.getBundle("resources/UIResources", new Locale(BBJarUpdater.APP_LANGUAGE.toString()));
        } else {
            RESOURCE_BUNDLE = ResourceBundle.getBundle("resources.UIResources");
        }
    }
    
    public static ISSuperController showSafe(Class<? extends ISSuperController> clazz, String fxmlFile, String title, Stage s,
            Modality modality, StageStyle ss, Object data, Callback<Object, Object> callback) throws Exception {
        return showSafe(false, clazz, fxmlFile, title, s, modality, ss, data, callback);
    }
    
    public static ISSuperController showSafe(boolean andWait, Class<? extends ISSuperController> clazz, String fxmlFile, String title,
            Stage s, Modality modality, StageStyle ss, Object data, Callback<Object, Object> callback) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(clazz.getResource(fxmlFile), RESOURCE_BUNDLE);
        Parent view = fxmlLoader.load();
        ISSuperController controller = fxmlLoader.getController();

        if (s == null) s = new Stage();
        //s.initOwner(null);
        try { s.initModality(modality);} catch (Exception e) { System.out.println(e.getMessage()); }
        s.initStyle(ss);
        Scene scene = new Scene(view);
        if (ss == StageStyle.TRANSPARENT) {
            view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
            scene.setFill(Color.TRANSPARENT);
        }
        s.setTitle(title);
        
        s.setScene(scene);
        controller.setStage(s);
        controller.setData(data);
        controller.setCallBack(callback);
        if (andWait) s.showAndWait();
        else s.show();
        return controller;
    }
    
    public static String resPathForClass(Class<? extends Initializable> clazz){
        return clazz.getSimpleName().replaceAll("Controller", "") + ".fxml";
    }
    
    public static FXMLLoader loaderForClass(Class<? extends Initializable> clazz){
        return new FXMLLoader(clazz.getResource(resPathForClass(clazz)), RESOURCE_BUNDLE);
    }
    
    public static ISSuperController show(Class<? extends ISSuperController> clazz, String title, Stage s, Modality m, StageStyle ss, Object data, Callback<Object, Object> c) throws Exception {
        return showSafe(clazz, resPathForClass(clazz), title, s, m, ss, data, c);
    }
    
    public WeakReference<Object> data;
    public WeakReference<Stage> stage;
    public Callback<Object,Object> callback;
    public boolean resultSuccess = false;
    public Object resultObject = null;
    
    public void setData(Object o) {
        if (o == null) {
            data = null;
            return;
        }
        data = new WeakReference<>(o);
    }
    public void setCallBack(Callback<Object,Object> c) {
        callback = c;
    }
    public void setStage(Stage s) {
        if (s == null) {
            stage = null;
            return;
        }
        stage = new WeakReference<>(s);
    }
    
    public void criticalError(Exception e) {
        Logger.getLogger(ISSuperController.class.getName()).log(Level.SEVERE, null, e);
        new Alert(Alert.AlertType.ERROR, "Critical error : " + e.getMessage()).show();
    }
    
    public static final String getS(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }
}
