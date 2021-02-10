package org.recap.ils.model.nypl.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.recap.ils.model.nypl.BibLookupData;
import org.recap.ils.model.nypl.ItemLookupData;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bibLookupData",
        "itemLookupData"
 })

@Getter
@Setter
public class ItemLookupResponse {

    @JsonProperty("bib_data")
    private BibLookupData bibLookupData;
    @JsonProperty("item_data")
    private ItemLookupData itemLookupData;

}
