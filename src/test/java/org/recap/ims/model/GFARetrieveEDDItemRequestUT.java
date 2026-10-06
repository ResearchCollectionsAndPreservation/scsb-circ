package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFARetrieveEDDItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFARetrieveEDDItemRequest() {
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest1 = new GFARetrieveEDDItemRequest();
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
