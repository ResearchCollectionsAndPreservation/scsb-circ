package org.recap.gfa.model;

import lombok.Data;

import java.util.List;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
@Data
public class GFALasStatusCheckRequest {
    private List<GFALasStatus> lasStatus;
}
