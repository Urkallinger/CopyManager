package de.urkallinger.copymanager.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id"
})
public abstract class DataObject<T extends DataObject<T>> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("id")
    protected long id;

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public T setId(long id) {
        this.id = id;
        return getThis();
    }
    
    protected abstract T getThis();
}
