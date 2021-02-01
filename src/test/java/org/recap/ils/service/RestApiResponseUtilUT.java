package org.recap.ils.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ils.model.nypl.*;
import org.recap.ils.model.nypl.response.ItemResponse;
import org.recap.ils.model.nypl.response.RecallResponse;
import org.recap.ils.model.nypl.response.RefileResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.model.response.ItemRecallResponse;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRefileResponse;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
@RunWith(MockitoJUnitRunner.class)
public class RestApiResponseUtilUT {

    @InjectMocks
    RestApiResponseUtil restApiResponseUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void testBuildItemInformationResponse(){
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
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("dueDate",new Date().toString());
        map.put("display","TEST");
        map.put("name","testLocation");
        itemData.setStatus(map);
        itemData.setLocation(map);
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setItemData(itemData);
        itemResponse.setCount(1);
        itemResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        itemResponse.setStatusCode(1);
        ItemInformationResponse itemInformationResponse = restApiResponseUtil.buildItemInformationResponse(itemResponse);
        assertNotNull(itemInformationResponse);
    }
    @Test
    public void getRestApiSourceForInstitution(){
        String result1 = restApiResponseUtil.getRestApiSourceForInstitution("NYPL", "PUL");
        assertNull(result1);
        String result2 = restApiResponseUtil.getRestApiSourceForInstitution("NYPL", "CUL");
        assertNull(result2);
    }
    @Test
    public void getNormalizedItemIdForNypl() throws Exception {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(123456);
        itemEntity.setOwningInstitutionItemId("124567");
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        Mockito.when(itemDetailsRepository.findByBarcode("123456")).thenReturn(Arrays.asList(itemEntity));
        String itemId = restApiResponseUtil.getNormalizedItemIdForRestProtocolApi("123456");
        assertNotNull(itemId);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(3);
        institutionEntity.setInstitutionName("NYPL");
        institutionEntity.setInstitutionCode("NYPL");
        return institutionEntity;
    }

    @Test
    public void testBuildItemRecallResponse(){
        RecallData recallData = getRecallData();
        RecallResponse recallResponse = new RecallResponse();
        recallResponse.setData(recallData);
        ItemRecallResponse itemRecallResponse = restApiResponseUtil.buildItemRecallResponse(recallResponse);
        assertNotNull(itemRecallResponse);
        RefileResponse refileResponse = new RefileResponse();
        RefileData refileData = getRefileData();
        refileResponse.setData(refileData);
        ItemRefileResponse itemRefileResponse = restApiResponseUtil.buildItemRefileResponse(refileResponse, "NYPL");
        assertNotNull(itemRefileResponse);
        Notice notice = getNotice();
        JobData jobData = new JobData();
        List<Notice> notices = new ArrayList<>();
        notices.add(notice);
        jobData.setNotices(notices);
        String res = null;
        try {
            res  = restApiResponseUtil.getJobStatusMessage(jobData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(res);

        String resNypl = restApiResponseUtil.getRestApiSourceForInstitution("NYPL", "PUL");
        assertNull(resNypl);
        String resItem= null;
        try {
            resItem  = restApiResponseUtil.getNormalizedItemIdForRestProtocolApi("33433001888415");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resDate = null;
        try {
            resDate  = restApiResponseUtil.expirationDateForRest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(resDate);

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
}
