package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.LookupUserInitiationData;
import org.extensiblecatalog.ncip.v2.service.LookupUserResponseData;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.StructuredAddress;
import org.extensiblecatalog.ncip.v2.service.UserAddressInformation;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.extensiblecatalog.ncip.v2.service.UserPrivilege;
import org.json.JSONArray;
import org.json.JSONObject;
import org.recap.ScsbCommonConstants;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LookupUser extends ScsbNCIP {

    protected String fromAgency;
    private String stringValue = "value";

    public LookupUser setFromAgency(String fromAgency) {
        this.fromAgency = fromAgency;
        return this;
    }
    public LookupUserInitiationData getLookupUserInitiationData(String useridString, String ncipAgencyId) {
        LookupUserInitiationData lookupUserInitationData = new LookupUserInitiationData();
        InitiationHeader initiationHeader = new InitiationHeader();
        initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, ncipAgencyId, ncipAgencyId);
        UserId userid = new UserId();
        userid.setAgencyId(new AgencyId(fromAgency));
        userid.setUserIdentifierValue(useridString);
        lookupUserInitationData.setUserId(userid);
        lookupUserInitationData.setUserAddressInformationDesired(Boolean.TRUE);
        lookupUserInitationData.setBlockOrTrapDesired(Boolean.TRUE);
        lookupUserInitationData.setNameInformationDesired(Boolean.TRUE);
        lookupUserInitationData.setUserPrivilegeDesired(Boolean.TRUE);
        lookupUserInitationData.setUserIdDesired(Boolean.TRUE);
        lookupUserInitationData.setUserLanguageDesired(Boolean.FALSE);
        lookupUserInitationData.setInitiationHeader(initiationHeader);

        return lookupUserInitationData;
    }

    public JSONObject getLookupUserResponse(NCIPResponseData responseData) {
        if (!responseData.getProblems().isEmpty()) {
            return generateNcipProblems(responseData);
        }

        LookupUserResponseData lookupUserResponse = (LookupUserResponseData)responseData;
        JSONObject returnJson = new JSONObject();
        JSONObject returnJsonName = new JSONObject();

        gatherName(lookupUserResponse,returnJsonName);
        gatherPhysicalAddress(lookupUserResponse);
        returnJson.put("name",returnJsonName.get("firstName")+" "+returnJsonName.get("lastName"));
        returnJson.put("userId", getUserIdString(lookupUserResponse));
        returnJson.put("privileges", getPrivileges(lookupUserResponse));
        returnJson.put("electronicAddresses", gatherElectronicAddress(lookupUserResponse));

        return returnJson;
    }


    private JSONObject gatherName(LookupUserResponseData lookupUserResponse,JSONObject returnJson) {

        String firstName = "";
        String lastName = "";
        try {
            firstName = lookupUserResponse.getUserOptionalFields().getNameInformation().getPersonalNameInformation().getStructuredPersonalUserName().getGivenName();
            lastName = lookupUserResponse.getUserOptionalFields().getNameInformation().getPersonalNameInformation().getStructuredPersonalUserName().getSurname();
        }
        catch(Exception e) {
            log.info("Name not provided in response");
        }
        returnJson.put("firstName", firstName);
        returnJson.put("lastName", lastName);
        return returnJson;

    }

    private JSONArray gatherPhysicalAddress(LookupUserResponseData lookupUserResponse) {

        JSONArray jsonArray = new JSONArray();
        if (lookupUserResponse.getUserOptionalFields().getUserAddressInformations() == null) return jsonArray;
        Iterator<UserAddressInformation> iterator = lookupUserResponse.getUserOptionalFields().getUserAddressInformations().iterator();
        while (iterator.hasNext()) {
            UserAddressInformation address =  iterator.next();
            try {
                JSONObject json = new JSONObject();
                if (address.getPhysicalAddress() == null) continue;
                String type =  address.getPhysicalAddress().getPhysicalAddressType().getValue();
                StructuredAddress structuredAddress =  address.getPhysicalAddress().getStructuredAddress();
                JSONObject addressAsJson = new JSONObject();
                addressAsJson.put("lineOne", structuredAddress.getLine1());
                addressAsJson.put("lineTwo", structuredAddress.getLine2());
                addressAsJson.put("locality", structuredAddress.getLocality());
                addressAsJson.put("region", structuredAddress.getRegion());
                addressAsJson.put("postalCode", structuredAddress.getPostalCode());
                json.put("key",type);
                json.put(stringValue,addressAsJson);
                jsonArray.put(json);
            }
            catch(Exception e) {
                log.error("Unable to parse physical address");
                log.error(e.toString());
            }
        }
        return jsonArray;
    }


    private JSONArray gatherElectronicAddress(LookupUserResponseData lookupUserResponse) {

        JSONArray jsonArray = new JSONArray();
        if (lookupUserResponse.getUserOptionalFields() == null || lookupUserResponse.getUserOptionalFields().getUserAddressInformations() == null) return jsonArray;
        Iterator<UserAddressInformation> iterator = lookupUserResponse.getUserOptionalFields().getUserAddressInformations().iterator();
        while (iterator.hasNext()) {
            UserAddressInformation address =  iterator.next();
            try {
                JSONObject json = new JSONObject();
                if (address.getElectronicAddress() == null) continue;
                String type =  address.getElectronicAddress().getElectronicAddressType().getValue();
                String value =  address.getElectronicAddress().getElectronicAddressData();
                if (isEmailPattern(value)) type = "emailAddress";
                json.put("key", type);
                json.put(stringValue, value);
                jsonArray.put(json);
            }
            catch(Exception e) {
                log.error("Unable to parse electronic address");
                log.error(e.toString());
            }
        }
        return jsonArray;
    }

    private boolean isEmailPattern(String email) {
        String regex = ScsbCommonConstants.REGEX_FOR_EMAIL_ADDRESS;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private String getUserIdString(LookupUserResponseData lookupUserResponse) {
        if (lookupUserResponse.getUserId() != null)
            return lookupUserResponse.getUserId().getUserIdentifierValue();
        return "";
    }

    private JSONArray getPrivileges(LookupUserResponseData lookupUserResponse) {
        JSONArray jsonArray = new JSONArray();
        if (lookupUserResponse.getUserOptionalFields() == null || lookupUserResponse.getUserOptionalFields().getUserPrivileges() == null) return jsonArray;
        Iterator<UserPrivilege> iterator = lookupUserResponse.getUserOptionalFields().getUserPrivileges().iterator();
        while (iterator.hasNext()) {
            UserPrivilege priv = iterator.next();
            try {
                JSONObject json = new JSONObject();
                String type = priv.getAgencyUserPrivilegeType().getValue();
                String value = priv.getUserPrivilegeStatus().getUserPrivilegeStatusType().getValue();
                if (type.equalsIgnoreCase("status") && value.equalsIgnoreCase("active")) value = "OK";
                json.put("key", type);
                json.put(stringValue, value);
                jsonArray.put(json);
            }
            catch(Exception e) {
                log.error("Unable to parse user privilege");
                log.error(e.toString());
            }
        }
        return jsonArray;
    }
}
