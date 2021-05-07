package org.recap.controller;

import org.apache.camel.ProducerTemplate;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.service.ActiveMqQueuesInfo;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by angelind on 22/8/17.
 */
@RestController
@RequestMapping("/notifyPendingRequest")
public class EmailPendingRequestJobController {

    private static final Logger logger = LoggerFactory.getLogger(EmailPendingRequestJobController.class);

    /**
     * The Producer template.
     */
    @Autowired
    ProducerTemplate producerTemplate;

    /**
     * The Pending request limit.
     */
    @Value("${request.pending.limit}")
    Integer pendingRequestLimit;

    @Autowired
    private ActiveMqQueuesInfo activemqQueuesInfo;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * Send email for pending request string.
     *
     * @return the string
     * @throws Exception the exception
     */
    @PostMapping(value = "/sendEmailForPendingRequest")
    public String sendEmailForPendingRequest() throws Exception {
        for (String imsLocationCode : commonUtil.findAllImsLocationCodeExceptUN()) {
            Integer pendingRequests = activemqQueuesInfo.getActivemqQueuesInfo("las" + imsLocationCode + ScsbConstants.OUTGOING_QUEUE_SUFFIX);
            if (pendingRequests >= pendingRequestLimit) {
                logger.info("Pending Request at {} : {}", imsLocationCode, pendingRequests);
                EmailPayLoad emailPayLoad = new EmailPayLoad();
                emailPayLoad.setPendingRequestLimit(String.valueOf(pendingRequestLimit));
                emailPayLoad.setTo(propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.email.assist.to"));
                producerTemplate.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.EMAIL_BODY_FOR, ScsbConstants.EMAIL_HEADER_REQUEST_PENDING);
            }
        }
        return ScsbCommonConstants.SUCCESS;
    }

}
