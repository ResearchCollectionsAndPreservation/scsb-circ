package org.recap;

import org.extensiblecatalog.ncip.v2.service.AgencyUserPrivilegeType;
import org.extensiblecatalog.ncip.v2.service.ElectronicAddress;
import org.extensiblecatalog.ncip.v2.service.ElectronicAddressType;
import org.extensiblecatalog.ncip.v2.service.LookupUserResponseData;
import org.extensiblecatalog.ncip.v2.service.NCIPResponseData;
import org.extensiblecatalog.ncip.v2.service.NameInformation;
import org.extensiblecatalog.ncip.v2.service.PersonalNameInformation;
import org.extensiblecatalog.ncip.v2.service.PhysicalAddress;
import org.extensiblecatalog.ncip.v2.service.PhysicalAddressType;
import org.extensiblecatalog.ncip.v2.service.Problem;
import org.extensiblecatalog.ncip.v2.service.ProblemType;
import org.extensiblecatalog.ncip.v2.service.StructuredAddress;
import org.extensiblecatalog.ncip.v2.service.StructuredPersonalUserName;
import org.extensiblecatalog.ncip.v2.service.UserAddressInformation;
import org.extensiblecatalog.ncip.v2.service.UserId;
import org.extensiblecatalog.ncip.v2.service.UserOptionalFields;
import org.extensiblecatalog.ncip.v2.service.UserPrivilege;
import org.extensiblecatalog.ncip.v2.service.UserPrivilegeStatus;
import org.extensiblecatalog.ncip.v2.service.UserPrivilegeStatusType;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;
import org.recap.ncip.LookupUser;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;

public class LookupUserUT extends BaseTestCaseUT {

    @InjectMocks
    LookupUser lookupUser;

    @Test
    public void setFromAgency() {
        LookupUser lookupUser1 = lookupUser.setFromAgency("testAgency");
        assertNotNull(lookupUser1);
    }

    @Test
    public void getLookupUserResponse() {
        NCIPResponseData ncipResponseData = getLookupUserResponseData();
        JSONObject returnJson = lookupUser.getLookupUserResponse(ncipResponseData);
        assertNotNull(returnJson);
    }

