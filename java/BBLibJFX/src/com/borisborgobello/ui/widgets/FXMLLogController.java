/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.ui.widgets;

import com.borisborgobello.ui.controllers.BBSuperController;
import com.borisborgobello.utils.BBLog;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLLogController extends BBSuperController {

    @FXML
    private Label labelErrFile;
    @FXML
    private Label labelOutFile;
    @FXML
    private TextArea taErr;
    @FXML
    private TextArea taOut;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        taOut.setText(BBLog.getLogOutAsStr());
        taErr.setText(BBLog.getLogErrAsStr());
        labelErrFile.setText(BBLog.LOG_ERR());
        labelOutFile.setText(BBLog.LOG_OUT());
    }    
    
}
