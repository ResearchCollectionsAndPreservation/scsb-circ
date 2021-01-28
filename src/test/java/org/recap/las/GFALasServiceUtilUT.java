package org.recap.las;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.recap.las.model.*;
import org.recap.model.gfa.Ttitem;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class GFALasServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    GFALasServiceUtil gfaLasServiceUtil;

    @Test
    public void getLASRetrieveResponse(){
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        GFARetrieveItemResponse response = gfaLasServiceUtil.getLASRetrieveResponse(gfaRetrieveItemResponse);
        assertNotNull(response);
    }
    @Test
    public void getLASRetrieveResponseWithoutErrorcode(){
        GFARetrieveItemResponse gfaRetrieveItemResponse = getGfaRetrieveItemResponse();
        gfaRetrieveItemResponse.getDsitem().getTtitem().get(0).setErrorCode("");
        GFARetrieveItemResponse response = gfaLasServiceUtil.getLASRetrieveResponse(gfaRetrieveItemResponse);
        assertNotNull(response);
    }
    @Test
    public void getLASRetrieveResponseNullValue(){
        GFARetrieveItemResponse gfaRetrieveItemResponse = null;
        GFARetrieveItemResponse response = gfaLasServiceUtil.getLASRetrieveResponse(gfaRetrieveItemResponse);
        assertNotNull(response);
    }
    @Test
    public void getLASEddResponse(){
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        GFAEddItemResponse response = gfaLasServiceUtil.getLASEddResponse(gfaEddItemResponse);
        assertNotNull(response);
    }
    @Test
    public void getLASEddResponseWithoutErrorcode(){
        GFAEddItemResponse gfaEddItemResponse = getGFAEddItemResponse();
        gfaEddItemResponse.getDsitem().getTtitem().get(0).setErrorCode("");
        GFAEddItemResponse response = gfaLasServiceUtil.getLASEddResponse(gfaEddItemResponse);
        assertNotNull(response);
    }
    @Test
    public void getLASEddResponseNullValue(){
        GFAEddItemResponse gfaEddItemResponse = null;
        GFAEddItemResponse response = gfaLasServiceUtil.getLASEddResponse(gfaEddItemResponse);
        assertNotNull(response);
    }
    @Test
    public void convertJsonToString(){
        String jString = "{\"PUL\": \"PUL\"}";
        JSONObject jsonObject = new JSONObject(jString.toString());
        String response = gfaLasServiceUtil.convertJsonToString(jsonObject);
        assertNotNull(response);
    }

    private GFAEddItemResponse getGFAEddItemResponse() {
        GFAEddItemResponse gfaEddItemResponse = new GFAEddItemResponse();
        gfaEddItemResponse.setScreenMessage("Success");
        gfaEddItemResponse.setSuccess(true);
        RetrieveItemEDDRequest retrieveItemEDDRequest = new RetrieveItemEDDRequest();
        TtitemEDDResponse ttitemEDDResponse = new TtitemEDDResponse();
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setCustomerCode("123456");
        ttitemEDDResponse.setRequestId(1);
        ttitemEDDResponse.setErrorCode("error");
        retrieveItemEDDRequest.setTtitem(Arrays.asList(ttitemEDDResponse));
        gfaEddItemResponse.setDsitem(retrieveItemEDDRequest);
        return gfaEddItemResponse;
    }
    private GFARetrieveItemResponse getGfaRetrieveItemResponse() {
        GFARetrieveItemResponse gfaRetrieveItemResponse = new GFARetrieveItemResponse();
        RetrieveItem retrieveItem = new RetrieveItem();
        Ttitem ttitem = new Ttitem();
        ttitem.setRequestId(1);
        ttitem.setItemStatus("Success");
        ttitem.setItemBarcode("123456");
        ttitem.setErrorCode("error");
        retrieveItem.setTtitem(Arrays.asList(ttitem));
        gfaRetrieveItemResponse.setSuccess(true);
        gfaRetrieveItemResponse.setDsitem(retrieveItem);
        gfaRetrieveItemResponse.setScreenMessage("Success");
        return gfaRetrieveItemResponse;
    }
}
