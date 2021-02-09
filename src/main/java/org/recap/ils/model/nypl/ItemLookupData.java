package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Created by Suresh.s
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "barcode",
        "process_type",
        })
public class ItemLookupData {

    @JsonProperty("barcode")
    private Object barcode;
    @JsonProperty("process_type")
    private Object processtype;

    /**
     * Gets nypl source.
     *
     * @return The nyplSource
     */
    @JsonProperty("process_type")
    public Object getProcesstype() {
        return processtype;
    }

    /**
     * Sets link
     *
     * @param processtype The processtype
     */
    @JsonProperty("process_type")
    public void setProcesstype(Object processtype) {
        this.processtype = processtype;
    }

    @JsonProperty("barcode")
    public Object getBarcode() {
        return barcode;
    }

    @JsonProperty("barcode")
    public void setBarcode(Object barcode) {
        this.barcode = barcode;
    }

}
