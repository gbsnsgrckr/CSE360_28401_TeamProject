package tests;

import application.Message;
import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * MessageTestingAutomation is a console-based test runner used to validate
 * the message creation and insertion functionality of the message database.
 * It includes both positive and negative test cases to ensure the system behaves
 * correctly under valid and invalid input conditions.
 * 
 * @author Chris
 * @version 1.0
 * @since 2025-03-24

 */
public class MessageTestingAutomation {


    /**
     * Runs a suite of automated tests to verify that message records are correctly
     * inserted, validated, and managed in the database.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        DatabaseHelper db = new DatabaseHelper();

        try {
            db.connectToDatabase();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            return;
        }

        System.out.println("----- STARTING MESSAGE DATABASE TESTS -----\n");

        // Test 1: Check if any messages currently exist in the database.
        try {
            List<Message> after = db.qaHelper.getAllMessages();
            int count = after.size();
            boolean hasMessages = count > 0;
            System.out.println("[Test 1] Messages exist in the database: " + hasMessages + " (Count: " + count + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Test 2: Look for a specific known message by subject.
        try {
            boolean found = false;
            List<Message> messages = db.qaHelper.getAllMessages();
            for (Message m : messages) {
                if ("Meeting Reminder".equals(m.getSubject())) {
                    found = true;
                    break;
                }
            }
            System.out.println("[Test 2] 'Meeting Reminder' message found: " + found);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Test 3: Validate that all messages contain both a non-empty subject and message body.
        try {
            boolean allValid = true;
            List<Message> messages = db.qaHelper.getAllMessages();
            for (Message m : messages) {
                if (m.getSubject() == null || m.getSubject().isBlank() ||
                    m.getMessage() == null || m.getMessage().isBlank()) {
                    allValid = false;
                    System.out.println("[DEBUG] Invalid message found: ID=" + m.getMessageID());
                    break;
                }
            }
            System.out.println("[Test 3] All messages have valid subject and body: " + allValid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Test 4: Attempt to insert a message with an empty subject and body.
        try {
            Message badMsg = new Message(1337, 2, "", "");
            db.qaHelper.createMessage(badMsg);
            System.out.println("[Test 4] ERROR: Empty message inserted (should not happen).");
        } catch (Exception e) {
            System.out.println("[Test 4] Passed: Empty message was rejected.");
        }

        // Test 5: Attempt to insert a message with null subject and body.
        try {
            Message badMsg = new Message(1337, 2, null, null);
            db.qaHelper.createMessage(badMsg);
            System.out.println("[Test 5] ERROR: Null message inserted (should not happen).");
        } catch (Exception e) {
            System.out.println("[Test 5] Passed: Null message was rejected.");
        }

        // Test 6: Ensure that all messages in the database have valid, auto generated message IDs.
        try {
            boolean allHaveIDs = true;
            List<Message> messages = db.qaHelper.getAllMessages();
            for (Message m : messages) {
                if (m.getMessageID() <= 0) {
                    allHaveIDs = false;
                    break;
                }
            }
            System.out.println("[Test 6] All messages have valid message IDs: " + allHaveIDs);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("\n----- MESSAGE DATABASE TESTS COMPLETE -----");
    }
}
