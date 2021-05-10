package org.recap.controllerIT;

import org.junit.Test;
import org.recap.BaseControllerUT;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmailPendingRequestJobControllerIT extends BaseControllerUT {

    @Test
    public void sendEmailForPendingRequest() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(post("/notifyPendingRequest/sendEmailForPendingRequest")
        ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }
}
