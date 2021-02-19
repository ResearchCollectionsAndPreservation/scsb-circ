package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Suresh.s .
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bibId",
        "title",
        })
@Getter
@Setter
public class BibLookupData {
    @JsonProperty("mms_id")
    private Object bibId;
    @JsonProperty("title")
    private Object title;
}
