package org.recap.ils.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.ils.service.NyplOauthTokenApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 9/12/16.
 */
public class NyplOauthTokenApiServiceUT extends BaseTestCaseUT {

    @InjectMocks
    NyplOauthTokenApiService nyplOauthTokenApiService;

    @Mock
    RestTemplate restTemplate;

    private String operatorUserId="recap";

    private String operatorPassword="recap";

    String nyplOauthTokenApiUrl = "https://isso.nypl.org/oauth/token";

    @Before
    public void setup(){
        ReflectionTestUtils.setField(nyplOauthTokenApiService, "nyplOauthTokenApiUrl", nyplOauthTokenApiUrl);
    }
    @Test
    public void testGenerateOAuthToken() throws Exception {
        String object = "{ \"access_token\":\"John\" }";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(object,HttpStatus.OK);
        String authorization = "Basic " + new String(Base64Utils.encode((operatorUserId + ":" + operatorPassword).getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", authorization);

        HttpEntity<String> requestEntity = new HttpEntity("grant_type=client_credentials", headers);
        Mockito.when(restTemplate.exchange(nyplOauthTokenApiUrl, HttpMethod.POST, requestEntity, String.class)).thenReturn(responseEntity);
        String accessToken = nyplOauthTokenApiService.generateAccessTokenForNyplApi(operatorUserId, operatorPassword);
        assertNotNull(accessToken);
    }
}
