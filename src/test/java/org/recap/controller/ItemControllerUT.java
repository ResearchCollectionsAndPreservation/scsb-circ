package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class ItemControllerUT extends BaseTestCaseUT {

    @InjectMocks
    ItemController itemController;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void findByBarcodeIn(){
        List<ItemEntity> itemEntities = new ArrayList<>();
        ItemEntity itemEntity = getItemEntity();
        itemEntities.add(itemEntity);
        String barcodes ="244467";
        ItemController itemController = new ItemController(itemDetailsRepository);
        Mockito.when(itemDetailsRepository.findByBarcodeInAndComplete(any())).thenReturn(itemEntities);
        List<ItemEntity> itemEntityList = itemController.findByBarcodeIn(barcodes);
        assertNotNull(itemEntityList);
    }
    private ItemEntity getItemEntity(){
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId("1");
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
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        itemEntity.setInstitutionEntity(institutionEntity);
        BibliographicEntity bibliographicEntity = getBibliographicEntity();
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        return itemEntity;
    }
    private BibliographicEntity getBibliographicEntity() {
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setBibliographicId(1);
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId("1234");
        return bibliographicEntity;
    }
}
