/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.ui.BBUITableViewUtils;
import com.borisborgobello.jfx.ui.controllers.BBTabTableViewController;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;

/**
 * FXML Controller class
 *
 * @author borisborgobello
 */
public class BBLibFXMLSampleTableViewController extends BBTabTableViewController<BBLibSampleCountryPhone> {
    
    final static String GOOGLE_IMG_URL = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
    
    @FXML CheckBox cbSpecialSort;
    
    @FXML void clkSpecialSort() {
        broadcastChange();
    }
    
    // Can be considered as distant data to synchronize on
    final private ObservableList<BBLibSampleCountryPhone> realData = FXCollections.observableArrayList(BBLibSampleCountryPhone.load());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }

    @Override
    protected List<BBLibSampleCountryPhone> tableReloadUnfilteredData() {
        return realData;
    }

    @Override
    protected boolean tableItemPassFilters(BBLibSampleCountryPhone rowItem, String searchTxt) {
        return rowItem.toSearchStr().contains(searchTxt);
    }

    @Override
    protected void tableAfterFilterSortData(ObservableList<BBLibSampleCountryPhone> dataFiltered) {
        if (cbSpecialSort.isSelected()) {
            dataFiltered.sort((o1, o2) -> { return Integer.compare(o1.getDialCodeInt(), o2.getDialCodeInt()); });
        }
    }

    @Override
    protected ObservableValue<?> tableValue(int flatColIndx, BBLibSampleCountryPhone d, SimpleStringProperty emptyDummy) {
        switch (flatColIndx) {
            case 0:return ovRowIdx(d);
            case 1:return ov(d.name);
            case 2:return ov(d.dialCode);
            case 3:return ov(d.code);
            case 4:return ov(d.getDialCodeInt());
            case 7:return ov(GOOGLE_IMG_URL);
            case 9:return ov("FF00FF|00FF00");
        }
        return emptyDummy;
    }

    // To add specific comparators
    @Override
    protected Comparator<String> tableColComparator(TableColumn t, int flatColIdx) {
        switch (flatColIdx) {
        }
        return null;
    }

    @Override
    protected void tableSetupCFAndEtc() {
        
        BBUITableViewUtils.configureDynamiqueButtonCellFactory(table, 5, new BBUITableViewUtils.DynamiqueButtonCallbacks<BBLibSampleCountryPhone>() {
            @Override
            public boolean shouldDiplayButton(int idx, BBLibSampleCountryPhone item) {
                return Math.random() > 0.5;
            }
            @Override
            public String buttonTitle(int idx, BBLibSampleCountryPhone item) {
                return "Delete " + item.code + " ?";
            }
            @Override
            public void buttonClicked(int idx, BBLibSampleCountryPhone item) {
                notifyInfo("Deleted " + item.code);
                realData.remove(item);
                broadcastChange();
            }
            @Override public void setupButton(Button btn, BBLibSampleCountryPhone item) {}
        });
        
        
        BBUITableViewUtils.configureDynamiqueSuperCell(table, 6, new BBUITableViewUtils.DynamiqueTableCellCallbacks<BBLibSampleCountryPhone>() {
            @Override
            public boolean dtcIsEmptyCell(int rowIdx, BBLibSampleCountryPhone rowItem, Object cellValue, boolean empty) {
                return Math.random() > 0.5;
            }
            @Override
            public boolean dtcUpdate(BBUITableViewUtils.DynamiqueTableCell<BBLibSampleCountryPhone> cell, int idx, BBLibSampleCountryPhone rowItem, Object cellValue, boolean empty) throws Exception {
                int die = (int) (Math.random()*6 + 0.5);
                switch (die) {
                    case 0: cell.loadBackgroundColor("#FF0000");
                    case 1: cell.loadBackgroundColors("#FF0000", "#00FF00", "0000FF");
                    case 2: cell.loadButton("Dynamic Button").setText("BUTTON");
                    case 3: cell.loadCheckBox(false);
                    case 4: cell.loadImage(40, 40, GOOGLE_IMG_URL);
                    case 5: cell.loadSmallButton("Small button !");
                    case 6: cell.loadText("That's text!");
                }
                return true;
            }
        });
        
        BBUITableViewUtils.configureImageCellFactory(table, 7, 40, 40, false);
        BBUITableViewUtils.configureButtonCellFactory(table, 8, "Clone", (BBLibSampleCountryPhone param) -> { notifyInfo("Clicked " + param.code); return null; });
        BBUITableViewUtils.configureDualBackgroundFactory(table, 9);
        
        BBUITableViewUtils.configureDynamiqueImageCF(table, 10, new BBUITableViewUtils.DynamiqueImageCFCallbacks<BBLibSampleCountryPhone>() {
            @Override
            public boolean shouldDisplayImage(int idx, BBLibSampleCountryPhone item) {
                return Math.random() > 0.5;
            }

            @Override
            public int getWidth(int idx, BBLibSampleCountryPhone item) {
                return 110;
            }

            @Override
            public int getHeight(int idx, BBLibSampleCountryPhone item) {
                return 110;
            }

            @Override
            public String getUrl(int idx, BBLibSampleCountryPhone item) {
                return GOOGLE_IMG_URL;
            }
        });
    }
}
