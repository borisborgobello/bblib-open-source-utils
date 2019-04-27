/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui.controllers;

import com.borisborgobello.jfx.utils.BBLog;
import com.borisborgobello.jfx.utils.BBRes;
import com.borisborgobello.jfx.utils.Callb;
import com.borisborgobello.jfx.utils.Callb2;
import com.borisborgobello.jfx.ui.BBUIHelper;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 *
 * @author borisborgobello
 *
 * A convenient class following the builder design pattern in order to build
 * BBSuperControllers
 */
public class BBSuperContBuilder {

    public static BBSuperContBuilder inst(BBSuperController owner, Class<? extends BBSuperController> clazz, String title) {
        return new BBSuperContBuilder(owner, clazz, title);
    }
    
    public static BBSuperContBuilder inst(BBSuperController owner, Class<? extends BBSuperController> clazz, String title,
            Modality m, StageStyle ss) {
        return inst(owner, clazz, title).setStageStyle(ss).setModality(m);
    }
    
    public static BBSuperController show(BBSuperController owner, Class<? extends BBSuperController> clazz, String title, Modality m, StageStyle ss, Object data) {
        return inst(owner, clazz, title, m, ss).setData(data).show();
    }
    
    public static BBSuperController show(BBSuperController owner, Class<? extends BBSuperController> clazz, String title, Modality m, StageStyle ss) {
        return show(owner, clazz, title, m, ss, null, null);
    }
    
    public static BBSuperController show(BBSuperController owner, Class<? extends BBSuperController> clazz, String title,
            Modality m, StageStyle ss, Callb<BBSuperContBuilder> setupCB) {
        return show(owner, clazz, title, m, ss, setupCB, null);
    }
    
    public static BBSuperController show(BBSuperController owner, Class<? extends BBSuperController> clazz, String title,
            Modality m, StageStyle ss, Callb<BBSuperContBuilder> setupCB, Callb<BBSuperController> afterShow) {
         BBSuperContBuilder b = inst(owner, clazz, title, m, ss);
         if (setupCB != null) setupCB.run(b);
         BBSuperController c = b.build();
         b.show();
         if (afterShow != null) afterShow.run(c);
         return c;
    }

    BBSuperController owner;
    Class<? extends BBSuperController> clazz;
    String title;
    String fxml = null;
    boolean overrideController = false;

    Modality mod = Modality.APPLICATION_MODAL;
    StageStyle ss = StageStyle.DECORATED;

    Object data = null;
    Callb<Object> callback = null;
    Callb2<BBSuperController, Object> callback2 = null;
    Callback<Object, Object> callbackBidir = null;

    String language = null;
    String themeCSS = null;

    public BBSuperContBuilder(BBSuperController owner, Class<? extends BBSuperController> clazz, String title) {
        setOwner(owner);
        setClass(clazz);
        setTitle(title);
    }

    public final BBSuperContBuilder setOwner(BBSuperController owner) {
        this.owner = owner;
        return this;
    }

    public final BBSuperContBuilder setClass(Class<? extends BBSuperController> clazz) {
        this.clazz = clazz;
        return this;
    }

    public final BBSuperContBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public final BBSuperContBuilder setModality(Modality mod) {
        this.mod = mod;
        return this;
    }

    public final BBSuperContBuilder setStageStyle(StageStyle ss) {
        this.ss = ss;
        return this;
    }

    public final BBSuperContBuilder setData(Object data) {
        this.data = data;
        return this;
    }

    public final BBSuperContBuilder setCallback(Callb<Object> callback) {
        this.callback = callback;
        return this;
    }

    public final BBSuperContBuilder setCallback2(Callb2<BBSuperController, Object> callback2) {
        this.callback2 = callback2;
        return this;
    }

    public final BBSuperContBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public final BBSuperContBuilder setTheme(String themeCSS) {
        this.themeCSS = themeCSS;
        return this;
    }

    public final BBSuperContBuilder setCallbackBidir(Callback<Object, Object> callback) {
        this.callbackBidir = callback;
        return this;
    }

    public BBSuperContBuilder setFxml(
            Class<? extends BBSuperController> packageClass,
            Class<? extends BBSuperController> controllerNameClass) {
        fxml = packageClass.getName() + "#"
                + BBSuperController.resPathForClass(controllerNameClass);
        return this;
    }

