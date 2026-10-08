package org.recap.model.response;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class RequestStatusResponse  {
    private String owningInstitution = "";
    private String requestingInstitution = "";
    private String requestType = "";
    private String deliveryLocation = "";
    public String getDeliveryLocation() {
        return Optional.ofNullable(deliveryLocation).orElse("");
    }
    private String status;
    private String errorMessage;
    private String requestId;
    private String itemBarcode;
    private String screenMessage;


    @Override
    public String toString() {
        return "ItemRequestInformation{" +
                ", itemOwningInstitution='" + owningInstitution + '\'' +
                ", requestingInstitution='" + requestingInstitution + '\'' +
                ", requestType='" + requestType + '\'' +
                ", requestId='" + requestId + '\'' +
                ", deliveryLocation='" + deliveryLocation + '\'' +
                '}';
    }
}