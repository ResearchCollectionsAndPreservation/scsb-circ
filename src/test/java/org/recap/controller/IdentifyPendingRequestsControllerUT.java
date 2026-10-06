package org.recap.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.service.IdentifyPendingRequestService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class IdentifyPendingRequestsControllerUT {
    @InjectMocks
    IdentifyPendingRequestsController identifyPendingRequestsController;
    @Mock
    IdentifyPendingRequestService pendingRequestService;

    @Test
    public void identifyAndNotifyPendingRequests() {
        Mockito.when(pendingRequestService.identifyPendingRequest()).thenReturn(true);
        String result = identifyPendingRequestsController.identifyAndNotifyPendingRequests();
        assertNotNull(result);
        assertEquals("Success", result);
    }

    @Test
    public void noPendingRequests() {
        Mockito.when(pendingRequestService.identifyPendingRequest()).thenReturn(false);
        String result = identifyPendingRequestsController.identifyAndNotifyPendingRequests();
        assertNotNull(result);
        assertEquals("There are no pending requests or no new pending request", result);
    }
}
