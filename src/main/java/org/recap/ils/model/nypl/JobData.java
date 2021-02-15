package org.recap.ils.model.nypl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "started",
        "finished",
        "success",
        "notices",
        "successRedirectUrl",
        "startCallbackUrl",
        "successCallbackUrl",
        "failureCallbackUrl",
        "updateCallbackUrl"
})
@Getter
@Setter
public class JobData {
    @JsonProperty("id")
    private String id;
    @JsonProperty("started")
    private Boolean started;
    @JsonProperty("finished")
    private Boolean finished;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("notices")
    private List<Notice> notices = null;
    @JsonProperty("successRedirectUrl")
    private String successRedirectUrl;
    @JsonProperty("startCallbackUrl")
    private String startCallbackUrl;
    @JsonProperty("successCallbackUrl")
    private String successCallbackUrl;
    @JsonProperty("failureCallbackUrl")
    private String failureCallbackUrl;
    @JsonProperty("updateCallbackUrl")
    private String updateCallbackUrl;
}
