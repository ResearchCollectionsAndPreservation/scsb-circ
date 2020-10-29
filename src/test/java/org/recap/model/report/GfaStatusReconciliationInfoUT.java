package org.recap.model.report;

import org.junit.Test;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.jpa.ItemEntity;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class GfaStatusReconciliationInfoUT {

    @Test
    public void getGfaStatusReconciliationInfo(){
        GfaStatusReconciliationInfo gfaStatusReconciliationInfo = new GfaStatusReconciliationInfo();
        gfaStatusReconciliationInfo.setItemEntityChunkList(Arrays.asList(new ArrayList<ItemEntity>()));
        gfaStatusReconciliationInfo.setStatusReconciliationErrorCSVRecordList(Arrays.asList(new StatusReconciliationErrorCSVRecord()));
        gfaStatusReconciliationInfo.canEqual(gfaStatusReconciliationInfo);
        gfaStatusReconciliationInfo.toString();
        gfaStatusReconciliationInfo.equals(gfaStatusReconciliationInfo);
        gfaStatusReconciliationInfo.hashCode();

        assertNotNull(gfaStatusReconciliationInfo.getItemEntityChunkList());
        assertNotNull(gfaStatusReconciliationInfo.getStatusReconciliationErrorCSVRecordList());
    }
}
