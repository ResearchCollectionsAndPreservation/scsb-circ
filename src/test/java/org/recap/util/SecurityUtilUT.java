package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by premkb on 15/9/17.
 */

public class SecurityUtilUT extends BaseTestCaseUT {

    @InjectMocks
    private SecurityUtil securityUtil;

    private String testKey = "testkey";

    @Before
    public void setup() {
        ReflectionTestUtils.setField(securityUtil, "encryptionSecretKey", testKey);
    }

    @Test
    public void getEncryptedValue() {
        String value = "test@mail.com";
        String encryptedValue = securityUtil.getEncryptedValue(value);
        String decryptedValue = securityUtil.getDecryptedValue(encryptedValue);
    }
}
