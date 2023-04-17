package org.recap.ils.protocol.ncip;

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
import org.recap.ScsbConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Slf4j
public class CheckoutItem extends ScsbNCIP {

    public CheckOutItemInitiationData getCheckOutItemInitiationData(String itemIdentifier, Integer requestId, String patronIdentifier, String ncipAgencyId) {
        try {
            CheckOutItemInitiationData checkoutItemInitiationData = new CheckOutItemInitiationData();
            InitiationHeader initiationHeader = new InitiationHeader();
            initiationHeader = getInitiationHeaderwithoutScheme(initiationHeader, ScsbConstants.AGENCY_ID_SCSB, ncipAgencyId);
            UserId userid = new UserId();
            userid.setUserIdentifierValue(patronIdentifier);
            ItemId itemId = new ItemId();
            itemId.setItemIdentifierValue(itemIdentifier);
            RequestId requestIdentifier = new RequestId();
            if(requestId != null) {
                requestIdentifier.setRequestIdentifierValue(ScsbConstants.NCIP_REQUEST_ID_PREFIX+requestId);
            }
            else {
                requestIdentifier.setRequestIdentifierValue(ScsbConstants.NCIP_REQUEST_ID_PREFIX+(RandomUtils.nextInt(100000,100000000)));
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
        } catch (RuntimeException e) {
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
        returnJson.put(ScsbConstants.ITEM_BARCODE, checkoutItemResponse.getItemId().getItemIdentifierValue());
        returnJson.put(ScsbConstants.PATRON_IDENTIFIER, checkoutItemResponse.getUserId().getUserIdentifierValue());
        returnJson.put(ScsbConstants.DUE_DATE, dueDateString);
        return returnJson;
    }
}
