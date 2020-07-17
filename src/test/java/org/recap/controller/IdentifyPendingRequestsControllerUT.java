package org.recap.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;
import org.recap.service.IdentifyPendingRequestService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class IdentifyPendingRequestsControllerUT{
    @InjectMocks
    IdentifyPendingRequestsController identifyPendingRequestsController;
    @Mock
    IdentifyPendingRequestService pendingRequestService;
    @Test
    public void identifyAndNotifyPendingRequests(){
        Mockito.when(pendingRequestService.identifyPendingRequest()).thenReturn(true);
        String result = identifyPendingRequestsController.identifyAndNotifyPendingRequests();
        assertNotNull(result);
        assertEquals("Success",result);
    }
    @Test
    public void noPendingRequests(){
        Mockito.when(pendingRequestService.identifyPendingRequest()).thenReturn(false);
        String result = identifyPendingRequestsController.identifyAndNotifyPendingRequests();
        assertNotNull(result);
        assertEquals("There are no pending requests or no new pending request",result);
    }
}