    @Test
    public void gatherPhysicalAddress() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        ReflectionTestUtils.invokeMethod(lookupUser, "gatherPhysicalAddress", lookupUserResponseData);
    }

    @Test
    public void gatherPhysicalAddressException() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        lookupUserResponseData.getUserOptionalFields().getUserAddressInformations().get(0).getPhysicalAddress().setPhysicalAddressType(null);
        ReflectionTestUtils.invokeMethod(lookupUser, "gatherPhysicalAddress", lookupUserResponseData);
    }

    @Test
    public void gatherElectronicAddressException() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        lookupUserResponseData.getUserOptionalFields().getUserAddressInformations().get(0).getElectronicAddress().setElectronicAddressType(null);
        ReflectionTestUtils.invokeMethod(lookupUser, "gatherElectronicAddress", lookupUserResponseData);
    }

    @Test
    public void isEmailPatternFalse() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        ReflectionTestUtils.invokeMethod(lookupUser, "isEmailPattern", "test");
    }

    @Test
    public void getUserIdStringWithoutUserId() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        lookupUserResponseData.setUserId(null);
        JSONObject returnJson = new JSONObject();
        ReflectionTestUtils.invokeMethod(lookupUser, "getUserIdString", lookupUserResponseData, returnJson);
    }

    @Test
    public void getPrivilegesException() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        lookupUserResponseData.getUserOptionalFields().getUserPrivileges().get(0).setAgencyUserPrivilegeType(null);
        ReflectionTestUtils.invokeMethod(lookupUser, "getPrivileges", lookupUserResponseData);
    }

    @Test
    public void gatherNameException() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        JSONObject returnJson = new JSONObject();
        lookupUserResponseData.getUserOptionalFields().getNameInformation().setPersonalNameInformation(null);
        ReflectionTestUtils.invokeMethod(lookupUser, "gatherName", lookupUserResponseData, returnJson);
    }

    @Test
    public void getLookupUserResponseWithoutProblems() {
        LookupUserResponseData lookupUserResponseData = getLookupUserResponseData();
        lookupUserResponseData.setProblems(Collections.EMPTY_LIST);
        NCIPResponseData ncipResponseData = lookupUserResponseData;
        JSONObject returnJson = lookupUser.getLookupUserResponse(ncipResponseData);
        assertNotNull(returnJson);
    }

    private LookupUserResponseData getLookupUserResponseData() {
        LookupUserResponseData lookupUserResponseData = new LookupUserResponseData();
        Problem problem = getProblem();
        UserId userId = new UserId();
        userId.setUserIdentifierValue("24567");
        UserOptionalFields userOptionalFields = getUserOptionalFields();
        lookupUserResponseData.setProblems(Arrays.asList(problem));
        lookupUserResponseData.setUserOptionalFields(userOptionalFields);
        lookupUserResponseData.setUserId(userId);
        return lookupUserResponseData;
    }

    private UserOptionalFields getUserOptionalFields() {
        UserOptionalFields userOptionalFields = new UserOptionalFields();
        NameInformation nameInformation = getNameInformation();
        UserAddressInformation userAddressInformation = getUserAddressInformation();
        UserPrivilege userPrivilege = getUserPrivilege();
        userOptionalFields.setNameInformation(nameInformation);
        userOptionalFields.setUserAddressInformations(Arrays.asList(userAddressInformation));
        userOptionalFields.setUserPrivileges(Arrays.asList(userPrivilege));
        return userOptionalFields;
    }

    private UserPrivilege getUserPrivilege() {
        UserPrivilege userPrivilege = new UserPrivilege();
        AgencyUserPrivilegeType agencyUserPrivilegeType = new AgencyUserPrivilegeType("scheme", "345");
        UserPrivilegeStatus userPrivilegeStatus = new UserPrivilegeStatus();
        userPrivilegeStatus.setUserPrivilegeStatusType(new UserPrivilegeStatusType("scheme", "345"));
        userPrivilege.setAgencyUserPrivilegeType(agencyUserPrivilegeType);
        userPrivilege.setUserPrivilegeStatus(userPrivilegeStatus);
        return userPrivilege;
    }

    private UserAddressInformation getUserAddressInformation() {
        UserAddressInformation userAddressInformation = new UserAddressInformation();
        PhysicalAddress physicalAddress = getPhysicalAddress();
        ElectronicAddress electronicAddress = getElectronicAddress();
        userAddressInformation.setPhysicalAddress(physicalAddress);
        userAddressInformation.setElectronicAddress(electronicAddress);
        return userAddressInformation;
    }

    private ElectronicAddress getElectronicAddress() {
        ElectronicAddress electronicAddress = new ElectronicAddress();
        ElectronicAddressType electronicAddressType = new ElectronicAddressType("345");
        electronicAddress.setElectronicAddressType(electronicAddressType);
        electronicAddress.setElectronicAddressData("test@gmail.com");
        return electronicAddress;
    }

    private PhysicalAddress getPhysicalAddress() {
        PhysicalAddress physicalAddress = new PhysicalAddress();
        StructuredAddress structuredAddress = new StructuredAddress();
        structuredAddress.setLine1("No.2");
        structuredAddress.setLine2("North Street");
        structuredAddress.setLocality("TestCity");
        structuredAddress.setRegion("Region");
        structuredAddress.setPostalCode("345666");
        PhysicalAddressType physicalAddressType = new PhysicalAddressType("scheme", "345");
        physicalAddress.setStructuredAddress(structuredAddress);
        physicalAddress.setPhysicalAddressType(physicalAddressType);
        return physicalAddress;
    }

    private NameInformation getNameInformation() {
        NameInformation nameInformation = new NameInformation();
        PersonalNameInformation personalNameInformation = new PersonalNameInformation();
        StructuredPersonalUserName structuredPersonalUserName = new StructuredPersonalUserName();
        structuredPersonalUserName.setGivenName("test");
        structuredPersonalUserName.setSurname("name");
        personalNameInformation.setStructuredPersonalUserName(structuredPersonalUserName);
        nameInformation.setPersonalNameInformation(personalNameInformation);
        return nameInformation;
    }

    private Problem getProblem() {
        Problem problem = new Problem();
        ProblemType problemType = new ProblemType("43656");
        problem.setProblemType(problemType);
        problem.setProblemDetail("Bad Request");
        problem.setProblemValue("43656");
        problem.setProblemElement("Error");
        return problem;
    }
}
