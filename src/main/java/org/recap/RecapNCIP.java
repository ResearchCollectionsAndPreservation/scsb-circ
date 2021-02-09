package org.recap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.extensiblecatalog.ncip.v2.service.ApplicationProfileType;
import org.extensiblecatalog.ncip.v2.service.NCIPInitiationData;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.recap.ils.NCIPToolKitUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Slf4j
public class RecapNCIP {

    public JSONObject generateProblem(NCIPResponseData responseData) {
        JSONObject returnJson = new JSONObject();
        JSONArray array = new JSONArray();
        Iterator<Problem> i = responseData.getProblems().iterator();
        while (i.hasNext()) {
            Problem ncipProblem = (Problem)i.next();
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

    public JSONObject generateNcipProblems(NCIPResponseData responseData) {
        JSONObject returnJson = new JSONObject();
        JSONArray array = new JSONArray();
        Iterator<Problem> i = responseData.getProblems().iterator();
        while (i.hasNext()) {
            Problem ncipProblem = (Problem)i.next();
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
    public String getRequestBody(NCIPToolKitUtil ncipToolkitUtil, NCIPInitiationData ncipInitiationData) throws Exception
    {
        String requestBody = "";
            InputStream requestMessageStream = ncipToolkitUtil.translator.createInitiationMessageStream(ncipToolkitUtil.serviceContext, ncipInitiationData);
            requestBody = IOUtils.toString(requestMessageStream, StandardCharsets.UTF_8);
        return requestBody;
    }

    public ApplicationProfileType getApplicationProfileType() {
        return new ApplicationProfileType(null, RecapConstants.AGENCY_ID_SCSB);
    }

}
