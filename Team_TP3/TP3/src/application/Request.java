package application;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a Request made by a {@link User}, containing the request message,
 * status, approval flags, and optional notes. Requests can be marked by an instructor
 * or admin, closed or reopened, and may include references to prior versions.
 *
 * <p>This class supports the following features:</p>
 * <ul>
 *   <li>Tracking request content and user</li>
 *   <li>Status management: OPEN, CLOSED, REOPENED</li>
 *   <li>Notes appended in a semicolon-delimited format</li>
 *   <li>Flags to track instructor and admin approval</li>
 *   <li>An original request ID to support reopened requests</li>
 * </ul>
 * 
 * <p>Use this class when retrieving, displaying, or modifying user-submitted requests
 * in instructor/admin interfaces.</p>
 * 
 * @author CSE 360 Team 8
 */
public class Request {
    private int id;
    private StringProperty request;
    private User user;
    private boolean requestTOF;
    private boolean requestATOF;
    private String notes;
    private String status;
    private int originalId;

    /**
     * Constructs a new Request with default flags and status.
     * Used when creating a request not yet stored in the database.
     *
     * @param request the request message
     * @param user    the user who submitted the request
     */
    public Request(String request, User user) {
        this.id = -1;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = false;
        this.requestATOF = false;
        this.notes = "";
        this.status = "OPEN";
        this.originalId = -1;
    }

    /**
     * Constructs a Request using all fields (used for DB retrieval).
     *
     * @param id           request ID
     * @param request      the request message
     * @param user         the submitting user
     * @param requestTOF   instructor approval flag
     * @param requestATOF  admin approval flag
     * @param notes        notes (semicolon-delimited)
     * @param status       current status
     * @param originalId   original request ID if reopened
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

    /**
     * Constructs a Request with approval flags pre-set.
     *
     * @param request     the request message
     * @param user        the user who submitted the request
     * @param requestTOF  instructor flag
     * @param requestATOF admin flag
     */
    public Request(String request, User user, boolean requestTOF, boolean requestATOF) {
        this.id = -1;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = requestTOF;
        this.requestATOF = requestATOF;
        this.notes = "";
        this.status = "OPEN";
        this.originalId = -1;
    }

    // Getters and Setters

    /**
     * Returns the unique identifier for this request.
     * 
     * @return the request ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the request text as a JavaFX StringProperty.
     * 
     * @return the StringProperty of the request
     */
    public StringProperty requestProperty() {
        return request;
    }

    /**
     * Gets the request text as a string.
     * 
     * @return the request content
     */
    public String getRequest() {
        return request.get();
    }

    /**
     * Sets a new request message.
     * 
     * @param newRequest the updated request content
     */
    public void setRequest(String newRequest) {
        this.request.set(newRequest);
    }

    /**
     * Returns the user who submitted this request.
     * 
     * @return the {@link User} object
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the username associated with the request.
     * 
     * @return the username, or empty string if user is null
     */
    public String getUserName() {
        return (user != null) ? user.getUsername() : "";
    }

    /**
     * Returns whether the instructor has approved/acted on this request.
     * 
     * @return true if acted on, false otherwise
     */
    public boolean getRequestTOF() {
        return requestTOF;
    }

    /**
     * Sets the instructor approval flag.
     * 
     * @param requestTOF true if instructor acted on it
     */
    public void setRequestTOF(boolean requestTOF) {
        this.requestTOF = requestTOF;
    }

    /**
     * Returns whether the admin has approved/acted on this request.
     * 
     * @return true if acted on, false otherwise
     */
    public boolean getRequestATOF() {
        return requestATOF;
    }

    /**
     * Sets the admin approval flag.
     * 
     * @param requestATOF true if admin acted on it
     */
    public void setRequestATOF(boolean requestATOF) {
        this.requestATOF = requestATOF;
    }

    /**
     * Returns the semicolon-delimited notes.
     * 
     * @return the notes string
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes string.
     * 
     * @param notes semicolon-delimited notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the current request status.
     * 
     * @return status (e.g., "OPEN", "CLOSED", etc.)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the request status.
     * 
     * @param status new status string
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the original request ID if reopened.
     * 
     * @return the ID of the original request
     */
    public int getOriginalId() {
        return originalId;
    }

    /**
     * Sets the original ID for reopened requests.
     * 
     * @param originalId the ID of the original request
     */
    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }

    /**
     * Appends a note to the existing notes, separated by a semicolon.
     * 
     * @param note the note to be added
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
