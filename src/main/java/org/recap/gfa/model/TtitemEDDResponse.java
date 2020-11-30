package org.recap.gfa.model;

import lombok.Data;

/**
 * Created by sudhishk on 27/1/17.
 */
@Data
public class TtitemEDDResponse {
    private String itemBarcode;
    private String customerCode;
    private Integer requestId;
    private String requestor;
    private String requestorFirstName;
    private String requestorLastName;
    private String requestorMiddleName;
    private String requestorEmail;
    private String requestorOther;
    private String biblioTitle;
    private String biblioLocation;
    private String biblioAuthor;
    private String biblioVolume;
    private String biblioCode;
    private String articleTitle;
    private String articleAuthor;
    private String articleVolume;
    private String articleIssue;
    private String articleDate;
    private String startPage;
    private String endPage;
    private String pages;
    private String other;
    private String priority;
    private String notes;
    private String requestDate;
    private String requestTime;
    private String errorCode;
    private String errorNote;
}
