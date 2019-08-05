package org.recap.controller;

import org.recap.ReCAPConstants;
import org.recap.service.IdentifyPendingRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/identifyPendingRequest")
public class IdentifyPendingRequestsController {

    private static final Logger logger = LoggerFactory.getLogger(IdentifyPendingRequestsController.class);

    @Autowired
    IdentifyPendingRequestService pendingRequestService;

    @PostMapping(value = "/identifyAndNotifyPendingRequests")
    public String identifyAndNotifyPendingRequests(){
        boolean identifyPendingRequest = pendingRequestService.identifyPendingRequest();
        if(identifyPendingRequest) {
            logger.info("Identified pending requests and sent an email");
            return ReCAPConstants.SUCCESS;
        }
        else {
            logger.info(ReCAPConstants.NO_PENDING_REQUESTS_FOUND);
            return ReCAPConstants.NO_PENDING_REQUESTS_FOUND;
        }
    }
}
