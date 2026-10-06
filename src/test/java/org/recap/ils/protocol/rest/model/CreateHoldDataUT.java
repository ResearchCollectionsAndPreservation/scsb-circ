package org.recap.ils.protocol.rest.model;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CreateHoldDataUT extends BaseTestCaseUT {

    @Test
    public void getCreateHoldData() {
        CreateHoldData createHoldData = new CreateHoldData();
        createHoldData.setId(1);
        createHoldData.setDescription(new Description());
        assertNotNull(createHoldData.getId());
        assertNotNull(createHoldData.getDescription());
    }
}
