/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.utils.BBAllParsers;
import com.borisborgobello.jfx.utils.BBBroadcastManager;
import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBRes;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleUtilsController extends BBSuperController {

    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    void jsonParsingNSaveNCrypto() throws Exception {
        byte[] jsonData = BBRes.getResAsBytes(BBRes.getRes("borisborgobello", "countrypn.json"));
        ArrayList<BBLibSampleCountryPhone> al = BBCollections.newAL(
                    BBAllParsers.getPOJOFromBytes(jsonData, BBLibSampleCountryPhone[].class, false));
        
        
        // Basic encryption
        // Should be replaced with user key
        // Encrypt
        byte[] cryptedObject = BBAllParsers.getValuePOJOasBytes(al, true);
        // Decrypt
        BBLibSampleCountryPhone[] result = BBAllParsers.getPOJOFromBytes(cryptedObject, BBLibSampleCountryPhone[].class, true);
    }
    
    void notifCenter() {
        BBBroadcastManager.addBroadcastListener((int message) -> { notifyInfo("Boo!"); }, 0);
        BBBroadcastManager.broadcastChange(null, 0);
    }
}
