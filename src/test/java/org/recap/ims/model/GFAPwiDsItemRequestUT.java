package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class GFAPwiDsItemRequestUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiDsItemRequest() {

        GFAPwiDsItemRequest gfaPwiDsItemRequest = new GFAPwiDsItemRequest();
        GFAPwiDsItemRequest gfaPwiDsItemRequest1 = new GFAPwiDsItemRequest();
        gfaPwiDsItemRequest.setTtitem(Arrays.asList(new GFAPwiTtItemRequest()));
        gfaPwiDsItemRequest.equals(gfaPwiDsItemRequest);
        gfaPwiDsItemRequest.equals(gfaPwiDsItemRequest1);
        gfaPwiDsItemRequest1.equals(gfaPwiDsItemRequest);
        gfaPwiDsItemRequest.hashCode();
        gfaPwiDsItemRequest1.hashCode();
        gfaPwiDsItemRequest.toString();
    }
}
