package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Suresh.s
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "barcode",
        "process_type",
        })
@Getter
@Setter
public class ItemLookupData {
    @JsonProperty("barcode")
    private Object barcode;
    @JsonProperty("process_type")
    private Object processtype;
}
