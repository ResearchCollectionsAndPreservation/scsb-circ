package org.recap.ils;

import org.recap.model.AbstractResponseItem;
import org.recap.model.ILSConfigProperties;

public abstract class AbstractProtocolConnector {

    protected ILSConfigProperties ilsConfigProperties;
    protected String institutionCode;

    public abstract boolean supports(String protocol);

    public abstract void setInstitution(String institutionCode);

    public abstract void setIlsConfigProperties(ILSConfigProperties ilsConfigProperties);

    public String getHost() {
        return ilsConfigProperties.getHost();
    }

    public int getPort() {
        return ilsConfigProperties.getPort();
    }

    public String getOperatorUserId() {
        return ilsConfigProperties.getOperatorUserId();
    }

    public String getOperatorPassword() {
        return ilsConfigProperties.getOperatorPassword();
    }

    public String getOperatorLocation() {
        return ilsConfigProperties.getOperatorLocation();
    }

    /**
     * Lookup item abstract response item.
     *
     * @param itemIdentifier the item identifier
     * @return the abstract response item
     */
    public abstract AbstractResponseItem lookupItem(String itemIdentifier);

    /**
     * Check out item object.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @return the object
     */
    public abstract Object checkOutItem(String itemIdentifier, String patronIdentifier);

    /**
     * Check in item object.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @return the object
     */
    public abstract Object checkInItem(String itemIdentifier, String patronIdentifier);

    /**
     * Place hold object.
     *
     * @param itemIdentifier    the item identifier
     * @param patronIdentifier  the patron identifier
     * @param callInstitutionId the call institution id
     * @param itemInstitutionId the item institution id
     * @param expirationDate    the expiration date
     * @param bibId             the bib id
     * @param pickupLocation    the pickup location
     * @param trackingId        the tracking id
     * @param title             the title
     * @param author            the author
     * @param callNumber        the call number
     * @return the object
     */
    public abstract Object placeHold(String itemIdentifier, String patronIdentifier, String callInstitutionId, String itemInstitutionId, String expirationDate, String bibId, String pickupLocation, String trackingId, String title, String author, String callNumber);

    /**
     * Cancel hold object.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @param institutionId    the institution id
     * @param expirationDate   the expiration date
     * @param bibId            the bib id
     * @param pickupLocation   the pickup location
     * @param trackingId       the tracking id
     * @return the object
     */
    public abstract Object cancelHold(String itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId, String pickupLocation, String trackingId);

    /**
     * Create bib object.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @param institutionId    the institution id
     * @param titleIdentifier  the title identifier
     * @return the object
     */
    public abstract Object createBib(String itemIdentifier, String patronIdentifier, String institutionId, String titleIdentifier);

    /**
     * Patron validation boolean.
     *
     * @param institutionId    the institution id
     * @param patronIdentifier the patron identifier
     * @return the boolean
     */
    public abstract boolean patronValidation(String institutionId, String patronIdentifier);

    /**
     * Lookup patron abstract response item.
     *
     * @param patronIdentifier the patron identifier
     * @return the abstract response item
     */
    public abstract AbstractResponseItem lookupPatron(String patronIdentifier);

    /**
     * Recall item object.
     *
     * @param itemIdentifier   the item identifier
     * @param patronIdentifier the patron identifier
     * @param institutionId    the institution id
     * @param expirationDate   the expiration date
     * @param bibId            the bib id
     * @param pickupLocation   the pickup location
     * @return the object
     */
    public abstract Object recallItem(String  itemIdentifier, String patronIdentifier, String institutionId, String expirationDate, String bibId,String pickupLocation);

    /**
     * Refile Item object.
     *
     * @param itemIdentifier
     * @return the object
     */
    public abstract Object refileItem(String itemIdentifier);

}
