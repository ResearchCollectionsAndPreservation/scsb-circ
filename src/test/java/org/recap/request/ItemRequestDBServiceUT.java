package org.recap.request;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.RecapCommonConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 21/2/17.
 */
public class ItemRequestDBServiceUT extends BaseTestCase {

    @Autowired
    ItemRequestDBService itemRequestDBService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    InstitutionDetailsRepository institutionDetailRepository;

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test // Test Cases
    public void testUpdateRecapRequestItem() throws Exception {

        String requestStatusCode = "REFILED";
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setExpirationDate("2017-02-21");
        itemRequestInformation.setRequestingInstitution("NYPL");
        itemRequestInformation.setUsername("john");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setPatronBarcode("426598712");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestId(1);

        try {
            Integer response = itemRequestDBService.updateRecapRequestItem(itemRequestInformation, saveBibSingleHoldingsSingleItem().getItemEntities().get(0), requestStatusCode, null);
            assertNotNull(response);
            itemRequestInformation.setRequestId(0);
            Integer responsenew = itemRequestDBService.updateRecapRequestItem(itemRequestInformation, saveBibSingleHoldingsSingleItem().getItemEntities().get(0), requestStatusCode, null);
            assertNotNull(responsenew);
        } catch (Exception e) {

        }
        //
        Random random = new Random();
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = new InstitutionEntity();
        entity.setId(12);
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(entity.getId());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("7020");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        List<ItemEntity> list = new ArrayList<ItemEntity>();
        list.add(itemEntity);
        try {
            itemRequestDBService.updateItemAvailabilutyStatus(list, "recap");
            itemRequestDBService.rollbackUpdateItemAvailabilutyStatus(itemEntity, "recap");
            ItemRequestInformation itemRequestInformationnew = itemRequestDBService.rollbackAfterGFA(getItemInformationResponse());
            assertNotNull(itemRequestInformationnew);
        } catch (Exception e) {}

    }

    public ItemInformationResponse getItemInformationResponse() {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        itemInformationResponse.setCirculationStatus("test");
        itemInformationResponse.setSecurityMarker("test");
        itemInformationResponse.setFeeType("test");
        itemInformationResponse.setTransactionDate(new Date().toString());
        itemInformationResponse.setHoldQueueLength("10");
        itemInformationResponse.setTitleIdentifier("test");
        itemInformationResponse.setBibID("1223");
        itemInformationResponse.setDueDate(new Date().toString());
        itemInformationResponse.setExpirationDate("30-03-2017 00:00:00");
        itemInformationResponse.setRecallDate(new Date().toString());
        itemInformationResponse.setCurrentLocation("test");
        itemInformationResponse.setHoldPickupDate(new Date().toString());
        itemInformationResponse.setItemBarcode("32101077423406");
        itemInformationResponse.setRequestType("RECALL");
        itemInformationResponse.setRequestingInstitution("CUL");
        itemInformationResponse.setRequestId(2);
        return itemInformationResponse;
    }

    public BibliographicEntity getBibliographicEntity() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UOC");
        institutionEntity.setInstitutionName("University of Chicago");
      /*  InstitutionEntity entity = institutionDetailsRepository.save(institutionEntity);
        assertNotNull(entity);*/

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(2);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setCatalogingStatus("Complete");
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("7020");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("PB");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setCatalogingStatus(RecapCommonConstants.COMPLETE_STATUS);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;
    }

    public RequestTypeEntity createRequestType() {
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recallhold");
        requestTypeEntity.setRequestTypeDesc("Recallhold");
        RequestTypeEntity savedRequestTypeEntity = requestTypeDetailsRepository.save(requestTypeEntity);
        return savedRequestTypeEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() throws Exception {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        InstitutionEntity entity = institutionDetailRepository.save(institutionEntity);
        assertNotNull(entity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(entity.getId());
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("6027");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        BibliographicEntity savedBibliographicEntity = bibliographicDetailsRepository.saveAndFlush(bibliographicEntity);
        entityManager.refresh(savedBibliographicEntity);
        return savedBibliographicEntity;

    }

}