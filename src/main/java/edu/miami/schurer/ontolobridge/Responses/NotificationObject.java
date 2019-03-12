package edu.miami.schurer.ontolobridge.Responses;

import java.sql.Date;

public class NotificationObject {
    private int id;
    private boolean sent = false;
    private String type;
    private String address;
    private String title;
    private String message;
    private Date createDate;
    private Date sentDate;

    public NotificationObject(int id,String type,String title, boolean sent, String address, String message, Date createDate, Date sentDate) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.sent = sent;
        this.address = address;
        this.message = message;
        this.createDate = createDate;
        this.sentDate = sentDate;
    }

    public int getId() {
        return id;
    }

    public boolean isSent() {
        return sent;
    }

    public String getTitle() {
        return title;
    }

    public String getType(){
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getSentDate() {
        return sentDate;
    }
}
