package org.recap.camel.requestinitialdataload;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.camel.requestinitialdataload.processor.RequestInitialDataLoadProcessor;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.camel.route.StopRouteProcessor;
import org.recap.model.ILSConfigProperties;
import org.recap.util.CommonUtil;
import org.springframework.context.ApplicationContext;

public class RequestInitialLoadRouteBuilder extends RouteBuilder {

    ApplicationContext applicationContext;
    CommonUtil commonUtil;
    String institution;
    ILSConfigProperties ilsConfigProperties;

    public RequestInitialLoadRouteBuilder(CamelContext camelContext, ApplicationContext applicationContext,CommonUtil commonUtil,ILSConfigProperties ilsConfigProperties,String institution){
        super(camelContext);
        this.applicationContext=applicationContext;
        this.commonUtil=commonUtil;
        this.institution=institution;
        this.ilsConfigProperties=ilsConfigProperties;
    }

    @Override
    public void configure() throws Exception {

        /**
         * Predicate to idenitify is the input file is gz
         */
        Predicate gzipFile = new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                String fileName = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);
                return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));
            }
        };

        from(RecapCommonConstants.SFTP + commonUtil.getFTPPropertiesMap().get("userName") + RecapCommonConstants.AT + ilsConfigProperties.getFtpRequestInitialAccessionDir()  + RecapCommonConstants.PRIVATE_KEY_FILE + commonUtil.getFTPPropertiesMap().get("privateKey") + RecapCommonConstants.KNOWN_HOST_FILE + commonUtil.getFTPPropertiesMap().get("knownHost")+ RecapConstants.ACCESSION_RR_FTP_OPTIONS+ilsConfigProperties.getRequestInitialLoadWorkdir())
                .routeId(RecapConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution)
                .noAutoStartup()
                .choice()
                .when(gzipFile)
                .unmarshal()
                .gzipDeflater()
                .log(institution+" - Request Initial load FTP Route Unzip Complete")
                .process(new StartRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution))
                .to(RecapConstants.DIRECT+ RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
                .when(body().isNull())
                .process(new StopRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution))
                .log("No File To Process "+institution+" Request Initial load")
                .otherwise()
                .process(new StartRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution))
                .to(RecapConstants.DIRECT+ RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
                .endChoice();

        from(RecapConstants.DIRECT+ RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
                .routeId(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
                .noAutoStartup()
                .log("Request data load started for "+institution)
                .split(body().tokenize("\n",1000,true))
                .unmarshal().bindy(BindyType.Csv, RequestDataLoadCSVRecord.class)
                .bean(applicationContext.getBean(RequestInitialDataLoadProcessor.class, institution), RecapConstants.PROCESS_INPUT)
                .end()
                .onCompletion()
                .process(new StopRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution));

        from(RecapConstants.REQUEST_INITIAL_LOAD_FS_FILE+ilsConfigProperties.getRequestInitialLoadFilepath()+"?delete=true")
                .routeId(RecapConstants.REQUEST_INITIAL_LOAD_FS_ROUTE+institution)
                .noAutoStartup()
                .to(RecapCommonConstants.SFTP + commonUtil.getFTPPropertiesMap().get("userName") + RecapCommonConstants.AT + ilsConfigProperties.getFtpRequestInitialAccessionErrorFile()  +  RecapCommonConstants.PRIVATE_KEY_FILE + commonUtil.getFTPPropertiesMap().get("privateKey") +  RecapCommonConstants.KNOWN_HOST_FILE + commonUtil.getFTPPropertiesMap().get("knownHost")+"&fileName=InitialRequestLoadBarcodeFail_"+institution+"_${date:now:yyyyMMdd_HHmmss}.csv")
                .onCompletion()
                .bean(applicationContext.getBean(RequestDataLoadEmailService.class, institution), RecapConstants.PROCESS_INPUT)
                .process(new StopRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_FS_ROUTE+institution))
                .log("Request data load completed for "+institution);
    }
}
