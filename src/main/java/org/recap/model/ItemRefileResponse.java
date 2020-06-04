package org.recap.model;

import lombok.Getter;
import lombok.Setter;
import org.recap.ils.model.response.AbstractResponseItem;

/**
 * Created by sudhishk on 15/12/16.
 */
@Setter
@Getter
public class ItemRefileResponse extends AbstractResponseItem {

    private Integer requestId;
    private String jobId;

}
