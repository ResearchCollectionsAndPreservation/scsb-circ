package org.recap.model.search;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by hemalathas on 20/3/17.
 */
public class SearchItemResultRowUT extends BaseTestCaseUT {

    @Test
    public void testSearchItemResultRow() {

        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setCallNumber("X");
        searchItemResultRow.setChronologyAndEnum("test");
        searchItemResultRow.setCustomerCode("PB");
        searchItemResultRow.setBarcode("3216598422355545");
        searchItemResultRow.setUseRestriction("Others");
        searchItemResultRow.setCollectionGroupDesignation("Open");
        searchItemResultRow.setAvailability("Available");
        searchItemResultRow.setSelectedItem(false);
        searchItemResultRow.setItemId(1);
        searchItemResultRow.compareTo(null);
        searchItemResultRow.compareTo(searchItemResultRow);
        searchItemResultRow.equals(null);
        searchItemResultRow.equals(searchItemResultRow);
        searchItemResultRow.hashCode();
        SearchItemResultRow searchItemResultRow1 = new SearchItemResultRow();
        searchItemResultRow1.equals(searchItemResultRow);
        assertNotNull(searchItemResultRow.getCallNumber());
        assertNotNull(searchItemResultRow.getChronologyAndEnum());
        assertNotNull(searchItemResultRow.getCustomerCode());
        assertNotNull(searchItemResultRow.getBarcode());
        assertNotNull(searchItemResultRow.getUseRestriction());
        assertNotNull(searchItemResultRow.getCollectionGroupDesignation());
        assertNotNull(searchItemResultRow.getAvailability());
        assertNotNull(searchItemResultRow.isSelectedItem());
        assertNotNull(searchItemResultRow.getItemId());

    }


}