package org.recap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.extensiblecatalog.ncip.v2.service.AcceptItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.AcceptItemResponseData;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.BibliographicDescription;
import org.extensiblecatalog.ncip.v2.service.FromAgencyId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemDescription;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.ItemOptionalFields;
import org.extensiblecatalog.ncip.v2.service.PickupLocation;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.extensiblecatalog.ncip.v2.service.RequestedActionType;
import org.extensiblecatalog.ncip.v2.service.ToAgencyId;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

@Slf4j
@Getter
@Setter
public class AcceptItem extends RecapNCIP {

    private String requestIdString;
    private String useridString;
    private String itemIdString;
    private String pickupLocationString;
    protected String toAgency;
    protected String fromAgency;
    private String requestedActionTypeString;
    private String applicationProfileTypeString;
    private HashMap<String, HashMap> itemOptionalFields = new HashMap<String, HashMap>();

    public AcceptItem() {
        itemOptionalFields.put(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION, new HashMap<String, String>());
        itemOptionalFields.put(RecapConstants.ITEM_DESCRIPTION, new HashMap<String, String>());
    }

    public AcceptItem setRequestActionType(String action) {
        requestedActionTypeString = action;
        return this;
    }

    public AcceptItem setApplicationProfileType(String profileType) {
        applicationProfileTypeString = profileType;
        return this;
    }

    public AcceptItem setItemId(String itemId) {
        itemIdString = itemId;
        return this;
    }

    public AcceptItem setRequestId(String requestId) {
        requestIdString = requestId;
        return this;
    }

    public AcceptItem setUserId(String userId) {
        useridString = userId;
        return this;
    }

    public AcceptItem setPickupLocation(String pickupLocation) {
        pickupLocationString = pickupLocation;
        return this;
    }

    public AcceptItem addBibliographicDescription(String bibliographicDescriptionType, String value) {
        itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(bibliographicDescriptionType, value);
        return this;
    }

    public AcceptItem addItemDescription(String itemDescriptionType, String value) {
        itemOptionalFields.get(RecapConstants.ITEM_DESCRIPTION).put(itemDescriptionType, value);
        return this;
    }

    public AcceptItem setToAgency(String toAgency) {
        this.toAgency = toAgency;
        return this;
    }

    public AcceptItem setFromAgency(String fromAgency) {
        this.fromAgency = fromAgency;
        return this;
    }

    public AcceptItem setRequestedActionTypeString(String actionType) {
        this.requestedActionTypeString = actionType;
        return this;
    }

    // Convenience methods
    public AcceptItem setTitle(String title) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.TITLE, title);
        return this;
    }

    public AcceptItem setAuthor(String author) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.AUTHOR, author);
        return this;
    }

    public AcceptItem setPublisher(String publisher) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.PUBLISHER, publisher);
        return this;
    }

    public AcceptItem setPublicationDate(String pubDate) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.PUBLICATION_DATE, pubDate);
        return this;
    }

    public AcceptItem setIsbn(String isbn) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.ISBN, isbn);
        return this;
    }

    public AcceptItem setIssn(String issn) {
        this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).put(RecapConstants.ISSN, issn);
        return this;
    }

    public AcceptItem setCallNumber(String callNumber) {
        this.itemOptionalFields.get(RecapConstants.ITEM_DESCRIPTION).put(RecapConstants.CALL_NUMBER, callNumber);
        return this;
    }

    public String getAuthor() {
        return (String) this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).get(RecapConstants.AUTHOR);
    }

    public String getTitle() {
        return (String) this.itemOptionalFields.get(RecapConstants.BIBLIOGRAPHIC_DESCRIPTION).get(RecapConstants.TITLE);
    }

    public String getCallNo() {
        return (String) this.itemOptionalFields.get(RecapConstants.ITEM_DESCRIPTION).get(RecapConstants.CALL_NUMBER);
    }

    public AcceptItemInitiationData getAcceptItemInitiationData(String itemIdentifier, String patronIdentifier, String callInstitutionId, String itemInstitutionId, String expirationDate, String bibId, String pickupLocationString, String trackingId, String title, String author, String callNumber, String ncipAgencyId, String ncipScheme)  {
        AcceptItemInitiationData acceptItemInitationData = new AcceptItemInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        ApplicationProfileType applicationProfileType = getApplicationProfileType();
        initiationHeader.setApplicationProfileType(applicationProfileType);
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(ncipScheme, ncipAgencyId));
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(ncipScheme, RecapConstants.AGENCY_ID_SCSB));
        initiationHeader.setToAgencyId(toAgencyId);
        initiationHeader.setFromAgencyId(fromAgencyId);
        acceptItemInitationData.setInitiationHeader(initiationHeader);
        RequestId requestId = new RequestId();
        RandomUtils.nextInt();
        //  requestId.setAgencyId(new AgencyId(RecapConstants.AGENCY_ID_HVD));
        //  requestId.setRequestIdentifierValue(requestIdString);
        requestId.setRequestIdentifierValue((new Integer(RandomUtils.nextInt(100000,100000000)).toString()));
        RequestedActionType requestActionType = new RequestedActionType(null, "Hold For Pickup");
        UserId userid = new UserId();
        //userid.setAgencyId(new AgencyId(callInstitutionId));
        userid.setUserIdentifierValue(patronIdentifier);
        ItemId itemId = new ItemId();
        // itemId.setAgencyId(new AgencyId(itemInstitutionId));
        itemId.setItemIdentifierValue(itemIdentifier);
        ItemOptionalFields itemOptionalFields = new ItemOptionalFields();
        BibliographicDescription bibliographicDescription = new BibliographicDescription();
        bibliographicDescription.setAuthor(author);
        bibliographicDescription.setTitle(title);
        bibliographicDescription.setPublisher("Penguin");
        bibliographicDescription.setPublicationDate("2003");

        itemOptionalFields.setBibliographicDescription(bibliographicDescription);
        ItemDescription itemDescription = new ItemDescription();
        itemDescription.setCallNumber(callNumber);
        itemOptionalFields.setItemDescription(itemDescription);
        PickupLocation pickupLocation = new PickupLocation(pickupLocationString);

        Calendar cal = new GregorianCalendar();
        Date dueDate = DateUtils.addYears(new Date(), 1);
        cal.setTime(dueDate);


        acceptItemInitationData.setItemId(itemId);
        acceptItemInitationData.setPickupLocation(pickupLocation);
        acceptItemInitationData.setUserId(userid);
        acceptItemInitationData.setInitiationHeader(initiationHeader);
        acceptItemInitationData.setRequestId(requestId);
        acceptItemInitationData.setRequestedActionType(requestActionType);
        acceptItemInitationData.setItemOptionalFields(itemOptionalFields);
        acceptItemInitationData.setDateForReturn((GregorianCalendar) cal);

        return acceptItemInitationData;

    }

    public JSONObject getAcceptItemResponse(AcceptItemResponseData acceptItem) {
        JSONObject returnJson = new JSONObject();

        // DEAL W/PROBLEMS IN THE RESPONSE
        if (!acceptItem.getProblems().isEmpty()) {
            return generateNcipProblems(acceptItem);
        }

        String itemId = acceptItem.getItemId().getItemIdentifierValue();
        String requestId = acceptItem.getRequestId().getRequestIdentifierValue();

        returnJson.put("itemId", itemId);
        returnJson.put("requestId", requestId);
        return returnJson;
    }


}
