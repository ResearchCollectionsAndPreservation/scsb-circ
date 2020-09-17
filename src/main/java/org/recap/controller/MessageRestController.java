package org.recap.controller;

import org.json.JSONObject;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import java.util.Map;

@RefreshScope
@RestController
class MessageRestController {

    /* This class is for testing the spring cloud config properties */

    @Value("${institution:No data available}")
    private String institution;

    @Value("${ims_location:No data available}")
    private String imsLocation;

    @GetMapping("/ins/{institutionCode}/{institutionProperty}")
    String getValue(@PathVariable("institutionCode") String institutionCode,
                    @PathVariable("institutionProperty") String institutionProperty) {
        JSONObject json = new JSONObject(institution);
        String result = json.getJSONObject(institutionCode).get(institutionProperty).toString();
        return result;
    }
    @GetMapping("/ins/{institutionCode}")
    Map<String, Object> getValue(@PathVariable("institutionCode") String institutionCode) {
        JSONObject json = new JSONObject(institution);
        JSONObject result = json.getJSONObject(institutionCode);
        Map<String, Object> response = result.toMap();
        return response;
    }
    @GetMapping("/ins")
    Map<String, Object> getInsData() {
        JSONObject json = new JSONObject(institution);
        System.out.println(json.toString());
        Map<String, Object> response = json.toMap();
        return response;
    }
    @GetMapping("/imsLocation")
    Map<String, Object> getLocationData() {
        JSONObject json = new JSONObject(imsLocation);
        System.out.println(json.toString());
        Map<String, Object> response = json.toMap();
        return response;
    }
}