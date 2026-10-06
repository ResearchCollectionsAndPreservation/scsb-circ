package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFAPwdDsItemResponseUT extends BaseTestCaseUT {

    @Test
    public void getGFAPwdDsItemResponse() {
        GFAPwdDsItemResponse gfaPwdDsItemResponse = new GFAPwdDsItemResponse();
        GFAPwdDsItemResponse gfaPwdDsItemResponse1 = new GFAPwdDsItemResponse();
        gfaPwdDsItemResponse.setTtitem(Arrays.asList(new GFAPwdTtItemResponse()));
        gfaPwdDsItemResponse.setProdsBefore(new ProdsBefore());
        gfaPwdDsItemResponse.setProdsHasChanges(true);
        assertNotNull(gfaPwdDsItemResponse.getProdsBefore());
        assertNotNull(gfaPwdDsItemResponse.getProdsHasChanges());
        gfaPwdDsItemResponse.equals(gfaPwdDsItemResponse);
        gfaPwdDsItemResponse.equals(gfaPwdDsItemResponse1);
        gfaPwdDsItemResponse1.equals(gfaPwdDsItemResponse);
        gfaPwdDsItemResponse.hashCode();
        gfaPwdDsItemResponse1.hashCode();
        gfaPwdDsItemResponse.toString();
    }
}
