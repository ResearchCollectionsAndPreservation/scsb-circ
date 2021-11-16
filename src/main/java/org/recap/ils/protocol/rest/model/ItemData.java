package org.recap.ils.protocol.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by rajeshbabuk on 7/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "nyplSource",
        "bibIds",
        "id",
        "nyplType",
        "updatedDate",
        "createdDate",
        "deletedDate",
        "deleted",
        "location",
        "status",
        "barcode",
        "callNumber",
        "itemType",
        "fixedFields",
        "varFields"
})
@Getter
@Setter
public class ItemData {
    @JsonProperty("nyplSource")
    private String nyplSource;
    @JsonProperty("bibIds")
    private List<String> bibIds = null;
    @JsonProperty("id")
    private String id;
    @JsonProperty("nyplType")
    private String nyplType;
    @JsonProperty("updatedDate")
    private String updatedDate;
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("deletedDate")
    private Object deletedDate;
    @JsonProperty("deleted")
    private Object deleted;
    @JsonProperty("location")
    private Object location;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("barcode")
    private Object barcode;
    @JsonProperty("callNumber")
    private Object callNumber;
    @JsonProperty("itemType")
    private Object itemType;
}
