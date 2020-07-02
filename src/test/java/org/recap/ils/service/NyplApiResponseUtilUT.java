package org.recap.ils.service;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ils.model.nypl.*;
import org.recap.ils.model.nypl.response.ItemResponse;
import org.recap.ils.model.nypl.response.RecallResponse;
import org.recap.ils.model.nypl.response.RefileResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.ils.model.response.ItemRecallResponse;
import org.recap.model.jpa.ItemRefileResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NyplApiResponseUtilUT extends BaseTestCase {
    @Autowired
    private NyplApiResponseUtil nyplApiResponseUtil;

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
    public void testbuildItemRecallResponse(){
        RecallData recallData = new RecallData();
        recallData.setCreatedDate(new Date().toString());
        recallData.setId(1);
        recallData.setItemBarcode("33433001888415");
        recallData.setJobId("879591d67acdf584");
        recallData.setOwningInstitutionId("PUL");
        recallData.setUpdatedDate(new Date().toString());
        RecallResponse recallResponse = new RecallResponse();
        recallResponse.setData(recallData);
        ItemRecallResponse itemRecallResponse = nyplApiResponseUtil.buildItemRecallResponse(recallResponse);
        assertNotNull(itemRecallResponse);
        RefileResponse refileResponse = new RefileResponse();
        RefileData refileData = new RefileData();
        refileData.setCreatedDate(new Date().toString());
        refileData.setId(1);
        refileData.setItemBarcode("33433001888415");
        refileData.setJobId("879591d67acdf584");
        refileData.setUpdatedDate(new Date().toString());
        refileResponse.setData(refileData);
        ItemRefileResponse itemRefileResponse = nyplApiResponseUtil.buildItemRefileResponse(refileResponse);
        assertNotNull(itemRefileResponse);
        Notice notice = new Notice();
        notice.setCreatedDate(new Date().toString());
        notice.setData("data");
        notice.setText("Data");
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
        assertNotNull(resNypl);
        String resItem= null;
        try {
            resItem  = nyplApiResponseUtil.getNormalizedItemIdForNypl("33433001888415");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(resItem);
        String resDate = null;
        try {
            resDate  = nyplApiResponseUtil.expirationDateForNypl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(resDate);

    }
}
