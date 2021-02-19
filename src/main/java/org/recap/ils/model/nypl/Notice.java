package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "createdDate",
        "text",
        "data"
})
@Getter
@Setter
public class Notice {
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("text")
    private String text;
    @JsonProperty("data")
    private Object data;
}
