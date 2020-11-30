package org.recap.gfa.model;

import lombok.Data;

/**
 * Created by sudhishk on 8/12/16.
 */
@Data
public class GFAEddItemResponse {
    private RetrieveItemEDDRequest dsitem;
    private boolean success;
    private String screenMessage;
}
