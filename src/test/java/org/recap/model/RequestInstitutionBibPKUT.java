package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.model.jpa.RequestInstitutionBibPK;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 23/3/17.
 */
public class RequestInstitutionBibPKUT{

    @Test
    public void testRequestInstitutionBibPK(){
        RequestInstitutionBibPK requestInstitutionBibPK = new RequestInstitutionBibPK();
        requestInstitutionBibPK.setItemId(1);
        requestInstitutionBibPK.equals(requestInstitutionBibPK);
        requestInstitutionBibPK.hashCode();
        requestInstitutionBibPK.setOwningInstitutionId(1);
        RequestInstitutionBibPK requestInstitutionBibPK1 = new RequestInstitutionBibPK(1,1);
        requestInstitutionBibPK1.equals(requestInstitutionBibPK);
        requestInstitutionBibPK1.hashCode();
        requestInstitutionBibPK.toString();
        assertNotNull(requestInstitutionBibPK.getOwningInstitutionId());
        assertNotNull(requestInstitutionBibPK.getItemId());
    }

}