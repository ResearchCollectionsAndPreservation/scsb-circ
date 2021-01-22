package org.recap.request;

import org.apache.camel.ProducerTemplate;
import org.recap.RecapConstants;
import org.recap.camel.EmailPayLoad;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by sudhishk on 19/1/17.
 */
@Service
public class EmailService {

    @Value("${email.bulk.request.to}")
    private String bulkRequestEmailTo;

    @Autowired
    private ProducerTemplate producer;

    @Autowired
    private PropertyUtil propertyUtil;

    /**
     * Send email method for recall process, the information is send to the mail queue, with .
     *
     * @param customerCode   the customer code
     * @param itemBarcode    the item barcode
     * @param messageDisplay the message display
     * @param patronBarcode  the patron barcode
     * @param toInstitution  the to institution
     */
    public void sendEmail(String customerCode, String itemBarcode, String imsLocationCode, String messageDisplay, String patronBarcode, String toInstitution, String subject) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setTo(emailIdTo(toInstitution, imsLocationCode));
        emailPayLoad.setCc(emailIdCC(toInstitution, imsLocationCode));
        emailPayLoad.setCustomerCode(customerCode);
        emailPayLoad.setItemBarcode(itemBarcode);
        emailPayLoad.setMessageDisplay(messageDisplay);
        emailPayLoad.setPatronBarcode(patronBarcode);
        emailPayLoad.setSubject(subject + itemBarcode);
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_BODY_FOR, RecapConstants.REQUEST_RECALL_MAIL_QUEUE);
    }

    /**
     * Send email for bulk request process.
     *
     * @param bulkRequestId
     * @param bulkRequestName
     * @param bulkRequestFileName
     * @param bulkRequestStatus
     * @param subject
     */
    public void sendBulkRequestEmail(String bulkRequestId, String bulkRequestName, String bulkRequestFileName, String bulkRequestStatus, String bulkRequestCsvFileData, String subject) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setTo(bulkRequestEmailTo);
        emailPayLoad.setBulkRequestId(bulkRequestId);
        emailPayLoad.setBulkRequestName(bulkRequestName);
        emailPayLoad.setBulkRequestFileName(bulkRequestFileName);
        emailPayLoad.setBulkRequestStatus(bulkRequestStatus);
        emailPayLoad.setBulkRequestCsvFileData(bulkRequestCsvFileData);
        emailPayLoad.setSubject(subject);
        producer.sendBodyAndHeader(RecapConstants.EMAIL_Q, emailPayLoad, RecapConstants.EMAIL_BODY_FOR, RecapConstants.BULK_REQUEST_EMAIL_QUEUE);
    }

    /**
     * @param institution
     * @return
     */
    private String emailIdTo(String institution, String imsLocationCode) {
        if (institution.equalsIgnoreCase(RecapConstants.GFA)) {
            return propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.email.request.cancel.to");
        } else {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, "email.recall.request.to");
        }
    }

    private String emailIdCC(String institution, String imsLocationCode) {
        if (institution.equalsIgnoreCase(RecapConstants.GFA)) {
            return propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, "las.email.request.recall.cc");
        } else {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, "email.request.recall.cc");
        }
    }
}
