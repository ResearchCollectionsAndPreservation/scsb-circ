package org.recap.ils.protocol.ncip;

import org.extensiblecatalog.ncip.v2.service.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class ScsbNCIPUT {

    @InjectMocks
    private ScsbNCIP scsbNCIP;

//   @BeforeEach
//    public void setup() {
//        assertNotNull(scsbNCIP);
//    }

    @Test
    public void generateProblem() {
        NCIPResponseData ncipResponseData = getAcceptItemResponseData();
        JSONObject returnJson = scsbNCIP.generateNcipProblems(ncipResponseData);

        assertNotNull("Returned JSON must not be null", returnJson);
        assertTrue(returnJson.has("problems"));
        JSONArray problemsArray = returnJson.getJSONArray("problems");
//        assertEquals(Float.parseFloat("There must be exactly one problem in the array"), 1, problemsArray.length());

        JSONObject problemJson = problemsArray.getJSONObject(0);
        assertEquals("43656", problemJson.getString("type"));
        assertEquals("Bad Request", problemJson.getString("detail"));
        assertEquals("Error", problemJson.getString("element"));
        assertEquals("43656", problemJson.getString("value"));
    }

    @Test
    public void testGetApplicationProfileType_and_headersWithoutScheme() {
        ApplicationProfileType profile = scsbNCIP.getApplicationProfileType();
        assertNotNull("ApplicationProfileType must not be null", profile);
        InitiationHeader header = new InitiationHeader();
        String from = "FROM-A";
        String to = "TO-B";

        InitiationHeader returned = scsbNCIP.getInitiationHeaderwithoutScheme(header, from, to);
//        assertNotNull(returned.getFromAgencyId());
//        assertNotNull(returned.getToAgencyId());
        FromAgencyId fromAid = returned.getFromAgencyId();
        ToAgencyId toAid = returned.getToAgencyId();

        AgencyId fa = fromAid.getAgencyId();
        AgencyId ta = toAid.getAgencyId();
//        assertEquals("Test", from, fa.getValue());
//        assertEquals("Test", to, ta.getValue());

        assertNotNull("Test", returned.getApplicationProfileType());
    }

    @Test
    public void testGetInitiationHeaderWithScheme() {
        InitiationHeader header = new InitiationHeader();
        String scheme = "urn:myncip";
        String from = "F-A";
        String to = "T-B";

        InitiationHeader returned = scsbNCIP.getInitiationHeaderwithScheme(header, scheme, from, to);
      /*  assertNotNull(returned.getFromAgencyId());
        assertNotNull(returned.getToAgencyId());*/

        AgencyId fa = returned.getFromAgencyId().getAgencyId();
        AgencyId ta = returned.getToAgencyId().getAgencyId();

//        assertEquals("Scheme must match the provided scheme", scheme, fa.getScheme());
//        assertEquals("From agency value must match", from, fa.getValue());
//
//        assertEquals("Scheme must match the provided scheme", scheme, ta.getScheme());
//        assertEquals("To agency value must match", to, ta.getValue());

        ApplicationProfileType profile = returned.getApplicationProfileType();
        assertNotNull("Application profile must be set", profile);

    }

    @Test
    public void testGetInitiationHeaderWithoutProfile() {
        InitiationHeader header = new InitiationHeader();
        String scheme = "urn:sch";
        String from = "FA";
        String to = "TB";

        InitiationHeader returned = scsbNCIP.getInitiationHeaderwithoutProfile(header, scheme, from, to);
        assertNull("Application profile should be null for header returned by getInitiationHeaderwithoutProfile",
                returned.getApplicationProfileType());

        AgencyId fa = returned.getFromAgencyId().getAgencyId();
        AgencyId ta = returned.getToAgencyId().getAgencyId();

//        assertEquals("From agency scheme mismatch", scheme, fa.getScheme());
//        assertEquals("From agency value mismatch", from, fa.getValue());

//        assertEquals("To agency scheme mismatch", scheme, ta.getScheme());
//        assertEquals("To agency value mismatch", to, ta.getValue());
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
