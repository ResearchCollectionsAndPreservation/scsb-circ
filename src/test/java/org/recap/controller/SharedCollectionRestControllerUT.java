package org.recap.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recap.model.deaccession.DeAccessionItem;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.service.common.SetupDataService;
import org.recap.service.deaccession.DeAccessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Created by premkb on 26/12/16.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class SharedCollectionRestControllerUT {

    @InjectMocks
    private SharedCollectionRestController sharedCollectionRestController;

    @Mock
    DeAccessionService deAccessionService;

    @Mock
    private SetupDataService setupDataService;

    @Test
    public void deAccession() throws Exception {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        DeAccessionItem deAccessionItem = new DeAccessionItem();
        deAccessionItem.setItemBarcode("1");
        deAccessionItem.setDeliveryLocation("PB");
        deAccessionRequest.setDeAccessionItems(Arrays.asList(deAccessionItem));
        Map<String, String> map = new HashMap<>();
        map.put("Institution", "PUL");
        Mockito.when(deAccessionService.deAccession(deAccessionRequest)).thenReturn(map);
        ResponseEntity result = sharedCollectionRestController.deAccession(deAccessionRequest);
        assertNotNull(result);
    }

    @Test
    public void deAccessionNull() throws Exception {
        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        Mockito.when(deAccessionService.deAccession(deAccessionRequest)).thenReturn(null);
        ResponseEntity result = sharedCollectionRestController.deAccession(deAccessionRequest);
        assertNull(result);
    }

}
