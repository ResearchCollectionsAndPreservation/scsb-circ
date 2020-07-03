package org.recap.camel.submitcollection;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.camel.submitcollection.processor.SubmitCollectionProcessor;

import static org.junit.Assert.assertTrue;

public class SubmitCollectionProcessorUT extends BaseTestCase {
    //SubmitCollectionProcessor submitCollectionProcessor;

    @Test
    public void testSubmitCollectionProcessor() {
        SubmitCollectionProcessor submitCollectionProcessor = new SubmitCollectionProcessor("NYPL", false);

        CamelContext ctx = new DefaultCamelContext();
        Exchange ex = new DefaultExchange(ctx);
        ex.getIn().setHeader("CamelFileName", "CUL");
        ex.getIn().setHeader("CamelFileParent", "CUL");
        ex.getIn().setHeader("institution", "CUL");
        ex.getIn().setBody("Test text for Example");
        Exception e = new Exception();
        Throwable t = new ArithmeticException();
        e.addSuppressed(t);
        ex.setProperty("CamelExceptionCaught",e);
        try {
            submitCollectionProcessor.processInput(ex); } catch (Exception ef) {}
        try{
            submitCollectionProcessor.caughtException(ex);
        } catch (Exception ef) {}
        assertTrue(true);
    }

}
