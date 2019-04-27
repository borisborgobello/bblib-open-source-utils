/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui.dialogs;

import com.borisborgobello.jfx.ui.dialogs.FXMLDynamicDialogController.FXMLDynamicDialogControllerBuilder;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.utils.BBRes;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 *
 * @author borisborgobello
 */
public class BBDialogs {
    
    public static String alertConfirmationAssetPath = BBRes.DefaultAssets.S_ICON_BALL_HELP;
    public static String alertErrorAssetPath = BBRes.DefaultAssets.S_ICON_BALL_KO;
    public static String alertNoneAssetPath = BBRes.DefaultAssets.S_ICON_BALL_PACMAN;
    public static String alertInformationAssetPath = BBRes.DefaultAssets.S_ICON_BALL_INFO;
    public static String alertWarningAssetPath = BBRes.DefaultAssets.S_MEDIA_PAUSE;

    private static Alert decorateAlert(BBSuperController c, Alert a) {
        final Alert.AlertType at = a.getAlertType();
        String icon;
        if (at == Alert.AlertType.CONFIRMATION) {
            icon = alertConfirmationAssetPath;
        } else if (at == Alert.AlertType.ERROR) {
            icon = alertErrorAssetPath;
        } else if (at == Alert.AlertType.INFORMATION) {
            icon = alertInformationAssetPath;
        } else if (at == Alert.AlertType.NONE) {
            icon = alertNoneAssetPath;
        } else if (at == Alert.AlertType.WARNING) {
            icon = alertWarningAssetPath;
        } else {
            return a;
        }
        a.getDialogPane().getStylesheets().add("resources/css/modena_dark.css");
        return decorateAlert(c, a, icon);
    }
    
    public static Alert fixOwner(BBSuperController c, Alert a) {
        a.initOwner(c.stage.get());
        //a.initModality(Modality.WINDOW_MODAL);
        return a;
    }

    public static Optional<ButtonType> infoQuick(BBSuperController c, String s) {
        return decorateAlert(c, new Alert(Alert.AlertType.INFORMATION, s)).showAndWait();
    }
    public static Optional<ButtonType> errorQuick(BBSuperController c, String s) {
        return decorateAlert(c, new Alert(Alert.AlertType.ERROR, s)).showAndWait();
    }
    public static Optional<ButtonType> questionQuick(BBSuperController c, String s) {
        return decorateAlert(c, new Alert(Alert.AlertType.CONFIRMATION, s)).showAndWait();
    }
    public static Optional<ButtonType> questionQuick(BBSuperController c, String s, ButtonType... buttons) {
        return decorateAlert(c,new Alert(Alert.AlertType.CONFIRMATION, s, buttons)).showAndWait();
    }
    public static boolean questionQuickB(BBSuperController c, String s, String trueBtnTitle, String falseBtnTitle) {
        return questionQuick(c, s, new ButtonType(trueBtnTitle, ButtonData.YES), new ButtonType(falseBtnTitle, ButtonData.NO)).get().getButtonData() == ButtonBar.ButtonData.YES;
        //return decorateAlert(c,new Alert(Alert.AlertType.CONFIRMATION, s)).showAndWait().get() == ButtonType.OK;
    }
    public static Boolean questionQuickTrilean(BBSuperController c, String s, String trueBtnTitle, String falseBtnTitle, String nullButton) {
        ButtonData bd = questionQuick(c, s, 
                new ButtonType(trueBtnTitle, ButtonData.YES), 
                new ButtonType(falseBtnTitle, ButtonData.NO),
                new ButtonType(nullButton, ButtonData.CANCEL_CLOSE)
        ).get().getButtonData();
        if (bd == ButtonBar.ButtonData.YES) return true;
        if (bd == ButtonBar.ButtonData.NO) return false;
        return null;
        //return decorateAlert(c,new Alert(Alert.AlertType.CONFIRMATION, s)).showAndWait().get() == ButtonType.OK;
    }
    
    public static boolean questionQuickB(BBSuperController c, String s) {
        return decorateAlert(c,new Alert(Alert.AlertType.CONFIRMATION, s)).showAndWait().get() == ButtonType.OK;
    }
    
    public static boolean questionQuickBYesNo(BBSuperController c, String s) {
        return questionQuickB(c, s, "YES", "NO");
    }
    
    public static boolean questionQuickScollableLabel(BBSuperController c, String s) {
        try {
            return FXMLScrollTextDialogController.showScrollD(c, s);
        } catch (Exception e) {
            c.criticalError(e);
        }
        return false;
    }
    public static Optional<ButtonType> noneQuick(BBSuperController c, String s) {
        return decorateAlert(c,new Alert(Alert.AlertType.NONE, s)).showAndWait();
    }
    public static Optional<ButtonType> warnQuick(BBSuperController c, String s) {
        return decorateAlert(c,new Alert(Alert.AlertType.WARNING, s)).showAndWait();
    }

    public static FXMLDynamicDialogControllerBuilder dynamic(BBSuperController c, String title, String desc) {
        return FXMLDynamicDialogControllerBuilder.inst(c, title, desc);
    }
    public static FXMLDynamicDialogControllerBuilder dynamic(BBSuperController c, String title) {
        return FXMLDynamicDialogControllerBuilder.inst(c, title);
    }
    
    
    public static enum GraphicSize {
        NORMAL, BIG, VERY_BIG, CRAZY_BIG, RIDICULOUS_BIG;

        public int sizeForGS() {
            switch (this) {
                case BIG:
                    return 110;
                case VERY_BIG:
                    return 150;
                case CRAZY_BIG:
                    return 400;
                case RIDICULOUS_BIG:
                    return 500;
                default:
                    return 80;
            }
        }
    }

    public static final Alert decorateAlert(BBSuperController c, Alert a, String icon) {
        return decorateAlert(c, a, icon, GraphicSize.NORMAL);
    }

    public static final Alert decorateAlert(BBSuperController c, Alert a, String icon, GraphicSize gs) {
        
        String path = BBRes.getRes(icon);
        int size = gs.sizeForGS();
        try {
            ImageView iv = new ImageView(path);
            iv.setFitHeight(size);
            iv.setFitWidth(size);
            iv.setPreserveRatio(true);
            a.setGraphic(iv);
        } catch (Exception ex) {
            Logger.getLogger(c.getClass().getName()).log(Level.WARNING, null, ex);
        }
        fixOwner(c, a);
        return a;
    }
    
    public static final File promptForFile(BBSuperController c, String title, String extTitle, String... exts) {
        for (int i = 0; i < exts.length; i++) {
            exts[i] = "*." + exts[i];
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, exts));
        File selectedFile = fileChooser.showOpenDialog(c.stage.get());
        return selectedFile;
    }

    public static final List<File> promptForFiles(BBSuperController c, String title, String extTitle, String... exts) {
        for (int i = 0; i < exts.length; i++) {
            exts[i] = "*." + exts[i];
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, exts));
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(c.stage.get());
        return selectedFile;
    }

    public static final File promptForDirectory(BBSuperController c, String title) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(title);
        File selectedFile = fileChooser.showDialog(c.stage.get());
        return selectedFile;
    }

    public static File promptChooserToSaveFile(BBSuperController c, String title, String extTitle, String ext) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(extTitle, "*." + ext));
        return fileChooser.showSaveDialog(c.stage.get());
    }
}
