package org.recap.controller;

import org.apache.camel.CamelContext;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.requestinitialdataload.RequestInitialLoadRouteBuilder;
import org.recap.model.ILSConfigProperties;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.recap.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by hemalathas on 16/6/17.
 */
@RestController
@RequestMapping("/requestInitialDataLoad")
public class RequestDataLoadController {

    private static final Logger logger = LoggerFactory.getLogger(RequestDataLoadController.class);

    @Autowired
    CommonUtil commonUtil;

    @Autowired
    CamelContext camelContext;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    PropertyUtil propertyUtil;

    @Autowired
    InstitutionDetailsRepository institutionDetailsRepository;

    @PostMapping(value = "/startRequestInitialLoad")
    public String startAccessionReconcilation() throws Exception{
        logger.info("Request Initial DataLoad Starting.....");
        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        for (String institution : allInstitutionCodeExceptHTC) {
            ILSConfigProperties ilsConfigProperties = propertyUtil.getILSConfigProperties(institution);
            camelContext.addRoutes(new RequestInitialLoadRouteBuilder(camelContext,applicationContext,commonUtil,ilsConfigProperties,
                    institution));
        }
        for (String institution : allInstitutionCodeExceptHTC) {
            camelContext.getRouteController().startRoute(RecapConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution);
        }
        return RecapCommonConstants.SUCCESS;
    }
}
