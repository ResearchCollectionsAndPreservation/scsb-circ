package org.recap.ils.model.request;

import org.junit.Test;
import org.recap.ils.model.nypl.request.CancelHoldRequest;

import static org.junit.Assert.assertNotNull;

public class CancelHoldRequestUT {
    @Test
    public void getCancelHoldRequest(){
        CancelHoldRequest cancelHoldRequest = new CancelHoldRequest();
        cancelHoldRequest.setItemBarcode("123456");
        cancelHoldRequest.setOwningInstitutionId("1");
        cancelHoldRequest.setPatronBarcode("123456");
        cancelHoldRequest.setTrackingId("1");

        assertNotNull(cancelHoldRequest.getItemBarcode());
        assertNotNull(cancelHoldRequest.getOwningInstitutionId());
        assertNotNull(cancelHoldRequest.getPatronBarcode());
        assertNotNull(cancelHoldRequest.getTrackingId());
    }
}
