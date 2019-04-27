/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import com.borisborgobello.jfx.dialogs.BBDialogs;
import com.borisborgobello.jfx.ui.BBUIHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javafx.stage.FileChooser;
import javafx.util.Pair;

/**
 *
 * @author borisborgobello
 */
public class BBCSV {

    protected static final String S_CSV_SEPARATOR_ROW = "\n";
    protected static final String S_CSV_SEPARATOR_COLUMN = ",";

    public static void writeCSVFileO(File file, ArrayList<ArrayList<Object>> table) throws IOException {
        file.getParentFile().mkdirs();
        StringBuilder csvBuilder = new StringBuilder();

        for (ArrayList<Object> row : table) {
            for (Object column : row) {
                if (!(column instanceof Integer || column instanceof Double)) {
                    csvBuilder.append("\"");
                }
                csvBuilder.append(column);
                if (!(column instanceof Integer || column instanceof Double)) {
                    csvBuilder.append("\"");
                }
                csvBuilder.append(S_CSV_SEPARATOR_COLUMN);
            }
            csvBuilder.append(S_CSV_SEPARATOR_ROW);
        }
        csvBuilder.append(S_CSV_SEPARATOR_ROW);

        Files.write(file.toPath(), csvBuilder.toString().getBytes());
    }

    public static void writeCSVFile(File file, ArrayList<ArrayList<String>> table) throws IOException {
        file.getParentFile().mkdirs();
        StringBuilder csvBuilder = new StringBuilder();

        for (ArrayList<String> row : table) {
            for (String column : row) {
                csvBuilder.append("\"");
                csvBuilder.append(column);
                csvBuilder.append("\"");
                csvBuilder.append(S_CSV_SEPARATOR_COLUMN);
            }
            csvBuilder.append(S_CSV_SEPARATOR_ROW);
        }

        Files.write(file.toPath(), csvBuilder.toString().getBytes());
    }

    public static void quickRepRow(ArrayList<ArrayList<String>> dataTable, String line, boolean title, int lineSkipBefore, int lineSkipAfter) {
        for (int i = 0; i < lineSkipBefore; i++) {
            dataTable.add(new ArrayList<>());
        }

        if (title) {
            line = "###### " + line + " ######";
        }

        ArrayList<String> lineA = new ArrayList<>();
        lineA.add(line);
        dataTable.add(lineA);

        for (int i = 0; i < lineSkipAfter; i++) {
            dataTable.add(new ArrayList<>());
        }
    }

    public static void quickRepRowO(ArrayList<ArrayList<Object>> dataTable, Object line, boolean title, int lineSkipBefore, int lineSkipAfter) {
        for (int i = 0; i < lineSkipBefore; i++) {
            dataTable.add(new ArrayList<>());
        }

        if (title) {
            line = "###### " + line + " ######";
        }

        ArrayList<Object> lineA = new ArrayList<>();
        lineA.add(line);
        dataTable.add(lineA);

        for (int i = 0; i < lineSkipAfter; i++) {
            dataTable.add(new ArrayList<>());
        }
    }

    public static void quickRepRow(ArrayList<ArrayList<String>> dataTable, String line) {
        quickRepRow(dataTable, line, false, 0, 0);
    }

    public static void quickRepRowParams(ArrayList<ArrayList<String>> dataTable, String... str) {
        ArrayList<String> lineA = new ArrayList<>();
        lineA.addAll(Arrays.asList(str));
        dataTable.add(lineA);
    }

    public static void quickRepRowParamsO(ArrayList<ArrayList<Object>> dataTable, Object... str) {
        ArrayList<Object> lineA = new ArrayList<>();
        lineA.addAll(Arrays.asList(str));
        dataTable.add(lineA);
    }

    public static void quickRepRowO(ArrayList<ArrayList<Object>> dataTable, Object line) {
        quickRepRowO(dataTable, line, false, 0, 0);
    }

    public static final Pair<File, HashMap<String, String>> parseBicolumnCSV(BBSuperController c) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(BBApplication.MAIN_STAGE);
        if (selectedFile == null) {
            return null;
        }

        String barcodes;

        try {
            barcodes = new String(Files.readAllBytes(selectedFile.toPath()));
        } catch (IOException ex) {
            c.criticalError(ex);
            return null;
        }

        HashMap<String, String> refToBarcode = new HashMap<>();
        barcodes = barcodes.replaceAll("\r", "");
        String[] table = barcodes.split("\n");
        for (String s : table) {
            String[] columns = s.split(",");
            if (columns.length < 2) {
                continue;
            }
            if (refToBarcode.containsKey(columns[0])) {
                throw new RuntimeException("Duplicate !!" + columns[0]);
            }
            refToBarcode.put(columns[0], columns[1]);
        }

        StringBuilder sb = new StringBuilder("Please verify following information :\n");
        sb.append("Rows : ").append(refToBarcode.size()).append("\n");
        for (String ref : refToBarcode.keySet()) {
            sb.append(ref).append("\t-->\t").append(refToBarcode.get(ref)).append("\n");
        }

        if (BBDialogs.questionQuickScollableLabel(c, sb.toString())) {
            return new Pair<>(selectedFile, refToBarcode);
        }
        return null;
    }

    public static File promptForCSV(BBSuperController c) {
        return BBUIHelper.promptForFile(c, "Choose CSV file", "CSV files", "csv");
    }

    public static File promptForSaveCSV(BBSuperController c) {
        return BBUIHelper.promptChooserToSaveFile(c, "Saving to CSV...", "CSV files", "csv");
    }
}
