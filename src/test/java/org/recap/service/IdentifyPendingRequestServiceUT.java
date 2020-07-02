package org.recap.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class IdentifyPendingRequestServiceUT extends BaseTestCase {
    @Autowired
    IdentifyPendingRequestService identifyPendingRequestService;

    @Test
    public void testidentifyPendingRequest() {
        boolean status = identifyPendingRequestService.identifyPendingRequest();
        assertTrue(!status);
    }
}
