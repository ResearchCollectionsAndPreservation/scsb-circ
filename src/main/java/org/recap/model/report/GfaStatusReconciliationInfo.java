package org.recap.model.report;

import lombok.Data;
import org.recap.model.csv.StatusReconciliationErrorCSVRecord;
import org.recap.model.jpa.ItemEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class GfaStatusReconciliationInfo {
    List<List<ItemEntity>> itemEntityChunkList = new ArrayList<>();
    List<StatusReconciliationErrorCSVRecord> statusReconciliationErrorCSVRecordList = new ArrayList<>();
}
