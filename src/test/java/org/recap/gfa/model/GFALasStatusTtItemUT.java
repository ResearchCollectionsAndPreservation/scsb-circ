package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class GFALasStatusTtItemUT extends BaseTestCaseUT {

    @Test
    public void getGFALasStatusTtItem(){
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
        gfaLasStatusTtItem.canEqual(gfaLasStatusTtItem);
        gfaLasStatusTtItem.canEqual(gfaLasStatusTtItem1);
        gfaLasStatusTtItem.toString();

        assertNotNull(gfaLasStatusTtItem.getScreenMessage());
        assertNotNull(gfaLasStatusTtItem.getSuccess());
        assertNotNull(gfaLasStatusTtItem.getImsLocationCode());
    }
}
