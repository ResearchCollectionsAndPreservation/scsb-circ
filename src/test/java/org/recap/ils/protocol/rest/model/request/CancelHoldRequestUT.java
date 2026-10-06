package org.recap.ils.protocol.rest.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CancelHoldRequestUT {
    @Test
    public void getCancelHoldRequest() {
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
