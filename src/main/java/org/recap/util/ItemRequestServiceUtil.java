package org.recap.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.gfa.model.TtitemEDDResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.repository.jpa.GenericPatronDetailsRepository;
import org.recap.request.EmailService;
import org.recap.service.RestHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
@Service
public class ItemRequestServiceUtil {

    private final Logger logger = LoggerFactory.getLogger(ItemRequestServiceUtil.class);

    @Value("${ils.princeton.cul.patron}")
    private String princetonCULPatron;

    @Value("${ils.princeton.nypl.patron}")
    private String princetonNYPLPatron;

    @Value("${ils.columbia.pul.patron}")
    private String columbiaPULPatron;

    @Value("${ils.columbia.nypl.patron}")
    private String columbiaNYPLPatron;

    @Value("${ils.nypl.princeton.patron}")
    private String nyplPrincetonPatron;

    @Value("${ils.nypl.columbia.patron}")
    private String nyplColumbiaPatron;

    @Value("${ils.princeton.cul.patron.edd}")
    private String princetonEDDCULPatron;

    @Value("${ils.princeton.nypl.patron.edd}")
    private String princetonEDDNYPLPatron;

    @Value("${ils.columbia.pul.patron.edd}")
    private String columbiaEDDPULPatron;

    @Value("${ils.columbia.nypl.patron.edd}")
    private String columbiaEDDNYPLPatron;

    @Value("${ils.nypl.princeton.patron.edd}")
    private String nyplEDDPrincetonPatron;

    @Value("${ils.nypl.columbia.patron.edd}")
    private String nyplEDDColumbiaPatron;

    @Value("${scsb.solr.client.url}")
    private String scsbSolrClientUrl;

    @Autowired
    private RestHeaderService restHeaderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Autowired
    private GenericPatronDetailsRepository genericPatronDetailsRepository;

    @Autowired
    private PropertyUtil propertyUtil;

    public RestHeaderService getRestHeaderService(){
        return restHeaderService;
    }

