package org.recap.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.FileUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by chenchulakshmig on 13/9/16.
 */
@Component
public class EmailRouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EmailRouteBuilder.class);

    private String emailBodyLasStatus;
    private String emailBodyRecall;
    private String emailBodyDeletedRecords;
    private String emailPassword;
    private String emailBodyForRequestPending;
    private String emailBodyForSubmitCollection;
    private String emailBodyForSubmitCollectionEmptyDirectory;
    private String emailBodyForExceptionInSubmitColletion;
    private String emailBodyForBulkRequestProcess;
    private  String subjectHeader = "subject";
    private  String smtps = "smtps://";
    private  String emailPayLoadSubject = "${header.emailPayLoad.subject}";
    private  String emailPayLoadTo = "${header.emailPayLoad.to}";
    private String password = "&password=";
    private String userName = "?username=";
    private String emailPayLoadcc = "${header.emailPayLoad.cc}";
    private String emailPayLoadMessage = "${header.emailPayLoad.messageDisplay}";

    /**
     * Instantiates a new Email route builder.
     *
     * @param context           the context
     * @param username          the username
     * @param passwordDirectory the password directory
     * @param from              the from
     * @param subject           the subject
     * @param requestPendingTo  the request pending to
     * @param smtpServer        the smtp server
     */
    @Autowired
    public EmailRouteBuilder(CamelContext context, @Value("${email.smtp.server.username}") String username, @Value("${email.smtp.server.password.file}") String passwordDirectory,
                             @Value("${email.smtp.server.address.from}") String from, @Value("${email.request.recall.subject}") String subject,
                             @Value("${recap-las.email.recap.assist.email.to}") String requestPendingTo, @Value("${email.smtp.server}") String smtpServer) {
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    loadEmailPassword();
                    loadEmailBodyForRequestPending();
                    loadEmailBodyTemplateForNoData();
                    loadEmailBodyTemplateForSubmitCollectionEmptyDirectory();
                    loadEmailBodyTemplateForExceptionInSubmitCollection();
                    loadEmailBodyForBulkRequest();
                    emailBodyRecall = loadEmailLasStatus(RecapConstants.REQUEST_RECALL_EMAIL_TEMPLATE);
                    emailBodyLasStatus = loadEmailLasStatus(RecapConstants.REQUEST_LAS_STATUS_EMAIL_TEMPLATE);
                    emailBodyDeletedRecords=loadEmailLasStatus(RecapConstants.DELETED_RECORDS_EMAIL_TEMPLATE);

                    from(RecapConstants.EMAIL_Q)
                            .routeId(RecapConstants.EMAIL_ROUTE_ID)
                            .setHeader("emailPayLoad").body(EmailPayLoad.class)
                            .onCompletion().log("Email has been sent successfully.")
                            .end()
                            .choice()
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.REQUEST_RECALL_MAIL_QUEUE))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyRecall))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("Email for Recall")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.REQUEST_LAS_STATUS_MAIL_QUEUE))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyLasStatus))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("Email for LAS Status")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.SUBMIT_COLLECTION))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyForSubmitCollection))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .setHeader("cc", simple(emailPayLoadcc))
                                    .log("email body for submit collection")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.SUBMIT_COLLECTION_FOR_NO_FILES))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyForSubmitCollectionEmptyDirectory))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("email body for submit collection")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.REQUEST_ACCESSION_RECONCILATION_MAIL_QUEUE))
                                    .log("email for accession Reconciliation")
                                    .setHeader(subjectHeader, simple("Barcode Reconciliation Report"))
                                    .setBody(simple(emailPayLoadMessage))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .setHeader("cc", simple(emailPayLoadcc))
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.DELETED_MAIL_QUEUE))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyDeletedRecords))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("Email Send for Deleted Records")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo("StatusReconcilation"))
                                    .log("email for status Reconciliation")
                                    .setHeader(subjectHeader, simple("\"Out\" Status Reconciliation Report"))
                                    .setBody(simple(emailPayLoadMessage))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .setHeader("cc", simple(emailPayLoadcc))
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                               .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.DAILY_RECONCILIATION))
                                    .log("email for Daily Reconciliation")
                                    .setHeader(subjectHeader, simple("Daily Reconciliation Report"))
                                    .setBody(simple(emailPayLoadMessage))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.REQUEST_INITIAL_DATA_LOAD))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailPayLoadMessage))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("Email for request initial data load")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.SUBMIT_COLLECTION_EXCEPTION))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyForExceptionInSubmitColletion))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .setHeader("cc", simple(emailPayLoadcc))
                                    .log("Email sent for exception in submit collection")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.EMAIL_HEADER_REQUEST_PENDING))
                                    .setHeader(subjectHeader, simple("LAS Pending Request Queue"))
                                    .setBody(simple(emailBodyForRequestPending))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(requestPendingTo))
                                    .log("Email for request pending")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.EMAIL_HEADER_REQUEST_STATUS_PENDING))
                                    .setHeader(subjectHeader,simple(emailPayLoadSubject))
                                    .setBody(simple(emailPayLoadMessage))
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .setHeader("cc", simple(emailPayLoadcc))
                                    .log("Email for pending Request status")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                                 .when(header(RecapConstants.EMAIL_BODY_FOR).isEqualTo(RecapConstants.BULK_REQUEST_EMAIL_QUEUE))
                                    .setHeader(subjectHeader, simple(emailPayLoadSubject))
                                    .setBody(simple(emailBodyForBulkRequestProcess))
                                    .process(new Processor() {
                                        @Override
                                        public void process(Exchange exchange) throws Exception {
                                        try {
                                            AttachmentMessage in = exchange.getMessage(AttachmentMessage.class);

                                            EmailPayLoad emailPayLoad = (EmailPayLoad) in.getHeader("emailPayLoad");
                                            in.addAttachment("Results_" + emailPayLoad.getBulkRequestFileName(), new DataHandler(emailPayLoad.getBulkRequestCsvFileData(), "text/csv"));
                                        } catch (Exception ex) {
                                            logger.info(RecapCommonConstants.LOG_ERROR , ex);
                                        }
                                        }
                                    })
                                    .setHeader("from", simple(from))
                                    .setHeader("to", simple(emailPayLoadTo))
                                    .log("Email sent for bulk request process")
                                    .to(smtps + smtpServer + userName + username + password + emailPassword)
                    ;
                }

                private String loadEmailLasStatus(String emailTemplate) {
                    return getEmailBodyString(emailTemplate).toString();
                }

                private void loadEmailBodyTemplateForNoData() {
                    emailBodyForSubmitCollection = getEmailBodyString(RecapConstants.SUBMIT_COLLECTION_EMAIL_BODY_VM).toString();
                }

                private void loadEmailBodyTemplateForSubmitCollectionEmptyDirectory() {
                    emailBodyForSubmitCollectionEmptyDirectory = getEmailBodyString(RecapConstants.SUBMIT_COLLECTION_EMAIL_BODY_FOR_EMPTY_DIRECTORY_VM).toString();
                }

                private void loadEmailBodyTemplateForExceptionInSubmitCollection() {
                    emailBodyForExceptionInSubmitColletion = getEmailBodyString(RecapConstants.SUBMIT_COLLECTION_EXCEPTION_BODY_VM).toString();
                }

                private void loadEmailPassword() {
                    File file = new File(passwordDirectory);
                    if (file.exists()) {
                        try {
                            emailPassword = FileUtils.readFileToString(file, StandardCharsets.UTF_8).trim();
                        } catch (IOException e) {
                            logger.error(RecapCommonConstants.LOG_ERROR,e);
                        }
                    }
                }

                private void loadEmailBodyForRequestPending() {
                    emailBodyForRequestPending = getEmailBodyString(RecapConstants.REQUEST_PENDING_EMAIL_BODY_VM).toString();
                }

                private void loadEmailBodyForBulkRequest() {
                    emailBodyForBulkRequestProcess = getEmailBodyString(RecapConstants.BULK_REQUEST_EMAIL_BODY_VM).toString();
                }

                private StringBuilder getEmailBodyString(String vmFileName) {
                    InputStream inputStream = getClass().getResourceAsStream(vmFileName);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder out = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            if (!line.isEmpty()) {
                                out.append(line);
                            }
                            out.append("\n");
                        }
                    } catch (IOException e) {
                        logger.error(RecapCommonConstants.LOG_ERROR, e);
                    }
                    return out;
                }
            });
        } catch (Exception e) {
            logger.error(RecapCommonConstants.REQUEST_EXCEPTION,e);
        }
    }
}
