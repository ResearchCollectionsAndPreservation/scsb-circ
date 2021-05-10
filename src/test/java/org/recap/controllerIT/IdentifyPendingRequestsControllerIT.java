package org.recap.controllerIT;

import org.junit.Test;
import org.recap.BaseControllerUT;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IdentifyPendingRequestsControllerIT extends BaseControllerUT {

    @Test
    public void identifyAndNotifyPendingRequests() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(post("/identifyPendingRequest/identifyAndNotifyPendingRequests")
        ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertEquals("There are no pending requests or no new pending request",result);
    }
}
