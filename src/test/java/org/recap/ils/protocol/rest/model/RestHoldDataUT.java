package org.recap.ils.protocol.rest.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.ils.protocol.rest.model.RestHoldData;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 3/4/17.
 */
public class RestHoldDataUT extends BaseTestCaseUT {

    @Test
    public void testRestHoldData() {
        RestHoldData restHoldData = new RestHoldData();
        restHoldData.setId(1);
        restHoldData.setPatron("test");
        restHoldData.setRecordType("test");
        restHoldData.setRecord("test");
        restHoldData.setNyplSource("test");
        restHoldData.setPickupLocation("PB");
        restHoldData.setNumberOfCopies(1);
        restHoldData.setNeededBy("test");
        restHoldData.setJobId("1");
        restHoldData.setUpdatedDate(new Date().toString());
        restHoldData.setCreatedDate(new Date().toString());
        restHoldData.setProcessed(true);
        restHoldData.setSuccess(true);

        assertNotNull(restHoldData.getId());
        assertNotNull(restHoldData.getPatron());
        assertNotNull(restHoldData.getRecordType());
        assertNotNull(restHoldData.getRecord());
        assertNotNull(restHoldData.getNyplSource());
        assertNotNull(restHoldData.getPickupLocation());
        assertNotNull(restHoldData.getNumberOfCopies());
        assertNotNull(restHoldData.getNeededBy());
        assertNotNull(restHoldData.getProcessed());
        assertNotNull(restHoldData.getSuccess());
        assertNotNull(restHoldData.getUpdatedDate());
        assertNotNull(restHoldData.getCreatedDate());
        assertNotNull(restHoldData.getJobId());
    }

}
