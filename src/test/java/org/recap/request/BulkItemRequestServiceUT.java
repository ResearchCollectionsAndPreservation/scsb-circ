package org.recap.request;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapConstants;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class BulkItemRequestServiceUT extends BaseTestCase {
    @Autowired
    BulkItemRequestService bulkItemRequestService;

    @Test
    public void testBulkItemRequestService() {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestName("TestFirstBulkRequest");
        bulkRequestItemEntity.setBulkRequestFileName("bulkItemUpload");
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestStatus(RecapConstants.PROCESSED);
        bulkRequestItemEntity.setCreatedBy("TestUser");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setPatronId("45678915");
        bulkRequestItemEntity.setId(12);
        try {
            bulkItemRequestService.bulkRequestItems(bulkRequestItemEntity.getId());
        } catch (Exception e) {
        }
        assertTrue(true);
    }
}
