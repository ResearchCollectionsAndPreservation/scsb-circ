package org.recap.model.deaccession;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class DeAccessionDBResponseEntityUT {

    @Test
    public void getDeAccessionDBResponseEntity(){
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBibliographicIds(Arrays.asList(123456,231456));
        deAccessionDBResponseEntity.setHoldingIds(Arrays.asList(237789,456788));
        deAccessionDBResponseEntity.setItemId(1);
        deAccessionDBResponseEntity.setItemStatus("Success");

        assertNotNull(deAccessionDBResponseEntity.getBibliographicIds());
        assertNotNull(deAccessionDBResponseEntity.getHoldingIds());
        assertNotNull(deAccessionDBResponseEntity.getItemId());
        assertNotNull(deAccessionDBResponseEntity.getItemStatus());

    }

}
