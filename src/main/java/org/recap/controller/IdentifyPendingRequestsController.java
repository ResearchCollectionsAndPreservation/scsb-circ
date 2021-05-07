package org.recap.controller;

import org.recap.ScsbConstants;
import org.recap.ScsbCommonConstants;
import org.recap.service.IdentifyPendingRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identifyPendingRequest")
public class IdentifyPendingRequestsController {

    @Autowired
    IdentifyPendingRequestService pendingRequestService;

    @PostMapping(value = "/identifyAndNotifyPendingRequests")
    public String identifyAndNotifyPendingRequests(){
        boolean identifyPendingRequest = pendingRequestService.identifyPendingRequest();
        if(identifyPendingRequest) {
            return ScsbCommonConstants.SUCCESS;
        }
        else {
            return ScsbConstants.NO_PENDING_REQUESTS_FOUND;
        }
    }
}
