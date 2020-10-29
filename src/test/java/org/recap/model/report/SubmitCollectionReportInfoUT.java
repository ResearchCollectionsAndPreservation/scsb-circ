package org.recap.model.report;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SubmitCollectionReportInfoUT {

    @Test
    public void getSubmitCollectionReportInfo(){
        SubmitCollectionReportInfo submitCollectionReportInfo= new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode("12356");
        submitCollectionReportInfo.setCustomerCode("PA");
        submitCollectionReportInfo.setMessage("test");
        submitCollectionReportInfo.setOwningInstitution("1");

        assertNotNull(submitCollectionReportInfo.getItemBarcode());
        assertNotNull(submitCollectionReportInfo.getCustomerCode());
        assertNotNull(submitCollectionReportInfo.getMessage());
        assertNotNull(submitCollectionReportInfo.getOwningInstitution());
    }
}
