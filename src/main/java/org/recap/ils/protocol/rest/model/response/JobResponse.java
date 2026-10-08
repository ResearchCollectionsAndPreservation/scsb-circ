package org.recap.ils.protocol.rest.model.response;

/**
 * Created by rajeshbabuk on 9/12/16.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import org.recap.ils.protocol.rest.model.DebugInfo;
import org.recap.ils.protocol.rest.model.JobData;

import java.util.List;

/**
 * The type Job response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "count",
        "statusCode",
        "statusMessage",
        "debugInfo"
})
@Data
public class JobResponse {

    @JsonProperty("data")
    private JobData data;
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("statusCode")
    private Integer statusCode;
    @JsonProperty("debugInfo")
    private List<DebugInfo> debugInfo = null;
    private String statusMessage;
}
