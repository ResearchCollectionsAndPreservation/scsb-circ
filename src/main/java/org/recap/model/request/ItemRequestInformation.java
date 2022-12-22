package org.recap.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hemalathas on 1/11/16.
 */
@Data
public class ItemRequestInformation implements Serializable {

    private List<String> itemBarcodes;
    private String titleIdentifier;
    private String itemOwningInstitution = "";
    private String patronBarcode = "";
    private String emailAddress = "";
    private String requestingInstitution = "";
    private String requestType = ""; // Retrieval,EDD, Hold, Recall, Borrow Direct
    private String deliveryLocation = "";
    private String customerCode = "";
    private String requestNotes = "";
    private String trackingId; // REST - trackingId
    private String author; // REST - author
    private String callNumber; // REST - callNumber
    private String translatedDeliveryLocation = "";

    /**
     * EDD Request
     */
    private String startPage;
    private String endPage;
    private String chapterTitle = "";
    private String expirationDate;
    private String bibId = "";
    private String username;
    private String issue;
    private String volume;
    private String itemAuthor;
    private String itemVolume;
    private Integer requestId;
    private String pickupLocation;
    private String eddNotes;
    private String imsLocationCode;

    /**
     * Is owning institution item boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    public boolean isOwningInstitutionItem() {
        boolean bSuccess;
        bSuccess = itemOwningInstitution.equalsIgnoreCase(requestingInstitution);
        return bSuccess;
    }


}
