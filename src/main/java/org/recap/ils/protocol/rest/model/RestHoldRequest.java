package org.recap.ils.protocol.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 9/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "patron",
        "recordType",
        "record",
        "nyplSource",
        "pickupLocation",
        "numberOfCopies",
        "neededBy"
})
@Getter
@Setter
public class RestHoldRequest {
    @JsonProperty("patron")
    private String patron;
    @JsonProperty("recordType")
    private String recordType;
    @JsonProperty("record")
    private String record;
    @JsonProperty("nyplSource")
    private String nyplSource;
    @JsonProperty("pickupLocation")
    private String pickupLocation;
    @JsonProperty("deliveryLocation")
    private String deliveryLocation;
    @JsonProperty("numberOfCopies")
    private Integer numberOfCopies;
    @JsonProperty("neededBy")
    private String neededBy;
}
