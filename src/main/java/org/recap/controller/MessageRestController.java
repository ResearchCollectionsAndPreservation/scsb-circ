package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;
@Slf4j
@RefreshScope
@RestController
class MessageRestController {




    /* This class is for testing the spring cloud config properties */

    @Value("${institution:No data available}")
    private String institution;

    @Value("${ims_location:No data available}")
    private String imsLocation;

    @GetMapping("/ins/{institutionCode}/{institutionProperty}")
    public String getValue(@PathVariable("institutionCode") String institutionCode,
                    @PathVariable("institutionProperty") String institutionProperty) {
        JSONObject json = new JSONObject(institution);
        return json.getJSONObject(institutionCode).get(institutionProperty).toString();
    }
    @GetMapping("/ins/{institutionCode}")
    public Map<String, Object> getValue(@PathVariable("institutionCode") String institutionCode) {
        JSONObject json = new JSONObject(institution);
        JSONObject result = json.getJSONObject(institutionCode);
        return result.toMap();
    }
    @GetMapping("/ins")
    public Map<String, Object> getInsData() {
        JSONObject json = new JSONObject(institution);
        log.info(json.toString());
        return json.toMap();
    }
    @GetMapping("/imsLocation")
    public Map<String, Object> getLocationData() {
        JSONObject json = new JSONObject(imsLocation);
        log.info(json.toString());
        return json.toMap();
    }
}
