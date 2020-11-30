package org.recap.gfa.model;

import lombok.Data;

/**
 * Created by sudhishk on 8/12/16.
 */
@Data
public class GFARetrieveItemResponse {
    private RetrieveItem dsitem;
    private boolean success;
    private String screenMessage;
}
