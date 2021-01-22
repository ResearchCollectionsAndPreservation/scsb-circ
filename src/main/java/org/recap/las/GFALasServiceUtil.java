package org.recap.las;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.recap.las.model.GFAEddItemResponse;
import org.recap.las.model.GFARetrieveItemResponse;
import org.recap.las.model.TtitemEDDResponse;
import org.recap.model.gfa.Ttitem;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Created by rajeshbabuk on 21/Jan/2021
 */
@Service
@Slf4j
public class GFALasServiceUtil {

    /**
     * Process LAS Retrieve Response
     *
     * @param gfaRetrieveItemResponseParam GFARetrieveItemResponse
     * @return GFARetrieveItemResponse
     */
    protected GFARetrieveItemResponse getLASRetrieveResponse(GFARetrieveItemResponse gfaRetrieveItemResponseParam) {
        GFARetrieveItemResponse gfaRetrieveItemResponse = gfaRetrieveItemResponseParam;
        if (gfaRetrieveItemResponse != null && gfaRetrieveItemResponse.getDsitem() != null && gfaRetrieveItemResponse.getDsitem().getTtitem() != null && !gfaRetrieveItemResponse.getDsitem().getTtitem().isEmpty()) {
            List<Ttitem> titemList = gfaRetrieveItemResponse.getDsitem().getTtitem();
            for (Ttitem ttitem : titemList) {
                if (StringUtils.isNotBlank(ttitem.getErrorCode())) {
                    gfaRetrieveItemResponse.setSuccess(false);
                    gfaRetrieveItemResponse.setScreenMessage(ttitem.getErrorNote());
                } else {
                    gfaRetrieveItemResponse.setSuccess(true);
                }
            }
        } else {
            if (gfaRetrieveItemResponse == null) {
                gfaRetrieveItemResponse = new GFARetrieveItemResponse();
            }
            gfaRetrieveItemResponse.setSuccess(true);
        }
        return gfaRetrieveItemResponse;
    }

    /**
     * Process LAS EDD Response
     *
     * @param gfaEddItemResponseParam GFAEddItemResponse
     * @return GFAEddItemResponse
     */
    protected GFAEddItemResponse getLASEddResponse(GFAEddItemResponse gfaEddItemResponseParam) {
        GFAEddItemResponse gfaEddItemResponse = gfaEddItemResponseParam;
        if (gfaEddItemResponse != null && gfaEddItemResponse.getDsitem() != null && gfaEddItemResponse.getDsitem().getTtitem() != null && !gfaEddItemResponse.getDsitem().getTtitem().isEmpty()) {
            List<TtitemEDDResponse> titemList = gfaEddItemResponse.getDsitem().getTtitem();
            for (TtitemEDDResponse ttitem : titemList) {
                if (StringUtils.isNotBlank(ttitem.getErrorCode())) {
                    gfaEddItemResponse.setSuccess(false);
                    gfaEddItemResponse.setScreenMessage(ttitem.getErrorNote());
                } else {
                    gfaEddItemResponse.setSuccess(true);
                }
            }
        } else {
            if (gfaEddItemResponse == null) {
                gfaEddItemResponse = new GFAEddItemResponse();
            }
            gfaEddItemResponse.setSuccess(true);
        }
        return gfaEddItemResponse;
    }

    protected String convertJsonToString(Object objJson) {
        String strJson = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            strJson = objectMapper.writeValueAsString(objJson);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
        return strJson;
    }
}
