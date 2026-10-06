package org.recap.model.deaccession;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 17/3/17.
 */
public class DeAccessionSolrRequestUT extends BaseTestCaseUT {


    @Test
    public void testDeAccessionSolrRequest() {
        DeAccessionSolrRequest deAccessionSolrRequest = new DeAccessionSolrRequest();
        deAccessionSolrRequest.setBibIds(Arrays.asList(123));
        deAccessionSolrRequest.setHoldingsIds(Arrays.asList(369));
        deAccessionSolrRequest.setItemIds(Arrays.asList(14752));
        deAccessionSolrRequest.setStatus("SUCCESS");
        assertNotNull(deAccessionSolrRequest.getBibIds());
        assertNotNull(deAccessionSolrRequest.getHoldingsIds());
        assertNotNull(deAccessionSolrRequest.getItemIds());
        assertNotNull(deAccessionSolrRequest.getStatus());
    }

}