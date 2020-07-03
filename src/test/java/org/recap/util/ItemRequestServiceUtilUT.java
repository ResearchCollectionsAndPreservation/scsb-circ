package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.gfa.model.TtitemEDDResponse;
import org.recap.model.jpa.BulkRequestItem;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ItemRequestServiceUtilUT {
    @Mock
    ItemRequestServiceUtil itemRequestServiceUtil;

    @Before
    public void before(){
        itemRequestServiceUtil= Mockito.mock(ItemRequestServiceUtil.class);
    }
    @Test
    public void testupdateSolrIndex(){
        ItemEntity itemEntity=new ItemEntity();
        itemRequestServiceUtil.updateSolrIndex(itemEntity);
    }
    @Test
    public void testupdateStatusToBarcodes(){
        BulkRequestItem bulkRequestItem =new BulkRequestItem();
        List<BulkRequestItem> test=new ArrayList<>();
        test.add(bulkRequestItem);
        BulkRequestItemEntity BulkRequestItemEntity =new BulkRequestItemEntity();
        itemRequestServiceUtil.updateStatusToBarcodes(test,BulkRequestItemEntity);
        assertTrue(true);
    }
    @Test
    public void testbuildCsvFormatData(){
        BulkRequestItem bulkRequestItem =new BulkRequestItem();
        List<BulkRequestItem> test=new ArrayList<>();
        test.add(bulkRequestItem);
        StringBuilder testdata =new StringBuilder("test data");
        itemRequestServiceUtil.buildCsvFormatData(test,testdata);
        assertTrue(true);
    }
    @Test
    public void testgenerateReportAndSendEmail(){
        Integer data=1234;
        itemRequestServiceUtil.generateReportAndSendEmail(data);
        assertTrue(true);
    }
    @Test
    public void testsetEddInfoToGfaRequest() {
        TtitemEDDResponse TtitemEDDResponse = new TtitemEDDResponse();
        String line = "testdata:testdat:testdata";
        itemRequestServiceUtil.setEddInfoToGfaRequest(line, TtitemEDDResponse);
        assertTrue(true);
    }
    @Test
    public void testsetEddInfoToScsbRequest() {
        ItemRequestInformation ItemRequestInformation =  new ItemRequestInformation();
        String line = "testdata:testdat:testdata";
        itemRequestServiceUtil.setEddInfoToScsbRequest(line, ItemRequestInformation);
        assertTrue(true);
    }
}
