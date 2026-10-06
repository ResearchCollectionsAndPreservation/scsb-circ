package org.recap.ims.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GFALasStatusTtItemUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusTtItem() {
        GFALasStatusTtItem gfaLasStatusTtItem = new GFALasStatusTtItem();
        GFALasStatusTtItem gfaLasStatusTtItem1 = new GFALasStatusTtItem();
        gfaLasStatusTtItem.setScreenMessage("Success");
        gfaLasStatusTtItem.setSuccess("true");
        gfaLasStatusTtItem.setImsLocationCode("22456");
        gfaLasStatusTtItem.equals(gfaLasStatusTtItem);
        gfaLasStatusTtItem.equals(gfaLasStatusTtItem1);
        gfaLasStatusTtItem1.equals(gfaLasStatusTtItem);
        gfaLasStatusTtItem.hashCode();
        gfaLasStatusTtItem1.hashCode();
        gfaLasStatusTtItem.toString();

        assertNotNull(gfaLasStatusTtItem.getScreenMessage());
        assertNotNull(gfaLasStatusTtItem.getSuccess());
        assertNotNull(gfaLasStatusTtItem.getImsLocationCode());
    }
}
