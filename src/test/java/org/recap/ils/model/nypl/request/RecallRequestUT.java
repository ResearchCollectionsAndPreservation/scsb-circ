package org.recap.ils.model.nypl.request;

import org.junit.Test;
import org.recap.ils.model.nypl.request.RecallRequest;

import static org.junit.Assert.assertNotNull;

public class RecallRequestUT {

    @Test
    public void getRecallRequest(){
        RecallRequest recallRequest = new RecallRequest();
        recallRequest.setItemBarcode("234567");
        recallRequest.setOwningInstitutionId("1");

        assertNotNull(recallRequest.getItemBarcode());
        assertNotNull(recallRequest.getOwningInstitutionId());
    }
}
