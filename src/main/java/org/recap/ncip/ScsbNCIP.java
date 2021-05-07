package org.recap.ncip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.extensiblecatalog.ncip.v2.service.AgencyId;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.FromAgencyId;
import org.extensiblecatalog.ncip.v2.service.InitiationHeader;
import org.extensiblecatalog.ncip.v2.service.NCIPInitiationData;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ServiceException;
import org.extensiblecatalog.ncip.v2.service.ToAgencyId;
import org.extensiblecatalog.ncip.v2.service.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.recap.ScsbConstants;
import org.recap.ils.NCIPToolKitUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class ScsbNCIP {

    public JSONObject generateNcipProblems(NCIPResponseData responseData) {
        JSONObject returnJson = new JSONObject();
        JSONArray array = new JSONArray();
        Iterator<Problem> i = responseData.getProblems().iterator();
        while (i.hasNext()) {
            Problem ncipProblem = i.next();
            JSONObject problem = new JSONObject();
            ncipProblem.getProblemDetail();
            problem.put("type",ncipProblem.getProblemType().getValue());
            problem.put("detail",ncipProblem.getProblemDetail());
            problem.put("element",ncipProblem.getProblemElement());
            problem.put("value", ncipProblem.getProblemValue());
            array.put(problem);
        }
        returnJson.put("problems", array);
        return returnJson;
    }
    public String getRequestBody(NCIPToolKitUtil ncipToolkitUtil, NCIPInitiationData ncipInitiationData) throws ServiceException, ValidationException, IOException
    {
        String requestBody = "";
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, ncipInitiationData);
            requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);
        return requestBody;
    }

    public ApplicationProfileType getApplicationProfileType() {
        return new ApplicationProfileType(null, ScsbConstants.AGENCY_ID_SCSB);
    }

    public InitiationHeader getInitiationHeaderwithoutScheme(InitiationHeader initiationHeader, String fromAgency, String toAgency){
        initiationHeader.setApplicationProfileType(getApplicationProfileType());
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(fromAgency));
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(toAgency));
        initiationHeader.setFromAgencyId(fromAgencyId);
        initiationHeader.setToAgencyId(toAgencyId);
        return initiationHeader;
    }

    public InitiationHeader getInitiationHeaderwithScheme(InitiationHeader initiationHeader, String ncipScheme, String fromAgency, String toAgency){
        initiationHeader.setApplicationProfileType(getApplicationProfileType());
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(ncipScheme, fromAgency));
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(ncipScheme, toAgency));
        initiationHeader.setFromAgencyId(fromAgencyId);
        initiationHeader.setToAgencyId(toAgencyId);
        return initiationHeader;
    }

    public InitiationHeader getInitiationHeaderwithoutProfile(InitiationHeader initiationHeader, String ncipScheme, String fromAgency, String toAgency){
        FromAgencyId fromAgencyId = new FromAgencyId();
        fromAgencyId.setAgencyId(new AgencyId(ncipScheme, fromAgency));
        ToAgencyId toAgencyId = new ToAgencyId();
        toAgencyId.setAgencyId(new AgencyId(ncipScheme, toAgency));
        initiationHeader.setFromAgencyId(fromAgencyId);
        initiationHeader.setToAgencyId(toAgencyId);
        return initiationHeader;
    }
}
