package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by rajeshbabuk on 10/1/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "updatedDate",
        "createdDate",
        "deletedDate",
        "deleted",
        "suppressed",
        "names",
        "barCodes",
        "expirationDate",
        "homeLibraryCode",
        "birthDate",
        "emails",
        "fixedFields",
        "varFields"
})
@Getter
@Setter
public class NyplPatronData {
    @JsonProperty("id")
    private String id;
    @JsonProperty("updatedDate")
    private String updatedDate;
    @JsonProperty("createdDate")
    private String createdDate;
    @JsonProperty("deletedDate")
    private Object deletedDate;
    @JsonProperty("deleted")
    private Boolean deleted;
    @JsonProperty("suppressed")
    private Boolean suppressed;
    @JsonProperty("names")
    private List<String> names = null;
    @JsonProperty("barCodes")
    private List<String> barCodes = null;
    @JsonProperty("expirationDate")
    private String expirationDate;
    @JsonProperty("homeLibraryCode")
    private String homeLibraryCode;
    @JsonProperty("birthDate")
    private String birthDate;
    @JsonProperty("emails")
    private List<String> emails = null;
}
