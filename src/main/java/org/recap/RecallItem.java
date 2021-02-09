package org.recap;

import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.FromAgencyId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.RecallItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.RecallItemResponseData;
import org.extensiblecatalog.ncip.v2.service.ToAgencyId;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class RecallItem extends RecapNCIP {
    public RecallItem() {

    }

    public RecallItemInitiationData getRecallItemInitiationData(String itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation, String ncipAgencyId, String ncipScheme) {
        RecallItemInitiationData recallItemInitiationData = new RecallItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        ApplicationProfileType applicationProfileType = getApplicationProfileType();
        initiationHeader.setApplicationProfileType(applicationProfileType);
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(ncipAgencyId));
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(ncipAgencyId));
        initiationHeader.setToAgencyId(toAgencyId);
        initiationHeader.setFromAgencyId(fromAgencyId);

        UserId userid = new UserId();
        userid.setUserIdentifierValue(patronIdentifier);


        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue(itemIdentifier);
        recallItemInitiationData.setInitiationHeader(initiationHeader);
        recallItemInitiationData.setItemId(itemId);
        recallItemInitiationData.setUserIdDesired(Boolean.TRUE);
        recallItemInitiationData.setLocationDesired(Boolean.TRUE);

        return recallItemInitiationData;
    }

    public JSONObject getRecallItemResponse(RecallItemResponseData recallItemResponseData) {
        JSONObject returnJson = new JSONObject();

        if (!recallItemResponseData.getProblems().isEmpty()) {
            return generateNcipProblems(recallItemResponseData);
        }

        String dueDateString = "";
        if (recallItemResponseData.getDateDue() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            formatter.setCalendar(recallItemResponseData.getDateDue());
            dueDateString = formatter.format(recallItemResponseData.getDateDue().getTime());
        }

        returnJson.put(RecapConstants.ITEM_ID, recallItemResponseData.getItemId().getItemIdentifierValue());
        returnJson.put("userId", recallItemResponseData.getUserId());
        returnJson.put("expirationDate", dueDateString);
        return  returnJson;
    }

}
