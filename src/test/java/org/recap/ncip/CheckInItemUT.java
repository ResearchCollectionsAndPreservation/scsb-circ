package org.recap.ncip;

import org.extensiblecatalog.ncip.v2.service.CheckInItemResponseData;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.ItemOptionalFields;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ProblemType;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertNotNull;

public class CheckInItemUT extends BaseTestCaseUT {

    @InjectMocks
    CheckinItem checkinItem;

    @Test
    public void getCheckInResponse() {
        CheckInItemResponseData checkInItemResponseData = getCheckInItemResponseData();
        JSONObject returnJson = checkinItem.getCheckInResponse(checkInItemResponseData);
        assertNotNull(returnJson);
    }

    @Test
    public void getCheckInResponseWithoutProblems() {
        CheckInItemResponseData checkInItemResponseData = getCheckInItemResponseData();
        checkInItemResponseData.setProblems(Collections.EMPTY_LIST);
        JSONObject returnJson = checkinItem.getCheckInResponse(checkInItemResponseData);
        assertNotNull(returnJson);
    }

    private CheckInItemResponseData getCheckInItemResponseData() {
        CheckInItemResponseData checkInItemResponseData = new CheckInItemResponseData();
        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("24365");
        Problem problem = getProblem();
        ItemOptionalFields itemOptionalFields = new ItemOptionalFields();
        itemOptionalFields.setDateDue(new GregorianCalendar());
        checkInItemResponseData.setItemId(itemId);
        checkInItemResponseData.setProblems(Arrays.asList(problem));
        checkInItemResponseData.setItemOptionalFields(itemOptionalFields);
        return checkInItemResponseData;
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
