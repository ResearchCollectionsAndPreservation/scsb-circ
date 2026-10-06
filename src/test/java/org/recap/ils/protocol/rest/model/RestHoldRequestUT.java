package org.recap.ils.protocol.rest.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 3/4/17.
 */
public class RestHoldRequestUT {


    @Test
    public void testRestHoldRequest() {
        RestHoldRequest restHoldRequest = new RestHoldRequest();
        restHoldRequest.setPatron("test");
        restHoldRequest.setRecordType("test");
        restHoldRequest.setRecord("test");
        restHoldRequest.setSource("test");
        restHoldRequest.setPickupLocation("PB");
        restHoldRequest.setNumberOfCopies(1);
        restHoldRequest.setNeededBy("test");
        restHoldRequest.setDeliveryLocation("test");

        assertNotNull(restHoldRequest.getPatron());
        assertNotNull(restHoldRequest.getDeliveryLocation());
        assertNotNull(restHoldRequest.getRecordType());
        assertNotNull(restHoldRequest.getRecord());
        assertNotNull(restHoldRequest.getSource());
        assertNotNull(restHoldRequest.getPickupLocation());
        assertNotNull(restHoldRequest.getNumberOfCopies());
        assertNotNull(restHoldRequest.getNeededBy());

    }

}
