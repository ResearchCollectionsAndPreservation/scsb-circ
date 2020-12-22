package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class RetrieveItemEDDRequestUT extends BaseTestCaseUT {
    @Test
    public void getRetrieveItemEDDRequest(){
        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        RetrieveItemEDDRequest retrieveItemEDDRequest1 = new RetrieveItemEDDRequest();
        retrieveItemEDDRequest.setTtitem(Arrays.asList(new TtitemEDDResponse()));
        retrieveItemEDDRequest.equals(retrieveItemEDDRequest);
        retrieveItemEDDRequest.equals(retrieveItemEDDRequest1);
        retrieveItemEDDRequest1.equals(retrieveItemEDDRequest);
        retrieveItemEDDRequest.canEqual(retrieveItemEDDRequest);
        retrieveItemEDDRequest.canEqual(retrieveItemEDDRequest1);
        retrieveItemEDDRequest.hashCode();
        retrieveItemEDDRequest1.hashCode();
        retrieveItemEDDRequest.toString();
    }
}
