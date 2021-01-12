package org.recap.ils.service;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 08/Jan/2021
 */
@Service
public class RestOauthTokenApiService {

    @Autowired
    RestTemplate restTemplate;

    public String generateAccessTokenForRestApi(String oauthTokenApiUrl, String operatorUserId, String operatorPassword) throws Exception {
        String authorization = "Basic " + new String(Base64Utils.encode((operatorUserId + ":" + operatorPassword).getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorization);

        HttpEntity<String> requestEntity = new HttpEntity("grant_type=client_credentials", headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(oauthTokenApiUrl, HttpMethod.POST, requestEntity, String.class);
        JSONObject jsonObject = new JSONObject(responseEntity.getBody());
        return (String) jsonObject.get("access_token");
    }
}
