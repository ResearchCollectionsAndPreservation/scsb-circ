package org.recap;

import org.extensiblecatalog.ncip.v2.service.AcceptItemResponseData;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ProblemType;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.ncip.RecapNCIP;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class RecapNCIPUT extends BaseTestCaseUT {

    @InjectMocks
    RecapNCIP recapNCIP;

    @Test
    public void generateProblem() {
        NCIPResponseData ncipResponseData = getAcceptItemResponseData();
        JSONObject returnJson = recapNCIP.generateNcipProblems(ncipResponseData);
        assertNotNull(returnJson);
    }

    private AcceptItemResponseData getAcceptItemResponseData() {
        AcceptItemResponseData acceptItemResponseData = new AcceptItemResponseData();
        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("24365");
        RequestId requestId = new RequestId();
        requestId.setRequestIdentifierValue("346892");
        Problem problem = getProblem();
        acceptItemResponseData.setItemId(itemId);
        acceptItemResponseData.setRequestId(requestId);
        acceptItemResponseData.setProblems(Arrays.asList(problem));
        return acceptItemResponseData;
    }

    private Problem getProblem() {
        Problem problem = new Problem();
        ProblemType problemType = new ProblemType("43656");
        problem.setProblemType(problemType);
        problem.setProblemDetail("Bad Request");
        problem.setProblemValue("43656");
        problem.setProblemElement("Error");
        return problem;
    }
}
