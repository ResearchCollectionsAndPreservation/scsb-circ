package org.recap.ils.protocol.rest.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ils.protocol.rest.model.CancelHoldData;
import org.recap.ils.protocol.rest.model.CheckinData;
import org.recap.ils.protocol.rest.model.CheckoutData;
import org.recap.ils.protocol.rest.model.CreateHoldData;
import org.recap.ils.protocol.rest.model.ItemData;
import org.recap.ils.protocol.rest.model.JobData;
import org.recap.ils.protocol.rest.model.Notice;
import org.recap.ils.protocol.rest.model.RecallData;
import org.recap.ils.protocol.rest.model.RefileData;
import org.recap.ils.protocol.rest.model.response.CancelHoldResponse;
import org.recap.ils.protocol.rest.model.response.CheckinResponse;
import org.recap.ils.protocol.rest.model.response.CheckoutResponse;
import org.recap.ils.protocol.rest.model.response.CreateHoldResponse;
import org.recap.ils.protocol.rest.model.response.ItemResponse;
import org.recap.ils.protocol.rest.model.response.RecallResponse;
import org.recap.ils.protocol.rest.model.response.RefileResponse;
import org.recap.model.response.ItemCheckinResponse;
import org.recap.model.response.ItemCheckoutResponse;
import org.recap.model.response.ItemHoldResponse;
import org.recap.model.response.ItemInformationResponse;
import org.recap.model.response.ItemRecallResponse;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.response.ItemRefileResponse;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by rajeshbabuk on 20/12/16.
 */
