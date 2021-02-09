package org.recap.ils.model.response;

import lombok.Data;
import java.util.HashMap;

@Data
public class ItemLookUpInformationResponse {

    private String barcode;
    private HashMap processtype;
    private String circulationStatus;
    private String bibId;
    private String title;

}
