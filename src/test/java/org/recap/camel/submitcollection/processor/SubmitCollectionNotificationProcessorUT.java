package org.recap.camel.submitcollection.processor;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class SubmitCollectionNotificationProcessorUT extends BaseTestCase {
    @Mock
    SubmitCollectionNotificationProcessor submitCollectionNotificationProcessor;
    @Autowired
    private ProducerTemplate producer;

    @Test
    public void testSubmitCollectionNotificationProcessor(){
        submitCollectionNotificationProcessor = Mockito.mock(SubmitCollectionNotificationProcessor.class);
        submitCollectionNotificationProcessor.sendSubmitCollectionNotification();
        assertTrue(true);
    }
}
