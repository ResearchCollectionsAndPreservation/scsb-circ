package org.recap.camel.requestinitialdataload;

import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.recap.RecapConstants;
import org.recap.camel.requestinitialdataload.processor.RequestInitialDataLoadProcessor;
import org.recap.camel.route.StartRouteProcessor;
import org.recap.camel.route.StopRouteProcessor;
import org.recap.util.CommonUtil;
import org.springframework.context.ApplicationContext;
import org.apache.camel.component.aws.s3.S3Constants;

public class RequestInitialLoadRouteBuilder extends RouteBuilder {

    ApplicationContext applicationContext;
    CommonUtil commonUtil;
    String institution;
    String requestInitialAccessionS3Dir;
    String requestInitialLoadFilepath;
    String requestInitialAccessionErrorFileS3Dir;

    public RequestInitialLoadRouteBuilder(CamelContext camelContext, ApplicationContext applicationContext, CommonUtil commonUtil, String institution, String requestInitialAccessionS3Dir, String requestInitialLoadFilepath, String requestInitialAccessionErrorFileS3Dir){
        super(camelContext);
        this.applicationContext=applicationContext;
        this.commonUtil=commonUtil;
        this.institution=institution;
        this.requestInitialAccessionS3Dir=requestInitialAccessionS3Dir;
        this.requestInitialLoadFilepath=requestInitialLoadFilepath;
        this.requestInitialAccessionErrorFileS3Dir=requestInitialAccessionErrorFileS3Dir;
    }

    @Override
    public void configure() throws Exception {

        /**
         * Predicate to idenitify is the input file is gz
         */
        Predicate gzipFile = exchange -> {
            if (exchange.getIn().getHeader("CamelAwsS3Key") != null) {
                String fileName = exchange.getIn().getHeader("CamelAwsS3Key").toString();
                return StringUtils.equalsIgnoreCase("gz", FilenameUtils.getExtension(fileName));
            } else
                return false;
        };

        from("aws-s3://{{scsbBucketName}}?prefix="+requestInitialAccessionS3Dir + institution + "/&deleteAfterRead=false&sendEmptyMessageWhenIdle=true&autocloseBody=false&region={{awsRegion}}&accessKey=RAW({{awsAccessKey}})&secretKey=RAW({{awsAccessSecretKey}})")
                .routeId(RecapConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution)
                .noAutoStartup()
                .choice()
                .when(gzipFile)
                .unmarshal()
                .gzipDeflater()
                .log(institution+" - Request Initial load S3 Route Unzip Complete")
                .process(new StartRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution))
                .to(RecapConstants.DIRECT+ RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
                .when(body().isNull())
                .process(new StopRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_FTP_ROUTE+institution))
                .log("No File To Process "+institution+" Request Initial load")
                //Below portion is commented since otherwise block is getting triggered always - TO DO - to fix the compatibility for s3
//                .otherwise()
//                .process(new StartRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution))
//                .to(RecapConstants.DIRECT+ RecapConstants.REQUEST_INITIAL_LOAD_DIRECT_ROUTE+institution)
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


        from(RecapConstants.REQUEST_INITIAL_LOAD_FS_FILE+ requestInitialLoadFilepath + "/" + institution +"?delete=true")
                .routeId(RecapConstants.REQUEST_INITIAL_LOAD_FS_ROUTE+institution)
                .noAutoStartup()
                .setHeader(S3Constants.KEY, simple(requestInitialAccessionErrorFileS3Dir + institution + "/InitialRequestLoadBarcodeFail_"+ institution +"_${date:now:yyyyMMdd_HHmmss}.csv"))
                .to(RecapConstants.SCSB_CAMEL_S3_TO_ENDPOINT)
                .onCompletion()
                .bean(applicationContext.getBean(RequestDataLoadEmailService.class, institution), RecapConstants.PROCESS_INPUT)
                .process(new StopRouteProcessor(RecapConstants.REQUEST_INITIAL_LOAD_FS_ROUTE+institution))
                .log("Request data load completed for "+institution);
    }
}
