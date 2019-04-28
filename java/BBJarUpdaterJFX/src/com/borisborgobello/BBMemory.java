
package com.borisborgobello;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "previous_server_file",
    "previous_local_bin"
})
public class BBMemory {
    
    @JsonIgnore
    private static final String S_MEMORY_FILE = new File("./memory.txt").getAbsolutePath();

    @JsonProperty("previous_server_file")
    public String previousServerFile;
    @JsonProperty("previous_local_bin")
    public String previousLocalBin;
    
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
        return new HashCodeBuilder()
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
        return new EqualsBuilder()
                .append(additionalProperties, rhs.additionalProperties).isEquals();
    }
    
    @JsonIgnore
    static BBMemory getMemory() throws Exception {
        try {
            return ISAllParsers.getPOJOFromJsonFile(new File(new File(S_MEMORY_FILE).getAbsolutePath()), BBMemory.class, false);
        } catch (Exception e) {
            return null;
        }
    }
    
    @JsonIgnore
    void save() throws Exception {
        ISAllParsers.savePOJOToFile(new File(S_MEMORY_FILE), this, false);
    }

    void delete() {
        new File(S_MEMORY_FILE).delete();
    }
}
