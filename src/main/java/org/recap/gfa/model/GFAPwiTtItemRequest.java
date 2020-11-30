package org.recap.gfa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

/**
 * Created by rajeshbabuk on 21/2/17.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "CustomerCode",
        "itemBarcode"
})
public class GFAPwiTtItemRequest {
    @JsonProperty("CustomerCode")
    private String customerCode;
    @JsonProperty("itemBarcode")
    private String itemBarcode;
}
