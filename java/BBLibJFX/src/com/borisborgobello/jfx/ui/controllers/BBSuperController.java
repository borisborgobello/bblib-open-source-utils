/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui.controllers;

import com.borisborgobello.jfx.utils.BBApplication;
import com.borisborgobello.jfx.utils.Callb;
import com.borisborgobello.jfx.utils.Callb2;
import com.borisborgobello.jfx.utils.BBTools;
import com.borisborgobello.jfx.ui.BBUIHelper;
import com.borisborgobello.jfx.utils.BBRes;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.util.Pair;

/**
 *
 * @author borisborgobello
 */
public abstract class BBSuperController implements Initializable {

    public static String resPathForClass(Class<? extends Initializable> clazz) {
        return clazz.getSimpleName().replaceAll("Controller", "") + ".fxml";
    }
    
    public static FXMLLoader loaderForClassNCustomFxml(Class<? extends Initializable> clazz, String fxml) {
        return loaderForClassNCustomFxml(clazz, fxml, BBRes.DEFAULT_BUNDLE);
    }

    public static FXMLLoader loaderForClassNCustomFxml(Class<? extends Initializable> clazz, String fxml, ResourceBundle bundle) {
        return new FXMLLoader(clazz.getResource(fxml), bundle);
    }

    public static FXMLLoader loaderForClass(Class<? extends Initializable> clazz, ResourceBundle bundle) {
        return new FXMLLoader(clazz.getResource(resPathForClass(clazz)), bundle);
    }
    
    public static FXMLLoader loaderForClass(Class<? extends Initializable> clazz) {
        return loaderForClass(clazz, BBRes.DEFAULT_BUNDLE);
    }
    
    public static <T extends BBSuperController> Pair<T, Parent> loadFXML(Class<T> clazz) throws IOException {
        return loadFXML(clazz, BBRes.DEFAULT_BUNDLE);
    }

    public static <T extends BBSuperController> Pair<T, Parent> loadFXML(Class<T> clazz, ResourceBundle bundle) throws IOException {
        FXMLLoader l = new FXMLLoader(clazz.getResource(resPathForClass(clazz)), bundle);
        Parent p = l.load();
        return new Pair<>(l.getController(), p);
    }
    private static Parent lastParent;

    public Parent lastLoadedView() {
        Parent tmp = lastParent;
        lastParent = null;
        return tmp;
    }
    
    public static <T extends BBSuperController> T loadUglyFXML(Class<T> clazz) throws IOException {
        return loadUglyFXML(clazz, BBRes.DEFAULT_BUNDLE);
    }

    public static <T extends BBSuperController> T loadUglyFXML(Class<T> clazz, ResourceBundle bundle) throws IOException {
        Pair<T, Parent> p = loadFXML(clazz, bundle);
        lastParent = p.getValue();
        return p.getKey();
    }

    public WeakReference<Object> data;
    public WeakReference<Stage> stage = null;
    public Callback<Object, Object> callback;
    public Callb2<BBSuperController, Object> callback2;
    public boolean resultSuccess = false;
    public Object resultObject = null;
    public ResourceBundle bundle = null;
    
    public WeakReference<BBSuperController> owner = null;

    public final BBSuperController show(boolean andWait) {
        onBeforeShow();
        if (andWait) {
            stage.get().showAndWait();
        } else {
            stage.get().show();
        }
        onAfterShow();
        return this;
    }
    
    public void onBeforeShow() {
    }

    public void onAfterShow() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
    }

    public void setData(Object o) {
        if (o == null) {
            data = null;
            return;
        }
        data = new WeakReference<>(o);
    }

    public void setCallBack(Callback<Object, Object> c) {
        callback = c;
    }

    public void setStage(Stage s) {
        if (s == null) {
            stage = null;
            return;
        }
        stage = new WeakReference<>(s);
    }

    public void tre(String msg) {
        throw new RuntimeException(msg);
    }

    public void criticalError(Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        BBUIHelper.notif("Critical error", e.getMessage()).showError();
        try {
            BBUIHelper.INFO_LABEL.setText(String.format("Error - %s", e.getMessage()));
        } catch (Exception e2) {
        }
        //ISDialogs.errorQuick(this,"Critical error, see logs and restart app : " + e.getMessage());
    }

    public void criticalError(String error) {
        criticalError(new Exception(error));
    }

    public void log(Exception e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
    }

    public final String getS(String key) {
        return bundle.getString(key);
    }

    public final void disableView(Node n) {
        n.setDisable(true);
        n.setVisible(false);
    }

    public void notifyInfo(String title, String content) {
        Platform.runLater(() -> {
            BBUIHelper.notif(title, content).showInformation();
            try {
                BBUIHelper.INFO_LABEL.setText(String.format("%s - %s", title, content));
            } catch (Exception e) {
            }
        });
    }

    public void notifyInfoShort(String title, String content) {
        Platform.runLater(() -> {
            BBUIHelper.notif(title, content, Duration.seconds(1)).showInformation();
            try {
                BBUIHelper.INFO_LABEL.setText(String.format("%s - %s", title, content));
            } catch (Exception e) {
            }
        });
    }

    public void notifyInfo(String content) {
        notifyInfo("Information", content);
    }

    public void notifyInfoShort(String content) {
        notifyInfoShort("Information", content);
    }

    public void notifyWarn(String content) {
        notifyInfo("Warning", content);
    }

    public void notifyWarn(String title, String content) {
        Platform.runLater(() -> {
            BBUIHelper.notif(title, content).showWarning();
            try {
                BBUIHelper.INFO_LABEL.setText(String.format("%s - %s", title, content));
            } catch (Exception e) {
            }
        });
    }

    public BBSuperController getC() {
        return this;
    }

    public void close() {
        Platform.runLater(() -> {
            stage.get().close();
        });
    }

    public void close(Object o) {
        Platform.runLater(() -> {
            callback.call(o);
            stage.get().close();
        });
    }

    boolean xyState = false;

    public void shakeStage() {

        Stage s = stage != null ? stage.get() : BBApplication.MAIN_STAGE;
        TimelineBuilder.create()
                .keyFrames(new KeyFrame(Duration.seconds(0.05), (ActionEvent actionEvent) -> {
                    if (xyState) {
                        s.setX(s.getX() + 10);
                        s.setY(s.getY() + 10);
                    } else {
                        s.setX(s.getX() - 10);
                        s.setY(s.getY() - 10);
                    }
                    xyState = !xyState;
        }))
                .autoReverse(true)
                .cycleCount(3)
                .build().play();
    }

    public Callb<Exception> emptyRunnable = (Exception e) -> {};

    public Callb<Exception> criticalErrorRunnable = (Exception e) -> {
        Platform.runLater(() -> {
            criticalError(e);
        });
    };

    public Callb<Exception> criticalErrorRunnableDismissProgress = (Exception e) -> {
        Platform.runLater(() -> {
            BBUIHelper.hideProgress(getC());
            criticalError(e);
        });
    };

    public Callback<Exception, Void> criticalErrorRunnable2 = (Exception e) -> {
        Platform.runLater(() -> {
            criticalError(e);
        });
        return null;
    };

    public abstract class RunnablePipeCritError implements Runnable {

        @Override
        final public void run() {
            try {
                run2();
            } catch (Exception e) {
                criticalError(e);
            }
        }

        protected abstract void run2() throws Exception;
    }

    public static boolean isEmpty(String s) {
        return BBTools.isEmpty(s);
    }

    public void showProgress(String title, double progress) {
        BBUIHelper.showProgress(this, title, progress);
    }

    public void hideProgress() {
        BBUIHelper.hideProgress(this);
    }
}

