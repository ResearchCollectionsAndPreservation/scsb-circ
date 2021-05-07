package org.recap.ncip;

import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.RecallItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.RecallItemResponseData;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;
import org.recap.ScsbConstants;

import java.text.SimpleDateFormat;

public class RecallItem extends ScsbNCIP {

    public RecallItemInitiationData getRecallItemInitiationData(String itemIdentifier, String patronIdentifier, String ncipAgencyId) {
        RecallItemInitiationData recallItemInitiationData = new RecallItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, ncipAgencyId, ncipAgencyId);
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

        returnJson.put(ScsbConstants.ITEM_ID, recallItemResponseData.getItemId().getItemIdentifierValue());
        returnJson.put("userId", recallItemResponseData.getUserId());
        returnJson.put("expirationDate", dueDateString);
        return  returnJson;
    }

}
