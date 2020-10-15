package org.recap.model.report;

import org.junit.Test;
import org.recap.model.deaccession.DeAccessionDBResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class GfaDeaccessionInfoUT {

    @Test
    public void getGfaDeaccessionInfo(){
        GfaDeaccessionInfo gfaDeaccessionInfo = new GfaDeaccessionInfo();
        gfaDeaccessionInfo.setDeAccessionDBResponseEntities(Arrays.asList(new DeAccessionDBResponseEntity()));
        gfaDeaccessionInfo.setUsername("test");
        gfaDeaccessionInfo.toString();
        gfaDeaccessionInfo.canEqual(gfaDeaccessionInfo);
        gfaDeaccessionInfo.equals(gfaDeaccessionInfo);
        gfaDeaccessionInfo.hashCode();
        assertNotNull(gfaDeaccessionInfo.getDeAccessionDBResponseEntities());
        assertNotNull(gfaDeaccessionInfo.getUsername());
    }
}
