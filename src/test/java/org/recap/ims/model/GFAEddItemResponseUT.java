package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFAEddItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAEddItemResponse() {
        GFAEddItemResponse gfaEddItemResponse = new GFAEddItemResponse();
        GFAEddItemResponse gfaEddItemResponse1 = new GFAEddItemResponse();
        gfaEddItemResponse.setDsitem(new RetrieveItemEDDRequest());
        gfaEddItemResponse.setSuccess(Boolean.TRUE);
        gfaEddItemResponse.setScreenMessage("success");
        gfaEddItemResponse.hashCode();
        gfaEddItemResponse1.hashCode();
        gfaEddItemResponse.equals(gfaEddItemResponse);
        gfaEddItemResponse.equals(gfaEddItemResponse1);
        gfaEddItemResponse1.equals(gfaEddItemResponse);
        gfaEddItemResponse.toString();
        assertNotNull(gfaEddItemResponse.getScreenMessage());
        assertNotNull(gfaEddItemResponse.getDsitem());
        assertNotNull(gfaEddItemResponse.isSuccess());
    }
}
