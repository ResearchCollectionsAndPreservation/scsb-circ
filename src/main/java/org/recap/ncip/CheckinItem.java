package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.CheckInItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CheckInItemResponseData;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.OnBehalfOfAgency;
import org.json.JSONObject;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class CheckinItem extends RecapNCIP {

    public CheckInItemInitiationData getCheckInItemInitiationData(String itemIdentifier, ItemDetailsRepository itemDetailsRepository, String behalfAgency, String ncipAgencyId, String ncipScheme) {
        CheckInItemInitiationData checkinItemInitiationData = new CheckInItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, RecapConstants.AGENCY_ID_SCSB, ncipAgencyId);
        if(behalfAgency != null) {
            if(behalfAgency.equals(RecapCommonConstants.ITEM)) {
                List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemIdentifier);
                ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;
                String imsLocation = itemEntity.getImsLocationEntity().getImsLocationCode();
                if(imsLocation.equalsIgnoreCase(RecapCommonConstants.RECAP)) {
                    imsLocation = "RD";
                }
                behalfAgency = itemEntity.getItemLibrary() + "_" + imsLocation;
            }
            OnBehalfOfAgency onBehalfOfAgency = new OnBehalfOfAgency();
            onBehalfOfAgency.setAgencyId(new AgencyId(null, behalfAgency));
            initiationHeader.setOnBehalfOfAgency(onBehalfOfAgency);
        }
        ItemId itemId = new ItemId();
       // itemId.setAgencyId(new AgencyId(ncipScheme, ncipAgencyId));
        itemId.setItemIdentifierValue(itemIdentifier);
        checkinItemInitiationData.setItemId(itemId);
        checkinItemInitiationData.setInitiationHeader(initiationHeader);
        return checkinItemInitiationData;
    }

    public JSONObject getCheckInResponse(CheckInItemResponseData checkinItemResponse) {

        JSONObject returnJson = new JSONObject();
        if (!checkinItemResponse.getProblems().isEmpty()) {
            return generateNcipProblems(checkinItemResponse);
        }

        String dueDateString = "";
        if(checkinItemResponse.getItemOptionalFields() != null && checkinItemResponse.getItemOptionalFields().getDateDue() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                formatter.setCalendar(checkinItemResponse.getItemOptionalFields().getDateDue());
                dueDateString = formatter.format(checkinItemResponse.getItemOptionalFields().getDateDue().getTime());
        }
        String itemId = checkinItemResponse.getItemId().getItemIdentifierValue();
        returnJson.put(RecapConstants.ITEM_ID, itemId);
        returnJson.put(RecapConstants.DUE_DATE, dueDateString);
        return returnJson;

    }
}
