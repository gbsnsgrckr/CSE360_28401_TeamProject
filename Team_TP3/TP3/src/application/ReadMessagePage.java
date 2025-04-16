package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Displays the details of a specific message, including sender, subject, reference,
 * and message content. Also provides a button to reply to the message.
 */
public class ReadMessagePage {
    private final DatabaseHelper databaseHelper;
    private final Message message;
    private User sender;

    /**
     * Constructs a ReadMessagePage using the given database helper and message.
     * It attempts to load the sender's user information from the database.
     *
     * @param databaseHelper The database helper used to fetch sender information.
     * @param message The message to be displayed.
     */
    public ReadMessagePage(DatabaseHelper databaseHelper, Message message) {
        this.databaseHelper = databaseHelper;
        this.message = message;
        try {
            this.sender = databaseHelper.getUser(message.getSenderID());
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
        }
    }

    /**
     * Displays the message information on the provided JavaFX stage.
     *
     * @param stage The JavaFX stage to render the message view on.
     */
    public void show(Stage stage) {
        // Sender Label
        Label senderLabel = new Label("From: " + (sender != null ? sender.getUsername() : "Unknown"));
        senderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Subject Label
        Label subjectLabel = new Label("Subject: " + message.getSubject());
        subjectLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
        
        // Reference Info Label
        String refType = message.getReferenceType();
        String refDisplay = "Reference: None";
        if (refType != null && message.getReferenceID() > 0) {
        	refDisplay = "Reference: " + refType + message.getReferenceID();
        	}
        Label referenceLabel = new Label(refDisplay);
        referenceLabel.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: #666;");


        // Message Content Area
        TextArea messageTextArea = new TextArea(message.getMessage());
        messageTextArea.setWrapText(true);
        messageTextArea.setEditable(false);
        messageTextArea.setPrefHeight(250);
        messageTextArea.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(messageTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Reply Button
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold; -fx-padding: 5px 15px;");
        replyButton.setOnAction(event -> {
            if (sender != null) {
                Stage replyStage = new Stage();
                
                int recipientId = sender.getUserId();
                int referenceId = message.getMessageID();
                String referenceType = "Message";
                
                new CreateMessagePage(databaseHelper, recipientId, referenceId, referenceType).show(replyStage);
            }
        });
        
     // Report Button
        Button reportButton = new Button("Report");
        reportButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold; -fx-padding: 5px 15px;");
        reportButton.setOnAction(event -> {
            if (sender != null) {
                Stage reportStage = new Stage();
                int recipientId = sender.getUserId();
                int referenceId = message.getMessageID();
                String referenceType = "Message";

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to report this message?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        new CreateMessagePage(databaseHelper, recipientId, referenceId, referenceType, true).show(reportStage);
                    }
                });
            }
        });


        // Button Box
        HBox buttonBox = new HBox(10, replyButton, reportButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Layout
        VBox contentLayout = new VBox(10, senderLabel, subjectLabel, referenceLabel, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Scene
        Scene scene = new Scene(contentLayout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Message ID: " + message.getMessageID());
        stage.show();
    }
}
