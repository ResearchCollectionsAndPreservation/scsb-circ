package org.recap.request.service;

import org.apache.camel.ProducerTemplate;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
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

    @Value("${" + PropertyKeyConstants.EMAIL_BULK_REQUEST_TO + "}")
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
        emailPayLoad.setSubject(subject + " " + itemBarcode);
        producer.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.EMAIL_BODY_FOR, ScsbConstants.REQUEST_RECALL_MAIL_QUEUE);
    }

    public void sendLASExceptionEmail(String customerCode, String itemBarcode, String messageDisplay, String patronBarcode, String toInstitution, String subject) {
        EmailPayLoad emailPayLoad = new EmailPayLoad();
        emailPayLoad.setTo(emailLASIdTo(toInstitution));
        emailPayLoad.setCc(emailLASIdCC(toInstitution));
        emailPayLoad.setCustomerCode(customerCode);
        emailPayLoad.setItemBarcode(itemBarcode);
        emailPayLoad.setMessageDisplay(messageDisplay);
        emailPayLoad.setPatronBarcode(patronBarcode);
        emailPayLoad.setSubject(subject + " " + itemBarcode);
        producer.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.EMAIL_BODY_FOR, ScsbConstants.REQUEST_LAS_STATUS_MAIL_QUEUE);
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
        producer.sendBodyAndHeader(ScsbConstants.EMAIL_Q, emailPayLoad, ScsbConstants.EMAIL_BODY_FOR, ScsbConstants.BULK_REQUEST_EMAIL_QUEUE);
    }

    /**
     * @param institution
     * @return
     */
    private String emailLASIdTo(String institution) {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_EMAIL_LAS_EXCEPTION_TO);
    }

    private String emailLASIdCC(String institution) {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_EMAIL_LAS_EXCEPTION_CC);
    }

    private String emailIdCC(String institution, String imsLocationCode) {
        if (institution.equalsIgnoreCase(ScsbConstants.GFA)) {
            return propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, PropertyKeyConstants.IMS.IMS_EMAIL_REQUEST_RECALL_CC);
        } else {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_EMAIL_REQUEST_RECALL_CC);
        }
    }

    private String emailIdTo(String institution, String imsLocationCode) {
        if (institution.equalsIgnoreCase(ScsbConstants.GFA)) {
            return propertyUtil.getPropertyByImsLocationAndKey(imsLocationCode, PropertyKeyConstants.IMS.IMS_EMAIL_REQUEST_CANCEL_TO);
        } else {
            return propertyUtil.getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_EMAIL_RECALL_REQUEST_TO);
        }
    }
}
