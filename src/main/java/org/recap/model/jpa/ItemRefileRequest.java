package org.recap.model.jpa;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by sudhishk on 15/12/16.
 */
@Setter
@Getter
public class ItemRefileRequest {
    private List<String> itemBarcodes;
    private List<Integer> requestIds;

}
