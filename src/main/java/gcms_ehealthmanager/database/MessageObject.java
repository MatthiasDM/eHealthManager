/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager.database;

import be.ehealth.businessconnector.ehbox.api.domain.DocumentMessage;
import be.fgov.ehealth.ehbox.consultation.protocol.v3.GetFullMessageResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author Matthias
 */
public class MessageObject {

    
    List<String> fileNames;
    @BsonProperty("binary")
    String message;
    String messageId;
    String title, sender, freetext, patient;
    String type, status;
    Map<String, String> customMetas;
    long date;

    public MessageObject(List<String> fileNames, String message, String messageId, String title, String sender, String freetext, String patient, String type, String status, Map<String, String> customMetas, long date) {
        this.fileNames = fileNames;
        this.message = message;
        this.messageId = messageId;
        this.title = title;
        this.sender = sender;
        this.freetext = freetext;
        this.patient = patient;
        this.type = type;
        this.status = status;
        this.customMetas = customMetas;
        this.date = date;
    }

    public Map<String, String> getCustomMetas() {
        return customMetas;
    }

    public void setCustomMetas(Map<String, String> customMetas) {
        this.customMetas = customMetas;
    }

   
    
    

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }  
   
        
    public MessageObject() {
    }

    @JsonIgnore
    public MessageObject getMessageObject() {
        return this;
    } 

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    } 
   

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFreetext() {
        return freetext;
    }

    public void setFreetext(String freetext) {
        this.freetext = freetext;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

 

}
