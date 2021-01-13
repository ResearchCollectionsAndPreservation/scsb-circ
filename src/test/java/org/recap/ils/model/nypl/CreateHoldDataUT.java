package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class CreateHoldDataUT extends BaseTestCaseUT {

    @Test
    public void getCreateHoldData(){
        CreateHoldData createHoldData = new CreateHoldData();
        createHoldData.setId(1);
        createHoldData.setDescription(new Description());
        assertNotNull(createHoldData.getId());
        assertNotNull(createHoldData.getDescription());
    }
}
