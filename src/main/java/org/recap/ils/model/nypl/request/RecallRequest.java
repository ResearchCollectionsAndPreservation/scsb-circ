package org.recap.ils.model.nypl.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 23/6/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "owningInstitutionId",
        "itemBarcode"
})
@Getter
@Setter
public class RecallRequest {
    @JsonProperty("owningInstitutionId")
    private String owningInstitutionId;
    @JsonProperty("itemBarcode")
    private String itemBarcode;
}
