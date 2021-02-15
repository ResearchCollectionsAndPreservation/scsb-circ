package org.recap;

import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ProblemType;
import org.extensiblecatalog.ncip.v2.service.RecallItemResponseData;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import org.recap.ncip.RecallItem;

import static org.junit.Assert.assertNotNull;

public class RecallItemUT extends BaseTestCaseUT {

    @InjectMocks
    RecallItem recallItem;

    @Test
    public void getRecallItemResponse() {
        RecallItemResponseData recallItemResponseData = getRecallItemResponseData();
        JSONObject returnJson = recallItem.getRecallItemResponse(recallItemResponseData);
        assertNotNull(returnJson);
    }

    @Test
    public void getRecallItemResponseWithoutProblems() {
        RecallItemResponseData recallItemResponseData = getRecallItemResponseData();
        recallItemResponseData.setProblems(Collections.EMPTY_LIST);
        JSONObject returnJson = recallItem.getRecallItemResponse(recallItemResponseData);
        assertNotNull(returnJson);
    }

    private RecallItemResponseData getRecallItemResponseData() {
        RecallItemResponseData recallItemResponseData = new RecallItemResponseData();
        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("24365");
        Problem problem = getProblem();
        UserId userId = new UserId();
        userId.setUserIdentifierValue("234556");
        recallItemResponseData.setItemId(itemId);
        recallItemResponseData.setProblems(Arrays.asList(problem));
        recallItemResponseData.setUserId(userId);
        recallItemResponseData.setDateDue(new GregorianCalendar());
        return recallItemResponseData;
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