/*
    public static BBSuperController showSafe(BBSuperController owner, Class<? extends BBSuperController> clazz, String fxmlFile, String title,
            Modality modality, StageStyle ss, Object data, Callback<Object, Object> callback) throws Exception {
        return showSafe(false, owner, clazz, fxmlFile, title, modality, ss, data, callback);
    }*/

    /*
    public static BBSuperController showTSafe(boolean andWait, BBSuperController owner, Class<? extends BBSuperController> clazz, String fxmlFile, String title,
            Modality modality, StageStyle ss, Object data, Callback<Object, Object> callback) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(clazz.getResource(fxmlFile), RESOURCE_BUNDLE);
        Parent view = fxmlLoader.load();
        BBSuperController controller = fxmlLoader.getController();

        if (owner != null) {
            controller.owner = new WeakReference<>(owner);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage s = null;
                try {
                    s = new Stage();
                    try {
                        s.initOwner(owner == null ? null : owner.stage.get());
                    } catch (Exception e) {
                    }
                    s.initModality(modality);
                    s.initStyle(ss);
                    Scene scene = new Scene(view);
                    if (ss == StageStyle.TRANSPARENT) {
                        view.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
                        scene.setFill(Color.TRANSPARENT);
                    }
                    setSceneStyleSheets(scene);
                    s.setTitle(title);

                    s.setScene(scene);
                    controller.setStage(s);
                    controller.setData(data);
                    controller.setCallBack(callback);
                    controller.onBeforeShow();
                    if (andWait) {
                        s.showAndWait();
                    } else {
                        s.show();
                    }
                    controller.onAfterShow();
                } catch (Exception e) {
                    if (s != null) {
                        s.close();
                    }
                    throw e;
                }
            }
        });
        return controller;
    }*/

    /*
    public static BBSuperController buildSafe(BBSuperController owner, Class<? extends BBSuperController> clazz, boolean overrideContFactory, String title, Modality m, StageStyle ss, Object data, Callback<Object, Object> c) throws Exception {
        return buildSafe(owner, clazz, overrideContFactory, resPathForClass(clazz), title, m, ss, data, c);
    }

    public static BBSuperController buildSafe(BBSuperController owner, Class<? extends BBSuperController> clazz, String title, Modality m, StageStyle ss, Object data, Callback<Object, Object> c) throws Exception {
        return buildSafe(owner, clazz, false, resPathForClass(clazz), title, m, ss, data, c);
    }

    public static BBSuperController buildSafe(BBSuperController owner, Class<? extends BBSuperController> clazz, String fxml, String title, Modality m, StageStyle ss, Object data, Callback<Object, Object> c) throws Exception {
        return buildSafe(owner, clazz, false, fxml, title, m, ss, data, c);
    }
*/
    /*
    public static BBSuperController show(BBSuperController owner, Class<? extends BBSuperController> clazz, String title, Modality m, StageStyle ss, Object data, Callback<Object, Object> c) throws Exception {
        return showSafe(owner, clazz, resPathForClass(clazz), title, m, ss, data, c);
    }

    public static BBSuperController showFast(BBSuperController owner, Class<? extends BBSuperController> clazz, String title, Object data, Callback<Object, Object> c) {
        try {
            return showSafe(owner, clazz, resPathForClass(clazz), title, Modality.APPLICATION_MODAL, StageStyle.DECORATED, data, c);
        } catch (Exception e) {
            owner.criticalError(e);
        }
        return null;
    }*/
