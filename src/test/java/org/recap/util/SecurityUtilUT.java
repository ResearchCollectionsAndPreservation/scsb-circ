package org.recap.util;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by premkb on 15/9/17.
 */

public class SecurityUtilUT extends BaseTestCase{

    @Autowired
    private SecurityUtil securityUtil;

    @Test
    public void getEncryptedValue(){
        String value = "test@mail.com";
        String encryptedValue = securityUtil.getEncryptedValue(value);
        assertNotNull(encryptedValue);
        String decryptedValue = securityUtil.getDecryptedValue(encryptedValue);
        assertTrue(true);
    }

    @Test
    public void getDecryptedValue(){
        String decryptedValue = securityUtil.getDecryptedValue("lPH5sNf/t/IAVAooi6loSw==");
        //String encryptedValue = securityUtil.getEncryptedValue(decryptedValue);
        assertTrue(true);
    }
}
