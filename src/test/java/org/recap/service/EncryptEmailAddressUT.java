package org.recap.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.ScsbCircApplication;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestItemStatusDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by akulak on 20/9/17.
 */
@SpringBootTest(classes = ScsbCircApplication.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class EncryptEmailAddressUT {

    @InjectMocks
    EncryptEmailAddressService mockedEncryptEmailAddressService;

    @Mock
    RequestItemDetailsRepository mockedRequestItemDetailsRepository;

    @Mock
    SecurityUtil mockedSecurityUtil;

    public static final String REQUEST_ID = "requestId";

    @Autowired
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    EncryptEmailAddressService encryptEmailAddressService;

    @Autowired
    SecurityUtil securityUtil;

    @Test
    public void checkEmailAddressEncryption() {
        try {
            RequestItemEntity requestItem = createRequestItem();
            requestItem = requestItemDetailsRepository.save(requestItem);
            String encryptedValue = securityUtil.getEncryptedValue("test@gmail.com");
            encryptEmailAddressService.encryptEmailAddress();
            RequestItemEntity requestItemEntity = requestItemDetailsRepository.findById(requestItem.getId()).orElse(null);
            String decryptedValue = securityUtil.getDecryptedValue(encryptedValue);
            assertNotNull(requestItemEntity);
            assertEquals("test@gmail.com", decryptedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void encryptEmailAddress() {
        Mockito.when(mockedRequestItemDetailsRepository.count()).thenReturn(10L);
        List<RequestItemEntity> requestItemEntityListToSave = new ArrayList<>();
        RequestItemEntity requestItemEntity = createRequestItem();
        requestItemEntityListToSave.add(requestItemEntity);
        Pageable pageable = PageRequest.of(0, 1000, Sort.Direction.ASC, REQUEST_ID);
        Page<RequestItemEntity> page = new PageImpl<>(requestItemEntityListToSave);
        Mockito.when(mockedRequestItemDetailsRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(mockedSecurityUtil.getEncryptedValue(requestItemEntity.getEmailId())).thenReturn("test@gmail.com");
        Mockito.when(mockedRequestItemDetailsRepository.saveAll(requestItemEntityListToSave)).thenReturn(requestItemEntityListToSave);
        String encryptEmailAddress = mockedEncryptEmailAddressService.encryptEmailAddress();
        assertNotNull(encryptEmailAddress);
    }

    @Test
    public void encryptEmailAddressException() {
        Mockito.when(mockedRequestItemDetailsRepository.count()).thenReturn(10L);
        RequestItemEntity requestItemEntity = createRequestItem();
        String encryptEmailAddress = mockedEncryptEmailAddressService.encryptEmailAddress();
        assertNotNull(encryptEmailAddress);
    }

    private RequestItemEntity createRequestItem() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();

        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("Recall");
        requestTypeEntity.setRequestTypeDesc("Recall");

        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setItemId(bibliographicEntity.getItemEntities().get(0).getId());
        requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
        requestItemEntity.setRequestingInstitutionId(2);
        requestItemEntity.setStopCode("test");
        requestItemEntity.setNotes("test");
        requestItemEntity.setItemEntity(bibliographicEntity.getItemEntities().get(0));
        requestItemEntity.setInstitutionEntity(institutionEntity);
        requestItemEntity.setPatronId("1");
        requestItemEntity.setCreatedDate(new Date());
        requestItemEntity.setRequestExpirationDate(new Date());
        requestItemEntity.setRequestStatusId(3);
        requestItemEntity.setCreatedBy("test");
        requestItemEntity.setEmailId("test@gmail.com");
        requestItemEntity.setLastUpdatedDate(new Date());
        return requestItemEntity;
    }

    private BibliographicEntity saveBibSingleHoldingsSingleItem() {
        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent("mock holdings".getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("test");
        holdingsEntity.setLastUpdatedBy("test");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
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
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));
        return bibliographicEntity;
    }
}