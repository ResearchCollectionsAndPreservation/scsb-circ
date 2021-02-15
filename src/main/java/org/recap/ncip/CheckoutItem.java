package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.extensiblecatalog.ncip.v2.service.CheckOutItemInitiationData;
import org.extensiblecatalog.ncip.v2.service.CheckOutItemResponseData;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;
import org.recap.RecapConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class CheckoutItem extends RecapNCIP {

    public CheckOutItemInitiationData getCheckOutItemInitiationData(String itemIdentifier, Integer requestId, String patronIdentifier, String ncipAgencyId) {
        try {
            CheckOutItemInitiationData checkoutItemInitiationData = new CheckOutItemInitiationData();
            InitiationHeader initiationHeader = new InitiationHeader();
            initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, RecapConstants.AGENCY_ID_SCSB, ncipAgencyId);
            UserId userid = new UserId();
            userid.setUserIdentifierValue(patronIdentifier);
            ItemId itemId = new ItemId();
            itemId.setItemIdentifierValue(itemIdentifier);
            RequestId requestIdentifier = new RequestId();
            if(requestId != null) {
                requestIdentifier.setRequestIdentifierValue(requestId.toString());
            }
            else {
                requestIdentifier.setRequestIdentifierValue((Integer.toString(RandomUtils.nextInt(100000,100000000))));
            }

            Calendar cal = new GregorianCalendar();

            Date expirationDate = DateUtils.addYears(new Date(), 1);
            cal.setTime(expirationDate);
            checkoutItemInitiationData.setInitiationHeader(initiationHeader);
            checkoutItemInitiationData.setItemId(itemId);
            checkoutItemInitiationData.setUserId(userid);
            checkoutItemInitiationData.setRequestId(requestIdentifier);
            checkoutItemInitiationData.setDesiredDateDue((GregorianCalendar) cal);
            return checkoutItemInitiationData;
        } catch (Exception e) {
            return new CheckOutItemInitiationData();
        }

    }

    public JSONObject getCheckoutResponse(CheckOutItemResponseData checkoutItemResponse) {
        JSONObject returnJson = new JSONObject();

        if (!checkoutItemResponse.getProblems().isEmpty()) {
            return generateNcipProblems(checkoutItemResponse);
        }

        String dueDateString = "";
        if (checkoutItemResponse.getDateDue() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            formatter.setCalendar(checkoutItemResponse.getDateDue());
            dueDateString = formatter.format(checkoutItemResponse.getDateDue().getTime());
        }
        returnJson.put(RecapConstants.ITEM_BARCODE, checkoutItemResponse.getItemId().getItemIdentifierValue());
        returnJson.put(RecapConstants.PATRON_IDENTIFIER, checkoutItemResponse.getUserId().getUserIdentifierValue());
        returnJson.put(RecapConstants.DUE_DATE, dueDateString);
        return returnJson;
    }
}
