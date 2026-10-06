package org.recap.camel.requestinitialdataload;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.util.PropertyUtil;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class RequestDataLoadEmailServiceUT {

    RequestDataLoadEmailService requestDataLoadEmailService;

    @Mock
    Exchange exchange;

    @Mock
    ProducerTemplate producerTemplate;

    @Mock
    PropertyUtil propertyUtil;

    @BeforeEach
    public void setUp() throws Exception {
        // instantiate the service (it only has a constructor that accepts institutionCode)
        requestDataLoadEmailService = new RequestDataLoadEmailService("");
        // inject mocked dependencies and test values
        ReflectionTestUtils.setField(requestDataLoadEmailService, "producerTemplate", producerTemplate);
        ReflectionTestUtils.setField(requestDataLoadEmailService, "propertyUtil", propertyUtil);
        ReflectionTestUtils.setField(requestDataLoadEmailService, "subjectForRequestInitialDataLoad", "testPul@gmail.com");
    }

    @Test
    public void processInputPUL() {
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode", "PUL");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "PUL");
        exchange.setProperty("CamelSplitIndex", 0);
        exchange.setProperty("CamelFileNameProduced", "test");
        requestDataLoadEmailService.processInput(exchange);
    }

    @Test
    public void processInputCUL() {
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode", "CUL");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex", 0);
        exchange.setProperty("CamelFileNameProduced", "test");
        requestDataLoadEmailService.processInput(exchange);
    }

    @Test
    public void processInputRest() {
        ReflectionTestUtils.setField(requestDataLoadEmailService, "institutionCode", "NYPL");
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex", 0);
        exchange.setProperty("CamelFileNameProduced", "test");
        requestDataLoadEmailService.processInput(exchange);
    }

    @Test
    public void processInputWithoutInstitutionCode() {
        CamelContext ctx = new DefaultCamelContext();
        Exchange exchange = new DefaultExchange(ctx);
        exchange.getIn().setHeader("John", "CUL");
        exchange.setProperty("CamelSplitIndex", 0);
        exchange.setProperty("CamelFileNameProduced", "test");
        requestDataLoadEmailService.processInput(exchange);
    }

}
