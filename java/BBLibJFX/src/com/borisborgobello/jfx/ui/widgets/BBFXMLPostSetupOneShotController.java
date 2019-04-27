/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui.widgets;

import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBLog;
import com.borisborgobello.jfx.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author borisborgobello
 */
public class BBFXMLPostSetupOneShotController extends BBSuperController {
    
    /*public ArrayList<Node> enabled() {
        return BBCollections.newAL(
            //bloc1Button, bloc1TF
        );
    }*/
    
    public ArrayList<Node> enabledViews() { return enableViews(); }
    protected void clkGeneralButton(int idx) throws Exception {}
    protected void clkBlocButton(int bloc, String input) throws Exception {}
    
    protected boolean postSetupWork() throws Exception { return false; } // Return true to block execution
    
    
    // IMMUTABLE PART //
    // IMMUTABLE PART //
    // IMMUTABLE PART //
    
    //@FXML HBox bloc1;
    @FXML public Label bloc1Label;
    @FXML public TextField bloc1TF;
    @FXML public Button bloc1Button;
    
    //@FXML HBox bloc2;
    @FXML public Label bloc2Label;
    @FXML public TextField bloc2TF;
    @FXML public Button bloc2Button;
    
    @FXML public Button generalButton1;
    @FXML public Button generalButton2;
    @FXML public Button generalButton3;
    
    protected final ArrayList<Node> enableViews(Node ... nodes) { return BBCollections.newAL(nodes); }
    
    
    /*
        This method blocks execution of normal until the controller is closed
        -> postSetupWork will be called in any case
        -> If any view is enabled, execution will be blocked
    */
    public static void postSetupOneShot(BBFXMLPostSetupOneShotController subclass) {
        BBFXMLPostSetupOneShotController cont = (BBFXMLPostSetupOneShotController) BBSuperContBuilder
                .inst(null, subclass.getClass(), "Post Setup One shot")
                .setFxml(BBFXMLPostSetupOneShotController.class)
                .setOverrideController(true)
                .build();
        if (cont.enabledViews().isEmpty()) return;
        cont.show(true);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Node> allNodes = BBCollections.newAL(
                bloc1Label, bloc1TF, bloc1Button, 
                bloc2Label, bloc2TF, bloc2Button,
                generalButton1, generalButton2, generalButton3);
        
        ArrayList<Node> enabledNodes = enabledViews();
        for (Node n : allNodes) {
            if (!enabledNodes.contains(n)) n.setVisible(false);
        }
        
        try { 
            BBLog.s("POST WORK !"); 
            boolean wait = postSetupWork(); 
            //if (wait) return;
        }
        catch (Exception e) {
            notifyWarn(e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML void clkGeneralButtonInternal(ActionEvent event) {
        Button btn = (Button) event.getSource();
        try {
        if (btn == generalButton1) { BBLog.s("Clicked general 1"); clkGeneralButton(1); }
        else if (btn == generalButton2) { BBLog.s("Clicked general 2"); clkGeneralButton(2); }
        else if (btn == generalButton3) { BBLog.s("Clicked general 3"); clkGeneralButton(3); }
        } catch (Exception e) {
            notifyWarn(e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML void clkBlocButtonInternal(ActionEvent event) {
        Button btn = (Button) event.getSource();
        try {
            int bloc = 0;
            String txt = "";
            if (btn == bloc1Button) { bloc = 1; txt = bloc1TF.getText(); }
            if (btn == bloc2Button) { bloc = 2; txt = bloc2TF.getText(); }
            BBLog.s(String.format("Clicked %d with text => %s", bloc, txt));
            clkBlocButton(bloc, txt);
        } catch (Exception e) {
            notifyWarn(e.getMessage());
            e.printStackTrace();
        }
    }
}
