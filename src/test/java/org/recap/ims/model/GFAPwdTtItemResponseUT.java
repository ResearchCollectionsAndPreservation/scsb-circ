package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.sql.Time;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFAPwdTtItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdTtItemResponse() {
        GFAPwdTtItemResponse gfaPwdTtItemResponse = new GFAPwdTtItemResponse();
        GFAPwdTtItemResponse gfaPwdTtItemResponse1 = new GFAPwdTtItemResponse();
        gfaPwdTtItemResponse.setCustomerCode("PB");
        gfaPwdTtItemResponse.setItemBarcode("231365");
        gfaPwdTtItemResponse.setDestination("test");
        gfaPwdTtItemResponse.setDeliveryMethod("test");
        gfaPwdTtItemResponse.setRequestor("test");
        gfaPwdTtItemResponse.setRequestorFirstName("test");
        gfaPwdTtItemResponse.setRequestorLastName("test");
        gfaPwdTtItemResponse.setRequestorMiddleName("test");
        gfaPwdTtItemResponse.setRequestorEmail("test@email.com");
        gfaPwdTtItemResponse.setRequestorOther("test");
        gfaPwdTtItemResponse.setPriority("first");
        gfaPwdTtItemResponse.setNotes("test");
        gfaPwdTtItemResponse.setRequestDate(new Date());
        gfaPwdTtItemResponse.setRequestTime(new Time(Long.valueOf(10)));
        gfaPwdTtItemResponse.setErrorCode("test");
        gfaPwdTtItemResponse.setErrorNote("test");
        gfaPwdTtItemResponse.equals(gfaPwdTtItemResponse);
        gfaPwdTtItemResponse.equals(gfaPwdTtItemResponse1);
        gfaPwdTtItemResponse1.equals(gfaPwdTtItemResponse);
        gfaPwdTtItemResponse.hashCode();
        gfaPwdTtItemResponse1.hashCode();
        gfaPwdTtItemResponse.toString();

        assertNotNull(gfaPwdTtItemResponse.getItemBarcode());
        assertNotNull(gfaPwdTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwdTtItemResponse.getNotes());
        assertNotNull(gfaPwdTtItemResponse.getErrorNote());
        assertNotNull(gfaPwdTtItemResponse.getErrorCode());
        assertNotNull(gfaPwdTtItemResponse.getDeliveryMethod());
        assertNotNull(gfaPwdTtItemResponse.getDestination());
        assertNotNull(gfaPwdTtItemResponse.getPriority());
        assertNotNull(gfaPwdTtItemResponse.getRequestDate());
        assertNotNull(gfaPwdTtItemResponse.getRequestor());
        assertNotNull(gfaPwdTtItemResponse.getRequestorEmail());
        assertNotNull(gfaPwdTtItemResponse.getRequestorFirstName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorLastName());
        assertNotNull(gfaPwdTtItemResponse.getRequestorMiddleName());
        assertNotNull(gfaPwdTtItemResponse.getRequestTime());
        assertNotNull(gfaPwdTtItemResponse.getRequestorOther());
    }
}
