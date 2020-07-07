package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;

public class FeeTypeFactoryUT extends BaseTestCase {
    @Mock
    FeeTypeFactory feeTypeFactory;

    @Test
    public void testGetFeeType() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.EMPTY;
        Mockito.when(feeTypeFactory.getFeeType("13")).thenReturn(returnValue);
        assertEquals(returnValue, feeTypeFactory.getFeeType("13"));
    }
}
