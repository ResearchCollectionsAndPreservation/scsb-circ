package org.recap.las.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.RetrieveItem;
import org.recap.model.gfa.Ttitem;

import java.util.Arrays;

public class RetrieveItemUT extends BaseTestCaseUT {

    @Test
    public void getRetrieveItem(){
        RetrieveItem retrieveItem = new RetrieveItem();
        RetrieveItem retrieveItem1 = new RetrieveItem();
        retrieveItem.setTtitem(Arrays.asList(new Ttitem()));
        retrieveItem.equals(retrieveItem);
        retrieveItem.equals(retrieveItem1);
        retrieveItem1.equals(retrieveItem);
        retrieveItem.hashCode();
        retrieveItem1.hashCode();
        retrieveItem.toString();
    }
}
