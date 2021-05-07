package org.recap.controller;

import org.apache.camel.CamelContext;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.camel.requestinitialdataload.RequestInitialLoadRouteBuilder;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    InstitutionDetailsRepository institutionDetailsRepository;

    @Value("${request.initial.accession}")
    String requestInitialAccessionS3Dir;

    @Value("${request.initial.load.filepath}")
    String requestInitialLoadFilepath;

    @Value("${request.initial.accession.error.file}")
    String requestInitialAccessionErrorFileS3Dir;

    @PostMapping(value = "/startRequestInitialLoad")
    public String startAccessionReconciliation() throws Exception{
        logger.info("Request Initial DataLoad Starting.....");
        List<String> allInstitutionCodeExceptHTC = institutionDetailsRepository.findAllInstitutionCodeExceptHTC();
        for (String institution : allInstitutionCodeExceptHTC) {
            camelContext.addRoutes(new RequestInitialLoadRouteBuilder(camelContext, applicationContext, commonUtil,
                    institution, requestInitialAccessionS3Dir, requestInitialLoadFilepath, requestInitialAccessionErrorFileS3Dir));
        }
        for (String institution : allInstitutionCodeExceptHTC) {
            camelContext.getRouteController().startRoute(ScsbConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution);
        }
        logger.info("After Request Initial DataLoad process : {}", camelContext.getRoutes().size());
        return ScsbCommonConstants.SUCCESS;
    }
}
