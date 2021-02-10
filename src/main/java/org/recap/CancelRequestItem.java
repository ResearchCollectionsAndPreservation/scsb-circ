package org.recap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CancelRequestItemResponseData;
import org.extensiblecatalog.ncip.v2.service.FromAgencyId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.extensiblecatalog.ncip.v2.service.RequestType;
import org.extensiblecatalog.ncip.v2.service.ToAgencyId;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;
import org.recap.controller.RequestItemController;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

@Slf4j
public class CancelRequestItem extends RecapNCIP {

    private String requestIdString;
    private String useridString;
    private String itemIdString;
    private String pickupLocationString;
    protected String toAgency;
    protected String fromAgency;
    private String requestTypeString;
    private String applicationProfileTypeString;
    private HashMap<String, HashMap> itemOptionalFields = new HashMap<String, HashMap>();

    public CancelRequestItem() {
        itemOptionalFields.put(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION, new HashMap<String, String>());
    }

    public org.recap.CancelRequestItem setRequestType(String action) {
        requestTypeString = action;
        return this;
    }

    public org.recap.CancelRequestItem setApplicationProfileType(String profileType) {
        applicationProfileTypeString = profileType;
        return this;
    }

    public org.recap.CancelRequestItem setItemId(String itemId) {
        itemIdString = itemId;
        return this;
    }

    public org.recap.CancelRequestItem setRequestId(String requestId) {
        requestIdString = requestId;
        return this;
    }

    public org.recap.CancelRequestItem setUserId(String userId) {
        useridString = userId;
        return this;
    }


    public org.recap.CancelRequestItem setToAgency(String toAgency) {
        this.toAgency = toAgency;
        return this;
    }

    public org.recap.CancelRequestItem setFromAgency(String fromAgency) {
        this.fromAgency = fromAgency;
        return this;
    }

    public org.recap.CancelRequestItem setRequestTypeString(String actionType) {
        this.requestTypeString = actionType;
        return this;
    }

    // Convenience methods
    public org.recap.CancelRequestItem setTitle(String title) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.TITLE, title);
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

    public CancelRequestItemInitiationData getCancelRequestItemInitiationData(String itemIdentifier, Integer requestId, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocationString, String trackingId, String ncipAgencyId, String ncipScheme) {

        CancelRequestItemInitiationData cancelRequestItemInitiationData = new CancelRequestItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        ApplicationProfileType applicationProfileType = getApplicationProfileType();
        initiationHeader.setApplicationProfileType(applicationProfileType);
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(ncipScheme, ncipAgencyId));
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(ncipScheme, ncipAgencyId));
        initiationHeader.setToAgencyId(toAgencyId);
        initiationHeader.setFromAgencyId(fromAgencyId);
        cancelRequestItemInitiationData.setInitiationHeader(initiationHeader);
        RequestId requestIdentifier = new RequestId();
        if(requestId != null) {
            requestIdentifier.setRequestIdentifierValue(requestId.toString());
        }
        else {
            requestIdentifier.setRequestIdentifierValue((Integer.valueOf(RandomUtils.nextInt(100000,100000000)).toString()));
        }
        RequestType requestType = new RequestType(null, RecapConstants.HOLD);
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
        returnJson.put(RecapConstants.ITEM_ID, itemId);
        returnJson.put(RecapConstants.REQUEST_ID, requestId);
        return returnJson;
    }
}

