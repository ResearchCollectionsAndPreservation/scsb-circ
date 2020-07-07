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
public class GFAPwdDsItemRequest {

    @JsonProperty("ttitem")
    private List<GFAPwdTtItemRequest> ttitem = null;

    /**
     * Gets ttitem.
     *
     * @return the ttitem
     */
    @JsonProperty("ttitem")
    public List<GFAPwdTtItemRequest> getTtitem() {
        return ttitem;
    }

    /**
     * Sets ttitem.
     *
     * @param ttitem the ttitem
     */
    @JsonProperty("ttitem")
    public void setTtitem(List<GFAPwdTtItemRequest> ttitem) {
        this.ttitem = ttitem;
    }

}