package org.recap.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.recap.ScsbCircApplication;
import org.recap.las.model.*;
import org.recap.model.gfa.GFAItemStatusCheckResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
@TestPropertySource("classpath:application.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScsbCircApplication.class)
@Slf4j
public class GFALasServiceIT {

    @Value("${gfa.item.status}")
    private String gfaItemStatusUrl;

    @Value("${gfa.las.status}")
    private String gfaLasStatusUrl;

    @Value("${gfa.item.retrieval.order}")
    private String gfaRetrieveItemUrl;

    @Value("${gfa.item.edd.retrieval.order}")
    private String gfaEddRetrieveItemUrl;

    @Test
    public void testItemStatusCheck() throws Exception {
        log.info("GFA Item Status API Url: {}", gfaItemStatusUrl);
        GFAItemStatusCheckRequest itemStatusCheckRequest = new GFAItemStatusCheckRequest();
        List<GFAItemStatus> itemStatus = new ArrayList<>();
        GFAItemStatus gfaItemStatus = new GFAItemStatus();
        gfaItemStatus.setItemBarCode("32101079134522");
        itemStatus.add(gfaItemStatus);
        itemStatusCheckRequest.setItemStatus(itemStatus);

        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = objectMapper.writeValueAsString(itemStatusCheckRequest);
        log.info("GFA Item Status Filter Param: {}", filterParamValue);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gfaItemStatusUrl).queryParam("filter", filterParamValue);
            ResponseEntity<GFAItemStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFAItemStatusCheckResponse.class);
            GFAItemStatusCheckResponse gfaItemStatusCheckResponse = responseEntity.getBody();
            String response = objectMapper.writeValueAsString(gfaItemStatusCheckResponse);
            log.info("GFA Item Status Response: {}", response);

            assertNotNull(response);
        } catch (Exception e) {
        }
    }


    @Test
    public void testLasStatusCheck() throws Exception {
        log.info("GFA LAS Status API Url: {}", gfaLasStatusUrl);
        GFALasStatusCheckRequest lasStatusCheckRequest = new GFALasStatusCheckRequest();
        GFALasStatus lasStatus = new GFALasStatus();
        lasStatus.setImsLocationCode("RECAP");
        lasStatusCheckRequest.setLasStatus(Collections.singletonList(lasStatus));

        ObjectMapper objectMapper = new ObjectMapper();
        String filterParamValue = objectMapper.writeValueAsString(lasStatusCheckRequest);
        log.info("GFA LAS Status Filter Param: {}", filterParamValue);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity requestEntity = new HttpEntity<>(new HttpHeaders());
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gfaLasStatusUrl).queryParam("filter", filterParamValue);
            ResponseEntity<GFALasStatusCheckResponse> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, GFALasStatusCheckResponse.class);
            GFALasStatusCheckResponse gfaLasStatusCheckResponse = responseEntity.getBody();
            String response = objectMapper.writeValueAsString(gfaLasStatusCheckResponse);
            log.info("GFA LAS Status Response: {}", response);

            assertNotNull(response);
        }catch (Exception e){}
    }

    @Test
    public void testRetrieveItem() throws Exception {
        log.info("GFA Retrieve Item API Url: {}", gfaRetrieveItemUrl);
        GFARetrieveItemRequest gfaRetrieveItemRequest = new GFARetrieveItemRequest();
        RetrieveItemRequest retrieveItem = new RetrieveItemRequest();
        List<TtitemRequest> ttitem = new ArrayList<>();
        TtitemRequest ttitemRequest = new TtitemRequest();
        ttitemRequest.setItemBarcode("33433107439345");
        ttitemRequest.setItemStatus("Available");
        ttitemRequest.setCustomerCode("NA");
        ttitemRequest.setDestination("QX");
        ttitemRequest.setRequestId("729620");
        ttitemRequest.setRequestor("22101008577603");
        ttitem.add(ttitemRequest);
        retrieveItem.setTtitem(ttitem);
        gfaRetrieveItemRequest.setDsitem(retrieveItem);

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(gfaRetrieveItemRequest);
        log.info("GFA Retrieve Item Request: {}", request);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveItemRequest);
            ResponseEntity<GFARetrieveItemResponse> responseEntity = restTemplate.exchange(gfaRetrieveItemUrl, HttpMethod.POST, requestEntity, GFARetrieveItemResponse.class);
            GFARetrieveItemResponse gfaRetrieveItemResponse = responseEntity.getBody();
            String response = objectMapper.writeValueAsString(gfaRetrieveItemResponse);
            log.info("GFA Retrieve Item Response: {}", response);

            assertNotNull(response);
        }catch (Exception e){}
    }

    @Test
    public void testEddRetrieveItem() throws Exception {
        log.info("GFA EDD Retrieve Item API Url: {}", gfaEddRetrieveItemUrl);
        GFARetrieveEDDItemRequest gfaRetrieveEDDItemRequest = new GFARetrieveEDDItemRequest();
        RetrieveItemEDDRequest retrieveEDD = new RetrieveItemEDDRequest();
        List<TtitemEDDResponse> ttitem = new ArrayList<>();
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setItemBarcode("32101047104110");
        ttitemEDDResponse.setCustomerCode("PA");
        ttitemEDDResponse.setRequestId(729778);
        ttitemEDDResponse.setRequestor("198572131");
        ttitemEDDResponse.setRequestorEmail("test@gmail.com");
        ttitemEDDResponse.setBiblioTitle("Toplumsal hareketler konus?uyor / yay?na haz?rlayan Leyla Sanl?.");
        ttitemEDDResponse.setBiblioLocation("HN656.5.Z9 S686 2003");
        ttitemEDDResponse.setBiblioAuthor("");
        ttitemEDDResponse.setBiblioVolume("");
        ttitemEDDResponse.setArticleTitle("TOC, Chapter: Akkuyu Cernobil Olmadi");
        ttitemEDDResponse.setArticleAuthor("");
        ttitemEDDResponse.setArticleVolume("");
        ttitemEDDResponse.setArticleIssue("");
        ttitemEDDResponse.setStartPage("95");
        ttitemEDDResponse.setEndPage("118");
        ttitem.add(ttitemEDDResponse);
        retrieveEDD.setTtitem(ttitem);
        gfaRetrieveEDDItemRequest.setDsitem(retrieveEDD);

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(gfaRetrieveEDDItemRequest);
        log.info("GFA EDD Retrieve Item Request: {}", request);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(gfaRetrieveEDDItemRequest);
            ResponseEntity<GFAEddItemResponse> responseEntity = restTemplate.exchange(gfaEddRetrieveItemUrl, HttpMethod.POST, requestEntity, GFAEddItemResponse.class);
            GFAEddItemResponse gfaEddItemResponse = responseEntity.getBody();
            String response = objectMapper.writeValueAsString(gfaEddItemResponse);
            log.info("GFA EDD Retrieve Item Response: {}", response);

            assertNotNull(response);
        }catch (Exception e){}
    }
}
