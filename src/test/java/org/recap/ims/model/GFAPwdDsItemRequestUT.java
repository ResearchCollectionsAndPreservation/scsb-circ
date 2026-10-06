package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFAPwdDsItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdDsItemRequest() {
        GFAPwdDsItemRequest gfaPwdDsItemRequest = new GFAPwdDsItemRequest();
        GFAPwdDsItemRequest gfaPwdDsItemRequest1 = new GFAPwdDsItemRequest();
        gfaPwdDsItemRequest.setTtitem(Arrays.asList(new GFAPwdTtItemRequest()));
        assertNotNull(gfaPwdDsItemRequest.getTtitem());
        gfaPwdDsItemRequest.equals(gfaPwdDsItemRequest);
        gfaPwdDsItemRequest.equals(gfaPwdDsItemRequest1);
        gfaPwdDsItemRequest1.equals(gfaPwdDsItemRequest);
        gfaPwdDsItemRequest.hashCode();
        gfaPwdDsItemRequest1.hashCode();
        gfaPwdDsItemRequest.toString();
    }
}
