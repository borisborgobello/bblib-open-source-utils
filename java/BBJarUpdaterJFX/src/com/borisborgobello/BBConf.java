
package com.borisborgobello;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "basic_auth",
    "url_check_last_bin",
    "url_bin_folder",
    "export_folder",
    "exec_command",
    "bin_path",
    "is_mac_updater"
})
public class BBConf {

    @JsonProperty("basic_auth")
    public String basicAuth;
    
    @JsonProperty("url_check_last_bin")
    public String urlCheckLastBin;
    @JsonProperty("url_bin_folder")
    public String urlBinFolder;
    @JsonProperty("export_folder")
    public String exportFolder;
    @JsonProperty("bin_path")
    public String binPath;
    @JsonProperty("exec_command")
    public String execCommand;
    @JsonProperty("is_mac_updater")
    public String isMacUpdater;
    
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(urlBinFolder).append(urlCheckLastBin)
                .append(binPath).append(exportFolder).append(basicAuth).append(basicAuth)
                .append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof BBConf) == false) {
            return false;
        }
        BBConf rhs = ((BBConf) other);
        return new EqualsBuilder().append(urlBinFolder, rhs.urlBinFolder)
                .append(urlCheckLastBin, rhs.urlCheckLastBin)
                .append(binPath, rhs.binPath)
                .append(basicAuth, rhs.basicAuth)
                .append(exportFolder, rhs.exportFolder)
                .append(additionalProperties, rhs.additionalProperties).isEquals();
    }
    @JsonIgnore
    public Boolean isMacUpdater() {
        if (isMacUpdater.equalsIgnoreCase("true")) return true;
        if (isMacUpdater.equalsIgnoreCase("false")) return false;
        return null;
    }

    @JsonIgnore
    void check() throws Exception {
        if (ISTools.isEmpty(isMacUpdater) || isMacUpdater() == null)
            throw new Exception("Field is_mac_updater must be filled with true or false");
        if (ISTools.isEmpty(urlCheckLastBin))throw new Exception("url_check_last_bin cannot be empty");
        if (ISTools.isEmpty(exportFolder))throw new Exception("export_folder cannot be empty");
        if (ISTools.isEmpty(binPath))throw new Exception("bin_path cannot be empty");
        if (ISTools.isEmpty(execCommand))throw new Exception("exec_command not set ! Use open -a bin_path OR java -jar bin_path etc...");
        if (!urlCheckLastBin.contains("https")) throw new Exception("Unsecured HTTP connection. Please move to HTTPS.");
        if (!ISTools.isEmpty(urlBinFolder) && urlBinFolder.contains("http://")) throw new Exception("Unsecured HTTP connection. Please move to HTTPS.");
    }

}
