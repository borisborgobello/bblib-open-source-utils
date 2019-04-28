package com.borisborgobello.jfx.asamples;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.borisborgobello.jfx.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.ui.widgets.FXMLLogController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleMainController extends BBSuperController {
    @Override public void initialize(URL url, ResourceBundle rb) {}  
    
    @FXML void onClickThreadingSamples() {
        BBSuperContBuilder.show(this, BBLibFXMLSampleThreadingController.class, 
                "Handler samples", Modality.APPLICATION_MODAL, StageStyle.DECORATED);
    }
    
    @FXML void onClickDialogSamples() {
        BBSuperContBuilder.show(this, BBLibFXMLSampleDialogsController.class, 
                "Dialog samples", Modality.APPLICATION_MODAL, StageStyle.DECORATED);
    }
    
    @FXML void onClickTVC() {
        BBSuperContBuilder.show(this, BBLibFXMLSampleTableViewController.class, 
                "TableView sample", Modality.APPLICATION_MODAL, StageStyle.DECORATED);
    }
    
    @FXML void onClickLogs() {
        BBSuperContBuilder.show(this, FXMLLogController.class, 
                "Logs", Modality.APPLICATION_MODAL, StageStyle.DECORATED);
    }
}