@Service
public class RestApiResponseUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestApiResponseUtil.class);

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * The Item details repository.
     */
    @Autowired
    ItemDetailsRepository itemDetailsRepository;

    /**
     * Gets item details repository.
     *
     * @return the item details repository
     */
    public ItemDetailsRepository getItemDetailsRepository() {
        return itemDetailsRepository;
    }

    /**
     * Build item information response from itemResponse object..
     *
     * @param itemResponse the item response
     * @return the item information response
     */
    public ItemInformationResponse buildItemInformationResponse(ItemResponse itemResponse) {
        ItemInformationResponse itemInformationResponse = new ItemInformationResponse();
        ItemData itemData = itemResponse.getItemData();
        itemInformationResponse.setItemBarcode((String) itemData.getBarcode());
        itemInformationResponse.setBibID(itemData.getBibIds().get(0));
        itemInformationResponse.setBibIds(itemData.getBibIds());
        itemInformationResponse.setCallNumber((String) itemData.getCallNumber());
        itemInformationResponse.setItemType((String) itemData.getItemType());
        itemInformationResponse.setSource(itemData.getNyplSource());
        itemInformationResponse.setUpdatedDate(formatFromSipDate(itemData.getUpdatedDate()));
        itemInformationResponse.setCreatedDate(formatFromSipDate(itemData.getCreatedDate()));
        itemInformationResponse.setDeletedDate(formatFromSipDate((String) itemData.getDeletedDate()));
        itemInformationResponse.setDeleted(itemData.getDeleted() != null ? (Boolean) itemData.getDeleted() : Boolean.FALSE);
        if (null != itemData.getStatus()) {
            itemInformationResponse.setDueDate(formatDueDate((String) ((LinkedHashMap) itemData.getStatus()).get("dueDate")));
            itemInformationResponse.setCirculationStatus((String) ((LinkedHashMap) itemData.getStatus()).get("display"));
        }
        if (null != itemData.getLocation()) {
            itemInformationResponse.setCurrentLocation((String) ((LinkedHashMap) itemData.getLocation()).get("name"));
        }
        itemInformationResponse.setSuccess(true);
        return itemInformationResponse;
    }

    private String formatFromSipDate(String sipDate) {
        SimpleDateFormat sipFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat requiredFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
        return requiredFormattedDate(sipDate, sipFormat, requiredFormat);
    }

    private String formatDueDate(String sipDate) {
        SimpleDateFormat sipFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat requiredFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
        return requiredFormattedDate(sipDate, sipFormat, requiredFormat);
    }

    /**
     * Build item checkout response from checkoutResponse object.
     *
     * @param checkoutResponse the checkout response
     * @return the item checkout response
     */
    public ItemCheckoutResponse buildItemCheckoutResponse(CheckoutResponse checkoutResponse) {
        ItemCheckoutResponse itemCheckoutResponse = new ItemCheckoutResponse();
        CheckoutData checkoutData = checkoutResponse.getData();
        itemCheckoutResponse.setItemBarcode(checkoutData.getItemBarcode());
        itemCheckoutResponse.setPatronIdentifier(checkoutData.getPatronBarcode());
        itemCheckoutResponse.setCreatedDate(formatFromSipDate(checkoutData.getCreatedDate()));
        itemCheckoutResponse.setUpdatedDate(formatFromSipDate((String) checkoutData.getUpdatedDate()));
        itemCheckoutResponse.setDueDate(formatDueDate(checkoutData.getDesiredDateDue()));
        itemCheckoutResponse.setProcessed(null != checkoutData.getProcessed() ? checkoutData.getProcessed() : Boolean.FALSE);
        itemCheckoutResponse.setJobId(checkoutData.getJobId());
        itemCheckoutResponse.setSuccess(checkoutData.getSuccess());
        return itemCheckoutResponse;
    }

    /**
     * Build item checkin response from checkinResponse object.
     *
     * @param checkinResponse the checkin response
     * @return the item checkin response
     */
    public ItemCheckinResponse buildItemCheckinResponse(CheckinResponse checkinResponse) {
        ItemCheckinResponse itemCheckinResponse = new ItemCheckinResponse();
        CheckinData checkinData = checkinResponse.getData();
        itemCheckinResponse.setItemBarcode(checkinData.getItemBarcode());
        itemCheckinResponse.setCreatedDate(formatFromSipDate(checkinData.getCreatedDate()));
        itemCheckinResponse.setUpdatedDate(formatFromSipDate((String) checkinData.getUpdatedDate()));
        itemCheckinResponse.setProcessed(null != checkinData.getProcessed() ? checkinData.getProcessed() : Boolean.FALSE);
        itemCheckinResponse.setJobId(checkinData.getJobId());
        itemCheckinResponse.setSuccess(checkinData.getSuccess());
        return itemCheckinResponse;
    }

    /**
     * Build item hold response from createHoldResponse object.
     *
     * @param createHoldResponse the create hold response
     * @return the item hold response
     * @throws Exception the exception
     */
    public ItemHoldResponse buildItemHoldResponse(CreateHoldResponse createHoldResponse) throws Exception {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        CreateHoldData holdData = createHoldResponse.getData();
        itemHoldResponse.setItemOwningInstitution(holdData.getOwningInstitutionId());
        itemHoldResponse.setItemBarcode(holdData.getItemBarcode());
        itemHoldResponse.setPatronIdentifier(holdData.getPatronBarcode());
        itemHoldResponse.setTrackingId(holdData.getTrackingId());
        itemHoldResponse.setCreatedDate(formatFromSipDate(holdData.getCreatedDate()));
        itemHoldResponse.setUpdatedDate(formatFromSipDate((String) holdData.getUpdatedDate()));
        itemHoldResponse.setExpirationDate(expirationDateForRest());
        return itemHoldResponse;
    }

    /**
     * Build item cancel hold response from cancelHoldResponse object.
     *
     * @param cancelHoldResponse the cancel hold response
     * @return the item hold response
     */
    public ItemHoldResponse buildItemCancelHoldResponse(CancelHoldResponse cancelHoldResponse) {
        ItemHoldResponse itemHoldResponse = new ItemHoldResponse();
        CancelHoldData holdData = cancelHoldResponse.getData();
        itemHoldResponse.setItemOwningInstitution(holdData.getOwningInstitutionId());
        itemHoldResponse.setItemBarcode(holdData.getItemBarcode());
        itemHoldResponse.setPatronIdentifier(holdData.getPatronBarcode());
        itemHoldResponse.setTrackingId(holdData.getTrackingId());
        itemHoldResponse.setCreatedDate(formatFromSipDate(holdData.getCreatedDate()));
        itemHoldResponse.setUpdatedDate(formatFromSipDate((String) holdData.getUpdatedDate()));
        return itemHoldResponse;
    }

    /**
     * Build item recall response from recallResponse object.
     *
     * @param recallResponse the recall response
     * @return the item recall response
     */
    public ItemRecallResponse buildItemRecallResponse(RecallResponse recallResponse) {
        ItemRecallResponse itemRecallResponse = new ItemRecallResponse();
        RecallData recallData = recallResponse.getData();
        itemRecallResponse.setItemOwningInstitution(recallData.getOwningInstitutionId());
        itemRecallResponse.setItemBarcode(recallData.getItemBarcode());
        itemRecallResponse.setJobId(recallData.getJobId());
        return itemRecallResponse;
    }

    /**
     * Build item refile response from refileResponse object.
     *
     * @param refileResponse the refile response
     * @return the item refile response
     */
    public ItemRefileResponse buildItemRefileResponse(RefileResponse refileResponse, String owningInstitution) {
        ItemRefileResponse itemRefileResponse = new ItemRefileResponse();
        RefileData refileData = refileResponse.getData();
        itemRefileResponse.setItemBarcode(refileData.getItemBarcode());
        itemRefileResponse.setItemOwningInstitution(owningInstitution);
        itemRefileResponse.setJobId(refileData.getJobId());
        return itemRefileResponse;
    }

    /**
     * Gets job status message from job data.
     *
     * @param jobData the job data
     * @return the job status message
     * @throws Exception the exception
     */
    public String getJobStatusMessage(JobData jobData) throws Exception {
        String jobStatus = null;
        List<Notice> notices = jobData.getNotices();
        if (CollectionUtils.isNotEmpty(notices)) {
            Collections.reverse(notices);
            Notice notice = notices.get(0);
            jobStatus = notice.getText();
        }
        return jobStatus;
    }

    /**
     * Gets rest api source based on requesting institution and owning institution.
     *
     * @param reqInstitution the Requesting institution id
     * @param owningInstitution the Owning institution id
     * @return the nypl source
     */
    public String getRestApiSourceForInstitution(String reqInstitution, String owningInstitution) {
        return propertyUtil.getPropertyByInstitutionAndKey(reqInstitution, ScsbConstants.ILS_SOURCE_FOR_ITEM + owningInstitution.toLowerCase());
    }

    /**
     * Gets normalized item id for Rest.
     *
     * @param itemBarcode the item barcode
     * @return the normalized item id for rest
     * @throws Exception the exception
     */
    public String getNormalizedItemIdForRestProtocolApi(String itemBarcode) throws Exception {
        String itemId = null;
        List<ItemEntity> itemEntities = getItemDetailsRepository().findByBarcode(itemBarcode);
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            ItemEntity itemEntity = itemEntities.get(0);
            if (null != itemEntity.getInstitutionEntity()) {
                String institutionCode = itemEntity.getInstitutionEntity().getInstitutionCode();
                itemId = itemEntity.getOwningInstitutionItemId();
                String isNormalizeOwningInstItemId = propertyUtil.getPropertyByInstitutionAndKey(institutionCode, PropertyKeyConstants.ILS.ILS_NORMALIZE_OWNING_INST_ITEM_ID);
                if (Boolean.TRUE.toString().equalsIgnoreCase(isNormalizeOwningInstItemId)) {
                    itemId = itemId.replace(".i", ""); // Remove prefix .i
                    itemId = StringUtils.chop(itemId); // Remove last check digit or char
                }
            }
        }
        return itemId;
    }

    /**
     * Gets expiration date for rest.
     *
     * @return the expiration date for rest
     * @throws Exception the exception
     */
    public String getExpirationDateForRest() throws Exception {
        Date expirationDate = DateUtils.addYears(new Date(), 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ScsbConstants.REST_HOLD_DATE_FORMAT);
        return simpleDateFormat.format(expirationDate);
    }

    public String expirationDateForRest() throws Exception {
        Date expirationDate = DateUtils.addYears(new Date(), 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ScsbConstants.DATE_FORMAT);
        return simpleDateFormat.format(expirationDate);
    }

    /**
     * Gets item owning institution by item barcode.
     *
     * @param itemBarcode the item barcode
     * @return the item owning institution by item barcode
     * @throws Exception the exception
     */
    public String getItemOwningInstitutionByItemBarcode(String itemBarcode) throws Exception {
        String institutionCode = null;
        List<ItemEntity> itemEntities = getItemDetailsRepository().findByBarcode(itemBarcode);
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            ItemEntity itemEntity = itemEntities.get(0);
            if (null != itemEntity.getInstitutionEntity()) {
                institutionCode = itemEntity.getInstitutionEntity().getInstitutionCode();
            }
        }
        return institutionCode;
    }
    
    private String requiredFormattedDate(String sipDate, SimpleDateFormat sipFormat, SimpleDateFormat requiredFormat)
    {
        String reformattedStr = "";
        try {
            if (sipDate != null && sipDate.trim().length() > 0) {
                reformattedStr = requiredFormat.format(sipFormat.parse(sipDate));
            }
        } catch (ParseException e) {
            logger.error(ScsbCommonConstants.REQUEST_EXCEPTION, e);
        }
        return reformattedStr;
    }
}
