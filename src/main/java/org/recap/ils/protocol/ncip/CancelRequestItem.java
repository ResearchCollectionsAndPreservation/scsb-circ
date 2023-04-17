package org.recap.ils.protocol.ncip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemResponseData;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.extensiblecatalog.ncip.v2.service.RequestType;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;
import org.recap.ScsbConstants;

import java.util.HashMap;

@Slf4j
public class CancelRequestItem extends ScsbNCIP {

    private String requestIdString;
    private String useridString;
    private String itemIdString;
    protected String toAgency;
    protected String fromAgency;
    private String requestTypeString;
    private String applicationProfileTypeString;
    private HashMap<String, HashMap<String,String>> itemOptionalFields = new HashMap<>();

    public CancelRequestItem() {
        itemOptionalFields.put(ScsbConstants.BIBLIOGRAPHIC_DESCRIPTION, new HashMap<>());
    }

    public CancelRequestItem setRequestType(String action) {
        requestTypeString = action;
        return this;
    }

    public CancelRequestItem setApplicationProfileType(String profileType) {
        applicationProfileTypeString = profileType;
        return this;
    }

    public CancelRequestItem setItemId(String itemId) {
        itemIdString = itemId;
        return this;
    }

    public CancelRequestItem setRequestId(String requestId) {
        requestIdString = requestId;
        return this;
    }

    public CancelRequestItem setUserId(String userId) {
        useridString = userId;
        return this;
    }


    public CancelRequestItem setToAgency(String toAgency) {
        this.toAgency = toAgency;
        return this;
    }

    public CancelRequestItem setFromAgency(String fromAgency) {
        this.fromAgency = fromAgency;
        return this;
    }

    public CancelRequestItem setRequestTypeString(String actionType) {
        this.requestTypeString = actionType;
        return this;
    }


    // Convenience methods
    public CancelRequestItem setTitle(String title) {
        this.itemOptionalFields.get(ScsbConstants.BIBLIOGRAPHIC_DESCRIPTION).put(ScsbConstants.TITLE, title);
        return this;
    }

    public String getFromAgency() {
        return fromAgency;
    }

    public String getToAgency() {
        return toAgency;
    }

    public String getRequestId() {
        return requestIdString;
    }

    public String getItemId() {
        return itemIdString;
    }

    public String getUserId() {
        return useridString;
    }

    public String getRequestTypeString() {
        return requestTypeString;
    }

    public String getApplicationProfileTypeString() {
        return applicationProfileTypeString;
    }


    public CancelRequestItemInitiationData getCancelRequestItemInitiationData(Integer requestId, String patronIdentifier, String ncipAgencyId, String ncipScheme) {

        CancelRequestItemInitiationData cancelRequestItemInitiationData = new CancelRequestItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        initiationHeader = getInitiationHeaderwithScheme(initiationHeader, ncipScheme, ncipAgencyId, ncipAgencyId);
        cancelRequestItemInitiationData.setInitiationHeader(initiationHeader);
        RequestId requestIdentifier = new RequestId();
        if(requestId != null) {
            requestIdentifier.setRequestIdentifierValue(ScsbConstants.NCIP_REQUEST_ID_PREFIX + requestId);
        }
        else {
            requestIdentifier.setRequestIdentifierValue(ScsbConstants.NCIP_REQUEST_ID_PREFIX + RandomUtils.nextInt(100000,100000000));
        }
        RequestType requestType = new RequestType(null, ScsbConstants.HOLD);
        UserId userid = new UserId();
        userid.setUserIdentifierValue(patronIdentifier);
        cancelRequestItemInitiationData.setUserId(userid);
        cancelRequestItemInitiationData.setInitiationHeader(initiationHeader);
        cancelRequestItemInitiationData.setRequestId(requestIdentifier);
        cancelRequestItemInitiationData.setRequestType(requestType);

        return cancelRequestItemInitiationData;

    }

    public JSONObject getCancelRequestItemResponse(CancelRequestItemResponseData cancelRequestItemResponseData) {

        JSONObject returnJson = new JSONObject();

        if (!cancelRequestItemResponseData.getProblems().isEmpty()) {
            return generateNcipProblems(cancelRequestItemResponseData);
        }

        String itemId = cancelRequestItemResponseData.getItemId().getItemIdentifierValue();
        String requestId = cancelRequestItemResponseData.getRequestId().getRequestIdentifierValue();
        returnJson.put(ScsbConstants.ITEM_ID, itemId);
        returnJson.put(ScsbConstants.REQUEST_ID, requestId);
        return returnJson;
    }
}

