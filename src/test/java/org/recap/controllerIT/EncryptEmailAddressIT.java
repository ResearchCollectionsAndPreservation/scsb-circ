package org.recap.controllerIT;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.recap.BaseControllerUT;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EncryptEmailAddressIT extends BaseControllerUT {

    @Autowired
    private RequestItemDetailsRepository requestItemDetailsRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void startEncryptEmailAddress() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/encryptEmailAddress/startEncryptEmailAddress")
                ).andExpect(status().isOk())
                .andReturn();
        String result = mvcResult.getResponse().getContentAsString();
        assertNotNull(result);
        int status = mvcResult.getResponse().getStatus();
        assertTrue(status == 200);
    }
}
