/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import com.borisborgobello.ui.controllers.BBSuperController;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author borisborgobello
 */
public class BBCSVBuilder<T extends Object> {

    final boolean stringonly;
    final ArrayList<ArrayList<T>> dataTable;

    public BBCSVBuilder() {
        this(false);
    }

    public BBCSVBuilder(boolean stringonly) {
        this.stringonly = stringonly;
        dataTable = new ArrayList<>();
    }

    public BBCSVBuilder(ArrayList<ArrayList<T>> b, boolean stringonly) {
        this.dataTable = b;
        this.stringonly = stringonly;
    }

    public static BBCSVBuilder<String> newStringOnly() {
        return new BBCSVBuilder(true);
    }

    public static BBCSVBuilder<Object> newHeterogeneous() {
        return new BBCSVBuilder();
    }

    public BBCSVBuilder<T> addColumn(T value) {
        if (dataTable.isEmpty()) {
            dataTable.add(new ArrayList<>());
        }
        dataTable.get(dataTable.size() - 1).add(value);
        return this;
    }

    public BBCSVBuilder<T> quickRepRow(T line, boolean title, int lineSkipBefore, int lineSkipAfter) {
        for (int i = 0; i < lineSkipBefore; i++) {
            dataTable.add(new ArrayList<>());
        }

        ArrayList lineA = new ArrayList<>();
        if (title) {
            lineA.add("###### " + line + " ######");
        } else {
            lineA.add(line);
        }
        dataTable.add(lineA);

        for (int i = 0; i < lineSkipAfter; i++) {
            dataTable.add(new ArrayList<>());
        }
        return this;
    }

    public BBCSVBuilder<T> quickRepRow(T line) {
        return quickRepRow(line, false, 0, 0);
    }

    public BBCSVBuilder<T> quickRepRowParams(T... str) {
        ArrayList<T> lineA = new ArrayList<>();
        lineA.addAll(Arrays.asList(str));
        dataTable.add(lineA);
        return this;
    }

    public ArrayList<ArrayList<T>> getDataMutable() {
        return dataTable;
    }

    public File fout = null;

    public BBCSVBuilder<T> promptForSaveCSV(BBSuperController c) {
        fout = BBCSV.promptForSaveCSV(c);
        return this;
    }

    public BBCSVBuilder<T> writeCSVFileToChosenFile() throws Exception {
        return writeCSVToFile(fout);
    }

    public BBCSVBuilder<T> writeCSVToFile(File file) throws Exception {
        if (file == null) {
            throw new RuntimeException("No file selected");
        }
        file.getParentFile().mkdirs();
        StringBuilder csvBuilder = new StringBuilder();

        for (ArrayList<T> row : dataTable) {
            for (T column : row) {
                if (!(column instanceof Integer || column instanceof Double)) {
                    csvBuilder.append("\"");
                }
                csvBuilder.append(column);
                if (!(column instanceof Integer || column instanceof Double)) {
                    csvBuilder.append("\"");
                }
                csvBuilder.append(BBCSV.S_CSV_SEPARATOR_COLUMN);
            }
            csvBuilder.append(BBCSV.S_CSV_SEPARATOR_ROW);
        }
        csvBuilder.append(BBCSV.S_CSV_SEPARATOR_ROW);

        Files.write(file.toPath(), csvBuilder.toString().getBytes());

        return this;
    }
}
