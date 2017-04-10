package de.urkallinger.copymanager.model;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "extension",
        "category"
})
public class Extension extends DataObject<Extension> {

	private static final long serialVersionUID = 1L;

	@JsonProperty("extension")
	private String extension;
    
    @JsonProperty("category")
    private String category;

    @JsonProperty("extension")
    public String getExtension() {
        return extension;
    }

    @JsonProperty("extension")
    public Extension setExtension(String extension) {
        this.extension = extension;
        return this;
    }
    
    @JsonProperty("category")
    public String getCategory() {
        return category;
    }
    
    @JsonProperty("category")
    public Extension setCategory(String category) {
    	this.category = category;
    	return this;
    }

    @Override
	protected Extension getThis() {
		return this;
	}
	
    @Override
    public String toString() {
    	return extension;
    }
}
