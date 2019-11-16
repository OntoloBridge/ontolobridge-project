package edu.miami.schurer.ontolobridge.Responses;

import java.sql.Date;

public class NotificationObject {
    private int id;
    private boolean sent = false;
    private String notification_method;
    private String address;
    private String title;
    private String message;
    private Date createdDate;
    private Date sentDate;

    public NotificationObject(int id, String notification_method, String title, boolean sent, String address, String message, Date createdDate, Date sentDate) {
        this.id = id;
        this.notification_method = notification_method;
        this.title = title;
        this.sent = sent;
        this.address = address;
        this.message = message;
        this.createdDate = createdDate;
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

    public String getNotificationMethod(){
        return notification_method;
    }

    public String getAddress() {
        return address;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getSentDate() {
        return sentDate;
    }
}
