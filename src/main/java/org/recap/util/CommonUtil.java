package org.recap.util;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.HoldingsEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.report.SubmitCollectionReportInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommonUtil {

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
        holdingsEntity.setCreatedBy(RecapConstants.SUBMIT_COLLECTION);
        holdingsEntity.setLastUpdatedDate(currentDate);
        holdingsEntity.setLastUpdatedBy(RecapConstants.SUBMIT_COLLECTION);
        Integer owningInstitutionId = bibliographicEntity.getOwningInstitutionId();
        holdingsEntity.setOwningInstitutionId(owningInstitutionId);
        return holdingsEntity;
    }

    public void buildSubmitCollectionReportInfoAndAddFailures(BibliographicEntity fetchedBibliographicEntity, List<SubmitCollectionReportInfo> failureSubmitCollectionReportInfoList, String owningInstitution, Map.Entry<String, Map<String, ItemEntity>> incomingHoldingItemMapEntry, ItemEntity incomingItemEntity) {
        SubmitCollectionReportInfo submitCollectionReportInfo = new SubmitCollectionReportInfo();
        submitCollectionReportInfo.setItemBarcode(incomingItemEntity.getBarcode());
        submitCollectionReportInfo.setCustomerCode(incomingItemEntity.getCustomerCode());
        submitCollectionReportInfo.setOwningInstitution(owningInstitution);
        String existingOwningInstitutionHoldingsId = getExistingItemEntityOwningInstItemId(fetchedBibliographicEntity,incomingItemEntity);
        submitCollectionReportInfo.setMessage(RecapConstants.SUBMIT_COLLECTION_FAILED_RECORD+" - Owning institution holdings id mismatch - incoming owning institution holdings id " +incomingHoldingItemMapEntry.getKey()+ ", existing owning institution item id "+incomingItemEntity.getOwningInstitutionItemId()
                +", existing owning institution holdings id "+existingOwningInstitutionHoldingsId+", existing owning institution bib id "+fetchedBibliographicEntity.getOwningInstitutionBibId());
        failureSubmitCollectionReportInfoList.add(submitCollectionReportInfo);
    }

    private String getExistingItemEntityOwningInstItemId(BibliographicEntity fetchedBibliographicEntity,ItemEntity incomingItemEntity){
        for(ItemEntity fetchedItemEntity:fetchedBibliographicEntity.getItemEntities()){
            if(fetchedItemEntity.getOwningInstitutionItemId().equals(incomingItemEntity.getOwningInstitutionItemId())){
                return fetchedItemEntity.getHoldingsEntities().get(0).getOwningInstitutionHoldingsId();
            }
        }
        return "";
    }
}
