package org.recap.request;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.controller.RequestItemController;
import org.recap.controller.RequestItemValidatorController;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.las.GFALasService;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.util.ItemRequestServiceUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by hemalathas on 17/2/17.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ItemEDDRequestServiceUT extends BaseTestCaseUT {

    private static final Logger logger = LoggerFactory.getLogger(ItemEDDRequestServiceUT.class);

    @InjectMocks
    ItemEDDRequestService itemEDDRequestService;

    @Mock
    Exchange exchange;

    @Mock
    private PropertyUtil propertyUtil;

    @Mock
    GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    RequestItemController requestItemController;

    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Mock
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    private RequestItemValidatorController requestItemValidatorController;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private GFALasService gfaLasService;



    @Test
    public void testEddRequestItem() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("UC");

        ItemInformationResponse itemResponseInformation1 = new ItemInformationResponse();
        itemResponseInformation1.setSuccess(true);
        itemResponseInformation1.setRequestTypeForScheduledOnWO(true);

        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();

        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        itemCheckoutResponse.setSuccess(true);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        itemEDDRequestService.getRequestTypeDetailsRepository();
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.updateRecapRequestItem(any(), any(),any())).thenReturn(1);
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemRequestService.updateItemAvailabilityStatus(List.of(itemEntity), itemRequestInfo.getUsername())).thenReturn(true);
        Mockito.when(itemRequestService.getGfaLasService()).thenReturn(gfaLasService);
        Mockito.when(gfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.LAS_REFILE_REQUEST_PLACED)).thenReturn(1);
        Mockito.when(requestItemController.getItemRequestService()).thenReturn(itemRequestService);
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(getGenericPatronEntity());
        Mockito.when(itemRequestService.updateGFA(any(), any())).thenReturn(itemResponseInformation);
        Mockito.doNothing().when(itemRequestService).sendMessageToTopic(any(), any(), any(), any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInfo.getRequestingInstitution(), "use.generic.patron.edd.self")).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
        Mockito.when(itemRequestService.updateGFA(any(), any())).thenReturn(itemResponseInformation1);
        Mockito.when(requestItemController.checkoutItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution())).thenReturn(itemCheckoutResponse);
        ItemInformationResponse itemInfoResponse1 = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse1);
        itemCheckoutResponse.setSuccess(false);
        Mockito.when(requestItemController.checkoutItem(itemRequestInfo, itemRequestInfo.getItemOwningInstitution())).thenReturn(itemCheckoutResponse);
        ItemInformationResponse itemInfoResponse2 = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse2);
    }
    @Test
    public void testEddRequestItemInnerException() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("UC");

        ItemInformationResponse itemResponseInformation1 = new ItemInformationResponse();
        itemResponseInformation1.setSuccess(true);
        itemResponseInformation1.setRequestTypeForScheduledOnWO(true);

        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();

        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        itemCheckoutResponse.setSuccess(true);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        itemEDDRequestService.getRequestTypeDetailsRepository();
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.updateRecapRequestItem(any(), any(), any())).thenReturn(1);
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemRequestService.updateItemAvailabilityStatus(List.of(itemEntity), itemRequestInfo.getUsername())).thenReturn(true);
        Mockito.when(itemRequestService.getGfaLasService()).thenReturn(gfaLasService);
        Mockito.when(gfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(itemRequestService.updateGFA(any(), any())).thenReturn(itemResponseInformation);
        Mockito.doNothing().when(itemRequestService).sendMessageToTopic(any(), any(), any(), any());
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(itemRequestService.updateRecapRequestItem(any(), any(), any())).thenReturn(1);
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenThrow(new NullPointerException());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInfo.getRequestingInstitution(), "use.generic.patron.edd.self")).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }
    @Test
    public void testEddRequestItemWithFailureResponse() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("PUL");

        ItemInformationResponse itemResponseInformation1 = new ItemInformationResponse();
        itemResponseInformation1.setSuccess(true);

        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING)).thenReturn(1);
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PENDING)).thenReturn(1);
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemRequestService.updateItemAvailabilityStatus(List.of(itemEntity), itemRequestInfo.getUsername())).thenReturn(true);
        Mockito.when(itemRequestService.getGfaLasService()).thenReturn(gfaLasService);
        Mockito.when(gfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(itemRequestService.updateGFA(any(), any())).thenReturn(itemResponseInformation);
        Mockito.doNothing().when(itemRequestService).sendMessageToTopic(any(), any(), any(), any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInfo.getRequestingInstitution(), "use.generic.patron.edd.cross")).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }
    @Test
    public void testEddRequestItemWithFailureResponseInnerException() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        itemRequestInfo.setRequestingInstitution("PUL");

        ItemInformationResponse itemResponseInformation1 = new ItemInformationResponse();
        itemResponseInformation1.setSuccess(true);

        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING)).thenReturn(1);
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PENDING)).thenReturn(1);
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemRequestService.updateItemAvailabilityStatus(List.of(itemEntity), itemRequestInfo.getUsername())).thenReturn(true);
        Mockito.when(itemRequestService.getGfaLasService()).thenReturn(gfaLasService);
        Mockito.when(gfaLasService.isUseQueueLasCall(any())).thenReturn(true);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(itemRequestServiceUtil.getPatronIdBorrowingInstitution(any(), any(), any())).thenThrow(new NullPointerException());
        Mockito.when(itemRequestService.updateGFA(any(), any())).thenReturn(itemResponseInformation);
        Mockito.doNothing().when(itemRequestService).sendMessageToTopic(any(), any(), any(), any());
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemRequestInfo.getRequestingInstitution(), "use.generic.patron.edd.cross")).thenReturn(Boolean.TRUE.toString());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }

    @Test
    public void testEddRequestItemwWithoutStatusAvailability() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(true);

        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.updateRecapRequestItem(itemRequestInfo, itemEntity, RecapConstants.REQUEST_STATUS_PROCESSING)).thenReturn(1);
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemRequestService.updateItemAvailabilityStatus(List.of(itemEntity), itemRequestInfo.getUsername())).thenReturn(false);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
        assertEquals(itemInfoResponse.getTitleIdentifier(), "Title Of the Book");
    }

    @Test
    public void testEddRequestItemWithoutItemEntity() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(true);
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(null);
        Mockito.when(itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange)).thenCallRealMethod();
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }

    @Test
    public void testEddRequestItemWithoutRequestId() throws Exception {
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setTitle("Title Of the Book");
        ResponseEntity res = new ResponseEntity(RecapConstants.WRONG_ITEM_BARCODE, HttpStatus.CONTINUE);
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        ItemInformationResponse itemResponseInformation = new ItemInformationResponse();
        itemResponseInformation.setSuccess(true);
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        ItemEntity itemEntity = bibliographicEntity.getItemEntities().get(0);
        Mockito.when(itemRequestService.searchRecords(any())).thenReturn(searchResultRow);
        Mockito.when(itemRequestService.removeDiacritical(searchResultRow.getTitle().replaceAll("[^\\x00-\\x7F]", "?"))).thenReturn("Title Of the Book");
        Mockito.when(itemRequestService.searchRecords(itemEntity)).thenReturn(getSearchResultRowList());
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
        assertEquals(itemInfoResponse.getTitleIdentifier(), "Title Of the Book");
    }

    @Test
    public void testEddRequestRestClientException() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        BibliographicEntity bibliographicEntity = saveBibSingleHoldingsSingleItem();
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenReturn(bibliographicEntity.getItemEntities());
        Mockito.when(itemRequestService.searchRecords(any())).thenThrow(new RestClientException("Bad Request"));
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }

    @Test
    public void testEddRequestException() {
        ItemRequestInformation itemRequestInfo = getItemRequestInformation();
        Mockito.when(itemDetailsRepository.findByBarcodeIn(itemRequestInfo.getItemBarcodes())).thenThrow(new NullPointerException());
        ItemInformationResponse itemInfoResponse = itemEDDRequestService.eddRequestItem(itemRequestInfo, exchange);
        assertNotNull(itemInfoResponse);
    }

    private ItemRequestInformation getItemRequestInformation() {
        ItemRequestInformation itemRequestInfo = new ItemRequestInformation();
        itemRequestInfo.setItemBarcodes(Arrays.asList("23"));
        itemRequestInfo.setItemOwningInstitution("PUL");
        itemRequestInfo.setExpirationDate(new Date().toString());
        itemRequestInfo.setRequestingInstitution("PUL");
        itemRequestInfo.setTitleIdentifier("titleIdentifier");
        itemRequestInfo.setPatronBarcode("4356234");
        itemRequestInfo.setBibId("1");
        itemRequestInfo.setDeliveryLocation("PB");
        itemRequestInfo.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInfo.setRequestType("Recall");
        itemRequestInfo.setRequestNotes("Test");
        itemRequestInfo.setImsLocationCode(getImsLocationEntity().getImsLocationCode());
        return itemRequestInfo;
    }

    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }

    @Test
    public void getPatronIdForOwningInstitutionOnEdd() {
        GenericPatronEntity genericPatronEntity = getGenericPatronEntity();
        Mockito.when(genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(any(), any())).thenReturn(genericPatronEntity);
        itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(RecapCommonConstants.PRINCETON);
        itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(RecapCommonConstants.COLUMBIA);
        itemEDDRequestService.getPatronIdForOwningInstitutionOnEdd(RecapCommonConstants.NYPL);
    }

    private GenericPatronEntity getGenericPatronEntity() {
        GenericPatronEntity genericPatronEntity = new GenericPatronEntity();
        genericPatronEntity.setGenericPatronId(1);
        genericPatronEntity.setEddGenericPatron("edd");
        genericPatronEntity.setCreatedBy("test");
        genericPatronEntity.setCreatedDate(new Date());
        genericPatronEntity.setRetrievalGenericPatron("retrieve");
        genericPatronEntity.setItemOwningInstitutionId(1);
        genericPatronEntity.setOwningInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionEntity(new InstitutionEntity());
        genericPatronEntity.setRequestingInstitutionId(1);
        genericPatronEntity.setUpdatedBy("test");
        genericPatronEntity.setUpdatedDate(new Date());
        return genericPatronEntity;
    }

    public BibliographicEntity saveBibSingleHoldingsSingleItem() {

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        assertNotNull(institutionEntity);

        Random random = new Random();
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("mock Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(institutionEntity.getId());
        bibliographicEntity.setOwningInstitutionBibId("");
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
        itemEntity.setBarcode("0235");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setCatalogingStatus("Completed");
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);
        itemEntity.setImsLocationEntity(getImsLocationEntity());

        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;

    }

    public SearchResultRow getSearchResultRowList() {
        SearchResultRow searchItemResultRow = new SearchResultRow();
        searchItemResultRow.setTitle("Title Of the Book");
        searchItemResultRow.setAuthor("AuthorBook");
        searchItemResultRow.setCustomerCode("PB");
        searchItemResultRow.setBarcode("123");
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setCollectionGroupDesignation("Shared");
        searchItemResultRow.setItemId(1);
        return searchItemResultRow;
    }

}
