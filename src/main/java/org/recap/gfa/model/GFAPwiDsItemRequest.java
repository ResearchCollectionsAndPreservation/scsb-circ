package org.recap.gfa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

/**
 * Created by rajeshbabuk on 21/2/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ttitem"
})
public class GFAPwiDsItemRequest {

    @JsonProperty("ttitem")
    private List<GFAPwiTtItemRequest> ttitem = null;

    /**
     * Gets ttitem.
     *
     * @return the ttitem
     */
    @JsonProperty("ttitem")
    public List<GFAPwiTtItemRequest> getTtitem() {
        return ttitem;
    }

    /**
     * Sets ttitem.
     *
     * @param ttitem the ttitem
     */
    @JsonProperty("ttitem")
    public void setTtitem(List<GFAPwiTtItemRequest> ttitem) {
        this.ttitem = ttitem;
    }

}
