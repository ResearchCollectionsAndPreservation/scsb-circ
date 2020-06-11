package org.recap.model.jpa;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by rajeshbabuk on 15/9/16.
 */
@Data
public class HoldingsPK implements Serializable {
    private Integer owningInstitutionId;
    private String owningInstitutionHoldingsId;
}

