package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

public class GFARetrieveItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFARetrieveItemRequest(){
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        GFARetrieveItemRequest gfaRetrieveItemRequest1 = new GFARetrieveItemRequest();
        gfaRetrieveItemRequest.setDsitem(new RetrieveItemRequest());
        gfaRetrieveItemRequest.equals(gfaRetrieveItemRequest);
        gfaRetrieveItemRequest1.equals(gfaRetrieveItemRequest);
        gfaRetrieveItemRequest.equals(gfaRetrieveItemRequest1);
        gfaRetrieveItemRequest.canEqual(gfaRetrieveItemRequest);
        gfaRetrieveItemRequest.canEqual(gfaRetrieveItemRequest1);
        gfaRetrieveItemRequest.hashCode();
        gfaRetrieveItemRequest1.hashCode();
        gfaRetrieveItemRequest.toString();
    }
}
