package org.recap.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class RequestItemValidatorControllerTest {

    @InjectMocks
    private RequestItemValidatorController requestItemValidatorController;

    @Test
    public void testGetHttpHeaders() {
        ReflectionTestUtils.invokeMethod(requestItemValidatorController, "getHttpHeaders");
    }
}
