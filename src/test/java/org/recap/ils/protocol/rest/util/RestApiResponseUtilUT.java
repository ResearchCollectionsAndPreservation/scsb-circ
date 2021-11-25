package org.recap.ils.protocol.rest.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.ils.protocol.rest.model.*;
import org.recap.ils.protocol.rest.model.response.*;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.response.*;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class RestApiResponseUtilUT extends BaseTestCaseUT {

    @InjectMocks
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    private PropertyUtil propertyUtil;

    @Test
    public void testBuildItemInformationResponse() {
        ItemResponse itemResponse = getItemResponse();
        ItemInformationResponse itemInformationResponse = restApiResponseUtil.buildItemInformationResponse(itemResponse);
        assertNotNull(itemInformationResponse);
    }


    @Test
    public void getRestApiSourceForInstitution() {
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey("NYPL", ScsbConstants.ILS_SOURCE_FOR_ITEM + "PUL".toLowerCase())).thenReturn("test");
        String result1 = restApiResponseUtil.getRestApiSourceForInstitution("NYPL", "PUL");
        assertNotNull(result1);
    }

    @Test
    public void getNormalizedItemIdForRest() throws Exception {
        ItemEntity itemEntity = getItemEntity();
        Mockito.when(itemDetailsRepository.findByBarcode("123456")).thenReturn(Arrays.asList(itemEntity));
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey(itemEntity.getInstitutionEntity().getInstitutionCode(), PropertyKeyConstants.ILS.ILS_NORMALIZE_OWNING_INST_ITEM_ID)).thenReturn(Boolean.TRUE.toString());
        String itemId = restApiResponseUtil.getNormalizedItemIdForRestProtocolApi("123456");
        assertNotNull(itemId);
    }

    @Test
    public void buildItemCheckoutResponse() {
        CheckoutResponse checkoutResponse = getCheckoutResponse();
        ItemCheckoutResponse itemCheckoutResponse = restApiResponseUtil.buildItemCheckoutResponse(checkoutResponse);
        assertNotNull(itemCheckoutResponse);
    }

    @Test
    public void buildItemCheckInResponse() {
        CheckinResponse checkinResponse = getCheckinResponse();
        ItemCheckinResponse itemCheckinResponse = restApiResponseUtil.buildItemCheckinResponse(checkinResponse);
        assertNotNull(itemCheckinResponse);
    }

    @Test
    public void buildItemHoldResponse() throws Exception {
        CreateHoldResponse createHoldResponse = getCreateHoldResponse();
        ItemHoldResponse itemHoldResponse = restApiResponseUtil.buildItemHoldResponse(createHoldResponse);
        assertNotNull(itemHoldResponse);
    }

    @Test
    public void buildItemCancelHoldResponse() throws Exception {
        CancelHoldResponse cancelHoldResponse = getCancelHoldResponse();
        ItemHoldResponse itemHoldResponse = restApiResponseUtil.buildItemCancelHoldResponse(cancelHoldResponse);
        assertNotNull(itemHoldResponse);
    }

    @Test
    public void testBuildItemRecallResponse() {
        RecallResponse recallResponse = getRecallResponse();
        ItemRecallResponse itemRecallResponse = restApiResponseUtil.buildItemRecallResponse(recallResponse);
        assertNotNull(itemRecallResponse);
    }

    @Test
    public void testBuildItemRefileResponse() {
        RefileResponse refileResponse = getRefileResponse();
        ItemRefileResponse itemRefileResponse = restApiResponseUtil.buildItemRefileResponse(refileResponse, "NYPL");
        assertNotNull(itemRefileResponse);
    }

    @Test
    public void getExpirationDateForRest() throws Exception {
        String response = restApiResponseUtil.getExpirationDateForRest();
        assertNotNull(response);
    }

    @Test
    public void getItemOwningInstitutionByItemBarcode() throws Exception {
        Mockito.when(itemDetailsRepository.findByBarcode(any())).thenReturn(Arrays.asList(getItemEntity()));
        String response = restApiResponseUtil.getItemOwningInstitutionByItemBarcode("3254365");
        assertNotNull(response);
    }

    @Test
    public void requiredFormattedDate() {
        String sipDate = "31-01-2020";
        SimpleDateFormat sipFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat requiredFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
        ReflectionTestUtils.invokeMethod(restApiResponseUtil, "requiredFormattedDate", sipDate, sipFormat, requiredFormat);
    }

    @Test
    public void testBuildItemJobResponse() {
        JobData jobData = getJobData();
        String res = null;
        try {
            res = restApiResponseUtil.getJobStatusMessage(jobData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(res);
    }

    @Test
    public void testgetRestApiSourceForInstitution() {
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey("NYPL", ScsbConstants.ILS_SOURCE_FOR_ITEM + "PUL".toLowerCase())).thenReturn("test");
        String resNypl = restApiResponseUtil.getRestApiSourceForInstitution("NYPL", "PUL");
        assertNotNull(resNypl);
    }

    @Test
    public void getNormalizedItemIdForRestProtocolApi() {
        String resItem = null;
        try {
            resItem = restApiResponseUtil.getNormalizedItemIdForRestProtocolApi("33433001888415");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void expirationDateForRest() {
        String resDate = null;
        try {
            resDate = restApiResponseUtil.expirationDateForRest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(resDate);
    }

    private JobData getJobData() {
        JobData jobData = new JobData();
        List<Notice> notices = new ArrayList<>();
        Notice notice = getNotice();
        notices.add(notice);
        jobData.setNotices(notices);
        return jobData;
    }

    private RefileResponse getRefileResponse() {
        RefileResponse refileResponse = new RefileResponse();
        RefileData refileData = getRefileData();
        refileResponse.setData(refileData);
        return refileResponse;
    }

    private RecallResponse getRecallResponse() {
        RecallResponse recallResponse = new RecallResponse();
        RecallData recallData = getRecallData();
        recallResponse.setData(recallData);
        return recallResponse;
    }

    private Notice getNotice() {
        Notice notice = new Notice();
        notice.setCreatedDate(new Date().toString());
        notice.setData("data");
        notice.setText("Data");
        return notice;
    }

    private RefileData getRefileData() {
        RefileData refileData = new RefileData();
        refileData.setCreatedDate(new Date().toString());
        refileData.setId(1);
        refileData.setItemBarcode("33433001888415");
        refileData.setJobId("879591d67acdf584");
        refileData.setUpdatedDate(new Date().toString());
        return refileData;
    }

    private RecallData getRecallData() {
        RecallData recallData = new RecallData();
        recallData.setCreatedDate(new Date().toString());
        recallData.setId(1);
        recallData.setItemBarcode("33433001888415");
        recallData.setJobId("879591d67acdf584");
        recallData.setOwningInstitutionId("PUL");
        recallData.setUpdatedDate(new Date().toString());
        return recallData;
    }

    private ItemResponse getItemResponse() {
        ItemResponse itemResponse = new ItemResponse();
        ItemData itemData = getItemData();
        itemResponse.setItemData(itemData);
        itemResponse.setCount(1);
        itemResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        itemResponse.setStatusCode(1);
        return itemResponse;
    }

    private ItemData getItemData() {
        ItemData itemData = new ItemData();
        itemData.setNyplSource("test");
        itemData.setBibIds(Arrays.asList("123"));
        itemData.setId("468");
        itemData.setNyplType("test");
        itemData.setUpdatedDate(new Date().toString());
        itemData.setDeletedDate(new Date().toString());
        itemData.setDeleted(true);
        itemData.setBarcode("33236547125452");
        itemData.setCallNumber("12");
        itemData.setItemType("test");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("dueDate", new Date().toString());
        map.put("display", "TEST");
        map.put("name", "testLocation");
        itemData.setStatus(map);
        itemData.setLocation(map);
        return itemData;
    }

    private ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(123456);
        itemEntity.setOwningInstitutionItemId("124567");
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        return itemEntity;
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(3);
        institutionEntity.setInstitutionName("NYPL");
        institutionEntity.setInstitutionCode("NYPL");
        return institutionEntity;
    }

    private CheckoutResponse getCheckoutResponse() {
        CheckoutResponse checkoutResponse = new CheckoutResponse();
        checkoutResponse.setCount(1);
        checkoutResponse.setStatusCode(1);
        CheckoutData checkoutData = new CheckoutData();
        checkoutData.setId(1);
        checkoutData.setJobId("1");
        checkoutData.setSuccess(true);
        checkoutData.setProcessed(true);
        checkoutResponse.setData(checkoutData);
        return checkoutResponse;
    }

    private CheckinResponse getCheckinResponse() {
        CheckinResponse checkinResponse = new CheckinResponse();
        checkinResponse.setCount(1);
        CheckinData checkinData = new CheckinData();
        checkinData.setCreatedDate(new Date().toString());
        checkinData.setJobId("1");
        checkinData.setSuccess(true);
        checkinData.setProcessed(true);
        checkinResponse.setData(checkinData);
        checkinResponse.setStatusCode(244);
        return checkinResponse;
    }

    private CreateHoldResponse getCreateHoldResponse() {
        CreateHoldResponse createHoldResponse = new CreateHoldResponse();
        CreateHoldData createHoldData = new CreateHoldData();
        Description description = new Description();
        description.setAuthor("John");
        description.setCallNumber("PB");
        description.setTitle("test");
        createHoldData.setCreatedDate("2017-03-30");
        createHoldData.setDescription(description);
        createHoldData.setId(1);
        createHoldData.setItemBarcode("33433001888415");
        createHoldData.setPatronBarcode("23333095887111");
        createHoldData.setOwningInstitutionId("NYPL");
        createHoldData.setTrackingId("1231");
        createHoldData.setUpdatedDate("2017-03-30");
        createHoldResponse.setCount(1);
        createHoldResponse.setData(createHoldData);
        createHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        createHoldResponse.setStatusCode(1);
        return createHoldResponse;
    }

    private CancelHoldResponse getCancelHoldResponse() {
        CancelHoldResponse cancelHoldResponse = new CancelHoldResponse();
        CancelHoldData cancelHoldData = new CancelHoldData();
        cancelHoldData.setCreatedDate("2017-03-30");
        cancelHoldData.setId(1);
        cancelHoldData.setItemBarcode("33433001888415");
        cancelHoldData.setPatronBarcode("23333095887111");
        cancelHoldData.setOwningInstitutionId("NYPL");
        cancelHoldData.setTrackingId("1231");
        cancelHoldData.setUpdatedDate("2017-03-30");
        cancelHoldData.setJobId("1");
        cancelHoldResponse.setData(cancelHoldData);
        cancelHoldResponse.setStatusCode(1);
        cancelHoldResponse.setCount(1);
        cancelHoldResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        assertNotNull(cancelHoldData.getId());
        return cancelHoldResponse;
    }
}
