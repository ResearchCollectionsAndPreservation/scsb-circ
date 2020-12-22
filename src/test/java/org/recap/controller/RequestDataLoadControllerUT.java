package org.recap.controller;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.model.ILSConfigProperties;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RequestDataLoadControllerUT extends BaseTestCaseUT {

    @InjectMocks
    RequestDataLoadController requestDataLoadController;

    @Mock
    CommonUtil commonUtil;

    @Mock
    CamelContext camelContext;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    RouteController routeController;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void startAccessionReconcilation() throws Exception{

        String pul = "{\"PUL:\":\"PUL\"}";
        String cul = "{\"CUL:\":\"CUL\"}";
        String nypl = "{\"NYPL:\":\"NYPL\"}";
        List<String> institutionCodeList = new ArrayList<>();
        institutionCodeList.add(pul);
        institutionCodeList.add(cul);
        institutionCodeList.add(nypl);
        ILSConfigProperties ilsConfigProperties = new ILSConfigProperties();
        Mockito.when(institutionDetailsRepository.findAllInstitutionCodeExceptHTC()).thenReturn(institutionCodeList);
        for (String institution : institutionCodeList) {
            Mockito.when(propertyUtil.getILSConfigProperties(institution)).thenReturn(ilsConfigProperties);
        }
        Mockito.when(camelContext.getRouteController()).thenReturn(routeController);
        String result = requestDataLoadController.startAccessionReconcilation();
        assertNotNull(result);
        assertEquals("Success",result);
    }
}
