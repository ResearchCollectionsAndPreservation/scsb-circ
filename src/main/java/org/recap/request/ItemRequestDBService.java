package org.recap.request;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.RecapCommonConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.*;
import org.recap.util.CommonUtil;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by sudhishk on 1/12/16.
 */
@Component
public class ItemRequestDBService {

    private static final Logger logger = LoggerFactory.getLogger(ItemRequestDBService.class);

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RecapConstants.DATE_FORMAT);

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    private OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Autowired
    private DeliveryCodeDetailsRepository deliveryCodeDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private CommonUtil commonUtil;
    
    private String errorNote = " with error note - ";

    /**
     * Update recap request item integer.
     *
     * @param itemRequestInformation the item request information
     * @param itemEntity             the item entity
     * @param requestStatusCode      the request status code
     * @return the integer
     */
    public Integer updateRecapRequestItem(ItemRequestInformation itemRequestInformation, ItemEntity itemEntity, String requestStatusCode, BulkRequestItemEntity bulkRequestItemEntity) {
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        if (null != bulkRequestItemEntity) {
            requestItemEntity.setBulkRequestItemEntity(bulkRequestItemEntity);
        }
        RequestItemEntity savedItemRequest;
        Integer requestId = 0;
        try {
            RequestStatusEntity requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(requestStatusCode);
            InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(itemRequestInformation.getRequestingInstitution());
            RequestTypeEntity requestTypeEntity = requestTypeDetailsRepository.findByrequestTypeCode(itemRequestInformation.getRequestType());
            //Request Item
            if (itemRequestInformation.getRequestId() != null && itemRequestInformation.getRequestId() > 0) {
                requestItemEntity = requestItemDetailsRepository.findById(itemRequestInformation.getRequestId()).orElse(requestItemEntity);
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemRequestInformation.getExpirationDate()));
                requestItemEntity.setLastUpdatedDate(new Date());
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
            } else {
                requestItemEntity.setItemId(itemEntity.getId());
                requestItemEntity.setRequestingInstitutionId(institutionEntity.getId());
                requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemRequestInformation.getExpirationDate()));
                requestItemEntity.setCreatedBy(commonUtil.getUser(itemRequestInformation.getUsername()));
                requestItemEntity.setCreatedDate(new Date());
                requestItemEntity.setLastUpdatedDate(new Date());
                requestItemEntity.setPatronId(itemRequestInformation.getPatronBarcode());
                requestItemEntity.setStopCode(itemRequestInformation.getDeliveryLocation());
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                if(StringUtils.isNotBlank(itemRequestInformation.getEmailAddress()) && null == bulkRequestItemEntity){
                    requestItemEntity.setEmailId(securityUtil.getEncryptedValue(itemRequestInformation.getEmailAddress()));
                }else {
                    requestItemEntity.setEmailId(itemRequestInformation.getEmailAddress());
                }
            }
            requestItemEntity.setNotes(itemRequestInformation.getRequestNotes());
            savedItemRequest = requestItemDetailsRepository.saveAndFlush(requestItemEntity);
            requestId = savedItemRequest.getId();
            commonUtil.saveItemChangeLogEntity(savedItemRequest.getId(), commonUtil.getUser(itemRequestInformation.getUsername()), RecapConstants.REQUEST_ITEM_INSERT, savedItemRequest.getItemId() + " - " + savedItemRequest.getPatronId());
        logger.info("SCSB DB Update Successful");
        } catch (ParseException e) {
            logger.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return requestId;
    }

    /**
     * Update recap request item item information response.
     *
     * @param itemInformationResponse the item information response
     * @return the item information response
     */
    public ItemInformationResponse updateRecapRequestItem(ItemInformationResponse itemInformationResponse) {

        RequestItemEntity requestItemEntity;
        RequestItemEntity savedItemRequest;
        RequestStatusEntity requestStatusEntity=null;
        Integer requestId = 0;
        try {
            if (!itemInformationResponse.isSuccess()) {
                requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION);
            }else {
                if (itemInformationResponse.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                }else if (itemInformationResponse.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_EDD);
                }else if (itemInformationResponse.getRequestType().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RECALLED);
                }
            }

            if (itemInformationResponse.getRequestId() != null && itemInformationResponse.getRequestId() > 0) {
                requestItemEntity = requestItemDetailsRepository.findById(itemInformationResponse.getRequestId()).orElse(null);
                if(requestItemEntity != null) {
                    if (requestStatusEntity != null) {
                        requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                    }
                    requestItemEntity.setRequestExpirationDate(getExpirationDate(itemInformationResponse.getExpirationDate()));
                    requestItemEntity.setNotes(itemInformationResponse.getRequestNotes());
                    requestItemEntity.setLastUpdatedDate(new Date());
                }
            } else {
                requestItemEntity = new RequestItemEntity();
                RequestTypeEntity requestTypeEntity = requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType());
                InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution());

                //Request Item
                requestItemEntity.setItemId(itemInformationResponse.getItemId());
                requestItemEntity.setRequestingInstitutionId(institutionEntity.getId());
                requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemInformationResponse.getExpirationDate()));
                requestItemEntity.setCreatedBy(commonUtil.getUser(itemInformationResponse.getUsername()));
                requestItemEntity.setCreatedDate(new Date());
                requestItemEntity.setLastUpdatedDate(new Date());
                requestItemEntity.setPatronId(itemInformationResponse.getPatronBarcode());
                requestItemEntity.setStopCode(itemInformationResponse.getDeliveryLocation());
                if(requestStatusEntity != null) {
                    requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                }
                if (StringUtils.isNotBlank(itemInformationResponse.getEmailAddress())){
                    requestItemEntity.setEmailId(securityUtil.getEncryptedValue(itemInformationResponse.getEmailAddress()));
                }else {
                    requestItemEntity.setEmailId(itemInformationResponse.getEmailAddress());
                }
                requestItemEntity.setNotes(itemInformationResponse.getRequestNotes());
            }
            savedItemRequest = requestItemDetailsRepository.saveAndFlush(requestItemEntity);
            if (savedItemRequest != null) {
                requestId = savedItemRequest.getId();
                commonUtil.saveItemChangeLogEntity(savedItemRequest.getId(), commonUtil.getUser(itemInformationResponse.getUsername()), RecapConstants.REQUEST_ITEM_INSERT, savedItemRequest.getItemId() + " - " + savedItemRequest.getPatronId());
            }
            itemInformationResponse.setRequestId(requestId);
            logger.info("SCSB DB Update Successful");
        } catch (ParseException e) {
            logger.error(RecapConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION, e);
        }
        return itemInformationResponse;
    }

    /**
     * Update recap request status item information response.
     *
     * @param itemInformationResponse the item information response
     * @return the item information response
     */
    public ItemInformationResponse updateRecapRequestStatus(ItemInformationResponse itemInformationResponse) {
        RequestStatusEntity requestStatusEntity=null;
        Optional<RequestItemEntity> requestItemEntity = requestItemDetailsRepository.findById(itemInformationResponse.getRequestId());
        if(requestItemEntity.isPresent()) {
            BulkRequestItemEntity bulkRequestItemEntity = requestItemEntity.get().getBulkRequestItemEntity();
            String notes = RecapConstants.USER + " : " + requestItemEntity.get().getNotes();
            if (null != bulkRequestItemEntity) {
                itemInformationResponse.setBulk(true);
                notes = notes + "\n" + RecapConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId();
            }
            requestItemEntity.get().setNotes(notes);
            if (itemInformationResponse.isSuccess()) {
                if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RETRIEVAL)) {
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                } else if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_EDD)) {
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_EDD);
                } else if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(RecapCommonConstants.REQUEST_TYPE_RECALL)) {
                    // This change is to update the Recall order to Retrieval order upon refile of the existing retrieval order from LAS.
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapCommonConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                }
            } else {
                requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(RecapConstants.REQUEST_STATUS_EXCEPTION);
                if (itemInformationResponse.isBulk()) {
                    if (null != bulkRequestItemEntity) {
                        requestItemEntity.get().setNotes(notes + "\n" + RecapConstants.REQUEST_LAS_EXCEPTION + RecapConstants.REQUEST_ITEM_GFA_FAILURE + errorNote + itemInformationResponse.getScreenMessage() + "\n" + RecapConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId());
                    }
                    else {
                        requestItemEntity.get().setNotes(notes + "\n" + RecapConstants.REQUEST_LAS_EXCEPTION + RecapConstants.REQUEST_ITEM_GFA_FAILURE + errorNote + itemInformationResponse.getScreenMessage() + "\n" + RecapConstants.BULK_REQUEST_ID_TEXT);
                    }
                }
                requestItemEntity.get().setNotes(notes + "\n" + RecapConstants.REQUEST_LAS_EXCEPTION + RecapConstants.REQUEST_ITEM_GFA_FAILURE + errorNote + itemInformationResponse.getScreenMessage());
            }
            if(requestStatusEntity != null) {
                requestItemEntity.get().setRequestStatusId(requestStatusEntity.getId());
            }
            requestItemDetailsRepository.save(requestItemEntity.get());
        }
        return itemInformationResponse;
    }

    /**
     * Update item availabiluty status.
     *
     * @param itemEntities the item entities
     * @param userName     the user name
     */
    public void updateItemAvailabilityStatus(List<ItemEntity> itemEntities, String userName) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(RecapCommonConstants.NOT_AVAILABLE);
        for (ItemEntity itemEntity : itemEntities) {
            itemEntity.setItemAvailabilityStatusId(itemStatusEntity.getId()); // Not Available
            itemEntity.setLastUpdatedBy(commonUtil.getUser(userName));

            commonUtil.saveItemChangeLogEntity(itemEntity.getId(), commonUtil.getUser(userName), RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, RecapConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_UPDATE);
        }
        // Not Available
        itemDetailsRepository.saveAll(itemEntities);
        itemDetailsRepository.flush();
    }

    private Date getExpirationDate(String expirationDate) throws ParseException {
        if (StringUtils.isNotBlank(expirationDate)) {
            logger.info("Expiration date from response : {}", expirationDate);
            try {
                return simpleDateFormat.parse(expirationDate);
            } catch (Exception ex) {
                logger.error(RecapCommonConstants.REQUEST_EXCEPTION, ex);
            }
        }
        return null;
    }

    /**
     * Rollback after gfa item request information.
     *
     * @param itemInformationResponse the item information response
     * @return the item request information
     */
    public ItemRequestInformation rollbackAfterGFA(ItemInformationResponse itemInformationResponse) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        Optional<RequestItemEntity> requestItemEntity = requestItemDetailsRepository.findById(itemInformationResponse.getRequestId());
        if(requestItemEntity.isPresent()) {
            OwnerCodeEntity ownerCodeEntity= ownerCodeDetailsRepository.findByOwnerCode(requestItemEntity.get().getItemEntity().getCustomerCode());
            DeliveryCodeEntity deliveryCodeEntity= deliveryCodeDetailsRepository.findByDeliveryCode( requestItemEntity.get().getStopCode());
            commonUtil.rollbackUpdateItemAvailabilityStatus(requestItemEntity.get().getItemEntity(), RecapConstants.GUEST_USER);
            commonUtil.saveItemChangeLogEntity(itemInformationResponse.getRequestId(), requestItemEntity.get().getCreatedBy(), RecapConstants.REQUEST_ITEM_GFA_FAILURE, RecapConstants.REQUEST_ITEM_GFA_FAILURE + itemInformationResponse.getScreenMessage());
            itemRequestInformation.setBibId(requestItemEntity.get().getItemEntity().getBibliographicEntities().get(0).getOwningInstitutionBibId());
            itemRequestInformation.setPatronBarcode(requestItemEntity.get().getPatronId());
            itemRequestInformation.setItemBarcodes(Collections.singletonList(requestItemEntity.get().getItemEntity().getBarcode()));
            itemRequestInformation.setPickupLocation(deliveryCodeEntity.getPickupLocation());
            itemRequestInformation.setItemOwningInstitution(requestItemEntity.get().getItemEntity().getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setRequestingInstitution(requestItemEntity.get().getInstitutionEntity().getInstitutionCode());
        }
        return itemRequestInformation;
    }

}
