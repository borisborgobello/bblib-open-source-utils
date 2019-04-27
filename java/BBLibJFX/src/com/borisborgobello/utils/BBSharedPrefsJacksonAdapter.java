/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author borisborgobello
 */
public class BBSharedPrefsJacksonAdapter implements BBSharedPrefs.BBSharedPrefsAdapter {
    @Override
    public HashMap<String, Object> read(File f, boolean crypted) throws Exception {
        return BBAllParsers.getHashMapFromJsonFile(f, crypted);
    }
    @Override
    public void write(File f, HashMap<String, Object> map, boolean crypted) throws Exception {
        BBAllParsers.savePOJOToFile(f, map, crypted);
    }
}
