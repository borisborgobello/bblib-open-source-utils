/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.asamples;

import com.borisborgobello.jfx.utils.BBAllParsers;
import com.borisborgobello.jfx.utils.BBCollections;
import com.borisborgobello.jfx.utils.BBRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author borisborgobello
 */
public class BBLibSampleCountryPhone {

    public BBLibSampleCountryPhone() {}
    @JsonProperty("name")
    public String name;
    @JsonProperty("dial_code")
    public String dialCode;
    @JsonProperty("code")
    public String code;

    @JsonIgnore
    transient String fastSearch = null;

    @JsonIgnore
    public String toSearchStr() {
        return fastSearch == null ? fastSearch = (name + " " + dialCode + " " + code).toLowerCase() : fastSearch;
    }
    
    @JsonIgnore
    transient Integer fastDCI = null;
    @JsonIgnore
    public Integer getDialCodeInt() {
        return fastDCI == null ? fastDCI = Integer.parseInt(dialCode.replaceAll("\\+", "").trim()) : fastDCI;
    }

    public static ArrayList<BBLibSampleCountryPhone> load() {
        try {
            ArrayList<BBLibSampleCountryPhone> al = BBCollections.newAL(
                    BBAllParsers.getPOJOFromBytes(BBRes.getResAsBytes(BBRes.getRes("borisborgobello", "countrypn.json")), BBLibSampleCountryPhone[].class, false));
            
            return al;
        } catch (Exception ex) {
            Logger.getLogger(BBLibFXMLSampleTableViewController.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }
}
