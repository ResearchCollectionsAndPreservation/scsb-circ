package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.model.gfa.ScsbLasItemStatusCheckModel;
import org.recap.las.GFALasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gfaService")
@Slf4j
public class GFAServiceController {

    @Autowired
    private GFALasService gfaLasService;

    @GetMapping(value = "/itemStatusCheck")
    public String itemStatusCheck(@RequestParam String itemBarcode) {
        return gfaLasService.callGfaItemStatus(itemBarcode);
    }

    @PostMapping(value = "/multipleItemsStatusCheck", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScsbLasItemStatusCheckModel> multipleItemsStatusCheck(@RequestBody List<ScsbLasItemStatusCheckModel> itemsStatusCheckModel) {
        return gfaLasService.getGFAItemStatusCheckResponseByBarcodesAndImsLocationList(itemsStatusCheckModel);
    }
}
