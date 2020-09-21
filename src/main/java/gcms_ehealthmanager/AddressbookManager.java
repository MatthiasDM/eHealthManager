/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.businessconnector.addressbook.*;
import be.ehealth.businessconnector.addressbook.session.AddressbookSessionService;
import be.ehealth.businessconnector.addressbook.session.AddressbookSessionServiceFactory;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;
import be.fgov.ehealth.addressbook.protocol.v1.GetProfessionalContactInfoRequest;
import be.fgov.ehealth.addressbook.protocol.v1.GetProfessionalContactInfoResponse;
import org.joda.time.DateTime;

public class AddressbookManager {

    private static GetProfessionalContactInfoRequest createGetProfessionalContactInfo(String NIHII) {
        GetProfessionalContactInfoRequest request = new GetProfessionalContactInfoRequest();
        request.setNIHII(NIHII);
        request.setIssueInstant(DateTime.now());
        return request;
    }

    public static String getProfessionalContactInfo(String NIHII) throws ConnectorException {
        GetProfessionalContactInfoRequest request = createGetProfessionalContactInfo(NIHII);

        AddressbookSessionService service = AddressbookSessionServiceFactory.getAddressbookSessionService();

        GetProfessionalContactInfoResponse response = service.getProfessionalContactInfo(request);

        //AddressbookTestUtils.verifyResponseGetProfessionalContactInfo(response);
        return ConnectorXmlUtils.toString(response);
    }
}
