/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui.dialogs;

import com.borisborgobello.jfx.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLDynamicDialogController extends BBSuperController {
    
    public static class FXMLDynamicDialogControllerBuilder {
        final FXMLDynamicDialogController currentCont;
        private FXMLDynamicDialogControllerBuilder(BBSuperController c, String title, String desc) {
            this.currentCont = (FXMLDynamicDialogController) 
                    BBSuperContBuilder.inst(c, FXMLDynamicDialogController.class, title).setStageStyle(StageStyle.UTILITY).setData(desc).build();
        }
        public static FXMLDynamicDialogControllerBuilder inst(BBSuperController c, String title) { return new FXMLDynamicDialogControllerBuilder(c, title, null); }
        public static FXMLDynamicDialogControllerBuilder inst(BBSuperController c, String title, String desc) { return new FXMLDynamicDialogControllerBuilder(c, title, desc); }
        
        public FXMLDynamicDialogControllerBuilder registerView(String name, Node n) {
            currentCont.container.getChildren().add(n);
            currentCont.customNodes.put(name, n);
            return this;
        }
        
        public FXMLDynamicDialogControllerBuilder registerView(String name, Node n, Node root) {
            currentCont.container.getChildren().add(root);
            currentCont.customNodes.put(name, n);
            return this;
        }
        public FXMLDynamicDialogControllerBuilder registerLabelTF(String name, String label, String tfText, String tfHint) {
            TextField tf = new TextField(tfText);
            tf.setPromptText(tfHint);
            Label l = new Label(label, tf);
            registerView(name, tf, l);
            return this;
        }
        public FXMLDynamicDialogControllerBuilder registerSurfaceCheck(Callback<FXMLDynamicDialogController, Boolean> cb) {
            currentCont.callback = (Object param) -> cb.call(currentCont);
            return this;
        }
        public FXMLDynamicDialogController show(boolean andWait) {
            return (FXMLDynamicDialogController) currentCont.show(andWait);
        }
    }
    
    final private HashMap<String,Node> customNodes = new HashMap<>();
    public Node getNode(String name) { return customNodes.get(name); }
    public TextField getNodeTF(String name) { return (TextField) customNodes.get(name); }
    public String getNodeTFText(String name) { return getNodeTF(name).getText(); }
    public int getNodeTFTextInt(String name) { return Integer.parseInt(getNodeTFText(name)); }
    public boolean getNodeTFTextIntCheck(BBSuperController c, String name) { 
        try { getNodeTFTextInt("width"); return true; } catch (Exception e) { c.notifyWarn(e.toString()); return false; }
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
    public VBox container;
    
    boolean surfaceCheck() {
        if (callback == null) return true;
        else return (boolean) callback.call(this);
    }
    
    @FXML void clkOK() {
        if (surfaceCheck()) close();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
}
