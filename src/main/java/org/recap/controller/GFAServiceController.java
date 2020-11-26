package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.recap.model.report.GfaDeaccessionInfo;
import org.recap.request.GFAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gfaService")
@Slf4j
public class GFAServiceController {

    @Autowired
    private GFAService gfaService;

    @GetMapping(value = "/itemStatusCheck")
    public String itemStatusCheck(String itemBarcode){
        return gfaService.callGfaItemStatus(itemBarcode);
    }

    @PostMapping(value = "/multipleItemsStatusCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public GFAItemStatusCheckResponse itemStatusCheck(@RequestBody List<String> itemBarcodes) {
        return gfaService.getGFAItemStatusCheckResponseByBarcodes(itemBarcodes);
    }

    @PostMapping(value = "/deaccessionInGfa", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deaccessionInGfa(@RequestBody GfaDeaccessionInfo gfaDeaccessionInfo) {
        gfaService.callGfaDeaccessionService(gfaDeaccessionInfo.getDeAccessionDBResponseEntities(), gfaDeaccessionInfo.getUsername());
    }
}
