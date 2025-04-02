package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.Request;
import application.User;
import databasePart1.DatabaseHelper;

import java.util.Arrays;

/**
 * This class tests the functionality of the request system, including
 * creating, closing, reopening requests, and adding notes to requests.
 */
public class RequestSystemTest {

    private DatabaseHelper db;
    private User instructor;
    private User admin;

    /**
     * Sets up the test environment before each test.
     * Initializes the database connection and registers an instructor and an admin user.
     */
    @Before
    public void setUp() throws Exception {
        db = new DatabaseHelper();
        db.connectToDatabase();

        // Set up instructor
        instructor = new User(9991, "instructorUser", "Instructor", "pass", "inst@example.com",
                Arrays.asList("Instructor"), false);
        db.register(instructor);

        // Set up admin
        admin = new User(9992, "adminUser", "Admin", "adminpass", "admin@example.com",
                Arrays.asList("Admin"), false);
        db.register(admin);

        db.currentUser = instructor;
    }

    /**
     * Tests the creation of a new request and verifies the default status, 
     * message, and associated user.
     * 
     * @throws SQLException if a database error occurs
     */
    @Test
    public void testCreateRequest() throws SQLException {
        String requestText = "Please fix classroom projector.";
        db.createNewRequest(requestText, instructor.getUsername());

        List<Request> all = db.getAllRequests();
        assertEquals(1, all.size());

        Request r = all.get(0);
        assertEquals("OPEN", r.getStatus());
        assertEquals(requestText, r.getRequest());
        assertEquals(instructor.getUsername(), r.getUserName());
        assertEquals(0, r.getOriginalId());
    }

    /**
     * Tests closing a request with a note by an admin user and verifies that the request
     * status and note are updated properly.
     * 
     * @throws SQLException if a database error occurs
     */
    @Test
    public void testCloseRequestWithNote() throws SQLException {
        db.createNewRequest("Setup printer in lab", instructor.getUsername());
        Request request = db.getAllRequests().get(0);

        db.currentUser = admin;
        db.closeRequest(request.getId(), "Printer installed successfully.");

        Request closed = db.getRequestById(request.getId());
        assertEquals("CLOSED", closed.getStatus());
        assertTrue(closed.getNotes().contains("Printer installed successfully."));
    }

    /**
     * Tests reopening a previously closed request and verifies that a new request
     * is created with a status of REOPENED and carries over the previous notes.
     * 
     * @throws SQLException if a database error occurs
     */
    @Test
    public void testReopenClosedRequest() throws SQLException {
        db.createNewRequest("Projector broken", instructor.getUsername());
        Request original = db.getAllRequests().get(0);

        db.currentUser = admin;
        db.closeRequest(original.getId(), "Replaced projector bulb.");

        db.currentUser = instructor;
        db.reopenRequest(original.getId(), "Still not working", "Seems like power issue", instructor.getUsername());

        List<Request> all = db.getAllRequests();
        assertEquals(2, all.size());

        Request reopened = all.get(1);
        assertEquals("REOPENED", reopened.getStatus());
        assertEquals(original.getId(), reopened.getOriginalId());
        assertTrue(reopened.getNotes().contains("Replaced projector bulb."));
        assertTrue(reopened.getNotes().contains("Seems like power issue"));
    }

    /**
     * Tests adding a note to an existing request without changing its status.
     * 
     * @throws SQLException if a database error occurs
     */
    @Test
    public void testAddNoteOnly() throws SQLException {
        db.createNewRequest("Need more whiteboard markers", instructor.getUsername());
        Request req = db.getAllRequests().get(0);

        db.addNoteToRequest(req.getId(), "Supplies ordered.");

        Request updated = db.getRequestById(req.getId());
        assertEquals("OPEN", updated.getStatus());
        assertTrue(updated.getNotes().contains("Supplies ordered."));
    }
}
