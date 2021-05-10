package org.recap.controllerIT;

import org.junit.Test;
import org.recap.BaseControllerUT;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemControllerIT extends BaseControllerUT {

    @Test
    public void findByBarcodeIn() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get("/item/findByBarcodeIn")
                .param("itemBarcode","CU69606943")).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }
}
