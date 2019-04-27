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
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class FXMLScrollTextDialogController extends BBSuperController {
    
    public static boolean showScrollD(BBSuperController c2, String content) throws Exception {
        return showScrollD(c2, content, false, false);
    }
    
    public static boolean showScrollD(BBSuperController c2, String content, boolean editable, boolean verticalOnly) throws Exception {
        FXMLScrollTextDialogController c = (FXMLScrollTextDialogController) BBSuperContBuilder.inst(c2, FXMLScrollTextDialogController.class, "")
                .setModality(Modality.APPLICATION_MODAL).setStageStyle(StageStyle.UNDECORATED).setData(content).showAndWait();
        return c.choseOK;
    }
    
    @FXML
    TextArea ta;
    
    boolean choseOK = false;
    
    
    @FXML public void onOK() {
        choseOK = true;
        stage.get().close();
    }
    @FXML public void onKO() {
        choseOK = false;
        stage.get().close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}
    @Override
    public void setData(Object o) {
        super.setData(o); //To change body of generated methods, choose Tools | Templates.
        String content = (String) o;
        ta.setText(content);
    }
}
