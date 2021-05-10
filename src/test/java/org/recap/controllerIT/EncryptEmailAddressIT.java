package org.recap.controllerIT;

import org.junit.Test;
import org.recap.BaseControllerUT;
import org.recap.BaseTestCase;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EncryptEmailAddressIT extends BaseControllerUT {

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void startEncryptEmailAddress() throws Exception{
        RequestItemEntity requestItemEntity = createRequestItem();
        MvcResult mvcResult = this.mockMvc.perform(get("/encryptEmailAddress/startEncryptEmailAddress")
        ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }

    public RequestItemEntity createRequestItem() throws Exception {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setEmailId("test@gmail.com");
        requestItemEntity.setItemEntity(getItemEntity());
        requestItemEntity.setBulkRequestItemEntity(getBulkRequestItemEntity());
        requestItemEntity.setNotes("notes");
        requestItemEntity.setRequestStatusId(768909);
        requestItemEntity.setRequestStatusEntity(getRequestStatusEntity());
        requestItemEntity.setRequestTypeEntity(getRequestTypeEntity());
        requestItemEntity.setCreatedBy(new Date().toString());
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setInstitutionEntity(getInstitutionEntity());
        requestItemEntity.setItemId(1);
        requestItemEntity.setLastUpdatedDate(new Date());
        requestItemEntity.setPatronId("244532");
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestingInstitutionId(1);
        RequestItemEntity savedRequestItemEntity = requestItemDetailsRepository.save(requestItemEntity);
        return savedRequestItemEntity;
    }

    private InstitutionEntity getInstitutionEntity(){
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("TEST");
        institutionEntity.setInstitutionName("TEST");
        return institutionEntity;
    }

    private ItemEntity getItemEntity(){
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("13457");
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("8956");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("4598");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        return itemEntity;
    }
    private BulkRequestItemEntity getBulkRequestItemEntity(){
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setId(1);
        bulkRequestItemEntity.setPatronId("123456");
        bulkRequestItemEntity.setStopCode("PA");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setBulkRequestFileData("BARCODE\tCUSTOMER_CODE\n32101075852275\tPK".getBytes());
        bulkRequestItemEntity.setNotes("test");
        bulkRequestItemEntity.setEmailId("test@gmail.com");
        bulkRequestItemEntity.setCreatedBy("test");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        return bulkRequestItemEntity;
    }

    private RequestTypeEntity getRequestTypeEntity() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("TEST");
        requestTypeEntity.setRequestTypeDesc("TEST");
        return requestTypeEntity;
    }

    private RequestStatusEntity getRequestStatusEntity() {
        RequestStatusEntity requestStatusEntity =  new RequestStatusEntity();
        requestStatusEntity.setRequestStatusCode("TEST");
        requestStatusEntity.setRequestStatusDescription("TEST");
        return requestStatusEntity;
    }
}
