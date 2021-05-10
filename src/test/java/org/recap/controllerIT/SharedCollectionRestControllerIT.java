package org.recap.controllerIT;

import org.junit.Test;
import org.recap.BaseControllerUT;
import org.recap.RecapCommonConstants;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.jpa.ItemRequestInformation;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SharedCollectionRestControllerIT extends BaseControllerUT {

    @Test
    public void deAccession() throws Exception{
        DeAccessionRequest deAccessionRequest = getDeAccessionRequest();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/deAccession")
                .requestAttr("deAccessionRequest",deAccessionRequest)).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
        assertEquals(RecapCommonConstants.SUCCESS,result);
    }

    private DeAccessionRequest getDeAccessionRequest() {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("1");
        deAccessionItem.setDeliveryLocation("PB");
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        return deAccessionRequest;
    }
}
