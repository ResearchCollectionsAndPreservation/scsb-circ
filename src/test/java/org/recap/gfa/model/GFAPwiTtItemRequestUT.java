package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAPwiTtItemRequest;

import static org.junit.Assert.assertNotNull;

public class GFAPwiTtItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiTtItemRequest(){
        GFAPwiTtItemRequest gfaPwiTtItemRequest = new GFAPwiTtItemRequest();
        GFAPwiTtItemRequest gfaPwiTtItemRequest1 = new GFAPwiTtItemRequest();
        gfaPwiTtItemRequest.setItemBarcode("2356");
        gfaPwiTtItemRequest.setCustomerCode("37648");
        gfaPwiTtItemRequest.equals(gfaPwiTtItemRequest);
        gfaPwiTtItemRequest.equals(gfaPwiTtItemRequest1);
        gfaPwiTtItemRequest1.equals(gfaPwiTtItemRequest);
        gfaPwiTtItemRequest.hashCode();
        gfaPwiTtItemRequest1.hashCode();
        gfaPwiTtItemRequest.canEqual(gfaPwiTtItemRequest);
        gfaPwiTtItemRequest.canEqual(gfaPwiTtItemRequest1);
        gfaPwiTtItemRequest.toString();

        assertNotNull(gfaPwiTtItemRequest.getItemBarcode());
        assertNotNull(gfaPwiTtItemRequest.getCustomerCode());
    }
}
