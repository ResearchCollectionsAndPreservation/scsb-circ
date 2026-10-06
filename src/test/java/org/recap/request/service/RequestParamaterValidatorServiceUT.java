package org.recap.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.ScsbCommonConstants;
import org.recap.common.ScsbConstants;
import org.recap.controller.ItemController;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.request.ItemRequestInformation;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by hemalathas on 3/11/16.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class RequestParamaterValidatorServiceUT {

    @InjectMocks
    RequestParamaterValidatorService requestParamaterValidatorService;

    @Mock
    ItemController itemController;

    @Mock
    CommonUtil commonUtil;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Test
    public void testForValidatingInvalidRequestingInstitution() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        itemRequestInformation.setEmailAddress("test@email.com");
        Mockito.when(institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(true);
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNotNull(responseEntity);
    }

    @Test
    public void testForValidatingInvalidRequestingInstitutionWithBarcode() {
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        InstitutionEntity institutionEntity = getInstitutionEntity();
        itemRequestInformation.setRequestType(ScsbConstants.EDD_REQUEST);
        Mockito.when(commonUtil.findAllInstitutionCodesExceptSupportInstitution()).thenReturn(Arrays.asList(institutionEntity.getInstitutionName()));
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNotNull(responseEntity);
        itemRequestInformation.setEmailAddress("");
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_RECALL);
        Mockito.when(institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(true);
        ResponseEntity responseEntity1 = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNotNull(responseEntity1);
    }

    private ItemRequestInformation getItemRequestInformation() {

        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_BORROW_DIRECT);
        itemRequestInformation.setRequestingInstitution("PUL");
        return itemRequestInformation;
    }

    @Test
    public void testForValidatingInvalidEmailAddress() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("33433014514719");
        itemBarcodeList.add("33433012968222");
        itemRequestInformation.setItemBarcodes(itemBarcodeList);
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType(ScsbCommonConstants.REQUEST_TYPE_BORROW_DIRECT);
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("test");
        Mockito.when(institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(true);
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), ScsbConstants.INVALID_EMAIL_ADDRESS + "\n");
    }

    @Test
    public void testForValidatingInvalidRequestType() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("33433014514719");
        itemBarcodeList.add("33433012968222");
        itemRequestInformation.setItemBarcodes(itemBarcodeList);
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType(null);
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("test@email.com");
        Mockito.when(institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(true);
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getBody(), ScsbConstants.INVALID_REQUEST_TYPE + "\n");
    }

    @Test
    public void testForValidatingEDDRequestType() {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        List<String> itemBarcodeList = new ArrayList<>();
        itemBarcodeList.add("33433014514719");
        itemRequestInformation.setItemBarcodes(itemBarcodeList);
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setChapterTitle("title");
        itemRequestInformation.setRequestType(ScsbConstants.EDD_REQUEST);
        itemRequestInformation.setDeliveryLocation("AC");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("test@email.com");
        itemRequestInformation.setStartPage("0");
        itemRequestInformation.setEndPage("0");
        Mockito.when(institutionDetailsRepository.existsByInstitutionCode(itemRequestInformation.getRequestingInstitution())).thenReturn(true);
        ResponseEntity responseEntity = requestParamaterValidatorService.validateItemRequestParameters(itemRequestInformation);
        assertNull(responseEntity);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("PUL");
        institutionEntity.setInstitutionName("PUL");
        return institutionEntity;
    }
}