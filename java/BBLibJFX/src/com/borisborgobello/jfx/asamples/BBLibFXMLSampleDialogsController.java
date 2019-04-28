/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.threading.Handler;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.ui.dialogs.BBDialogs;
import com.borisborgobello.jfx.ui.dialogs.FXMLDynamicDialogController;
import com.borisborgobello.jfx.ui.dialogs.FXMLInputDialogController;
import com.borisborgobello.jfx.utils.BBRes;
import com.borisborgobello.jfx.utils.BBTools;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleDialogsController extends BBSuperController {
    @Override public void initialize(URL url, ResourceBundle rb) {}    
    
    // ALERTS
    @FXML void clkAlertInfo() {
        BBDialogs.infoQuick(this, "Today it's windy !");
    }
    @FXML void clkAlertQuestionsMultiple() {
        ButtonBar.ButtonData choice = BBDialogs.questionQuick(this, "What do you want to eat ?", 
                new ButtonType("Icecream", ButtonBar.ButtonData.YES), 
                new ButtonType("Fries", ButtonBar.ButtonData.NO),
                new ButtonType("Tomatoes", ButtonBar.ButtonData.HELP),
                new ButtonType("Diet Coke", ButtonBar.ButtonData.APPLY)).get().getButtonData();
        
        if (choice == ButtonBar.ButtonData.APPLY) {
            notifyInfo("That's a drink!");
        } else {
            notifyInfo("Sure, 1.000.000$ please.");
        }
    }
    @FXML void clkAlertFast2() {
        notifyInfo("You took pill : " + (BBDialogs.questionQuickB(this, "Red or blue pill ? ", "Red !", "Better take blue!") ? "red" : "blue"));
    }
    @FXML void clkAlertFast3() {
        Boolean choice = BBDialogs.questionQuickTrilean(this, "Can you repeat the question ?", "Yes", "Nope", "Maybe");
        if (choice == null) notifyInfo("Please!");
        else if (choice) notifyInfo("Thanks!");
        else notifyInfo("So mean!");
    }
    @FXML void clkAlertCustom() {
        Alert a = new Alert(Alert.AlertType.ERROR, "Skynet is taking over !",
                    new ButtonType("Find shelter", ButtonBar.ButtonData.NO),
                    new ButtonType("Override Skynet", ButtonBar.ButtonData.YES));
                a.setTitle("You have 5 seconds to choose...");
        BBDialogs.decorateAlert(this, a, BBRes.DefaultAssets.S_ICON_BALL_SHUTDOWN, BBDialogs.GraphicSize.RIDICULOUS_BIG);
        notifyInfo(a.showAndWait().get().getButtonData() == ButtonBar.ButtonData.YES ? ":) !" : " :( ");
    }
    
    // FILE CHOOSERS
    @FXML void clkChooserFile() {
        File f = BBDialogs.promptForFile(this, "Choose an image file", "Image files...", "png", "jpg");
        if (f != null) { notifyInfo("You chose : " + f.getAbsolutePath()); }
    }
    @FXML void clkChooserFiles() {
        List<File> fs = BBDialogs.promptForFiles(this, "Choose an image file", "Image files...", "png", "jpg");
        if (fs != null) { notifyInfo("Number of files chosen : " + fs.size()); }
    }
    @FXML void clkChooserDir() {
        File f = BBDialogs.promptForDirectory(this, "Choose a directory...");
        if (f != null) { notifyInfo("You chose : " + f.getAbsolutePath()); }
    }
    @FXML void clkChooserSaveFile() {
        File f = BBDialogs.promptChooserToSaveFile(this, "Save file to...", "Image files...", "png");
        if (f != null) { notifyInfo("You chose : " + f.getAbsolutePath()); }
    }
    
    // CONTROLLERS
    @FXML void clkControllerDynamicD() {
        FXMLDynamicDialogController ddc = BBDialogs.dynamic(this, "Dirty job fixer...", "Please verify following sizes...")
                    .registerLabelTF("width", "Width (stitches)", ""+15, null)
                    .registerLabelTF("height", "Height (stitches)", ""+18, null)
                    .registerSurfaceCheck((FXMLDynamicDialogController param) -> 
                            param.getNodeTFTextIntCheck(BBLibFXMLSampleDialogsController.this, "width") 
                            && param.getNodeTFTextIntCheck(BBLibFXMLSampleDialogsController.this, "height"))
                    .show(true);
            
        int w1 = ddc.getNodeTFTextInt("width");
        int h1 = ddc.getNodeTFTextInt("height");
        notifyInfo(String.format("Thanks for choosing WxH = %dx%d", w1,h1));
    }
    @FXML void clkControllerInputD() {
        String result = FXMLInputDialogController.showD(this, "Please input something ! Or not. (Cancellable)", true);
        notifyInfo("Your input : " + result);
        
        result = FXMLInputDialogController.showDNotBlank(this, "Enter SYSTEM-CODE like : DMC-666. Result can't be blank.");
        notifyInfo("Your input : " + result);
        
        result = FXMLInputDialogController.showD(this, "Please write the word COOKIE", (String param) -> {
            String s = param.trim();
            if (BBTools.isEmpty(s)) {
                notifyWarn("Can't be empty!");
                return false;
            }
            if (!s.equalsIgnoreCase("cookie")) {
                notifyWarn("Incorrect!");
                return false;
            }
            return true;
        }, true).trim().toUpperCase();
        notifyInfo("Your input : " + result);
    }
    @FXML void clkControllerProgressD() {
        showProgress("Falling asleep...", 0);
        Handler.mainHandler.postDelayed(()->{showProgress("Soon...",0.2);}, 800);
        Handler.mainHandler.postDelayed(()->{showProgress("Nearly...",0.5);}, 1600);
        Handler.mainHandler.postDelayed(()->{hideProgress();}, 2000);
    }
    @FXML void clkControllerProgressScrollingTextD() {
        BBDialogs.questionQuickScollableLabel(this, 
                "What is Lorem Ipsum?\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
                "\n" +
                "Why do we use it?\n" +
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
                "\n" +
                "\n" +
                "Where does it come from?\n" +
                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                "\n" +
                "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.");
    }
}
