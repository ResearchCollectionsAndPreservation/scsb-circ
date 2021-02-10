package org.recap;

import org.extensiblecatalog.ncip.v2.service.*;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class CancelRequestItemUT extends BaseTestCaseUT {

    @InjectMocks
    CancelRequestItem cancelRequestItem = new CancelRequestItem();

    @Test
    public void getCancelRequestItem() {

        CancelRequestItem cancelRequestItem = new CancelRequestItem();

        cancelRequestItem.setRequestId("1");
        cancelRequestItem.setRequestType("testAction");
        cancelRequestItem.setItemId("1");
        cancelRequestItem.setRequestTypeString("RECALL");
        cancelRequestItem.setApplicationProfileType("testProfile");
        cancelRequestItem.setUserId("14642");
        cancelRequestItem.setTitle("testTitle");
        cancelRequestItem.setFromAgency("testAgency");
        cancelRequestItem.setToAgency("testAgency");

        assertNotNull(cancelRequestItem.getRequestId());
        assertNotNull(cancelRequestItem.getItemId());
        assertNotNull(cancelRequestItem.getRequestTypeString());
        assertNotNull(cancelRequestItem.getToAgency());
        assertNotNull(cancelRequestItem.getFromAgency());
        assertNotNull(cancelRequestItem.getUserId());
        assertNotNull(cancelRequestItem.getApplicationProfileType());

    }

    @Test
    public void getCancelRequestItemResponse() {
        CancelRequestItemResponseData cancelRequestItemResponseData = getCancelRequestItemResponseData();
        cancelRequestItem.getCancelRequestItemResponse(cancelRequestItemResponseData);
    }

    @Test
    public void getCancelRequestItemResponseWithEmptyProblems() {
        CancelRequestItemResponseData cancelRequestItemResponseData = getCancelRequestItemResponseData();
        cancelRequestItemResponseData.setProblems(Collections.EMPTY_LIST);
        cancelRequestItem.getCancelRequestItemResponse(cancelRequestItemResponseData);
    }

    private CancelRequestItemResponseData getCancelRequestItemResponseData() {
        CancelRequestItemResponseData cancelRequestItemResponseData = new CancelRequestItemResponseData();
        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("24365");
        RequestId requestId = new RequestId();
        requestId.setRequestIdentifierValue("346892");
        Problem problem = getProblem();
        cancelRequestItemResponseData.setItemId(itemId);
        cancelRequestItemResponseData.setRequestId(requestId);
        cancelRequestItemResponseData.setProblems(Arrays.asList(problem));
        return cancelRequestItemResponseData;
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
