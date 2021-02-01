package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwdTtItemRequest;

import static org.junit.Assert.assertNotNull;

public class GFAPwdTtItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdTtItemRequest(){
        GFAPwdTtItemRequest gfaPwdTtItemRequest = new GFAPwdTtItemRequest();
        GFAPwdTtItemRequest gfaPwdTtItemRequest1 = new GFAPwdTtItemRequest();
        gfaPwdTtItemRequest.setItemBarcode("2456788");
        gfaPwdTtItemRequest.setRequestor("edd");
        gfaPwdTtItemRequest.setCustomerCode("243576");
        gfaPwdTtItemRequest.setDestination("PA");
        gfaPwdTtItemRequest.equals(gfaPwdTtItemRequest);
        gfaPwdTtItemRequest.equals(gfaPwdTtItemRequest1);
        gfaPwdTtItemRequest1.equals(gfaPwdTtItemRequest);
        gfaPwdTtItemRequest.hashCode();
        gfaPwdTtItemRequest1.hashCode();
        gfaPwdTtItemRequest.toString();

        assertNotNull(gfaPwdTtItemRequest.getItemBarcode());
        assertNotNull(gfaPwdTtItemRequest.getRequestor());
        assertNotNull(gfaPwdTtItemRequest.getCustomerCode());
        assertNotNull(gfaPwdTtItemRequest.getDestination());
    }
}
