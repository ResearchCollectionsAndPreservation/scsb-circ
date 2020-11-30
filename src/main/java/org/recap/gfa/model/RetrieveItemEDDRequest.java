package org.recap.gfa.model;

import lombok.Data;

import java.util.List;

/**
 * Created by sudhishk on 27/1/17.
 */
@Data
public class RetrieveItemEDDRequest {
    private List<TtitemEDDResponse> ttitem;
}
