package org.recap;

import org.extensiblecatalog.ncip.v2.service.AcceptItemResponseData;
import org.extensiblecatalog.ncip.v2.service.ItemId;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ProblemType;
import org.extensiblecatalog.ncip.v2.service.RequestId;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.ncip.AcceptItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class AcceptItemUT extends BaseTestCaseUT {

    @InjectMocks
    AcceptItem acceptItem = new AcceptItem();

    @Test
    public void setRequestActionType() {
        String action = "testAction";
        AcceptItem acceptItem1 = acceptItem.setRequestActionType(action);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setApplicationProfileType() {
        String profileType = "testProfile";
        AcceptItem acceptItem1 = acceptItem.setApplicationProfileType(profileType);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setItemId() {
        String itemId = "14546";
        AcceptItem acceptItem1 = acceptItem.setItemId(itemId);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setRequestId() {
        String requestId = "1";
        AcceptItem acceptItem1 = acceptItem.setRequestId(requestId);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setUserId() {
        String userId = "1244566";
        AcceptItem acceptItem1 = acceptItem.setUserId(userId);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setPickupLocation() {
        String pickupLocation = "1244566";
        AcceptItem acceptItem1 = acceptItem.setPickupLocation(pickupLocation);
        assertNotNull(acceptItem1);
    }

    @Test
    public void addBibliographicDescription() {
        String bibliographicDescriptionType = "oldBib";
        String value = "254667";
        AcceptItem acceptItem1 = acceptItem.addBibliographicDescription(bibliographicDescriptionType, value);
        assertNotNull(acceptItem1);
    }

    @Test
    public void addItemDescription() {
        String itemDescriptionType = "oldItem";
        String value = "254667";
        AcceptItem acceptItem1 = acceptItem.addItemDescription(itemDescriptionType, value);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setToAgency() {
        String toAgency = "testAgency";
        AcceptItem acceptItem1 = acceptItem.setToAgency(toAgency);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setFromAgency() {
        String fromAgency = "testAgency";
        AcceptItem acceptItem1 = acceptItem.setFromAgency(fromAgency);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setRequestedActionTypeString() {
        String actionType = "testActionType";
        AcceptItem acceptItem1 = acceptItem.setRequestedActionTypeString(actionType);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setTitle() {
        String title = "testTitle";
        AcceptItem acceptItem1 = acceptItem.setTitle(title);
        assertNotNull(acceptItem1.getTitle());
        assertNotNull(acceptItem1);
    }

    @Test
    public void setAuthor() {
        String author = "testAuthor";
        AcceptItem acceptItem1 = acceptItem.setAuthor(author);
        assertNotNull(acceptItem.getAuthor());
        assertNotNull(acceptItem1);
    }

    @Test
    public void setPublisher() {
        String publisher = "testPublisher";
        AcceptItem acceptItem1 = acceptItem.setPublisher(publisher);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setPublicationDate() {
        String pubDate = new Date().toString();
        AcceptItem acceptItem1 = acceptItem.setPublicationDate(pubDate);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setIsbn() {
        String isbn = "testIsbn";
        AcceptItem acceptItem1 = acceptItem.setIsbn(isbn);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setIssn() {
        String issn = "testIssn";
        AcceptItem acceptItem1 = acceptItem.setIssn(issn);
        assertNotNull(acceptItem1);
    }

    @Test
    public void setCallNumber() {
        String callNumber = "testCallNumber";
        AcceptItem acceptItem1 = acceptItem.setCallNumber(callNumber);
        assertNotNull(acceptItem1);
        assertNotNull(acceptItem1.getCallNo());
    }

    @Test
    public void getAcceptItemResponse() {
        AcceptItemResponseData acceptItemResponseData = getAcceptItemResponseData();
        JSONObject returnJson = acceptItem.getAcceptItemResponse(acceptItemResponseData);
        assertNotNull(returnJson);
    }

    @Test
    public void getAcceptItemResponseWithEmptyProblems() {
        AcceptItemResponseData acceptItemResponseData = getAcceptItemResponseData();
        acceptItemResponseData.setProblems(Collections.EMPTY_LIST);
        JSONObject returnJson = acceptItem.getAcceptItemResponse(acceptItemResponseData);
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
