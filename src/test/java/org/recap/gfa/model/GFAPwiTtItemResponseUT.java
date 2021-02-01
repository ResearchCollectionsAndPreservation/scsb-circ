package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwiTtItemResponse;

import static org.junit.Assert.assertNotNull;

public class GFAPwiTtItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiTtItemResponse(){
        GFAPwiTtItemResponse gfaPwiTtItemResponse = new GFAPwiTtItemResponse();
        GFAPwiTtItemResponse gfaPwiTtItemResponse1 = new GFAPwiTtItemResponse();
        gfaPwiTtItemResponse.setItemBarcode("37647859");
        gfaPwiTtItemResponse.setCustomerCode("234567");
        gfaPwiTtItemResponse.setErrorNote("error");
        gfaPwiTtItemResponse.setErrorCode("123445");
        gfaPwiTtItemResponse.equals(gfaPwiTtItemResponse);
        gfaPwiTtItemResponse.equals(gfaPwiTtItemResponse1);
        gfaPwiTtItemResponse1.equals(gfaPwiTtItemResponse);
        gfaPwiTtItemResponse.hashCode();
        gfaPwiTtItemResponse1.hashCode();
        gfaPwiTtItemResponse.toString();

        assertNotNull(gfaPwiTtItemResponse.getItemBarcode());
        assertNotNull(gfaPwiTtItemResponse.getCustomerCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorCode());
        assertNotNull(gfaPwiTtItemResponse.getErrorNote());
    }
}
