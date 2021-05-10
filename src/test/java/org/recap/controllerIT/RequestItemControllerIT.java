package org.recap.controllerIT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseControllerUT;
import org.recap.ScsbConstants;
import org.recap.model.jpa.ItemRequestInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RequestItemControllerIT extends BaseControllerUT {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void checkoutItem() throws Exception{
        ItemRequestInformation itemRequestInformation = getItemRequestInformation();
        MvcResult mvcResult = this.mockMvc.perform(post("/requestItem/checkoutItem")
                .content(objectMapper.writeValueAsString(itemRequestInformation))).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        //assertEquals(ScsbConstants.,result);
    }

    private ItemRequestInformation getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setPatronBarcode("45678915");
        itemRequestInformation.setRequestType("RETRIEVAL");
        itemRequestInformation.setDeliveryLocation("PB");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setRequestingInstitution("PUL");
        return itemRequestInformation;
    }
}
