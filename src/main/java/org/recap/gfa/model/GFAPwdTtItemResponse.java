package org.recap.gfa.model;

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
        "CustomerCode",
        "itemBarcode",
        "destination",
        "deliveryMethod",
        "requestor",
        "requestorFirstName",
        "requestorLastName",
        "requestorMiddleName",
        "requestorEmail",
        "requestorOther",
        "priority",
        "notes",
        "requestDate",
        "requestTime",
        "errorCode",
        "errorNote"
})
public class GFAPwdTtItemResponse {
    @JsonProperty("CustomerCode")
    private String customerCode;
    @JsonProperty("itemBarcode")
    private String itemBarcode;
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("deliveryMethod")
    private Object deliveryMethod;
    @JsonProperty("requestor")
    private String requestor;
    @JsonProperty("requestorFirstName")
    private Object requestorFirstName;
    @JsonProperty("requestorLastName")
    private Object requestorLastName;
    @JsonProperty("requestorMiddleName")
    private Object requestorMiddleName;
    @JsonProperty("requestorEmail")
    private Object requestorEmail;
    @JsonProperty("requestorOther")
    private Object requestorOther;
    @JsonProperty("priority")
    private Object priority;
    @JsonProperty("notes")
    private Object notes;
    @JsonProperty("requestDate")
    private Object requestDate;
    @JsonProperty("requestTime")
    private Object requestTime;
    @JsonProperty("errorCode")
    private Object errorCode;
    @JsonProperty("errorNote")
    private Object errorNote;
}