/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.ui.controllers;

import com.borisborgobello.utils.BBTools;
import com.borisborgobello.ui.BBUIHelper;
import com.borisborgobello.ui.BBUITableViewUtils;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.borisborgobello.utils.BBBroadcastManager;
import com.borisborgobello.utils.BBDateUtils;

/**
 *
 * @author borisborgobello
 */
public abstract class BBTabTableViewController<T> extends BBReactChildController{
    protected Comparator<String> COMP_INT() { return BBTools.INTEGER_COMPARATOR; }
    protected Comparator<String> COMP_DOUBLE() { return BBTools.DOUBLE_COMPARATOR; }
    protected Comparator<String> COMP_DATE() { return BBDateUtils.COMPARATOR_USER_DATE; }
    
    protected ObservableValue<? extends Object> ovRowIdx(T o) {
        return ov(dataFiltered.indexOf(o) +1);
    }
    
    protected ObservableValue<? extends Object> ovDate(String s) {
        return ov(BBDateUtils.dateUserForISODate(s));
    }
    protected ObservableValue<? extends Object> ovDate(ObservableValue s) {
        return ov(BBDateUtils.dateUserForISODate((String)s.getValue()));
    }
    protected ObservableValue<? extends Object> ov(Object o) {
        return new SimpleObjectProperty<>(o);
    }
  
    protected void broadcastChange() {
        BBBroadcastManager.broadcastChange(null, BBBroadcastManager.B_SOMETHING_CHANGED);
    }
    
    protected void requestRefilter() {
        tableFilterData(dataUnfiltered);
    }
    
    protected T getSelectedItem() {
        int idx = table.getSelectionModel().getSelectedIndex();
        if (idx == -1) return null;
        return dataFiltered.get(idx);
    }

    @FXML protected TableView table;
    @FXML protected TextField tfSearch;
    
    protected ObservableList<T> dataUnfiltered;
    protected ObservableList<T> dataFiltered;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //super.initialize(url, rb);
        BBBroadcastManager.addBroadcastListener(this, BBBroadcastManager.B_SOMETHING_CHANGED);
        
        if (tfSearch != null) {
            tfSearch.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                tableFilterData(dataUnfiltered);
            });
        }
        
        try {
            initTable();
        } catch (Exception ex) {
            criticalError(ex);
        }
    }    

    @Override
    public void onBeforeShow() {
        tableReloadData();
    }

    @Override
    public void onChangeBroadcastReceived(int message) {
        tableReloadData();
    }
    
    protected void reinitTable() {
        try {
            initTable();
        } catch (Exception ex) {
            criticalError(ex);
        }
    }
    
    

    private void initTable() throws Exception {
        tablePreInitModifyColumns();
        BBUITableViewUtils.configureCellValueFactory(this, table, new BBUIHelper.TableValueMatcher<T>() {
            @Override
            public ObservableValue<?> findValue(int flatColIndx, final T d, SimpleStringProperty emptyDummy) {
                try {
                    return tableValue(flatColIndx, d, emptyDummy);
                } catch (Exception ex) {
                    criticalError(ex);
                }
                return emptyDummy;
            }
            @Override
            public Comparator<?> findComparator(TableColumn t, int flatColIdx) {
                return tableColComparator(t, flatColIdx);
            }
        });
        if (tableCentered()) BBUITableViewUtils.centerTable(table);
        tableSetupCFAndEtc();
    }
    
    protected void tableReloadData() {
        List<T> l;
        try {
            l = tableReloadUnfilteredData();
            if (l == null) {
               throw new Exception("Empty data");
            }
        } catch (Exception e) {
            table.setItems(null);
            //e.printStackTrace();
            return;
        }
        
        dataUnfiltered = FXCollections.observableArrayList(l);
        tableFilterData(dataUnfiltered);
    }
    
    protected void tableFilterData(ObservableList<T> dataUnfilteredLocal) {
        if (dataUnfilteredLocal == null || dataUnfilteredLocal.isEmpty()) {
            table.setItems(null);
            return;
        }
        String searchText = "";
        if (tfSearch != null) {
            searchText = tfSearch.getText().toLowerCase();
            if (searchText.trim().equals("")) {
                searchText = "";
            }
        }
        
        dataFiltered = FXCollections.observableArrayList();
        
        for (T item : dataUnfilteredLocal) {
            if (tableItemPassFilters(item, searchText)) dataFiltered.add(item);
        }
        tableAfterFilterSortData(dataFiltered);
        table.setItems(dataFiltered);
    }
    
    abstract protected ObservableValue<?> tableValue(int flatColIndx, final T object, SimpleStringProperty emptyDummy);
    abstract protected Comparator<?> tableColComparator(TableColumn t, int flatColIdx);
    protected boolean tableCentered() { return true; }
    abstract protected List<T> tableReloadUnfilteredData();
    protected void tableSetupCFAndEtc() {}
    protected boolean tableItemPassFilters(T rowItem, String searchTxtLowerCased) { return true; }
    protected void tableAfterFilterSortData(ObservableList<T> dataFiltered) {}
    protected void tablePreInitModifyColumns() {}
}
