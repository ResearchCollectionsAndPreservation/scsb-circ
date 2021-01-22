package org.recap.las.model;

import lombok.Data;

/**
 * Created by sudhishk on 27/1/17.
 */
@Data
public class TtitemRequest {
    private String itemBarcode;
    private String itemStatus;
    private String customerCode;
    private String destination;
    private String requestId;
    private String requestor;
}
