package application;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a Request with details including the request text, the user who created it,
 * instructor and admin approval flags, and additional information used for database management.
 * <p>
 * This merged version includes:
 * <ul>
 *   <li>A unique ID for database purposes</li>
 *   <li>The actual request as a {@code StringProperty}</li>
 *   <li>The {@code User} who created the request</li>
 *   <li>A boolean flag ({@code requestTOF}) indicating if an instructor has acted on the request</li>
 *   <li>A boolean flag ({@code requestATOF}) indicating if an admin has acted on the request</li>
 *   <li>A semicolon-delimited string of notes</li>
 *   <li>A status string (e.g., "OPEN", "CLOSED", "REOPENED")</li>
 *   <li>An {@code originalId} that points to the original request if it was reopened</li>
 * </ul>
 * </p>
 */
public class Request {
    private int id;                     // Unique identifier (default: -1 for new requests)
    private StringProperty request;
    private User user;
    private boolean requestTOF;
    private boolean requestATOF;
    private String notes;               // Semicolon-delimited notes
    private String status;              // e.g., "OPEN", "CLOSED", "REOPENED", etc.
    private int originalId;             // Points to the original request's ID if reopened

    /**
     * Basic constructor for creating a new Request.
     * The approval flags are initialized to false,
     * notes to an empty string, status to "OPEN",
     * and id and originalId to default values.
     *
     * @param request the text of the request
     * @param user    the user who created the request
     */
    public Request(String request, User user) {
        this.id = -1; // default value for new requests
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = false;
        this.requestATOF = false;
        this.notes = "";
        this.status = "OPEN";
        this.originalId = -1; // default indicating no original request
    }

    /**
     * Overloaded constructor for creating a Request from database information.
     *
     * @param id           the unique identifier
     * @param request      the text of the request
     * @param user         the user who created the request
     * @param requestTOF   flag indicating if an instructor has acted on the request
     * @param requestATOF  flag indicating if an admin has acted on the request
     * @param notes        semicolon-delimited notes
     * @param status       the status of the request (e.g., "OPEN", "CLOSED", "REOPENED")
     * @param originalId   the original request's ID if this is a reopened request
     */
    public Request(int id, String request, User user, boolean requestTOF, boolean requestATOF, 
                   String notes, String status, int originalId) {
        this.id = id;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = requestTOF;
        this.requestATOF = requestATOF;
        this.notes = notes;
        this.status = status;
        this.originalId = originalId;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public StringProperty requestProperty() {
        return request;
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

    /**
     * Returns the username of the associated user.
     *
     * @return the username if the user exists, otherwise an empty string
     */
    public String getUserName() {
        return (user != null) ? user.getUsername() : "";
    }

    public boolean getRequestTOF() {
        return requestTOF;
    }

    public void setRequestTOF(boolean requestTOF) {
        this.requestTOF = requestTOF;
    }

    public boolean getRequestATOF() {
        return requestATOF;
    }

    public void setRequestATOF(boolean requestATOF) {
        this.requestATOF = requestATOF;
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

    /**
     * Appends a note to the existing notes, using a semicolon as a delimiter.
     *
     * @param note the note to add
     */
    public void addNote(String note) {
        if (note == null || note.isEmpty()) return;
        if (this.notes == null || this.notes.isEmpty()) {
            this.notes = note;
        } else {
            this.notes += ";" + note;
        }
    }
}
