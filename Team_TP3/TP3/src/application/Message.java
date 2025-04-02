package application;

import java.sql.SQLException;
import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;

/**
 * Represents a message exchanged between users in the system.
 * Each message can optionally reference a question, answer, or another message.
 */

public class Message {
	private int messageID;
	private int referenceID;
	private String referenceType;
	private int senderID;
	private int recipientID;
	private String subject;
	private String message;
	private LocalDateTime createdOn;
	private LocalDateTime updatedOn;
	private User sender;
	private User recipient;
		
	 /** Default constructor. */
    public Message() {
    }
    
    /**
     * Constructs a basic message without reference. Used for testing.
     *
     * @param senderID ID of the sender
     * @param recipientID ID of the recipient
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(int senderID, int recipientID, String subject, String message) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
    }
    
    /**
     * Constructs a message and retrieves sender and recipient User objects from the database.
     *
     * @param dbHelper Database helper for fetching user info
     * @param messageID Message ID
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message content
     */
    public Message(DatabaseHelper dbHelper, int messageID, int senderID, int recipientID, String subject, String message) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        try {
            this.sender = dbHelper.getUser(senderID);
            this.recipient = dbHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
            this.recipient = null;
        }
    }
    
    /**
     * Constructs a message with a reference and retrieves sender/recipient User objects.
     *
     * @param dbHelper Database helper for fetching user info
     * @param referenceID Referenced item ID (e.g., message, question, or answer)
     * @param referenceType Type of referenced item ("Message", "Question", "Answer")
     * @param messageID Message ID
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(DatabaseHelper dbHelper, int referenceID, String referenceType, int messageID, int senderID, int recipientID, String subject, String message) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.referenceID = referenceID;
		this.referenceType = referenceType;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        try {
            this.sender = dbHelper.getUser(senderID);
            this.recipient = dbHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
            this.recipient = null;
        }
    }
    
    /**
     * Constructs a message with reference details but without user objects.
     *
     * @param referenceID Referenced item ID
     * @param referenceType Type of referenced item
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(int referenceID, String referenceType, int senderID, int recipientID, String subject, String message) {
		this.referenceID = referenceID;
		this.referenceType = referenceType;
		this.senderID = senderID;
		this.recipientID = recipientID;
		this.subject = subject;
		this.message = message;
	}
    
    /**
     * Constructs a message with full timestamp info.
     *
     * @param messageID Message ID
     * @param referenceID Reference ID
     * @param referenceType Reference type
     * @param senderID Sender ID
     * @param recipientID Recipient ID
     * @param subject Subject
     * @param message Message content
     * @param createdOn Time the message was created
     * @param updatedOn Time the message was last updated
     */
    public Message(int messageID, int referenceID, String referenceType, int senderID, int recipientID, 
                   String subject, String message, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this.messageID = messageID;
        this.referenceID = referenceID;
        this.referenceType = referenceType;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }
	
	// Getters and Setters
    /** @return The unique ID of the message. */
    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    /** @return ID of the referenced item. */
    public int getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(int referenceID) {
        this.referenceID = referenceID;
    }

    /** @return Type of reference ("Message", "Question", "Answer"). */
    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    /** @return ID of the user who sent the message. */
    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }
    
    /** @return User object of the sender. */
    public User getSender() {
        return sender;
    }

    /** @return ID of the message recipient. */
    public int getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }

    /** @return User object of the recipient. */
    public User getRecipient() {
        return recipient;
    }

    /** @return Subject of the message. */
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /** @return Content/body of the message. */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /** @return Timestamp of when the message was created. */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /** @return Timestamp of when the message was last updated. */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Returns a string representation of the message for debugging or display.
     *
     * @return Formatted string representing key message attributes
     */
    public String toString() {
        return String.format(
            "MESSAGE:\n" +
            "Message ID: %d\n" +
            "Reference ID: %d\n" +
            "Reference Type: %s\n" +
            "Sender ID: %d\n" +
            "Recipient ID: %d\n" +
            "Subject: %s\n" +
            "Message: %s\n"
//            + "Created On: %s\n" +
//            "Updated On: %s"
            ,
            messageID, referenceID, referenceType, senderID, recipientID, subject, message
//            , createdOn, updatedOn
        );
        }
}
