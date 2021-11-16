package org.recap.ils.protocol.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 6/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "patron",
        "jobId",
        "processed",
        "success",
        "updatedDate",
        "createdDate",
        "recordType",
        "record",
        "nyplSource",
        "pickupLocation",
        "neededBy",
        "numberOfCopies"
})
@Getter
@Setter
public class RestHoldData {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("patron")
    private String patron;
    @JsonProperty("jobId")
    private String jobId;
    @JsonProperty("processed")
    private Boolean processed;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("updatedDate")
    private String updatedDate;
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("recordType")
    private String recordType;
    @JsonProperty("record")
    private String record;
    @JsonProperty("nyplSource")
    private String nyplSource;
    @JsonProperty("pickupLocation")
    private String pickupLocation;
    @JsonProperty("neededBy")
    private String neededBy;
    @JsonProperty("numberOfCopies")
    private Integer numberOfCopies;
}
