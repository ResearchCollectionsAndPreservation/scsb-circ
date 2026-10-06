package org.recap.ils.protocol.ncip;

import org.extensiblecatalog.ncip.v2.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class RequestItemTest {

    @Spy
    @InjectMocks
    private RequestItem requestItem;

    @Mock
    private NCIPResponseData responseData;

    @Test
    public void ctor_initializesOptionalMaps_andFluentSettersWork() {
        RequestItem r = new RequestItem()
                .setRequestActionType("Page")
                .setRequestedActionTypeString("Hold")
                .setApplicationProfileType("profile")
                .setItemId("I1")
                .setRequestId("R1")
                .setUserId("U1")
                .setPickupLocation("PL1")
                .setToAgency("TA")
                .setFromAgency("FA")
                .addBibliographicDescription(org.recap.common.ScsbConstants.TITLE, "MyTitle")
                .addItemDescription(org.recap.common.ScsbConstants.CALL_NUMBER, "QA76.73")
                .setTitle("T2")
                .setAuthor("A2")
                .setPublisher("P2")
                .setPublicationDate("2024")
                .setIsbn("978-1-4028-9462-6")
                .setIssn("2049-3630")
                .setCallNumber("QA999.99");

        HashMap<String, HashMap<String, String>> maps = r.getItemOptionalFields();
        assertNotNull(maps);
        assertTrue(maps.containsKey(org.recap.common.ScsbConstants.BIBLIOGRAPHIC_DESCRIPTION));
        assertTrue(maps.containsKey(org.recap.common.ScsbConstants.ITEM_DESCRIPTION));

        assertEquals("A2", r.getAuthor());
        assertEquals("T2", r.getTitle());
        assertEquals("QA999.99", r.getCallNo());

        assertEquals("I1", r.getItemIdString());
        assertEquals("R1", r.getRequestIdString());
        assertEquals("U1", r.getUseridString());
        assertEquals("PL1", r.getPickupLocationString());
        assertEquals("TA", r.getToAgency());
        assertEquals("FA", r.getFromAgency());
        assertEquals("Hold", r.getRequestedActionTypeString());
        assertEquals("profile", r.getApplicationProfileTypeString());
    }

    @Test
    public void getRequestItemInitiationData_populatesFields_usingSpyForHeaderHelper() {
        RequestItem requestItem = spy(new RequestItem());

        String itemIdentifier = "ITEM-123";
        Integer requestId = 42;
        String patronId = "PATRON-999";
        String owningInstItemId = "OWN-ABC";
        String title = "Some Title";
        String author = "Some Author";
        String pickup = "MAIN-CIRC";
        String callNumber = "QA10";
        String ncipAgencyId = "REQ-AGENCY";
        String ncipScheme = "Test";
        String itemAgencyId = "OWN-AGENCY";

        InitiationHeader stubHeader = new InitiationHeader();
        doReturn(stubHeader)
                .when(requestItem)
                .getInitiationHeaderwithoutProfile(
                        any(InitiationHeader.class),
                        eq(ncipScheme),
                        eq(itemAgencyId),
                        eq(ncipAgencyId));

        try (MockedConstruction<RequestScopeType> scopeMock =
                     mockConstruction(RequestScopeType.class,
                             (mock, ctx) -> {
                                 lenient().doReturn("Item").when(mock).toString();
                                 doReturn("Item").when(mock).getValue();
                             });
             MockedConstruction<RequestType> typeMock =
                     mockConstruction(RequestType.class,
                             (mock, ctx) -> {
                                 lenient().doReturn("Page").when(mock).toString();
                                 doReturn("Page").when(mock).getValue();
                             });
             MockedConstruction<PickupLocation> pickupMock =
                     mockConstruction(PickupLocation.class,
                             (mock, ctx) -> {
                                 lenient().doReturn(pickup).when(mock).toString();
                                 doReturn(pickup).when(mock).getValue();
                             });
             MockedConstruction<BibliographicId> bibIdMock =
                     mockConstruction(BibliographicId.class,
                             (mock, ctx) -> {
                                 BibliographicRecordId recId = mock(BibliographicRecordId.class);
                                 lenient().doReturn("").when(recId).getBibliographicRecordIdentifier();
                                 lenient().doNothing().when(recId).setBibliographicRecordIdentifier(any());
                                 lenient().doReturn(recId).when(mock).getBibliographicRecordId();
                                 lenient().doNothing().when(mock).setBibliographicRecordId(any());
                             });
             MockedConstruction<BibliographicRecordId> bibRecordIdMock =
                     mockConstruction(BibliographicRecordId.class,
                             (mock, ctx) -> {
                                 lenient().doReturn("").when(mock).getBibliographicRecordIdentifier();
                                 lenient().doNothing().when(mock).setBibliographicRecordIdentifier(any());
                             })) {

            RequestItemInitiationData data = requestItem.getRequestItemInitiationData(
                    itemIdentifier, requestId, patronId, owningInstItemId,
                    title, author, pickup, callNumber,
                    ncipAgencyId, ncipScheme, itemAgencyId);

            assertNotNull(data);
            assertNotNull(data.getInitiationHeader());
            assertNotNull(data.getInitiationHeader().getApplicationProfileType());
            assertEquals(itemAgencyId,
                    data.getInitiationHeader().getApplicationProfileType().getValue());

            assertNotNull(data.getUserId());
            assertEquals("barcode",
                    data.getUserId().getUserIdentifierType().getValue());
            assertEquals(patronId,
                    data.getUserId().getUserIdentifierValue());

            assertNotNull(data.getRequestId());
            assertEquals(itemIdentifier,
                    data.getRequestId().getRequestIdentifierValue());

            assertNotNull(data.getBibliographicIds());
            assertEquals(1, data.getBibliographicIds().size());
            assertNotNull(data.getBibliographicIds().get(0).getBibliographicRecordId());
            assertEquals("",
                    data.getBibliographicIds()
                            .get(0)
                            .getBibliographicRecordId()
                            .getBibliographicRecordIdentifier());

            assertNotNull(data.getRequestType());
            assertEquals("Page", data.getRequestType().getValue());

            assertNotNull(data.getRequestScopeType());
            assertEquals("Item", data.getRequestScopeType().getValue());

            assertNotNull(data.getPickupLocation());
            assertEquals(pickup, data.getPickupLocation().getValue());
        }
    }

    @Test
    public void getRequestItemResponse_errorBranch_usesGenerateNcipProblems() {
        List<Problem> problems = new ArrayList<>();
        problems.add(new Problem(new ProblemType("ValidationError"), null, "Missing something"));
        when(responseData.getProblems()).thenReturn(problems);

        doAnswer(invocation -> {
            NCIPResponseData arg = invocation.getArgument(0);
            JSONObject j = new JSONObject();
            j.put("Success", false);
            j.put("ProblemsCount", arg.getProblems().size());
            return j;
        }).when(requestItem).generateNcipProblems(any(NCIPResponseData.class));

        JSONObject out = requestItem.getRequestItemResponse(responseData);
        assertFalse(out.getBoolean("Success"));
        assertEquals(1, out.getInt("ProblemsCount"));
    }

    @Test
    public void getRequestItemResponse_successBranch_onlySuccessTrue() {
        when(responseData.getProblems()).thenReturn(Collections.emptyList());
        JSONObject out = requestItem.getRequestItemResponse(responseData);
        assertTrue(out.getBoolean("Success"));
        assertEquals(1, out.length());
    }
}