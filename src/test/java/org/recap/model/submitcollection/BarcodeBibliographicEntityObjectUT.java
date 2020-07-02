package org.recap.model.submitcollection;

import org.junit.Test;
import org.recap.BaseTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BarcodeBibliographicEntityObjectUT extends BaseTestCase {
    @Test
    public void BarcodeBibliographicEntityObject() {
        BarcodeBibliographicEntityObject barcodeBibliographicEntityObject = new BarcodeBibliographicEntityObject();
        barcodeBibliographicEntityObject.setBarcode("1234");
        barcodeBibliographicEntityObject.setOwningInstitutionBibId("1234");
        barcodeBibliographicEntityObject.setBibliographicEntity(null);
        assertNull(barcodeBibliographicEntityObject.getBibliographicEntity());
        assertNotNull(barcodeBibliographicEntityObject.getBarcode());
        assertNotNull(barcodeBibliographicEntityObject.getOwningInstitutionBibId());
        assertNotNull(barcodeBibliographicEntityObject.hashCode());
    }
}
