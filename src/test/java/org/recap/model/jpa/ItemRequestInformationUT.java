package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

public class ItemRequestInformationUT extends BaseTestCaseUT {

    @Test
    public void getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        ItemRequestInformation itemRequestInformation1 = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setUsername("Discovery");
        itemRequestInformation.setBibId("12");
        itemRequestInformation.setRequestId(1);
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setCallNumber("X");
        itemRequestInformation.setAuthor("John");
        itemRequestInformation.setItemVolume("2145");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setTrackingId("235");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setExpirationDate("30-03-2017 00:00:00");
        itemRequestInformation.setCustomerCode("PB");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setVolume("5");
        itemRequestInformation.setIssue("test");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setStartPage("10");
        itemRequestInformation.setEndPage("100");
        itemRequestInformation.canEqual(itemRequestInformation1);
        itemRequestInformation.equals(itemRequestInformation1);
        itemRequestInformation.equals(itemRequestInformation);
        itemRequestInformation1.equals(itemRequestInformation);
        itemRequestInformation.hashCode();
        itemRequestInformation1.hashCode();
    }
}
