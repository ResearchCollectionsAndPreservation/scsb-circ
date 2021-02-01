package org.recap.las.model;

import org.junit.Test;
import org.recap.model.gfa.Ttitem;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class TtitemUT {

    @Test
    public void getTtitem(){
        Ttitem ttitem = new Ttitem();
        Ttitem ttitem1 = new Ttitem();
        ttitem.setArticleAuthor("test");
        ttitem.setArticleDate(new Date().toString());
        ttitem.setArticleIssue("test");
        ttitem.setArticleTitle("test");
        ttitem.setArticleVolume("2");
        ttitem.setBiblioAuthor("test");
        ttitem.setBiblioCode("134434");
        ttitem.setBiblioLocation("test");
        ttitem.setBiblioTitle("test");
        ttitem.setBiblioVolume("3");
        ttitem.setCustomerCode("235664");
        ttitem.setDeliveryMethod("PA");
        ttitem.setDestination("PA");
        ttitem.setEndPage("test");
        ttitem.setErrorCode("error");
        ttitem.setErrorNote("error");
        ttitem.setItemBarcode("132345");
        ttitem.setItemStatus("status");
        ttitem.setNotes("note");
        ttitem.setOther("other");
        ttitem.setPages("pages");
        ttitem.setPriority("priority");
        ttitem.setRequestDate(new Date().toString());
        ttitem.setRequestId(1);
        ttitem.setRequestor("requestor");
        ttitem.setRequestorEmail("email");
        ttitem.setRequestorFirstName("fname");
        ttitem.setRequestorLastName("lname");
        ttitem.setRequestorMiddleName("mname");
        ttitem.setRequestorOther("other");
        ttitem.setRequestTime("time");
        ttitem.setStartPage("test");
        ttitem.equals(ttitem);
        ttitem.equals(ttitem1);
        ttitem1.equals(ttitem);
        ttitem.hashCode();
        ttitem1.hashCode();
        ttitem.toString();

        assertNotNull(ttitem.getArticleAuthor());
        assertNotNull(ttitem.getArticleDate());
        assertNotNull(ttitem.getArticleIssue());
        assertNotNull(ttitem.getArticleTitle());
        assertNotNull(ttitem.getArticleVolume());
        assertNotNull(ttitem.getBiblioAuthor());
        assertNotNull(ttitem.getBiblioCode());
        assertNotNull(ttitem.getBiblioLocation());
        assertNotNull(ttitem.getBiblioTitle());
        assertNotNull(ttitem.getBiblioVolume());
        assertNotNull(ttitem.getCustomerCode());
        assertNotNull(ttitem.getDeliveryMethod());
        assertNotNull(ttitem.getDestination());
        assertNotNull(ttitem.getEndPage());
        assertNotNull(ttitem.getErrorCode());
        assertNotNull(ttitem.getErrorCode());
        assertNotNull(ttitem.getErrorNote());
        assertNotNull(ttitem.getItemBarcode());
        assertNotNull(ttitem.getItemStatus());
        assertNotNull(ttitem.getNotes());
        assertNotNull(ttitem.getOther());
        assertNotNull(ttitem.getPages());
        assertNotNull(ttitem.getPriority());
        assertNotNull(ttitem.getRequestDate());
        assertNotNull(ttitem.getRequestId());
        assertNotNull(ttitem.getRequestor());
        assertNotNull(ttitem.getRequestorEmail());
        assertNotNull(ttitem.getRequestorFirstName());
        assertNotNull(ttitem.getRequestorLastName());
        assertNotNull(ttitem.getRequestorMiddleName());
        assertNotNull(ttitem.getRequestorOther());
        assertNotNull(ttitem.getRequestTime());
        assertNotNull(ttitem.getStartPage());
    }
}
