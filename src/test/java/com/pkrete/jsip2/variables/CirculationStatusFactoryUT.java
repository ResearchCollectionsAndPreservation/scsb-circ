package com.pkrete.jsip2.variables;

import com.pkrete.jsip2.exceptions.InvalidSIP2ResponseValueException;
import com.pkrete.jsip2.messages.responses.SIP2CreateBibResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CirculationStatusFactoryUT {
    @InjectMocks
    CirculationStatusFactory circulationStatusFactory;
    @Test
    public void testgetCirculationStatusForITEM_BARCODE_NOT_FOUND() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.ITEM_BARCODE_NOT_FOUND;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("00");
        circulationStatusFactory.toString();
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForOTHER() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.OTHER;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("01");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForON_ORDER() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.ON_ORDER;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("02");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForAVAILABLE() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.AVAILABLE;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("03");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForCHARGED() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.CHARGED;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("04");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForCHARGED_NOT_TO_BE_RECALLED_UNTIL_EARLIEST_RECALL_DATE() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.CHARGED_NOT_TO_BE_RECALLED_UNTIL_EARLIEST_RECALL_DATE;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("05");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForIN_PROCESS() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.IN_PROCESS;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("06");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForRECALLED() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.RECALLED;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("07");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForWAITING_ON_HOLD_SHELF() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.WAITING_ON_HOLD_SHELF;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("08");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForWAITING_TO_BE_RESHELVED() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.WAITING_TO_BE_RESHELVED;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("09");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForIN_TRANSIT() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.IN_TRANSIT;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("10");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForCLAIMED_RETURNED() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.CLAIMED_RETURNED;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("11");
        assertEquals(returnValue, result);
    }

    @Test
    public void testgetCirculationStatusForLOST() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.LOST;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("12");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForMISSING() throws InvalidSIP2ResponseValueException {
        CirculationStatus returnValue = CirculationStatus.MISSING;
        CirculationStatus result = circulationStatusFactory.getCirculationStatus("13");
        assertEquals(returnValue, result);
    }
    @Test
    public void testgetCirculationStatusForInvalidSIP2ResponseValueException() throws InvalidSIP2ResponseValueException {
        try {
            CirculationStatus result = circulationStatusFactory.getCirculationStatus("14");
        }
        catch (InvalidSIP2ResponseValueException invalidSIP2ResponseValueException)
        {
            invalidSIP2ResponseValueException.printStackTrace();
        }

    }

}
