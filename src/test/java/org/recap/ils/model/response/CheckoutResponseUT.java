package org.recap.ils.model.response;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.response.CheckinResponse;
import org.recap.ils.model.nypl.response.CheckoutResponse;

import static org.junit.Assert.assertTrue;

public class CheckoutResponseUT extends BaseTestCase {
    CheckoutResponse checkoutResponse;
    @Test
    public void testCheckinResponse(){
        checkoutResponse = new CheckoutResponse();
        checkoutResponse.getCount();
        checkoutResponse.getStatusCode();
        checkoutResponse.getDebugInfo();
        assertTrue(true);
    }
}
