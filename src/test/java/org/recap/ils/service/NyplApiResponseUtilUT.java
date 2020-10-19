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
public class NyplApiResponseUtilUT {

    @InjectMocks
    NyplApiResponseUtil nyplApiResponseUtil;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Test
    public void testbuildItemInformationResponse(){
        VarField varField = new VarField();
        SubField subField = new SubField();
        subField.setContent("test");
        subField.setTag("test");
        varField.setContent("test");
        varField.setFieldTag("test");
        varField.setInd1("test");
        varField.setInd2("test");
        varField.setFieldTag("test");
        varField.setSubFields(Arrays.asList(subField));
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
        itemData.setFixedFields("test");
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("dueDate",new Date().toString());
        map.put("display","TEST");
        map.put("name","testLocation");
        itemData.setStatus(map);
        itemData.setLocation(map);
        itemData.setVarFields(Arrays.asList(varField));
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setItemData(itemData);
        itemResponse.setCount(1);
        itemResponse.setDebugInfo(Arrays.asList(new DebugInfo()));
        itemResponse.setStatusCode(1);
        ItemInformationResponse itemInformationResponse = nyplApiResponseUtil.buildItemInformationResponse(itemResponse);
        assertNotNull(itemInformationResponse);
    }
    @Test
    public void getNyplSource(){
        String result1 = nyplApiResponseUtil.getNyplSource("NYPL");
        assertNull(result1);
        String result2 = nyplApiResponseUtil.getNyplSource("CUL");
        assertNull(result2);
    }
    @Test
    public void getNormalizedItemIdForNypl() throws Exception {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setItemId(123456);
        itemEntity.setOwningInstitutionItemId("124567");
        itemEntity.setInstitutionEntity(getInstitutionEntity());
        Mockito.when(itemDetailsRepository.findByBarcode("123456")).thenReturn(Arrays.asList(itemEntity));
        String itemId = nyplApiResponseUtil.getNormalizedItemIdForNypl("123456");
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
    public void testbuildItemRecallResponse(){
        RecallData recallData = getRecallData();
        RecallResponse recallResponse = new RecallResponse();
        recallResponse.setData(recallData);
        ItemRecallResponse itemRecallResponse = nyplApiResponseUtil.buildItemRecallResponse(recallResponse);
        assertNotNull(itemRecallResponse);
        RefileResponse refileResponse = new RefileResponse();
        RefileData refileData = getRefileData();
        refileResponse.setData(refileData);
        ItemRefileResponse itemRefileResponse = nyplApiResponseUtil.buildItemRefileResponse(refileResponse);
        assertNotNull(itemRefileResponse);
        Notice notice = getNotice();
        JobData jobData = new JobData();
        List<Notice> notices = new ArrayList<>();
        notices.add(notice);
        jobData.setNotices(notices);
        String res = null;
        try {
            res  = nyplApiResponseUtil.getJobStatusMessage(jobData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(res);

        String resNypl = nyplApiResponseUtil.getNyplSource("PUL");
        assertNull(resNypl);
        String resItem= null;
        try {
            resItem  = nyplApiResponseUtil.getNormalizedItemIdForNypl("33433001888415");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resDate = null;
        try {
            resDate  = nyplApiResponseUtil.expirationDateForNypl();
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
