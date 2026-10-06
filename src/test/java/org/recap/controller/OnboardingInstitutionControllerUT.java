package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.request.service.ItemEDDRequestService;
import org.recap.request.service.ItemRequestService;
import org.recap.util.PropertyUtil;
import org.springframework.context.ApplicationContext;

public class OnboardingInstitutionControllerUT extends BaseTestCaseUT {

    @InjectMocks
    OnboardingInstitutionController onboardingInstitutionController;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    private CamelContext camelContext;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private ItemEDDRequestService itemEDDRequestService;

    @Mock
    RouteBuilder routeBuilder;

    @BeforeEach
    public void setup() {
        String institutionCode = "PUL";
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, PropertyKeyConstants.ILS.ILS_TOPIC_RETRIEVAL_REQUEST)).thenReturn("scsbactivemq:topic:PUL.RequestT");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, PropertyKeyConstants.ILS.ILS_TOPIC_EDD_REQUEST)).thenReturn("recap");
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(institutionCode, PropertyKeyConstants.ILS.ILS_TOPIC_RECALL_REQUEST)).thenReturn("recap");
    }

    @Test
    public void createTopicsForNewInstitution() {
        String institutionCode = "PUL";
        onboardingInstitutionController.createTopicsForNewInstitution(institutionCode);
    }

    @Test
    public void createQueuesForNewImsLocation() {
        String imsLocationCode = "PUL";
        onboardingInstitutionController.createQueuesForNewImsLocation(imsLocationCode);
    }
}
