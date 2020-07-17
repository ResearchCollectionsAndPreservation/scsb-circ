package org.recap.processor;

import org.apache.camel.CamelContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.request.GFAService;
import org.recap.util.ItemRequestServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(MockitoJUnitRunner.class)
public class LasItemStatusCheckPollingProcessorUT {

    @InjectMocks
    LasItemStatusCheckPollingProcessor lasItemStatusCheckPollingProcessor;

    @Mock
    private GFAService gfaService;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    CamelContext camelContext;

    @Test
    public void pollLasItemStatusJobResponse(){
        String barcode ="123456";
        lasItemStatusCheckPollingProcessor.pollLasItemStatusJobResponse(barcode,camelContext);
    }
}
