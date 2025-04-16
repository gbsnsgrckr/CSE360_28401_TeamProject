package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Page where a user can create and send a new message.
 * Includes references to questions, answers, or previous messages.
 */
public class CreateMessagePage {
    private final DatabaseHelper databaseHelper;
    private final int recipientID;
    private User recipient;
	private int referenceID;
	private String referenceType;
	private boolean isReport = false;
	private String labelTxt;

	 /**
     * Constructs a CreateMessagePage for a specific recipient with optional reference context.
     *
     * @param databaseHelper Helper object for database access
     * @param recipientID ID of the recipient user
     * @param referenceID Optional ID of the item being referenced
     * @param referenceType Type of referenced item ("Question", "Answer", "Message", "Review")
     */
	public CreateMessagePage(DatabaseHelper databaseHelper, int recipientID, Integer referenceID, String referenceType) {
	    this.databaseHelper = databaseHelper;
	    this.recipientID = recipientID;
	    this.referenceID = referenceID;
	    this.referenceType = referenceType;

	    try {
	        this.recipient = databaseHelper.getUser(recipientID);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        this.recipient = null;
	    }
	    this.isReport = false; // HW4
	}
	
	/**
     * Constructs a CreateMessagePage for a specific recipient with optional reference context.
     *
     * @param databaseHelper Helper object for database access
     * @param recipientID ID of the recipient user
     * @param referenceID Optional ID of the item being referenced
     * @param referenceType Type of referenced item ("Question", "Answer", "Message", "Review")
     * @param isReport Flag to determine if this is a message or report
     */
	public CreateMessagePage(DatabaseHelper databaseHelper, int recipientID, Integer referenceID, String referenceType, boolean isReport) { // HW4
	    this.databaseHelper = databaseHelper;
	    this.recipientID = recipientID;
	    this.referenceID = referenceID;
	    this.referenceType = referenceType;

	    try {
	        this.recipient = databaseHelper.getUser(recipientID);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        this.recipient = null;
	    }
	    this.isReport = isReport;
	}
	
	/**
     * Displays the message composition window.
     *
     * @param primaryStage The JavaFX stage to show the UI on
     */
    public void show(Stage primaryStage) {
        // Label showing recipient username
    	if(!isReport) {
    		labelTxt = "To: ";
    	} else {
    		labelTxt = "Report User: ";
    	}
    	Label recipientLabel = new Label(labelTxt + (recipient != null ? recipient.getUsername() : "Unknown"));
		recipientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        
     // Reference Info Label (if available)
        Label referenceLabel;
        if (referenceType != null && referenceID > 0) {
            referenceLabel = new Label("Reference: " + referenceType + " " + referenceID);
            referenceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        } else {
            referenceLabel = new Label("Reference: None");
            referenceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
        }


        // Subject Input Field 
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter Subject");
        subjectField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        subjectField.setPrefWidth(500);

        // Message TextArea
        TextArea messageField = new TextArea();
        messageField.setPromptText("Type your message here...");
        messageField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        messageField.setPrefHeight(200);
        messageField.setPrefWidth(500); 
        messageField.setWrapText(true);

        // ScrollPane for Message Content
        ScrollPane scrollPane = new ScrollPane(messageField);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Send Button
        Button submitButton = new Button("Send");
        submitButton.setPrefWidth(100);
        submitButton.setPadding(new Insets(5, 15, 5, 15));
        
        // Cancel Button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");
        cancelButton.setPrefWidth(100);
        cancelButton.setPadding(new Insets(5, 15, 5, 15));

        // Button Box 
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Event Handlers
        submitButton.setOnAction(a -> {
        	if(!isReport) {
            String subject = subjectField.getText().trim();
            String messageText = messageField.getText().trim();
            User currentUser = databaseHelper.currentUser;
            int senderID = currentUser.getUserId();

            if (subject.isEmpty() || messageText.isEmpty()) {
                showAlert("Error", "Subject and message cannot be empty.");
                return;
            }

            try {
                Message newMessage = new Message(referenceID, referenceType, senderID, recipientID, subject, messageText);
                databaseHelper.qaHelper.createMessage(newMessage);
                showAlert("Success", "Your message has been sent successfully.");
                primaryStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while sending the message.");
            }
        	} else {
        		String subject = subjectField.getText().trim();
                String messageText = messageField.getText().trim();
                User currentUser = databaseHelper.currentUser;
                int senderID = currentUser.getUserId();

                if (subject.isEmpty() || messageText.isEmpty()) {
                    showAlert("Error", "Subject and message cannot be empty.");
                    return;
                }

                try {
                    Message Report = new Message(referenceID, referenceType, senderID, recipientID, subject, messageText, true); // HW4
                    databaseHelper.qaHelper.createMessage(Report, true);
                    showAlert("Success", "Report has been successfully generated.");
                    primaryStage.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while generating report.");
                }
        	}
        });

        cancelButton.setOnAction(e -> primaryStage.close());

        // Layout
        VBox contentLayout = new VBox(10, recipientLabel, referenceLabel, subjectField, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Scene
        Scene scene = new Scene(contentLayout, 600, 400);
        primaryStage.setScene(scene);
        if (!isReport) {
        	primaryStage.setTitle("New Message");
        } else {
        	primaryStage.setTitle("Report");
        }
        primaryStage.show();
    }
    
    /**
     * Shows an alert dialog to the user.
     *
     * @param title Title of the alert window
     * @param message Message content of the alert
     */
    // pop up alert showing message sent successfully
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
