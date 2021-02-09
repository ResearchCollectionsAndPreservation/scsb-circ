package org.recap.ils.model.nypl.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.recap.ils.model.nypl.BibLookupData;
import org.recap.ils.model.nypl.ItemLookupData;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bibLookupData",
        "itemLookupData"
 })

public class ItemLookupResponse {

    @JsonProperty("bib_data")
    private BibLookupData bibLookupData;
    @JsonProperty("item_data")
    private ItemLookupData itemLookupData;

    @JsonProperty("bib_data")
    public BibLookupData getBibLookupData() {
        return bibLookupData;
    }

    @JsonProperty("bib_data")
    public void setBibLookupData(BibLookupData bibLookupData) {
        this.bibLookupData = bibLookupData;
    }

    @JsonProperty("item_data")
    public ItemLookupData getItemLookupData() {
        return itemLookupData;
    }

    @JsonProperty("item_data")
    public void setItemLookupData(ItemLookupData itemLookupData) {
        this.itemLookupData = itemLookupData;
    }

}
