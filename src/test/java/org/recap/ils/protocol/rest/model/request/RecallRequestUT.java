package org.recap.ils.protocol.rest.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecallRequestUT {

    @Test
    public void getRecallRequest() {
        RecallRequest recallRequest = new RecallRequest();
        recallRequest.setItemBarcode("234567");
        recallRequest.setOwningInstitutionId("1");

        assertNotNull(recallRequest.getItemBarcode());
        assertNotNull(recallRequest.getOwningInstitutionId());
    }
}
