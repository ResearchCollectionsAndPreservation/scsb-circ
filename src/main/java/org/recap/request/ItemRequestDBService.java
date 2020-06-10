package org.recap.request;

import org.apache.commons.lang3.StringUtils;
import org.recap.ReCAPConstants;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.*;
import org.recap.model.jpa.*;
import org.recap.repository.*;
import org.recap.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by sudhishk on 1/12/16.
 */
@Component
public class ItemRequestDBService {

    private static final Logger logger = LoggerFactory.getLogger(ItemRequestDBService.class);

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    /**
     * The Item change log details repository.
     */
    @Autowired
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private RequestItemStatusDetailsRepository requestItemStatusDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Autowired
    private CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private SecurityUtil securityUtil;

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
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemRequestInformation.getExpirationDate(), itemRequestInformation.getRequestingInstitution()));
                requestItemEntity.setLastUpdatedDate(new Date());
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemEntity.setNotes(itemRequestInformation.getRequestNotes());
            } else {
                requestItemEntity.setItemId(itemEntity.getItemId());
                requestItemEntity.setRequestingInstitutionId(institutionEntity.getId());
                requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemRequestInformation.getExpirationDate(), itemRequestInformation.getRequestingInstitution()));
                requestItemEntity.setCreatedBy(getUser(itemRequestInformation.getUsername()));
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
                requestItemEntity.setNotes(itemRequestInformation.getRequestNotes());
            }
            savedItemRequest = requestItemDetailsRepository.saveAndFlush(requestItemEntity);
            if (savedItemRequest != null) {
                requestId = savedItemRequest.getId();
                saveItemChangeLogEntity(savedItemRequest.getId(), getUser(itemRequestInformation.getUsername()), ReCAPConstants.REQUEST_ITEM_INSERT, savedItemRequest.getItemId() + " - " + savedItemRequest.getPatronId());
            }
            logger.info("SCSB DB Update Successful");
        } catch (ParseException e) {
            logger.error(ReCAPConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, e);
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
                requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_EXCEPTION);
            }else {
                if (itemInformationResponse.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RETRIEVAL)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                }else if (itemInformationResponse.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_EDD)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_EDD);
                }else if (itemInformationResponse.getRequestType().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RECALL)){
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_RECALLED);
                }
            }

            if (itemInformationResponse.getRequestId() != null && itemInformationResponse.getRequestId() > 0) {
                requestItemEntity = requestItemDetailsRepository.findById(itemInformationResponse.getRequestId()).orElse(null);
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemInformationResponse.getExpirationDate(), itemInformationResponse.getRequestingInstitution()));
                requestItemEntity.setNotes(itemInformationResponse.getRequestNotes());
                requestItemEntity.setLastUpdatedDate(new Date());
            } else {
                requestItemEntity = new RequestItemEntity();
                RequestTypeEntity requestTypeEntity = requestTypeDetailsRepository.findByrequestTypeCode(itemInformationResponse.getRequestType());
                InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(itemInformationResponse.getRequestingInstitution());

                //Request Item
                requestItemEntity.setItemId(itemInformationResponse.getItemId());
                requestItemEntity.setRequestingInstitutionId(institutionEntity.getId());
                requestItemEntity.setRequestTypeId(requestTypeEntity.getId());
                requestItemEntity.setRequestExpirationDate(getExpirationDate(itemInformationResponse.getExpirationDate(), itemInformationResponse.getRequestingInstitution()));
                requestItemEntity.setCreatedBy(getUser(itemInformationResponse.getUsername()));
                requestItemEntity.setCreatedDate(new Date());
                requestItemEntity.setLastUpdatedDate(new Date());
                requestItemEntity.setPatronId(itemInformationResponse.getPatronBarcode());
                requestItemEntity.setStopCode(itemInformationResponse.getDeliveryLocation());
                requestItemEntity.setRequestStatusId(requestStatusEntity.getId());
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
                saveItemChangeLogEntity(savedItemRequest.getId(), getUser(itemInformationResponse.getUsername()), ReCAPConstants.REQUEST_ITEM_INSERT, savedItemRequest.getItemId() + " - " + savedItemRequest.getPatronId());
            }
            itemInformationResponse.setRequestId(requestId);
            logger.info("SCSB DB Update Successful");
        } catch (ParseException e) {
            logger.error(ReCAPConstants.REQUEST_PARSE_EXCEPTION, e);
        } catch (Exception e) {
            logger.error(ReCAPConstants.REQUEST_EXCEPTION, e);
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
        if(requestItemEntity != null) {
            BulkRequestItemEntity bulkRequestItemEntity = requestItemEntity.get().getBulkRequestItemEntity();
            String notes = ReCAPConstants.USER + " : " + requestItemEntity.get().getNotes();
            if (null != bulkRequestItemEntity) {
                itemInformationResponse.setBulk(true);
                notes = notes + "\n" + ReCAPConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId();
            }
            requestItemEntity.get().setNotes(notes);
            if (itemInformationResponse.isSuccess()) {
                if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RETRIEVAL)) {
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                } else if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_EDD)) {
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_EDD);
                } else if (requestItemEntity.get().getRequestTypeEntity().getRequestTypeCode().equalsIgnoreCase(ReCAPConstants.REQUEST_TYPE_RECALL)) {
                    // This change is to update the Recall order to Retrieval order upon refile of the existing retrieval order from LAS.
                    requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED);
                }
            } else {
                requestStatusEntity = requestItemStatusDetailsRepository.findByRequestStatusCode(ReCAPConstants.REQUEST_STATUS_EXCEPTION);
                if (itemInformationResponse.isBulk()) {
                    requestItemEntity.get().setNotes(notes + "\n" + ReCAPConstants.REQUEST_LAS_EXCEPTION + ReCAPConstants.REQUEST_ITEM_GFA_FAILURE + " with error note - " + itemInformationResponse.getScreenMessage() + "\n" + ReCAPConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId());
                }
                requestItemEntity.get().setNotes(notes + "\n" + ReCAPConstants.REQUEST_LAS_EXCEPTION + ReCAPConstants.REQUEST_ITEM_GFA_FAILURE + " with error note - " + itemInformationResponse.getScreenMessage());
            }
            requestItemEntity.get().setRequestStatusId(requestStatusEntity.getId());
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
    public void updateItemAvailabilutyStatus(List<ItemEntity> itemEntities, String userName) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(ReCAPConstants.NOT_AVAILABLE);
        for (ItemEntity itemEntity : itemEntities) {
            itemEntity.setItemAvailabilityStatusId(itemStatusEntity.getId()); // Not Available
            itemEntity.setLastUpdatedBy(getUser(userName));

            saveItemChangeLogEntity(itemEntity.getItemId(), getUser(userName), ReCAPConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, ReCAPConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_UPDATE);
        }
        // Not Available
        itemDetailsRepository.saveAll(itemEntities);
        itemDetailsRepository.flush();
    }

    /**
     * Rollback update item availabiluty status.
     *
     * @param itemEntity the item entity
     * @param userName   the user name
     */
    public void rollbackUpdateItemAvailabilutyStatus(ItemEntity itemEntity, String userName) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(ReCAPConstants.AVAILABLE);
        itemEntity.setItemAvailabilityStatusId(itemStatusEntity.getId()); // Available
        itemEntity.setLastUpdatedBy(getUser(userName));
        itemDetailsRepository.save(itemEntity);
        saveItemChangeLogEntity(itemEntity.getItemId(), getUser(userName), ReCAPConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, ReCAPConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_ROLLBACK);
    }

    /**
     * Save item change log entity.
     *
     * @param recordId      the record id
     * @param userName      the user name
     * @param operationType the operation type
     * @param notes         the notes
     */
    public void saveItemChangeLogEntity(Integer recordId, String userName, String operationType, String notes) {
        ItemChangeLogEntity itemChangeLogEntity = new ItemChangeLogEntity();
        itemChangeLogEntity.setUpdatedBy(userName);
        itemChangeLogEntity.setUpdatedDate(new Date());
        itemChangeLogEntity.setOperationType(operationType);
        itemChangeLogEntity.setRecordId(recordId);
        itemChangeLogEntity.setNotes(notes);
        itemChangeLogDetailsRepository.save(itemChangeLogEntity);
    }

    /**
     * Gets user.
     *
     * @param userId the user id
     * @return the user
     */
    public String getUser(String userId) {
        if (StringUtils.isBlank(userId)) {
            return "Discovery";
        } else {
            return userId;
        }
    }

    private Date getExpirationDate(String expirationDate, String requestingInstitutionId) throws ParseException {
        if (StringUtils.isNotBlank(expirationDate)) {
            logger.info("Expiration date from response : {}", expirationDate);
            try {
                return simpleDateFormat.parse(expirationDate);
            } catch (Exception ex) {
                logger.error(ReCAPConstants.REQUEST_EXCEPTION, ex);
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
        if(requestItemEntity != null) {
            CustomerCodeEntity customerCodeEntity= customerCodeDetailsRepository.findByCustomerCode(requestItemEntity.get().getItemEntity().getCustomerCode());
            rollbackUpdateItemAvailabilutyStatus(requestItemEntity.get().getItemEntity(), ReCAPConstants.GUEST_USER);
            saveItemChangeLogEntity(itemInformationResponse.getRequestId(), requestItemEntity.get().getCreatedBy(), ReCAPConstants.REQUEST_ITEM_GFA_FAILURE, ReCAPConstants.REQUEST_ITEM_GFA_FAILURE + itemInformationResponse.getScreenMessage());
            itemRequestInformation.setBibId(requestItemEntity.get().getItemEntity().getBibliographicEntities().get(0).getOwningInstitutionBibId());
            itemRequestInformation.setPatronBarcode(requestItemEntity.get().getPatronId());
            itemRequestInformation.setItemBarcodes(Arrays.asList(requestItemEntity.get().getItemEntity().getBarcode()));
            itemRequestInformation.setPickupLocation(customerCodeEntity.getPickupLocation());
            itemRequestInformation.setItemOwningInstitution(requestItemEntity.get().getItemEntity().getInstitutionEntity().getInstitutionCode());
            itemRequestInformation.setRequestingInstitution(requestItemEntity.get().getInstitutionEntity().getInstitutionCode());
        }
        return itemRequestInformation;
    }

}
