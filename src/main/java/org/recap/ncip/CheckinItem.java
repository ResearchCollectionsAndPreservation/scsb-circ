package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.CheckInItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CheckInItemResponseData;
import org.extensiblecatalog.ncip.v2.service.FromSystemId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.OnBehalfOfAgency;
import org.json.JSONObject;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ItemEntity;

import java.text.SimpleDateFormat;

@Slf4j
public class CheckinItem extends ScsbNCIP {

    public CheckInItemInitiationData getCheckInItemInitiationRemoteData(String itemIdentifier, ItemEntity itemEntity, String imsLocation, String remoteProfileType, String ncipAgencyId, String ncipScheme) {

        CheckInItemInitiationData checkinItemInitiationData = new CheckInItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        String behalfAgency = null;
        ItemId itemId = new ItemId();
        FromSystemId fromSystemId = new FromSystemId(ScsbConstants.NCIP_REMOTE_STORAGE);
        initiationHeader.setFromSystemId(fromSystemId);
        initiationHeader = getInitiationHeaderwithoutProfile(initiationHeader, ncipScheme, ncipAgencyId, ncipAgencyId);
        initiationHeader.setApplicationProfileType(new ApplicationProfileType(null, remoteProfileType));
            if (imsLocation != null && imsLocation.equalsIgnoreCase(ScsbConstants.RECAP)) {
                imsLocation = ScsbConstants.RECAP_DEPOSITORY;
            }
            if(itemEntity != null) {
                behalfAgency = itemEntity.getItemLibrary() + "." + itemEntity.getItemLibrary() + "_" + imsLocation;
            }
        OnBehalfOfAgency onBehalfOfAgency = new OnBehalfOfAgency();
        onBehalfOfAgency.setAgencyId(new AgencyId(ncipScheme, behalfAgency));
        initiationHeader.setOnBehalfOfAgency(onBehalfOfAgency);
        checkinItemInitiationData.setInitiationHeader(initiationHeader);
        itemId.setAgencyId(new AgencyId(ncipScheme, ncipAgencyId));
        itemId.setItemIdentifierValue(itemIdentifier);
        checkinItemInitiationData.setItemId(itemId);
        return checkinItemInitiationData;
    }
        public CheckInItemInitiationData getCheckInItemInitiationData(String itemIdentifier, String ncipAgencyId) {
            CheckInItemInitiationData checkinItemInitiationData = new CheckInItemInitiationData();
            InitiationHeader initiationHeader = new InitiationHeader();
            initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, ScsbConstants.AGENCY_ID_SCSB, ncipAgencyId);
            checkinItemInitiationData.setInitiationHeader(initiationHeader);
            ItemId itemId = new ItemId();
            itemId.setItemIdentifierValue(itemIdentifier);
            checkinItemInitiationData.setItemId(itemId);
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
        returnJson.put(ScsbConstants.ITEM_ID, itemId);
        returnJson.put(ScsbConstants.DUE_DATE, dueDateString);
        return returnJson;
    }
}
