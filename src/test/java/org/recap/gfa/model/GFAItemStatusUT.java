package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

public class GFAItemStatusUT extends BaseTestCaseUT {
    @Test
    public void getGFAItemStatus(){
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        GFAItemStatus gfaItemStatus1 = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("124356");
        gfaItemStatus.equals(gfaItemStatus);
        gfaItemStatus.equals(gfaItemStatus1);
        gfaItemStatus1.equals(gfaItemStatus);
        gfaItemStatus.hashCode();
        gfaItemStatus1.hashCode();
        gfaItemStatus.toString();
        gfaItemStatus.canEqual(gfaItemStatus);
        gfaItemStatus.canEqual(gfaItemStatus1);
    }
}
