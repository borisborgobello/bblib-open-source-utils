/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author borisborgobello
 */
public class BBSuperRubyModel extends BBSuperModel {
    
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("updated_at")
    public String updatedAt;
    @JsonProperty("lock_version")
    public Integer lockVersion;
    
    @JsonIgnore
    public String getCreatedAtPretty() { return getFromIsoDatePretty(createdAt); }
    @JsonIgnore
    public String getUpdatedAtPretty() { return getFromIsoDatePretty(updatedAt); }
}
