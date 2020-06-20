package org.recap.request;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.ReCAPConstants;
import org.recap.controller.RequestItemController;
import org.recap.ils.model.response.ItemCheckoutResponse;
import org.recap.ils.model.response.ItemInformationResponse;
import org.recap.model.jpa.BulkRequestItem;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemRequestInformation;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.repository.jpa.BulkRequestItemDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.ItemRequestServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by rajeshbabuk on 10/10/17.
 */
@Component
public class BulkItemRequestProcessService {

    private final Logger logger = LoggerFactory.getLogger(BulkItemRequestProcessService.class);

    @Autowired
    private BulkRequestItemDetailsRepository bulkRequestItemDetailsRepository;

    @Autowired
    private ItemRequestDBService itemRequestDBService;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private RequestItemController requestItemController;

    @Autowired
    private ItemRequestServiceUtil itemRequestServiceUtil;

    @Autowired
    private GFAService gfaService;

    /**
     * Process bulk request item.
     *
     * @param itemBarcode   the item barcode
     * @param bulkRequestId the bulk request id
     */
    public void processBulkRequestItem(String itemBarcode, Integer bulkRequestId) {
        Optional<BulkRequestItemEntity> bulkRequestItemEntity = bulkRequestItemDetailsRepository.findById(bulkRequestId);
        if (ReCAPConstants.COMPLETE.equals(itemBarcode)) {
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e){
                logger.info("Interrupted Exception");
            }
            bulkRequestItemEntity = bulkRequestItemDetailsRepository.findById(bulkRequestId);
            bulkRequestItemEntity.get().setBulkRequestStatus(ReCAPConstants.PROCESSED);
            bulkRequestItemEntity.get().setLastUpdatedDate(new Date());
            BulkRequestItemEntity savedBulkRequestItemEntity = bulkRequestItemDetailsRepository.save(bulkRequestItemEntity.get());
            List<RequestItemEntity> requestItemEntities = savedBulkRequestItemEntity.getRequestItemEntities();
            if (CollectionUtils.isNotEmpty(requestItemEntities)) {
                List<BulkRequestItem> bulkRequestItems = new ArrayList<>();
                for (RequestItemEntity requestItemEntity : requestItemEntities) {
                    BulkRequestItem bulkRequestItem = new BulkRequestItem();
                    bulkRequestItem.setItemBarcode(requestItemEntity.getItemEntity().getBarcode());
                    bulkRequestItem.setCustomerCode(requestItemEntity.getItemEntity().getCustomerCode());
                    bulkRequestItem.setRequestId(String.valueOf(requestItemEntity.getId()));
                    bulkRequestItem.setRequestStatus(requestItemEntity.getRequestStatusEntity().getRequestStatusDescription());
                    if (requestItemEntity.getRequestStatusEntity().getRequestStatusCode().equals(ReCAPConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED)
                            || requestItemEntity.getRequestStatusEntity().getRequestStatusCode().equals(ReCAPConstants.REQUEST_STATUS_PENDING)) {
                        bulkRequestItem.setStatus(ReCAPConstants.SUCCESS);
                    } else {
                        bulkRequestItem.setStatus(StringUtils.substringAfter(requestItemEntity.getNotes(), "Exception : "));
                    }
                    bulkRequestItems.add(bulkRequestItem);
                }
                itemRequestServiceUtil.updateStatusToBarcodes(bulkRequestItems, savedBulkRequestItemEntity);
            }
            itemRequestServiceUtil.generateReportAndSendEmail(bulkRequestId);
            logger.info("Bulk request processing completed for bulk request id : {}", bulkRequestId);
        } else {
            processBulkRequestForBarcode(itemBarcode, bulkRequestItemEntity.get());
        }
    }

    /**
     * Process request for each barcode.
     * @param itemBarcode
     * @param bulkRequestItemEntity
     */
    private void processBulkRequestForBarcode(String itemBarcode, BulkRequestItemEntity bulkRequestItemEntity) {
        try {
            List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
            ItemEntity itemEntity = itemEntities.get(0);
            ItemRequestInformation itemRequestInformation = buildItemRequestInformation(bulkRequestItemEntity);
            itemRequestDBService.updateItemAvailabilutyStatus(itemEntities, bulkRequestItemEntity.getCreatedBy());
            Integer requestId = itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.REQUEST_STATUS_PROCESSING, bulkRequestItemEntity);
            itemRequestInformation.setRequestId(requestId);
            itemRequestInformation.setItemBarcodes(Arrays.asList(itemEntity.getBarcode()));
            itemRequestInformation.setCustomerCode(itemEntity.getCustomerCode());
            ItemCheckoutResponse itemCheckoutResponse = (ItemCheckoutResponse) requestItemController.checkoutItem(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
            itemCheckoutResponse.setSuccess(true);
            if (itemCheckoutResponse.isSuccess()) {
                if (gfaService.isUseQueueLasCall()) {
                    itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.REQUEST_STATUS_PENDING, bulkRequestItemEntity);
                }
                ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
                itemInformationResponse.setRequestId(requestId);
                itemInformationResponse = gfaService.executeRetriveOrder(itemRequestInformation, itemInformationResponse);
                if(itemInformationResponse.isRequestTypeForScheduledOnWO()){
                    logger.info("Bulk Request : Request received on first scan");
                    itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.LAS_REFILE_REQUEST_PLACED,bulkRequestItemEntity);
                }
                else if (itemInformationResponse.isSuccess()) {
                    itemInformationResponse.setScreenMessage(ReCAPConstants.SUCCESSFULLY_PROCESSED_REQUEST_ITEM);
                    itemRequestInformation.setRequestNotes(itemRequestInformation.getRequestNotes() + "\n" + ReCAPConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId());
                    if (!gfaService.isUseQueueLasCall()) {
                        itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.REQUEST_STATUS_RETRIEVAL_ORDER_PLACED, bulkRequestItemEntity);
                    }
                } else {
                    requestItemController.checkinItem(itemRequestInformation, itemRequestInformation.getRequestingInstitution());
                    itemRequestDBService.rollbackUpdateItemAvailabilutyStatus(itemEntity, bulkRequestItemEntity.getCreatedBy());
                    itemRequestInformation.setRequestNotes(ReCAPConstants.USER + ":" + itemRequestInformation.getRequestNotes() + "\n" + ReCAPConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId() + "\n" + itemInformationResponse.getScreenMessage());
                    itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.REQUEST_STATUS_EXCEPTION, bulkRequestItemEntity);
                }
            } else {
                itemRequestDBService.rollbackUpdateItemAvailabilutyStatus(itemEntity, bulkRequestItemEntity.getCreatedBy());
                itemRequestInformation.setRequestNotes(ReCAPConstants.USER + ":" + itemRequestInformation.getRequestNotes() + "\n" + ReCAPConstants.BULK_REQUEST_ID_TEXT + bulkRequestItemEntity.getId() + "\n" + ReCAPConstants.REQUEST_ILS_EXCEPTION + itemCheckoutResponse.getScreenMessage());
                itemRequestDBService.updateRecapRequestItem(itemRequestInformation, itemEntity, ReCAPConstants.REQUEST_STATUS_EXCEPTION, bulkRequestItemEntity);
            }
            itemRequestServiceUtil.updateSolrIndex(itemEntity);
            logger.info("Request processing completed for barcode : {}", itemBarcode);
        } catch (Exception ex) {
            logger.error(ReCAPConstants.LOG_ERROR, itemBarcode);
        }
    }

    /**
     * Builds item request information object.
     * @param bulkRequestItemEntity
     * @return
     */
    private ItemRequestInformation buildItemRequestInformation(BulkRequestItemEntity bulkRequestItemEntity) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setRequestType(ReCAPConstants.REQUEST_TYPE_RETRIEVAL);
        itemRequestInformation.setRequestingInstitution(bulkRequestItemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setPatronBarcode(bulkRequestItemEntity.getPatronId());
        itemRequestInformation.setDeliveryLocation(bulkRequestItemEntity.getStopCode());
        itemRequestInformation.setEmailAddress(bulkRequestItemEntity.getEmailId());
        itemRequestInformation.setRequestNotes(bulkRequestItemEntity.getNotes());
        itemRequestInformation.setUsername(bulkRequestItemEntity.getCreatedBy() + "(Bulk)");
        return itemRequestInformation;
    }
}
