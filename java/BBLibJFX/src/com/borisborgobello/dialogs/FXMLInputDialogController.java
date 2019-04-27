/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.dialogs;

import com.borisborgobello.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.ui.controllers.BBSuperController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLInputDialogController extends BBSuperController {
    
    // Must be called from main thread
    public static String showDNotBlank(BBSuperController c, String title) {
        FXMLInputDialogController cont = (FXMLInputDialogController) BBSuperContBuilder.inst(c, FXMLInputDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.UTILITY)
                .setCallbackBidir((Object param) -> {
                    boolean blank = ((String)param).trim().length() == 0;
                    if (blank) {
                        c.notifyWarn("Empty field");
                    }
                    return !blank;
                }).showAndWait();
        return cont.tf.getText();
    }
    
    // Must be called from main thread
    public static String showD(BBSuperController c, String title, Callback<String, Boolean> check) {
        FXMLInputDialogController cont = (FXMLInputDialogController) BBSuperContBuilder.inst(c, FXMLInputDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.UTILITY)
                .setCallbackBidir(check == null ? null : (Object param) -> { return check.call((String) param); })
                .showAndWait();
        return cont.tf.getText();
    }
    
    public static String showD(BBSuperController c, String title, Callback<String, Boolean> check, boolean cancellable) {
        if (!cancellable) return showD(c, title, check);
        FXMLInputDialogController cont = (FXMLInputDialogController) BBSuperContBuilder.inst(c, FXMLInputDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL)
                .setStageStyle(StageStyle.UTILITY)
                .setCallbackBidir(check == null ? null : (Object param) -> { return check.call((String) param); }).build();
        cont.label.setText(title);
        cont.cancellable = true;
        cont.show(true);
        return cont.cancelled ? null : cont.tf.getText();
    }
    
    public static String showD(BBSuperController c, String title, boolean cancellable) {
        return showD(c, title, null, cancellable);
    }

    @Override
    public void setData(Object o) {
        super.setData(o);
        if (o instanceof String) {
            label.setText((String) o);
        }
    }
    
    @FXML
    public Label label;
    @FXML
    public TextField tf;
    @FXML
    public Button btnCancel;
    
    boolean cancellable = false;
    boolean cancelled = false;

    @Override
    public void onBeforeShow() {
        super.onBeforeShow();
        if (cancellable) {
            btnCancel.setOnAction((ActionEvent event) -> {
                cancelled = true;
                close();
            });
        } else {
            ((HBox)(btnCancel.getParent())).getChildren().remove(btnCancel);
        }
    }
    
    boolean surfaceCheck() {
        if (callback == null) return true;
        else return (boolean) callback.call(tf.getText());
    }
    
    @FXML void clkOK() {
        if (surfaceCheck()) close();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tf.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                clkOK();
            }
        });
    }
}
