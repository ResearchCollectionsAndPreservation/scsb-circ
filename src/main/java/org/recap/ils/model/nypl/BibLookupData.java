package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by Suresh.s .
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "bibId",
        "title",
        })
public class BibLookupData {

    @JsonProperty("mms_id")
    private Object bibId;
    @JsonProperty("title")
    private Object title;

    /**
     * Gets nypl source.
     *
     * @return The nyplSource
     */
    @JsonProperty("mms_id")
    public Object getBibId() {
        return bibId;
    }

    /**
     * Sets link
     *
     * @param bibId The bibId
     */
    @JsonProperty("mms_id")
    public void setBibId(Object bibId) {
        this.bibId = bibId;
    }

    @JsonProperty("title")
    public Object getTitle() {
        return title;
    }

    @JsonProperty("tile")
    public void setTile(Object title) {
        this.title = title;
    }

}
