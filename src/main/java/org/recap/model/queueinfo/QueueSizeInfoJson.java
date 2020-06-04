package org.recap.model.queueinfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by akulak on 20/10/17.
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueSizeInfoJson {

    private String value;

}
