/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author borisborgobello
 */
public class BBSuperModel {
    
    @JsonIgnore
    public Object cloneJ() {
        try {
            byte[] v = BBAllParsers.getValuePOJOasBytes(this, false);
            return BBAllParsers.getPOJOFromBytes(v, getClass(), false);
        } catch (Exception e ){}
        return null;
    }
    
    @JsonIgnore
    public String getFromIsoDatePretty(String s) { return BBDateUtils.dateUserForISODate(s); }
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
