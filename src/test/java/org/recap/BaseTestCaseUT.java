package org.recap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@TestPropertySource("classpath:application.properties")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class BaseTestCaseUT {

    @BeforeEach
    public void setup() {
        // MockitoExtension will initialize mocks for subclasses; no explicit openMocks required here
    }

    @Test
    public void contextLoads() {
    }
}
