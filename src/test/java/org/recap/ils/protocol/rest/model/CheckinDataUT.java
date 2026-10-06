package org.recap.ils.protocol.rest.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckinDataUT extends BaseTestCaseUT {

    @Test
    public void getCheckinData() {

        CheckinData checkinData = new CheckinData();
        checkinData.setId(1);
        checkinData.setProcessed(Boolean.TRUE);
        checkinData.setUpdatedDate(new Date());
        checkinData.setItemBarcode("246622");

        assertNotNull(checkinData.getId());
        assertNotNull(checkinData.getUpdatedDate());
        assertNotNull(checkinData.getProcessed());
        assertTrue(checkinData.getProcessed());
    }
}
