package org.recap.model;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.model.jpa.RequestInstitutionBibPK;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 23/3/17.
 */
public class RequestInstitutionBibPKUT extends BaseTestCaseUT {

    @Test
    public void testRequestInstitutionBibPK() {
        RequestInstitutionBibPK requestInstitutionBibPK = new RequestInstitutionBibPK();
        requestInstitutionBibPK.setItemId(1);
        requestInstitutionBibPK.equals(null);
        requestInstitutionBibPK.equals(requestInstitutionBibPK);
        requestInstitutionBibPK.hashCode();
        requestInstitutionBibPK.setOwningInstitutionId(1);
        RequestInstitutionBibPK requestInstitutionBibPK1 = new RequestInstitutionBibPK(2, 1);

        requestInstitutionBibPK1.equals(requestInstitutionBibPK);
        requestInstitutionBibPK1.hashCode();
        requestInstitutionBibPK.toString();
        assertNotNull(requestInstitutionBibPK.getOwningInstitutionId());
        assertNotNull(requestInstitutionBibPK.getItemId());
    }

}