package org.recap.gfa.model;

import java.util.List;
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
        "prods:hasChanges",
        "ttitem",
        "prods:before"
})
public class GFAPwiDsItemResponse {
    @JsonProperty("prods:hasChanges")
    private Boolean prodsHasChanges;
    @JsonProperty("ttitem")
    private List<GFAPwiTtItemResponse> ttitem = null;
    @JsonProperty("prods:before")
    private ProdsBefore prodsBefore;
}