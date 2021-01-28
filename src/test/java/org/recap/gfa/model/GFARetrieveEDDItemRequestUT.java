package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFARetrieveEDDItemRequest;
import org.recap.las.model.RetrieveItemEDDRequest;

import static org.junit.Assert.assertNotNull;

public class GFARetrieveEDDItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFARetrieveEDDItemRequest(){
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest =new GFARetrieveEDDItemRequest();
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest1 =new GFARetrieveEDDItemRequest();
        gfaRetrieveEDDItemRequest.setDsitem(new RetrieveItemEDDRequest());
        gfaRetrieveEDDItemRequest.equals(gfaRetrieveEDDItemRequest1);
        gfaRetrieveEDDItemRequest1.equals(gfaRetrieveEDDItemRequest);
        gfaRetrieveEDDItemRequest.equals(gfaRetrieveEDDItemRequest);
        gfaRetrieveEDDItemRequest.hashCode();
        gfaRetrieveEDDItemRequest1.hashCode();
        gfaRetrieveEDDItemRequest.toString();

        assertNotNull(gfaRetrieveEDDItemRequest.getDsitem());
    }
}
