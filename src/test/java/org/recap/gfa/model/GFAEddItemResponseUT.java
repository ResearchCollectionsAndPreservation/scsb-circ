package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.GFAEddItemResponse;
import org.recap.las.model.RetrieveItemEDDRequest;

import static org.junit.Assert.assertNotNull;

public class GFAEddItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAEddItemResponse(){
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
        gfaEddItemResponse.canEqual(gfaEddItemResponse1);
        gfaEddItemResponse.canEqual(gfaEddItemResponse);
        gfaEddItemResponse.toString();
        assertNotNull(gfaEddItemResponse.getScreenMessage());
        assertNotNull(gfaEddItemResponse.getDsitem());
        assertNotNull(gfaEddItemResponse.isSuccess());
    }
}
