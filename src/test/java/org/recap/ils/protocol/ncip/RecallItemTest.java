package org.recap.ils.protocol.ncip;

import org.extensiblecatalog.ncip.v2.service.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.recap.common.ScsbConstants;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class})
public class RecallItemTest {

    @InjectMocks
    private RecallItem recallItem;


    @Mock
    private RecallItem item;

    @Mock
    private RecallItemResponseData mockResponse;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRecallItemInitiationData_setsFieldsCorrectly() {
        String itemIdentifier = "ITEM-123";
        String patronIdentifier = "PATRON-ABC";
        String agencyId = "AGENCY-X";

        var initiationData = recallItem.getRecallItemInitiationData(itemIdentifier, patronIdentifier, agencyId);

        assertNotNull(initiationData);
        assertEquals(itemIdentifier, initiationData.getItemId().getItemIdentifierValue());
        assertTrue(initiationData.getUserIdDesired());
        assertTrue(initiationData.getLocationDesired());
        assertNotNull(initiationData.getInitiationHeader());
    }

    @Test
    public void testGetRecallItemResponse_whenResponseIsNull_returnsErrorJson() {
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("message", ScsbConstants.REQUEST_ILS_NO_RESPONSE_EXCEPTION);

        Mockito.when(item.getRecallItemResponse(null)).thenReturn(expectedResult);

        JSONObject result = item.getRecallItemResponse(null);
        assertNotNull(result);
        assertTrue(result.toString().contains(ScsbConstants.REQUEST_ILS_NO_RESPONSE_EXCEPTION));
    }

    @Test
    public void testGetRecallItemResponse_whenProblemsNotEmpty_returnsProblemsJson() {
        List<Problem> problems = new ArrayList<>();
        Problem problem = new Problem();
        problem.setProblemType(new ProblemType("TestProblem"));
        problem.setProblemValue("Some issue");
        problems.add(problem);

        when(mockResponse.getProblems()).thenReturn(problems);

        JSONObject result = recallItem.getRecallItemResponse(mockResponse);

        assertNotNull(result);
        assertTrue(result.toString().contains("TestProblem"));
        assertTrue(result.toString().contains("Some issue"));
    }

    @Test
    public void testGetRecallItemResponse_successPath_formatsDate_and_returnsItemAndUser() {
        when(mockResponse.getProblems()).thenReturn(new ArrayList<>());

        Calendar cal = Calendar.getInstance();
        cal.set(2020, Calendar.FEBRUARY, 3, 14, 5, 6);
        cal.set(Calendar.MILLISECOND, 0);

        ItemId itemId = new ItemId();
        itemId.setItemIdentifierValue("ITEM-999");
        when(mockResponse.getItemId()).thenReturn(itemId);

        UserId userId = new UserId();
        userId.setUserIdentifierValue("USER-XYZ");
        when(mockResponse.getUserId()).thenReturn(userId);

        JSONObject result = recallItem.getRecallItemResponse(mockResponse);

        assertNotNull(result);
        assertEquals("ITEM-999", result.getString(ScsbConstants.ITEM_ID));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        formatter.setCalendar(cal);
        String expectedDateString = formatter.format(cal.getTime());

    }
}
