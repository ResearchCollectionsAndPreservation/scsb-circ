package org.recap.util;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.jpa.*;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.ItemStatusDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private Map<String, Integer> itemStatusMap;
    private Map<String, Integer> collectionGroupMap;
    private Map<String, Integer> institutionEntityMap;
    private Map<String, String> ftpPropertiesMap;


    @Value("${" + PropertyKeyConstants.FTP_SERVER_USERNAME + "}")
    String ftpUserName;

    @Value("${" + PropertyKeyConstants.FTP_SERVER_PRIVATEKEY + "}")
    String ftpPrivateKey;

    @Value("${" + PropertyKeyConstants.FTP_SERVER_KNOWNHOST + "}")
    String ftpKnownHost;

    @Autowired
    private ItemStatusDetailsRepository itemStatusDetailsRepository;

    @Autowired
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private ItemDetailsRepository itemDetailsRepository;

    @Autowired
    private ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Autowired
    private ImsLocationDetailsRepository imsLocationDetailsRepository;

    @Autowired
    private PropertyUtil propertyUtil;

    @Value("${scsb.support.institution}")
    private String supportInstitution;

    /**
     * This method builds Holdings Entity from holdings content
     * @param bibliographicEntity
     * @param currentDate
     * @param errorMessage
     * @param holdingsContent
     * @return
     */
    public HoldingsEntity buildHoldingsEntity(BibliographicEntity bibliographicEntity, Date currentDate, StringBuilder errorMessage, String holdingsContent) {
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        if (StringUtils.isNotBlank(holdingsContent)) {
            holdingsEntity.setContent(holdingsContent.getBytes());
        } else {
            errorMessage.append(" Holdings Content cannot be empty");
        }
        holdingsEntity.setCreatedDate(currentDate);
        holdingsEntity.setCreatedBy(ScsbConstants.SUBMIT_COLLECTION);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setLastUpdatedBy(ScsbConstants.SUBMIT_COLLECTION);
        Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        return holdingsEntity;
    }

    public void addItemAndReportEntities(List<ItemEntity> itemEntities, List<ReportEntity> reportEntities, boolean processHoldings, HoldingsEntity holdingsEntity, Map<String, Object> itemMap) {
        ItemEntity itemEntity = (ItemEntity) itemMap.get("itemEntity");
        ReportEntity itemReportEntity = (ReportEntity) itemMap.get("itemReportEntity");
        if (itemReportEntity != null) {
            reportEntities.add(itemReportEntity);
        } else if (processHoldings) {
            if (holdingsEntity.getItemEntities() == null) {
                holdingsEntity.setItemEntities(new ArrayList<>());
            }
            holdingsEntity.getItemEntities().add(itemEntity);
            itemEntities.add(itemEntity);
        }
    }

    /**
     * Gets item status map.
     *
     * @return the item status map
     */
    public Map<String, Integer> getItemStatusMap() {
        if (null == itemStatusMap) {
            itemStatusMap = new HashMap<>();
            try {
                Iterable<ItemStatusEntity> itemStatusEntities = itemStatusDetailsRepository.findAll();
                for (Iterator<ItemStatusEntity> iterator = itemStatusEntities.iterator(); iterator.hasNext(); ) {
                    ItemStatusEntity itemStatusEntity = iterator.next();
                    itemStatusMap.put(itemStatusEntity.getStatusCode(), itemStatusEntity.getId());
                }
            } catch (Exception e) {
                logger.error(ScsbCommonConstants.LOG_ERROR,e);
            }
        }
        return itemStatusMap;
    }

    /**
     * Gets collection group map.
     *
     * @return the collection group map
     */
    public Map<String, Integer> getCollectionGroupMap() {
        if (null == collectionGroupMap) {
            collectionGroupMap = new HashMap<>();
            try {
                Iterable<CollectionGroupEntity> collectionGroupEntities = collectionGroupDetailsRepository.findAll();
                for (Iterator<CollectionGroupEntity> iterator = collectionGroupEntities.iterator(); iterator.hasNext(); ) {
                    CollectionGroupEntity collectionGroupEntity = iterator.next();
                    collectionGroupMap.put(collectionGroupEntity.getCollectionGroupCode(), collectionGroupEntity.getId());
                }
            } catch (Exception e) {
                logger.error(ScsbCommonConstants.LOG_ERROR,e);
            }
        }
        return collectionGroupMap;
    }

    /**
     * Gets institution entity map.
     *
     * @return the institution entity map
     */
    public Map<String, Integer> getInstitutionEntityMap() {
        if (null == institutionEntityMap) {
            institutionEntityMap = new HashMap<>();
            try {
                Iterable<InstitutionEntity> institutionEntities = institutionDetailsRepository.findAll();
                for (Iterator<InstitutionEntity> iterator = institutionEntities.iterator(); iterator.hasNext(); ) {
                    InstitutionEntity institutionEntity = iterator.next();
                    institutionEntityMap.put(institutionEntity.getInstitutionCode(), institutionEntity.getId());
                }
            } catch (Exception e) {
                logger.error(ScsbCommonConstants.LOG_ERROR,e);
            }
        }
        return institutionEntityMap;
    }

    /**
     * Rollback update item availabiluty status.
     *
     * @param itemEntity the item entity
     * @param userName   the user name
     */
    public void rollbackUpdateItemAvailabilityStatus(ItemEntity itemEntity, String userName) {
        ItemStatusEntity itemStatusEntity = itemStatusDetailsRepository.findByStatusCode(ScsbCommonConstants.AVAILABLE);
        itemEntity.setItemAvailabilityStatusId(itemStatusEntity.getId()); // Available
        itemEntity.setLastUpdatedBy(getUser(userName));
        itemDetailsRepository.save(itemEntity);
        saveItemChangeLogEntity(itemEntity.getId(), getUser(userName), ScsbConstants.REQUEST_ITEM_AVAILABILITY_STATUS_UPDATE, ScsbConstants.REQUEST_ITEM_AVAILABILITY_STATUS_DATA_ROLLBACK);
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

    /**
     * Get list of barcodes from item entities
     * @param itemEntities
     * @return
     */
    public List<String> getBarcodesList(List<ItemEntity> itemEntities) {
        List<String> itemBarcodes = new ArrayList<>();
        if (!itemEntities.isEmpty()) {
            for (ItemEntity itemEntity : itemEntities) {
                itemBarcodes.add(itemEntity.getBarcode());
            }
        }
        return itemBarcodes;
    }

    public Map<String, String> getFTPPropertiesMap() {
        if (null == ftpPropertiesMap) {
            ftpPropertiesMap = new HashMap<>();
            ftpPropertiesMap.put("userName",ftpUserName);
            ftpPropertiesMap.put("knownHost",ftpKnownHost);
            ftpPropertiesMap.put("privateKey",ftpPrivateKey);
        }
        return ftpPropertiesMap;
    }

    /**
     * Gets IMS Location by item barcode.
     *
     * @param itemBarcode the item barcode
     * @return the IMS Location by item barcode
     */
    public String getImsLocationCodeByItemBarcode(String itemBarcode) {
        String imsLocationCode = null;
        List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemBarcode);
        if (CollectionUtils.isNotEmpty(itemEntities)) {
            ItemEntity itemEntity = itemEntities.get(0);
            if (null != itemEntity.getImsLocationEntity()) {
                imsLocationCode = itemEntity.getImsLocationEntity().getImsLocationCode();
            }
        }
        return imsLocationCode;
    }

    /**
     * Get All Institution Codes Except Support Institution
     * @return institutionCodes
     */
    public List<String> findAllInstitutionCodesExceptSupportInstitution() {
        return institutionDetailsRepository.findAllInstitutionCodesExceptSupportInstitution(supportInstitution);
    }

    /**
     * Get All Institution Codes Except Support Institution
     * @return institutionCodes
     */
    public List<InstitutionEntity> findAllInstitutionsExceptSupportInstitution() {
        return institutionDetailsRepository.findAllInstitutionsExceptSupportInstitution(supportInstitution);
    }

    /**
     * Get All Ims Location Codes Except UN
     * @return imsLocationCodes
     */
    public List<String> findAllImsLocationCodeExceptUN(){
        return imsLocationDetailsRepository.findAllImsLocationCodeExceptUN();
    }

    /**
     * Checks if the Gfa item status is available
     * @param imsLocationCode IMS Location Code
     * @param imsItemStatus IMS Item Status
     * @return boolean
     */
    public boolean checkIfImsItemStatusIsAvailableOrNotAvailable(String imsLocationCode, String imsItemStatus, boolean checkAvailable) {
        String propertyKey = checkAvailable ? PropertyKeyConstants.IMS.IMS_AVAILABLE_ITEM_STATUS_CODES : PropertyKeyConstants.IMS.IMS_NOT_AVAILABLE_ITEM_STATUS_CODES;
        String imsItemStatusCodes = propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, propertyKey);
        return StringUtils.startsWithAny(imsItemStatus, imsItemStatusCodes.split(","));
    }

    /**
     * Checks if the IMS item status is requestable but not retrievable (In first scan)
     * @param imsLocationCode IMS Location Code
     * @param imsItemStatus IMS Item Status
     * @return boolean
     */
    public boolean checkIfImsItemStatusIsRequestableNotRetrievable(String imsLocationCode, String imsItemStatus) {
        String imsItemStatusCodes = propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, PropertyKeyConstants.IMS.IMS_REQUESTABLE_NOT_RETRIEVABLE_ITEM_STATUS_CODES);
        return StringUtils.isNotBlank(imsItemStatusCodes) && StringUtils.startsWithAny(imsItemStatus, imsItemStatusCodes.split(","));
    }

    /**
     * Checks if the IMS item status is Not on file (Item does not exist)
     * @param imsLocationCode IMS Location Code
     * @param imsItemStatus IMS Item Status
     * @return boolean
     */
    public boolean checkIfImsItemIsNotOnFile(String imsLocationCode, String imsItemStatus) {
        String imsItemStatusCodes = propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, PropertyKeyConstants.IMS.IMS_NOT_ON_FILE_ITEM_STATUS_CODES);
        return StringUtils.isNotBlank(imsItemStatusCodes) && StringUtils.startsWithAny(imsItemStatus, imsItemStatusCodes.split(","));
    }

    public ItemRequestInformation getItemRequestInformation(ItemEntity itemEntity) {
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setItemBarcodes(Collections.singletonList(itemEntity.getBarcode()));
        itemRequestInformation.setItemOwningInstitution(itemEntity.getInstitutionEntity().getInstitutionCode());
        itemRequestInformation.setBibId(itemEntity.getBibliographicEntities().get(0).getOwningInstitutionBibId());
        return itemRequestInformation;
    }

    /**
     * This method add routes to the camel context with the passed parameter values
     * @param camelContext Camel Context
     * @param uri URI
     * @param routeId Route Id
     * @param logMessage Log Message
     * @param bean bean
     * @param methodName Method Name
     * @throws Exception Exception
     */
    public void addRoutesToCamelContext(CamelContext camelContext, String uri, String routeId, String logMessage, Object bean, String methodName) throws Exception {
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(uri)
                        .routeId(routeId)
                        .log(logMessage)
                        .bean(bean, methodName);
            }
        });
    }
}
