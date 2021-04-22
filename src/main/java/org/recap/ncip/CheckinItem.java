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
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.ItemEntity;
import org.recap.repository.jpa.ItemDetailsRepository;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class CheckinItem extends RecapNCIP {

    public CheckInItemInitiationData getCheckInItemInitiationRemoteData(String itemIdentifier, ItemDetailsRepository itemDetailsRepository, String remoteProfileType, String ncipAgencyId, String ncipScheme) {

        CheckInItemInitiationData checkinItemInitiationData = new CheckInItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        String behalfAgency = null;
        ItemId itemId = new ItemId();
        FromSystemId fromSystemId = new FromSystemId(RecapConstants.NCIP_REMOTE_STORAGE);
        initiationHeader.setFromSystemId(fromSystemId);
        initiationHeader = getInitiationHeaderwithoutProfile(initiationHeader, ncipScheme, ncipAgencyId, ncipAgencyId);
        List<ItemEntity> itemEntities = itemDetailsRepository.findByBarcode(itemIdentifier);
        ItemEntity itemEntity = !itemEntities.isEmpty() ? itemEntities.get(0) : null;
        String imsLocation = itemEntity != null ? itemEntity.getImsLocationEntity().getImsLocationCode() : null;
        initiationHeader.setApplicationProfileType(new ApplicationProfileType(null, remoteProfileType));
            if (imsLocation != null && imsLocation.equalsIgnoreCase(RecapCommonConstants.RECAP)) {
                imsLocation = RecapConstants.RECAP_DEPOSITORY;
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
            initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, RecapConstants.AGENCY_ID_SCSB, ncipAgencyId);
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
        returnJson.put(RecapConstants.ITEM_ID, itemId);
        returnJson.put(RecapConstants.DUE_DATE, dueDateString);
        return returnJson;
    }
}