    public BBSuperContBuilder setFxml(
            Class<? extends BBSuperController> packageClass,
            String fxmlNameWithExt) {
        fxml = packageClass.getName() + "#" + fxmlNameWithExt;
        return this;
    }

    // Controller must be in same package as fxml file
    public BBSuperContBuilder setFxml(Class<? extends BBSuperController> controller) {
        fxml = controller.getName() + "#" + BBSuperController.resPathForClass(controller);
        return this;
    }

    public BBSuperContBuilder setOverrideController(boolean ov) {
        this.overrideController = ov;
        return this;
    }
    
    // For convenience
    public final void buildTSAsync(Callb<BBSuperController> cb) {
        Platform.runLater(() -> { cb.run(build()); });
    }
    
    // Can be called from background thread
    // This phase loads the controller and the view
    public final <T extends BBSuperController> Pair<T, Parent> buildPhase1() {
        try {
            T controller = null;
            String fxmlFile = fxml == null ? BBSuperController.resPathForClass(clazz) : fxml;

            URL u = null;
            if (fxmlFile.contains("#")) { // Class in same package of FXML + FXML name
                String[] t = fxmlFile.split("#");
                u = Class.forName(t[0]).getResource(t[1]);
            } else {
                u = clazz.getResource(fxmlFile);
            }
            ResourceBundle bundle = language == null ? BBUIHelper.DEFAULT_RESOURCE_BUNDLE : BBRes.getResBundleForLanguage(language);
            FXMLLoader fxmlLoader = new FXMLLoader(u, bundle);
            if (overrideController) {
                fxmlLoader.setControllerFactory((Class<?> param) -> {
                    try {
                        return clazz.newInstance();
                    } catch (Exception ex) {
                        BBLog.s("Error : Lambda classes and private classes are not supported. Use a public static class");
                        ex.printStackTrace();
                        return null;
                    }
                });
            }

            Parent view = fxmlLoader.load();
            controller = fxmlLoader.getController();
            controller.bundle = bundle;

            if (owner != null) {
                controller.owner = new WeakReference<>(owner);
            }
            return new Pair<>(controller, view);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    
    // Needs to be called on main thread
    public final <T extends BBSuperController> T buildPhase2(Pair<T, Parent> controllerViewPair) {
        // Unify callbacks
        Callback<Object, Object> cb = callbackBidir;
        if (cb == null && callback != null) {
            cb = (Object param) -> {
                callback.run(param);
                return null;
            };
        }
        
        Parent view = controllerViewPair.getValue();
        T controller = controllerViewPair.getKey();
        Stage s = null;
        try {
            s = new Stage();
            try {
                s.initOwner(owner == null ? null : owner.stage.get());
            } catch (Exception e) {
            }
            s.initModality(mod);
            s.initStyle(ss);
            Scene scene = new Scene(view);
            if (ss == StageStyle.TRANSPARENT) {
                view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
                scene.setFill(Color.TRANSPARENT);
            }
            BBUIHelper.setSceneStyleSheets(scene, themeCSS == null ? BBUIHelper.DEFAULT_THEME_CSS : themeCSS);
            s.setTitle(title);

            s.setScene(scene);
            controller.setStage(s);
            controller.setData(data);
            controller.setCallBack(cb);
        } catch (Exception e) {
            if (s != null) {
                s.close();
            }
            throw e;
        }
        controller.callback2 = this.callback2;
        return controller;
    }

    public final <T extends BBSuperController> T build() {
        try {
            return buildPhase2(buildPhase1());
        } catch (Exception ex) {
            if (owner != null) {
                owner.criticalError(ex);
            }
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    // Call when controller TS is needed, controller is to be returned synchronously
    // but the display will be done later on main thread
    public final <T extends BBSuperController> T buildTSAsync(Boolean showAndWait) {
        try {
            Pair<T,Parent> cvPair = buildPhase1();
            Platform.runLater(() -> {
                buildPhase2(cvPair);
                if (showAndWait != null) {
                    cvPair.getKey().show(showAndWait);
                }
            });
            return cvPair.getKey();
        } catch (Exception ex) {
            if (owner != null) {
                owner.criticalError(ex);
            }
            ex.printStackTrace();
        }
        return null;
    }
    
    public final BBSuperController show() {
        return build().show(false);
    }

    public final BBSuperController showAndWait() {
        return build().show(true);
    }
}
