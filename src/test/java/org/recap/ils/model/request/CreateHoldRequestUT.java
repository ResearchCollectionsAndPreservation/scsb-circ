package org.recap.ils.model.request;

import org.junit.Test;
import org.recap.ils.model.nypl.Description;
import org.recap.ils.model.nypl.request.CreateHoldRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CreateHoldRequestUT {

    @Test
    public  void getCreateHoldRequest(){
        CreateHoldRequest createHoldRequest= new CreateHoldRequest();
        createHoldRequest.setDescription(new Description());
        createHoldRequest.setItemBarcode("123456");
        createHoldRequest.setOwningInstitutionId("1");
        createHoldRequest.setPatronBarcode("123456");
        createHoldRequest.setTrackingId("1");

        assertNotNull(createHoldRequest.getDescription());
        assertNotNull(createHoldRequest.getItemBarcode());
        assertNotNull(createHoldRequest.getOwningInstitutionId());
        assertNotNull(createHoldRequest.getPatronBarcode());
        assertNotNull(createHoldRequest.getTrackingId());
    }
}
