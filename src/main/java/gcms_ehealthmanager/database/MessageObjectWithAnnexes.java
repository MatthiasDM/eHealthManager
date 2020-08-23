/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager.database;

import be.ehealth.businessconnector.ehbox.api.domain.DocumentMessage;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetFullMessageResponse;
import java.util.HashMap;

/**
 *
 * @author Matthias
 */
public class MessageObjectWithAnnexes{

  
    HashMap<String, byte[]> annexes;    
    DocumentMessage<GetFullMessageResponse> fullMessageResponse;
    MessageObject messageObject;
    
    public MessageObjectWithAnnexes() {
    }

    public MessageObjectWithAnnexes(HashMap<String, byte[]> annexes, DocumentMessage<GetFullMessageResponse> fullMessageResponse, MessageObject messageObject) {
        this.annexes = annexes;
        this.fullMessageResponse = fullMessageResponse;
        this.messageObject = messageObject;
    }

   
    public MessageObject getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(MessageObject messageObject) {
        this.messageObject = messageObject;
    }    
    
    public HashMap<String, byte[]> getAnnexes() {
        return annexes;
    }

    public void setAnnexes(HashMap<String, byte[]> annexes) {
        this.annexes = annexes;
    }

    public DocumentMessage<GetFullMessageResponse> getFullMessageResponse() {
        return fullMessageResponse;
    }

    public void setFullMessageResponse(DocumentMessage<GetFullMessageResponse> fullMessageResponse) {
        this.fullMessageResponse = fullMessageResponse;
    }
    
        

}
