package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FeeTypeFactoryUT {
    @InjectMocks
    FeeTypeFactory feeTypeFactory;

    @Test
    public void testGetFeeTypeForEMPTY() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.EMPTY;
        FeeType result =  feeTypeFactory.getFeeType("00");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForOTHER_UNKNONW() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.OTHER_UNKNONW;
        FeeType result =  feeTypeFactory.getFeeType("01");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForADMINISTRATIVE() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.ADMINISTRATIVE;
        FeeType result =  feeTypeFactory.getFeeType("02");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForDAMAGE() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.DAMAGE;
        FeeType result =  feeTypeFactory.getFeeType("03");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForOVERDUE() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.OVERDUE;
        FeeType result =  feeTypeFactory.getFeeType("04");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForPROCESSING() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.PROCESSING;
        FeeType result =  feeTypeFactory.getFeeType("05");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForRENTAL() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.RENTAL;
        FeeType result =  feeTypeFactory.getFeeType("06");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForREPLACEMENT() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.REPLACEMENT;
        FeeType result =  feeTypeFactory.getFeeType("07");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForCOMPUTER_ACCESS_CHARGE() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.COMPUTER_ACCESS_CHARGE;
        FeeType result =  feeTypeFactory.getFeeType("08");
        assertEquals(returnValue,result);
    }
    @Test
    public void testGetFeeTypeForHOLD_FEE() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.HOLD_FEE;
        FeeType result =  feeTypeFactory.getFeeType("09");
        assertEquals(returnValue,result);
    }

    @Test
    public void testGetFeeType() throws InvalidSIP2ResponseValueException {
        FeeType returnValue = FeeType.EMPTY;
        FeeType result =  feeTypeFactory.getFeeType("13");
        assertEquals(returnValue,result);
    }

}
