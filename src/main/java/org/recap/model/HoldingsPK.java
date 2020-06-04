package org.recap.model;


import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by rajeshbabuk on 15/9/16.
 */
@Setter
@Getter
public class HoldingsPK implements Serializable {
    private Integer owningInstitutionId;
    private String owningInstitutionHoldingsId;

    /**
     * Instantiates a new Holdings pk.
     */
    public HoldingsPK() {
        //Do nothing
    }

    /**
     * Instantiates a new Holdings pk.
     *
     * @param owningInstitutionId         the owning institution id
     * @param owningInstitutionHoldingsId the owning institution holdings id
     */
    public HoldingsPK(Integer owningInstitutionId, String owningInstitutionHoldingsId) {
        this.owningInstitutionId = owningInstitutionId;
        this.owningInstitutionHoldingsId = owningInstitutionHoldingsId;
    }


    @Override
    public int hashCode() {
        return Integer.valueOf(owningInstitutionId.toString()+owningInstitutionHoldingsId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HoldingsPK holdingsPK  = (HoldingsPK) obj;
        if(holdingsPK.getOwningInstitutionId().equals(owningInstitutionId) && holdingsPK.getOwningInstitutionHoldingsId().equals(owningInstitutionHoldingsId)){
            return true;
        }

        return false;
    }