    /**
     * Update solr index.
     *
     * @param itemEntity the item entity
     */
    public void updateSolrIndex(ItemEntity itemEntity) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity requestEntity = new HttpEntity<>(getRestHeaderService().getHttpHeaders());
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(scsbSolrClientUrl + RecapConstants.UPDATE_ITEM_STATUS_SOLR).queryParam(RecapConstants.UPDATE_ITEM_STATUS_SOLR_PARAM_ITEM_ID, itemEntity.getBarcode());
            ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, requestEntity, String.class);
            logger.info(responseEntity.getBody());
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
    }

    /**
     * Updates process status to each barcode in csv format.
     * @param bulkRequestItems
     * @param bulkRequestItemEntity
     */
    public void updateStatusToBarcodes(List<BulkRequestItem> bulkRequestItems, BulkRequestItemEntity bulkRequestItemEntity) {
        StringBuilder csvFormatDataBuilder = new StringBuilder();
        String requestData = new String(bulkRequestItemEntity.getBulkRequestFileData());
        csvFormatDataBuilder.append(requestData);
        buildCsvFormatData(bulkRequestItems, csvFormatDataBuilder);
        bulkRequestItemEntity.setBulkRequestFileData(csvFormatDataBuilder.toString().getBytes());
        bulkRequestItemDetailsRepository.save(bulkRequestItemEntity);
    }

    /**
     * Builds csv format data for all bulk request items.
     * @param exceptionBulkRequestItems
     * @param csvFormatDataBuilder
     */
    public void buildCsvFormatData(List<BulkRequestItem> exceptionBulkRequestItems, StringBuilder csvFormatDataBuilder) {
        for (BulkRequestItem bulkRequestItem : exceptionBulkRequestItems) {
            csvFormatDataBuilder.append("\n");
            csvFormatDataBuilder.append(bulkRequestItem.getItemBarcode()).append(",");
            csvFormatDataBuilder.append(bulkRequestItem.getCustomerCode()).append(",");
            csvFormatDataBuilder.append(bulkRequestItem.getRequestId()).append(",");
            csvFormatDataBuilder.append(bulkRequestItem.getRequestStatus()).append(",");
            csvFormatDataBuilder.append(StringEscapeUtils.escapeCsv(bulkRequestItem.getStatus()));
        }
    }

    /**
     * Generates report for the bulk request items and sends an email.
     * @param bulkRequestId
     */
    public void generateReportAndSendEmail(Integer bulkRequestId) {
        Optional<BulkRequestItemEntity> bulkRequestItemEntity = bulkRequestItemDetailsRepository.findById(bulkRequestId);
        if(bulkRequestItemEntity.isPresent()) {
            emailService.sendBulkRequestEmail(String.valueOf(bulkRequestItemEntity.get().getId()),
                    bulkRequestItemEntity.get().getBulkRequestName(), bulkRequestItemEntity.get().getBulkRequestFileName(),
                    bulkRequestItemEntity.get().getBulkRequestStatus(), new String(bulkRequestItemEntity.get().getBulkRequestFileData()),
                    "Bulk Request Process Report");
        }
    }

    /**
     * Builds edd info from request notes for LAS request queue.
     * @param line
     * @param ttitem001
     */
    public void setEddInfoToGfaRequest(String line, TtitemEDDResponse ttitem001) {
        String[] splitData = line.split(":");
        if (ArrayUtils.isNotEmpty(splitData) && splitData.length > 1) {
            if ("Start Page".equals(splitData[0])) {
                ttitem001.setStartPage(splitData[1].trim());
            } else if ("End Page".equals(splitData[0])) {
                ttitem001.setEndPage(splitData[1].trim());
            } else if ("Volume Number".equals(splitData[0])) {
                ttitem001.setArticleVolume(splitData[1].trim());
            } else if ("Issue".equals(splitData[0])) {
                ttitem001.setArticleIssue(splitData[1].trim());
            } else if ("Article Author".equals(splitData[0])) {
                ttitem001.setArticleAuthor(splitData[1].trim());
            } else if ("Article/Chapter Title".equals(splitData[0])) {
                ttitem001.setArticleTitle(splitData[1].trim());
            }
        }
    }

    /**
     * Builds edd info from request notes for SCSB request queue.
     * @param line
     * @param itemRequestInformation
     */
    public void setEddInfoToScsbRequest(String line, ItemRequestInformation itemRequestInformation) {
        String[] splitData = line.split(":");
        if (ArrayUtils.isNotEmpty(splitData) && splitData.length > 1) {
            if ("User".equals(splitData[0].trim())) {
                itemRequestInformation.setRequestNotes(splitData[1].trim());
            } else if ("Start Page".equals(splitData[0])) {
                itemRequestInformation.setStartPage(splitData[1].trim());
            } else if ("End Page".equals(splitData[0])) {
                itemRequestInformation.setEndPage(splitData[1].trim());
            } else if ("Volume Number".equals(splitData[0])) {
                itemRequestInformation.setVolume(splitData[1].trim());
            } else if ("Issue".equals(splitData[0])) {
                itemRequestInformation.setIssue(splitData[1].trim());
            } else if ("Article Author".equals(splitData[0])) {
                itemRequestInformation.setAuthor(splitData[1].trim());
            } else if ("Article/Chapter Title".equals(splitData[0])) {
                itemRequestInformation.setChapterTitle(splitData[1].trim());
            }
        }
    }

    public String getPatronIdBorrowingInstitution(String requestingInstitution, String owningInstitution, String requestType) {
        String patronId = "";
        GenericPatronEntity genericPatronEntity = genericPatronDetailsRepository.findByRequestingInstitutionCodeAndItemOwningInstitutionCode(requestingInstitution, owningInstitution);
        if (RecapCommonConstants.REQUEST_TYPE_EDD.equalsIgnoreCase(requestType)) {
            patronId = genericPatronEntity.getEddGenericPatron();
        } else {
            patronId = genericPatronEntity.getRetrievalGenericPatron();
        }
        logger.info("Own Ins: {}, Req Ins: {}, Cross PatronId: {}", owningInstitution, requestingInstitution, patronId);
        return patronId;
    }
}
