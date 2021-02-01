package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAItemStatus;
import org.recap.las.model.GFAItemStatusCheckRequest;

import java.util.Arrays;

public class GFAItemStatusCheckRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAItemStatusCheckRequest(){
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest = new GFAItemStatusCheckRequest();
        GFAItemStatusCheckRequest gfaItemStatusCheckRequest1 = new GFAItemStatusCheckRequest();
        gfaItemStatusCheckRequest.setItemStatus(Arrays.asList(new GFAItemStatus()));
        gfaItemStatusCheckRequest.equals(gfaItemStatusCheckRequest);
        gfaItemStatusCheckRequest.equals(gfaItemStatusCheckRequest1);
        gfaItemStatusCheckRequest1.equals(gfaItemStatusCheckRequest);
        gfaItemStatusCheckRequest.hashCode();
        gfaItemStatusCheckRequest1.hashCode();
        gfaItemStatusCheckRequest.toString();

    }
}
