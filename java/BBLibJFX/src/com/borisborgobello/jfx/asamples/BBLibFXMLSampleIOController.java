/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleIOController extends BBSuperController {
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    @FXML TextField tfURL;
    
    @FXML void clkGetWithProgress() {
        
    }
}
