/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.dialogs;

import com.borisborgobello.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.ui.controllers.BBSuperController;
import com.borisborgobello.utils.BBColorUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLProgressDialogController extends BBSuperController {
    
    public static BBSuperController showProgressD(BBSuperController c, String title) throws Exception {
        return BBSuperContBuilder.inst(c, FXMLProgressDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL).setStageStyle(StageStyle.UTILITY)
                .showAndWait();
    }
    
    public static BBSuperController showProgressDTSafeAsync(BBSuperController c, String title) throws Exception {
        return BBSuperContBuilder.inst(c, FXMLProgressDialogController.class, title)
                .setModality(Modality.APPLICATION_MODAL).setStageStyle(StageStyle.UTILITY)
                .buildTSAsync(true);
    }
    
    @FXML
    Label progressLabel;
    @FXML
    ProgressBar progressBar;
    
    boolean isDone = false;
    
    @Override
    public void onBeforeShow() {
        stage.get().setOnCloseRequest((WindowEvent event) -> {
            if (!isDone) event.consume();
        });
    }
    
    public void dismiss() {
        Platform.runLater(() -> {
            isDone = true;
            stage.get().close();
        });
    }
    
    public void updateProgress(double progress) {
        Platform.runLater(() -> {
            progressBar.setProgress(progress);
            progressBar.setStyle(BBColorUtils.getStyleForProgress(progress));
        });
    }
    
    public void updateTitle(String s) { 
        Platform.runLater(() -> {
            stage.get().setTitle(s);
        });
    }
    
    public void updateMessage(String s) { 
        Platform.runLater(() -> {
            progressLabel.setText(s);
        });
    }
}
