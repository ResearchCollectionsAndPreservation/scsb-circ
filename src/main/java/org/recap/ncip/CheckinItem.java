package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.extensiblecatalog.ncip.v2.service.*;
import org.json.JSONObject;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class CheckinItem extends RecapNCIP {

    public CheckInItemInitiationData getCheckInItemInitiationData(String itemIdentifier, ItemDetailsRepository itemDetailsRepository, String behalfAgency, String ncipAgencyId, String ncipScheme, Boolean isRemoteCheckin) {
        CheckInItemInitiationData checkinItemInitiationData = new CheckInItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        ItemId itemId = new ItemId();
        if(isRemoteCheckin) {
            FromSystemId fromSystemId = new FromSystemId(RecapConstants.NCIP_REMOTE_STORAGE);
            initiationHeader.setFromSystemId(fromSystemId);
            initiationHeader = getInitiationHeaderwithoutProfile(initiationHeader, ncipScheme, ncipAgencyId, ncipAgencyId);
            List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemIdentifier);
            ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;
            String imsLocation = itemEntity.getImsLocationEntity().getImsLocationCode();
            ApplicationProfileType applicationProfileType = null;
            if (imsLocation.equalsIgnoreCase(RecapCommonConstants.RECAP)) {
                applicationProfileType = new ApplicationProfileType(null, RecapConstants.RECAP_PROFILE);
            } else {
                applicationProfileType = new ApplicationProfileType(null, RecapConstants.HARVARD_PROFILE);
            }
            initiationHeader.setApplicationProfileType(applicationProfileType);
            if (behalfAgency != null && behalfAgency.equals(RecapCommonConstants.ITEM)) {
                if (imsLocation.equalsIgnoreCase(RecapCommonConstants.RECAP)) {
                    imsLocation = RecapConstants.RECAP_DEPOSITORY;
                }
                behalfAgency = itemEntity.getItemLibrary() + "." + itemEntity.getItemLibrary() + "_" + imsLocation;
            }
            OnBehalfOfAgency onBehalfOfAgency = new OnBehalfOfAgency();
            onBehalfOfAgency.setAgencyId(new AgencyId(ncipScheme, behalfAgency));
            initiationHeader.setOnBehalfOfAgency(onBehalfOfAgency);
            itemId.setAgencyId(new AgencyId(ncipScheme));
            itemId.setItemIdentifierValue(itemIdentifier);
            checkinItemInitiationData.setItemId(itemId);
        }
        else {
            initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, RecapConstants.AGENCY_ID_SCSB, ncipAgencyId);
            OnBehalfOfAgency onBehalfOfAgency = new OnBehalfOfAgency();
            onBehalfOfAgency.setAgencyId(new AgencyId(null, behalfAgency));
            initiationHeader.setOnBehalfOfAgency(onBehalfOfAgency);
            itemId.setItemIdentifierValue(itemIdentifier);
            checkinItemInitiationData.setItemId(itemId);
        }
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
