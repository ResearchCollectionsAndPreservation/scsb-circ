package org.recap.model;

import lombok.Getter;
import lombok.Setter;
import org.recap.ils.model.response.ItemInformationResponse;

/**
 * Created by sudhishk on 9/11/17.
 */
@Setter
@Getter
public class RequestInformation {
    private ItemRequestInformation itemRequestInfo;
    private ItemInformationResponse itemResponseInformation;

}
