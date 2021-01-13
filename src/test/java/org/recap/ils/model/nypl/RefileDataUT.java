package org.recap.ils.model.nypl;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class RefileDataUT extends BaseTestCaseUT {

    @Test
    public void getRefileData(){
        RefileData refileData = new RefileData();
        refileData.setId(1);
        refileData.setUpdatedDate(new Date());
        refileData.setCreatedDate(new Date().toString());
        assertNotNull(refileData.getId());
        assertNotNull(refileData.getUpdatedDate());
        assertNotNull(refileData.getCreatedDate());
    }
}
