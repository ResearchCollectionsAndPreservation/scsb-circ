package org.recap.service;

import org.apache.camel.ProducerTemplate;
import org.recap.ReCAPConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.model.PendingRequestEntity;
import org.recap.model.RequestItemEntity;
import org.recap.repository.PendingRequestDetailsRespository;
import org.recap.repository.RequestItemDetailsRepository;
import org.recap.repository.RequestItemStatusDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdentifyPendingRequestService {
    private static final Logger logger = LoggerFactory.getLogger(IdentifyPendingRequestService.class);

    @Autowired
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    PendingRequestDetailsRespository pendingRequestDetailsRespository;

    @Autowired
    ProducerTemplate producerTemplate;

    @Value("${pending.request.email.to}")
    private String pendingRequestEmailTo;

    @Value("${pending.request.email.cc}")
    private String pendingRequestEmailCc;

    public boolean identifyPendingRequest(){
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        List<RequestItemEntity> requestItemEntitiesInPending=new ArrayList<>();
        List<PendingRequestEntity> pendingRequestEntities=new ArrayList<>();
        List<RequestItemEntity> requestsToBeNotified;
        List<Integer> requestIds;
        List<Integer> notifiedRequestIds;
        List<RequestItemEntity> requestItemEntitiesInPendingList = requestItemDetailsRepository.findByRequestStatusCode(Collections.singletonList(ReCAPConstants.REQUEST_STATUS_PENDING));
        if(!requestItemEntitiesInPendingList.isEmpty()) {
            requestIds = requestItemEntitiesInPendingList.stream().map(RequestItemEntity::getRequestId).collect(Collectors.toList());
            List<PendingRequestEntity> notifiedPendingRequests = pendingRequestDetailsRespository.findByRequestIds(requestIds);
            notifiedRequestIds = notifiedPendingRequests.stream().map(PendingRequestEntity::getRequestId).collect(Collectors.toList());
            requestIds.removeAll(notifiedRequestIds);
            if(!requestIds.isEmpty()) {
                requestsToBeNotified = requestItemDetailsRepository.findByRequestIdIn(requestIds);
                if (requestsToBeNotified != null) {
                    findRequestStuckAbove5mins(requestItemEntitiesInPending, requestsToBeNotified);
                    for (RequestItemEntity requestItemEntity : requestItemEntitiesInPending) {
                        PendingRequestEntity pendingRequestEntity = setPendingRequestEntity(requestItemEntity);
                        pendingRequestEntities.add(pendingRequestEntity);
                    }
                    List<PendingRequestEntity> savedPendingRequestEntities = pendingRequestDetailsRespository.save(pendingRequestEntities);
                    if (savedPendingRequestEntities != null && !savedPendingRequestEntities.isEmpty()) {
                        String message = setEmailBodyForPendingRequest(savedPendingRequestEntities);
                        sendEmailNotificationForPendingRequests(emailPayLoad, message);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void findRequestStuckAbove5mins(List<RequestItemEntity> requestItemEntitiesInPending, List<RequestItemEntity> requestsToBeNotified) {
        for (RequestItemEntity requestItemEntity : requestsToBeNotified) {
            Date createdDate = requestItemEntity.getCreatedDate();
            long minutes = getDifferenceInMinutes(createdDate);
            if (minutes > 5) {
                requestItemEntitiesInPending.add(requestItemEntity);
            }
        }
    }

    private String setEmailBodyForPendingRequest(List<PendingRequestEntity> savedPendingRequestEntities) {
        String message=null;
        StringBuilder stringBuilder=new StringBuilder();
        for (PendingRequestEntity savedPendingRequestEntity : savedPendingRequestEntities) {
            if((message ==null)){
                stringBuilder.append("Below are the requests stuck in pending.")
                        .append("\nBarcode : ").append(savedPendingRequestEntity.getItemEntity().getBarcode())
                        .append("\t\t Request Created Date : ").append(savedPendingRequestEntity.getRequestItemEntity().getCreatedDate())
                        .append( "\t\t Request Type :").append(savedPendingRequestEntity.getRequestItemEntity().getRequestTypeEntity().getRequestTypeCode());
                message= String.valueOf(stringBuilder);
            }
            else {
                stringBuilder.append("\nBarcode : ").append(savedPendingRequestEntity.getItemEntity().getBarcode())
                        .append("\t\t Request Created Date : ").append(savedPendingRequestEntity.getRequestItemEntity().getCreatedDate())
                        .append( "\t\t Request Type :").append(savedPendingRequestEntity.getRequestItemEntity().getRequestTypeEntity().getRequestTypeCode());
                message=String.valueOf(stringBuilder);
            }
        }
        return message;
    }

    private void sendEmailNotificationForPendingRequests(EmailPayLoad emailPayLoad, String message) {
        emailPayLoad.setSubject(ReCAPConstants.REQUESTS_STUCK_IN__PENDING);
        emailPayLoad.setMessageDisplay(message);
        emailPayLoad.setTo(pendingRequestEmailTo);
        emailPayLoad.setCc(pendingRequestEmailCc);
        producerTemplate.sendBodyAndHeader(ReCAPConstants.EMAIL_Q, emailPayLoad, ReCAPConstants.EMAIL_BODY_FOR, ReCAPConstants.EMAIL_HEADER_REQUEST_STATUS_PENDING);
    }

    private PendingRequestEntity setPendingRequestEntity(RequestItemEntity pendingStatusRequestEntity) {
        PendingRequestEntity pendingRequestEntity = new PendingRequestEntity();
        pendingRequestEntity.setItemId(pendingStatusRequestEntity.getItemEntity().getItemId());
        pendingRequestEntity.setRequestId(pendingStatusRequestEntity.getRequestId());
        pendingRequestEntity.setItemEntity(pendingStatusRequestEntity.getItemEntity());
        pendingRequestEntity.setRequestCreatedDate(new Date());
        pendingRequestEntity.setRequestItemEntity(pendingStatusRequestEntity);
        return pendingRequestEntity;
    }

    private long getDifferenceInMinutes(Date createdDate) {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = Instant.ofEpochMilli(createdDate.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return Duration.between(localDateTime, now).toMinutes();
    }
}
