package com.theironyard.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Caroline on 3/15/16.
 */
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender;

    @ManyToOne
    User recipient;

    @Column(nullable = false)
    String filename;

    @NotNull
    int timeEntry;


    LocalDateTime dateTime;

    public int getTimeEntry() {
        return timeEntry;
    }

    public void setTimeEntry(int timeEntry) {
        this.timeEntry = timeEntry;
    }


    public Photo() {
    }

    public Photo(User sender, User recipient, String filename, int timeEntry) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.timeEntry = timeEntry;
    }

    public Photo(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
