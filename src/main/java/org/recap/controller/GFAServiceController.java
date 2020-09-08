package org.recap.controller;

import org.recap.model.csv.StatusReconciliationCSVRecord;
import org.recap.model.report.GfaDeaccessionInfo;
import org.recap.model.report.GfaStatusReconciliationInfo;
import org.recap.request.GFAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gfaService")
public class GFAServiceController {

    private static final Logger logger = LoggerFactory.getLogger(GFAServiceController.class);

    @Autowired
    private GFAService gfaService;

    /**
     * This Rest service initiates item status comparison with Gfa item statuses.
     *
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/itemStatusComparison")
    public List<StatusReconciliationCSVRecord> itemStatusComparison(GfaStatusReconciliationInfo gfaStatusReconciliationInfo) throws Exception{
       return gfaService.itemStatusComparison(gfaStatusReconciliationInfo.getItemEntityChunkList(), gfaStatusReconciliationInfo.getStatusReconciliationErrorCSVRecordList());
    }

    @GetMapping(value = "/itemStatusCheck")
    public String itemStatusCheck(String itemBarcode){
        return gfaService.callGfaItemStatus(itemBarcode);
    }

    @PostMapping(value = "/deaccessionInGfa")
    public void deaccessionInGfa(GfaDeaccessionInfo gfaDeaccessionInfo) {
        gfaService.callGfaDeaccessionService(gfaDeaccessionInfo.getDeAccessionDBResponseEntities(), gfaDeaccessionInfo.getUsername());
    }
}
