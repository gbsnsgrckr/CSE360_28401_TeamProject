package application;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Request {
    private int id;                 // NEW
    private StringProperty request;
    private User user;
    private boolean requestTOF;
    private String notes;           // NEW (semicolon-delimited)
    private String status;          // NEW ('OPEN','CLOSED','REOPENED', etc.)
    private int originalId;         // NEW (points to the old request’s ID if reopened)

    // Existing constructor
    public Request(String request, User user) {
        this.request = new SimpleStringProperty(request);
        this.user = user;
    }

    // Overloaded constructor for building from DB
    public Request(int id, String request, User user, boolean requestTOF, String notes,
                   String status, int originalId) {
        this.id = id;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = requestTOF;
        this.notes = notes;
        this.status = status;
        this.originalId = originalId;
    }

    // Getters / setters
    public int getId() {
        return id;
    }

    public String getRequest() {
        return request.get();
    }

    public void setRequest(String newRequest) {
        this.request.set(newRequest);
    }

    public User getUser() {
        return user;
    }
    public String getUserName() {
        return (user != null) ? user.getUsername() : "";
    }

    public boolean getRequestTOF() {
        return requestTOF;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public int getOriginalId() {
        return originalId;
    }
    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }

    // Helper to add a semicolon-delimited note
    public void addNote(String note) {
        if (note == null || note.isEmpty()) return;
        if (this.notes == null || this.notes.isEmpty()) {
            this.notes = note;
        } else {
            this.notes += ";" + note;
        }
    }

    public StringProperty requestProperty() {
        return request;
    }
}
