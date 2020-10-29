package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.report.GfaDeaccessionInfo;
import org.recap.model.report.GfaStatusReconciliationInfo;
import org.recap.request.GFAService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class GFAServiceControllerUT extends BaseTestCaseUT {
    @InjectMocks
    GFAServiceController gfaServiceController;
    @Mock
    GFAService gfaService;

    @Test
    public void itemStatusComparison() throws Exception {
        GfaStatusReconciliationInfo gfaStatusReconciliationInfo = new GfaStatusReconciliationInfo();
        StatusReconciliationErrorCSVRecord statusReconciliationErrorCSVRecord = new StatusReconciliationErrorCSVRecord();
        statusReconciliationErrorCSVRecord.setBarcode("23456");
        statusReconciliationErrorCSVRecord.setInstitution("PUL");
        statusReconciliationErrorCSVRecord.setReasonForFailure("FAILURE");
        gfaStatusReconciliationInfo.setStatusReconciliationErrorCSVRecordList(Arrays.asList(statusReconciliationErrorCSVRecord));
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecordList = new ArrayList<>();
        StatusReconciliationCSVRecord statusReconciliationCSVRecord = new StatusReconciliationCSVRecord();
        statusReconciliationCSVRecord.setBarcode("2346789");
        statusReconciliationCSVRecordList.add(statusReconciliationCSVRecord);
        Mockito.when(gfaService.itemStatusComparison(any(),any())).thenReturn(statusReconciliationCSVRecordList);
        List<StatusReconciliationCSVRecord> statusReconciliationCSVRecords = gfaServiceController.itemStatusComparison(gfaStatusReconciliationInfo);
        assertNotNull(statusReconciliationCSVRecords);
    }

    @Test
    public void itemStatusCheck(){
        String itemBarcode = "3455632";
        Mockito.when(gfaService.callGfaItemStatus(itemBarcode)).thenReturn("IN");
        String gfaItemStatusValue = gfaServiceController.itemStatusCheck(itemBarcode);
        assertNotNull(gfaItemStatusValue);
    }
    @Test
    public void deaccessionInGfa(){
        GfaDeaccessionInfo gfaDeaccessionInfo = new GfaDeaccessionInfo();
        gfaDeaccessionInfo.setUsername("test");
        gfaDeaccessionInfo.setDeAccessionDBResponseEntities(new ArrayList<>());
        Mockito.doNothing().when(gfaService).callGfaDeaccessionService(gfaDeaccessionInfo.getDeAccessionDBResponseEntities(), gfaDeaccessionInfo.getUsername());
        gfaServiceController.deaccessionInGfa(gfaDeaccessionInfo);

    }

}
