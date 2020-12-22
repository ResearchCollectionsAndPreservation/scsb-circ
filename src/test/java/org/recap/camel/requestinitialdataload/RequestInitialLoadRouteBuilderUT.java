package org.recap.camel.requestinitialdataload;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.model.ILSConfigProperties;
import org.recap.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class RequestInitialLoadRouteBuilderUT extends BaseTestCaseUT {

    @InjectMocks
    RequestInitialLoadRouteBuilder requestInitialLoadRouteBuilder;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    CommonUtil commonUtil;
    String institution;
    @Mock
    ILSConfigProperties ilsConfigProperties;

    @Test
    public void configure() throws Exception {
        requestInitialLoadRouteBuilder.configure();
    }
}
