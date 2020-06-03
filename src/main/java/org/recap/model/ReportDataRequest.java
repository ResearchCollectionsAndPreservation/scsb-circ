package org.recap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by premkb on 23/3/17.
 */
@Setter
@Getter
public class ReportDataRequest {

    private String fileName;
    private String institutionCode;
    private String reportType;
    private String transmissionType;

}
