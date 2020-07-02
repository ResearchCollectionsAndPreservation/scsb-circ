package org.recap.gfa.model;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertTrue;

public class TtitemRequestUT extends BaseTestCase {

    TtitemRequest ititemRequest;

    @Test
    public void testTtitemRequest() {
        ititemRequest = new TtitemRequest();
        ititemRequest.setRequestId("test");
        ititemRequest.setRequestor("test");
        assertTrue(true);
    }
}
