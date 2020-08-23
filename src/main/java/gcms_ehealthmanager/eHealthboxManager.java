/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager;

import gcms_ehealthmanager.database.MessageObjectWithAnnexes;
import be.ehealth.businessconnector.ehbox.api.domain.DocumentMessage;
import be.ehealth.businessconnector.ehbox.v3.builders.BuilderFactory;
import be.ehealth.businessconnector.ehbox.v3.builders.ConsultationMessageBuilder;
import be.ehealth.businessconnector.ehbox.v3.session.EhealthBoxServiceV3;
import be.ehealth.businessconnector.ehbox.v3.session.ServiceFactory;
import be.ehealth.technicalconnector.exception.ConnectorException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.utils.MarshallerHelper;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetFullMessageRequest;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetFullMessageResponse;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetMessageListResponseType;

import be.fgov.ehealth.ehbox.consultation.protocol.v3.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import gcms_ehealthmanager.database.DatabaseActions;
import gcms_ehealthmanager.database.FileObject;
import gcms_ehealthmanager.database.MessageObject;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.TemporalType;
import org.apache.http.auth.AuthenticationException;
import org.bson.conversions.Bson;
import org.junit.Assert;

/**
 *
 * @author Matthias
 */
public class eHealthboxManager {

    private static ConsultationMessageBuilder consultBuilder;

    public static String checkMessages() throws ConnectorException, JsonProcessingException, IOException, AuthenticationException, ClassNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonData = mapper.createObjectNode();
        String source = "INBOX";
        GetMessageListResponseType response = ServiceFactory.getEhealthBoxServiceV3().getMessageList(BuilderFactory.getRequestBuilder().createGetMessagesListRequest(source));
        Assert.assertEquals("100", response.getStatus().getCode());
        Assert.assertNotNull(response.getMessages());
        List<Message> list = response.getMessages();
        List<String> messages = new ArrayList<>();
        for (Message message : list) {
            MessageObject messageObject = Core.getMessageFromDatabase(message.getMessageId());

            if (messageObject == null) { // e.g. nog niet in de database
                messageObject = new MessageObject();
                messageObject.setMessageId(message.getMessageId());
                messageObject.setDate(Instant.now().toEpochMilli());//message.getMessageInfo().getPublicationDate().getMillis()));
                messageObject.setTitle(message.getContentInfo().getTitle());
                messageObject.setSender(message.getSender().getName() + " " + message.getSender().getFirstName() + " - " + message.getSender().getId());
                MessageObjectWithAnnexes fullMessage = getFullMessage("INBOX", message.getMessageId());
                messageObject.setMessage(fullMessage.getMessageObject().getMessage());
                messageObject.setFreetext(fullMessage.getFullMessageResponse().getFreeText());
                messageObject.setPatient(fullMessage.getFullMessageResponse().getPatientInss());
                messageObject.setFileNames(fullMessage.getMessageObject().getFileNames());
                fullMessage.setMessageObject(messageObject);
                Core.saveMessageObjectWithAnnexes(fullMessage);
            }

            messages.add(mapper.writeValueAsString(message));
        }

        return mapper.writeValueAsString(messages);
    }

    public static String getMessages(int page, int rows, BasicDBObject filterObject) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<org.bson.Document> messages = DatabaseActions.getObjectsSpecificList("ehealthbox", "gcms_ehealthmanager.database.MessageObject", filterObject, new BasicDBObject("date", -1), rows, new String[]{"message"}, page, rows);
              ObjectNode jsonData = mapper.createObjectNode();
                ObjectNode jsonParameters = mapper.createObjectNode();
                Long records = DatabaseActions.getObjectCount("ehealthbox", "gcms_ehealthmanager.database.MessageObject", filterObject);
                int total = records.intValue() / rows;
                jsonParameters.put("records", records);
                jsonParameters.put("page", page);
                jsonParameters.put("total", total);
                jsonParameters.putPOJO("rows", (messages));
//        { 
//  "total": "xxx", 
//  "page": "yyy", 
//  "records": "zzz",
//  "rows" : [
//    {"id" :"1", "cell" :["cell11", "cell12", "cell13"]},
//    {"id" :"2", "cell":["cell21", "cell22", "cell23"]},
//      ...
//  ]
//}

        return mapper.writeValueAsString(jsonParameters);
    }

    public static String getFile(String filename, String baseURL, String tempURL) {
        filename = new String(Base64.getDecoder().decode(filename));
        return DatabaseActions.getFile(filename, baseURL, tempURL);
    }

    public static MessageObjectWithAnnexes getFullMessage(String source, String _messageId) throws TechnicalConnectorException, IOException, ClassNotFoundException {
        GetFullMessageResponse response = null;
        MessageObjectWithAnnexes messageObjectWithAnnexes = new MessageObjectWithAnnexes();
        messageObjectWithAnnexes.setMessageObject(new MessageObject());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        consultBuilder = BuilderFactory.getConsultationMessageBuilder();
        MarshallerHelper<GetFullMessageResponse, GetFullMessageResponse> helper = new MarshallerHelper<>(GetFullMessageResponse.class, GetFullMessageResponse.class);

        try {
            source = "INBOX";
            EhealthBoxServiceV3 service = ServiceFactory.getEhealthBoxServiceV3();
            GetFullMessageRequest request = new GetFullMessageRequest();
            request.setMessageId(_messageId);
            request.setSource(source);
            response = service.getFullMessage(request);
            DocumentMessage<GetFullMessageResponse> message = (DocumentMessage<GetFullMessageResponse>) consultBuilder.buildFullMessage(response);
            byte[] fullMessageArray = helper.toXMLByteArray(response);
            messageObjectWithAnnexes.getMessageObject().setMessage(new String(fullMessageArray));
            messageObjectWithAnnexes.setFullMessageResponse(message);
            messageObjectWithAnnexes.setAnnexes(Core.getFilesFromFullMessage(message));
            // messageObjectWithAnnexes.getMessageObject().setMessage(fullMessageArray);
            messageObjectWithAnnexes.getMessageObject().setFileNames(new ArrayList<>(messageObjectWithAnnexes.getAnnexes().keySet()));

            //String fileName = Core.saveMessageWithAnnexes(messageObjectWithAnnexes);
            return messageObjectWithAnnexes;
        } catch (ConnectorException ex) {
            Logger.getLogger(eHealthboxManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String dumpFiles(String regex, String outputDir, int dumpPeriod) throws ClassNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Bson filter = Filters.and(
                Filters.gt("upload_date", Instant.now().minus(dumpPeriod, ChronoUnit.MINUTES).toEpochMilli()),
                Filters.regex("fileid", regex));
        ArrayList<org.bson.Document> fileObjects = DatabaseActions.getObjectsSpecificList("fileobjects", "gcms_ehealthmanager.database.FileObject", filter, null, 1000, new String[]{}, 1, 50);

        for (int i = 0; i < fileObjects.size(); i++) {
            FileObject fileObject = mapper.convertValue(fileObjects.get(i), FileObject.class);
            DatabaseActions.downloadFileToDir(fileObject.fileid, outputDir);
        }

        return "done";
    }

}
