/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import be.ehealth.businessconnector.consultrnv2.session.ConsultrnSessionServiceFactory;
import be.ehealth.technicalconnector.idgenerator.IdGeneratorFactory;
import be.ehealth.technicalconnector.utils.ConnectorXmlUtils;
import be.fgov.ehealth.rn.commons.v1.SsinType;
import be.fgov.ehealth.rn.personservice.core.v1.SearchPersonBySsinCriteriaType;
import be.fgov.ehealth.rn.personservice.protocol.v1.SearchPersonBySsinRequest;
import be.fgov.ehealth.rn.personservice.protocol.v1.SearchPersonBySsinResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Matthias
 */
public class ConsultRRManager {

    public static void setup() {
//        ConfigFactory.getConfigValidator().setProperty("endpoint.consultrnv2.personservice", "https://services-int.ehealth.fgov.be/rnconsult/PersonService/v1");
        //ConfigFactory.getConfigValidator().setProperty("endpoint.consultrnv2.personservice", "https://preview-intr2.api.ehealth.fgov.be/rnconsult/PersonService/v1");
    }

    public static String SearchPersonBySsin(String ssin) throws Exception {
        SearchPersonBySsinRequest request = new SearchPersonBySsinRequest();//TRANSFORMER.transform("/examples/request/searchPersonBySsinRequest.xml", SearchPersonBySsinRequest.class);
        SearchPersonBySsinCriteriaType criteria = new SearchPersonBySsinCriteriaType();
        SsinType ssinType = new SsinType();
        ssinType.setValue(ssin);
        criteria.setSsin(ssinType);
        request.setCriteria(criteria);
        request.setIssueInstant(DateTime.now());
        //request.setId(IdGeneratorFactory.getIdGenerator("uuid").generateId());    
        request.setApplicationId("99090999702");
        SearchPersonBySsinResponse response = ConsultrnSessionServiceFactory.getConsultrnPersonService().searchPersonBySsin(request);
        return ConnectorXmlUtils.toString(response);
    }

//    public <T> T transform(String fileLocation, Class<T> clazz) throws Exception {
//        Map<String, Object> velocityContext = new HashMap<String, Object>();
//        velocityContext.put("issueInstant", DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'+02:00'").print(new DateTime()));
//        velocityContext.put("requestId", IdGeneratorFactory.getIdGenerator("uuid").generateId());
//        velocityContext.put("currentDate", DateTimeFormat.forPattern("yyyy-MM-dd").print(new DateTime()));
//        velocityContext.put("generatedChars1", RandomStringUtils.randomAlphabetic(10).toLowerCase());
//        velocityContext.put("generatedChars2", RandomStringUtils.randomAlphabetic(10).toLowerCase());
//        velocityContext.put("variableName", generateVariableName());
//        return null;//FileTestUtils.toObject(velocityContext, fileLocation, clazz);
//    }
}
