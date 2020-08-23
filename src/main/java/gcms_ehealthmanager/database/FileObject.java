/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gcms_ehealthmanager.database;

/**
 *
 * @author Matthias
 */
public class FileObject {
     
    public String fileid;
  
    public String name;
   
    public String type;
  
    public long upload_date;
   
    public String content_type;
  
    public FileObject() {
    }

    public FileObject(String fileid, String name, String type, long upload_date, String content_type) {
        this.fileid = fileid;
        this.name = name;
        this.type = type;
        this.upload_date = upload_date;
        this.content_type = content_type;
    }

    public long getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(long upload_date) {
        this.upload_date = upload_date;
    }
   

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }   

}
