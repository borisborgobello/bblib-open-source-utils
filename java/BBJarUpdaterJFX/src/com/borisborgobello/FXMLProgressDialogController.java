/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLProgressDialogController extends ISSuperController {
    
    public static ISSuperController showProgressD(String title, Stage toUse) throws Exception {
        return showSafe(FXMLProgressDialogController.class, resPathForClass(FXMLProgressDialogController.class),
                title, toUse, Modality.APPLICATION_MODAL, StageStyle.UNDECORATED, null, null);
    }
    
    @FXML
    Label progressLabel;
    @FXML
    ProgressBar progressBar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    
    public void dismiss() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.get().close();
            }
        });
    }
    
    public void updateProgress(double progress) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
        progressBar.setStyle(getStyleForProgress(progress));
            }
        });
    }
    
    public void updateTitle(String s) { 
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressLabel.setText(s);
            }
        });
         }

    private String getStyleForProgress(double progress) {
        return "-fx-accent: " + getColorForProgress(progress);
    }

    private String getColorForProgress(double progress) {
        if (progress < 0.5) {
            String hex = Integer.toHexString((int) (progress*2*255));
            if (hex.length() == 1) hex = "0" + hex;
            return "#FF" + hex + "00";
        }
        else {
            String hex = Integer.toHexString((int) ((1-progress)*2*255));
            if (hex.length() == 1) hex = "0" + hex;
            return "#" + hex + "FF00";
        } // 1 - 2progress +1 = 2(1 - progress)*255
    }
}
