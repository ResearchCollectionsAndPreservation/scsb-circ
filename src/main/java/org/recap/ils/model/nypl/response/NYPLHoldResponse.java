package org.recap.ils.model.nypl.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.recap.ils.model.nypl.DebugInfo;
import org.recap.ils.model.nypl.NYPLHoldData;

import java.util.List;

/**
 * Created by rajeshbabuk on 6/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "count",
        "statusCode",
        "debugInfo"
})
@Getter
@Setter
public class NYPLHoldResponse {

    @JsonProperty("data")
    private NYPLHoldData data;
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("debugInfo")
    private List<DebugInfo> debugInfo = null;
}
