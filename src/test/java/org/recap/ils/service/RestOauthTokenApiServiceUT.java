package org.recap.ils.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.ils.model.nypl.response.CancelHoldResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

public class RestOauthTokenApiServiceUT extends BaseTestCaseUT {

    @InjectMocks
    RestOauthTokenApiService restOauthTokenApiService;

    @Mock
    RestTemplate restTemplate;

    @Test
    public void generateAccessTokenForRestApi() throws Exception {
        String oauthTokenApiUrl = "test";
        String operatorUserId = "test";
        String operatorPassword = "test";
        String object = "{ \"access_token\":\"John\" }";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(object, HttpStatus.OK);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<CancelHoldResponse>>any());
        String response = restOauthTokenApiService.generateAccessTokenForRestApi(oauthTokenApiUrl, operatorUserId, operatorPassword);
        assertNotNull(response);
    }
}
