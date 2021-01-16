package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class RecallDataUT extends BaseTestCaseUT {

    @Test
    public void getRecallData(){
        RecallData recallData = new RecallData();
        recallData.setId(1);
        recallData.setUpdatedDate(new Date().toString());
        recallData.setCreatedDate(new Date().toString());
        assertNotNull(recallData.getId());
        assertNotNull(recallData.getUpdatedDate());
        assertNotNull(recallData.getCreatedDate());

    }
}
