package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

public class GFAPwiResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwiResponse() {
        GFAPwiResponse gfaPwiResponse = new GFAPwiResponse();
        GFAPwiResponse gfaPwiResponse1 = new GFAPwiResponse();
        gfaPwiResponse.setDsitem(new GFAPwiDsItemResponse());
        gfaPwiResponse.equals(gfaPwiResponse);
        gfaPwiResponse.equals(gfaPwiResponse1);
        gfaPwiResponse1.equals(gfaPwiResponse);
        gfaPwiResponse.hashCode();
        gfaPwiResponse1.hashCode();
        gfaPwiResponse.toString();
    }
}
