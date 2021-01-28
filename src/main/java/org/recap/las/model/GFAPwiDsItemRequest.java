package org.recap.las.model;

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
        "ttitem"
})
public class GFAPwiDsItemRequest {
    @JsonProperty("ttitem")
    private List<GFAPwiTtItemRequest> ttitem = null;
}
