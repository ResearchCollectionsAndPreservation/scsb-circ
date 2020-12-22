package org.recap.camel.requestinitialdataload;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class RequestDataLoadEmailServiceUT {

    @InjectMocks
    RequestDataLoadEmailService requestDataLoadEmailService;

    @Mock
    Exchange exchange;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    PropertyUtil propertyUtil;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(requestDataLoadEmailService, "subjectForRequestInitialDataLoad","testPul@gmail.com" );
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void processInputPUL(){
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode","PUL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.setProperty("CamelSplitIndex",0);
        exchange.setProperty("CamelFileNameProduced","test");
        requestDataLoadEmailService.processInput(exchange);
    }
    @Test
    public void processInputCUL(){
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode","CUL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex",0);
        exchange.setProperty("CamelFileNameProduced","test");
        requestDataLoadEmailService.processInput(exchange);
    }
    @Test
    public void processInputNYPL(){
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode","NYPL" );
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex",0);
        exchange.setProperty("CamelFileNameProduced","test");
        requestDataLoadEmailService.processInput(exchange);
    }
    @Test
    public void processInputWithoutInstitutionCode(){
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex",0);
        exchange.setProperty("CamelFileNameProduced","test");
        requestDataLoadEmailService.processInput(exchange);
    }

}
