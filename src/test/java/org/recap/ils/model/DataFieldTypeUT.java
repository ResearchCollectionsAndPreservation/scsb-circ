package org.recap.ils.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class DataFieldTypeUT extends BaseTestCaseUT {

    @Test
    public void dataFieldType() {
        DataFieldType dataFieldType = new DataFieldType();
        dataFieldType.setSubfield(null);
        dataFieldType.setId("1");
        dataFieldType.setInd1("1");
        dataFieldType.setInd2("0");
        dataFieldType.setTag("245");
        assertNotNull(dataFieldType.getSubfield());
        assertNotNull(dataFieldType.getId());
        assertNotNull(dataFieldType.getInd1());
        assertNotNull(dataFieldType.getInd2());
    }
}
