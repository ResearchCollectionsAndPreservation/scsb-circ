package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by rajeshbabuk on 8/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "jobId",
        "processed",
        "success",
        "updatedDate",
        "createdDate",
        "patronBarcode",
        "itemBarcode",
        "desiredDateDue"
})
@Getter
@Setter
public class CheckoutData {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("jobId")
    private String jobId;
    @JsonProperty("processed")
    private Boolean processed;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("updatedDate")
    private Object updatedDate;
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("patronBarcode")
    private String patronBarcode;
    @JsonProperty("itemBarcode")
    private String itemBarcode;
    @JsonProperty("desiredDateDue")
    private String desiredDateDue;
}

