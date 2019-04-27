/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.ui;

import com.borisborgobello.jfx.BBLibInit;
import com.borisborgobello.jfx.ui.dialogs.FXMLDynamicDialogController;
import com.borisborgobello.jfx.ui.dialogs.FXMLInputDialogController;
import com.borisborgobello.jfx.ui.dialogs.FXMLProgressDialogController;
import com.borisborgobello.jfx.ui.dialogs.FXMLScrollTextDialogController;
import com.borisborgobello.jfx.ui.controllers.BBSuperContBuilder;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.ui.widgets.BBFXMLPostSetupOneShotController;
import com.borisborgobello.jfx.ui.widgets.FXMLLogController;
import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBLog;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author borisborgobello
 */
public class FXMLLoadingTest {
    
    public FXMLLoadingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        com.sun.javafx.application.PlatformImpl.startup(()->{});
        BBLog.init(true);
    }
    
    @AfterClass
    public static void tearDownClass() {
        com.sun.javafx.application.PlatformImpl.exit();
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test the inflatability of BBLib FXML
     */
    @Test
    public void testAllBBLibFXML() { // could be made automatic by auto discovery in jar
        System.out.println("testAllBBLibFXML");
        ArrayList<Class<? extends BBSuperController>> classesToTest = BBCollections.newAL(
                FXMLDynamicDialogController.class,
                FXMLInputDialogController.class,
                FXMLProgressDialogController.class,
                FXMLScrollTextDialogController.class,
                BBFXMLPostSetupOneShotController.class,
                FXMLLogController.class
                );
        
        classesToTest.forEach((c) -> {
            System.out.println("Testing controller -> " + c.getName());
            BBSuperContBuilder.inst(null, c, "TEST").buildPhase1(); 
            System.out.println("OK");
        });
    }
}
