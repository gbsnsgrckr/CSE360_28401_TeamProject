package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

import databasePart1.*;

public class CreateMessagePage {
    private final DatabaseHelper databaseHelper;
    private final int recipientID;

    public CreateMessagePage(DatabaseHelper databaseHelper, int recipientID) {
        this.databaseHelper = databaseHelper;
        this.recipientID = recipientID;
    }

    public void show(Stage primaryStage) {

        Label messageLabel = new Label("Compose a new message:");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter a subject for your message");
        subjectField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        subjectField.setMaxWidth(400);

        TextArea messageField = new TextArea();
        messageField.setPromptText("Type your question here");
        messageField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        messageField.setMaxWidth(400);
        messageField.setPrefHeight(125);
        messageField.setWrapText(true);

        Button submitButton = new Button("Submit");
        submitButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        submitButton.setDefaultButton(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

        submitButton.setOnAction(a -> {
            String subject = subjectField.getText();
            String questionText = messageField.getText();
            User currentUser = databaseHelper.currentUser;
            int senderID = currentUser.getUserId();

            try {
                Message newMessage = new Message(senderID, recipientID, subject, questionText);
                databaseHelper.qaHelper.createMessage(newMessage);
                System.out.println("Message successfully posted.");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Your message has been submitted successfully.");
                alert.showAndWait();

                Stage newStage = new Stage();
                newStage.initStyle(StageStyle.TRANSPARENT);

                primaryStage.close();

                new StudentHomePage(databaseHelper).show(newStage);

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
                errorLabel.setText("An error occurred while submitting the message.");
            }
        });

        cancelButton.setOnAction(e -> primaryStage.close());

        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, messageLabel, subjectField, messageField, buttonBox, errorLabel);
        layout.setMinSize(500, 340);
        layout.setMaxSize(500, 340);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
        layout.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(layout);

        primaryStage.getIcons().clear();

        primaryStage.setScene(new Scene(root, 940, 400));
        primaryStage.setTitle("");
        primaryStage.show();
    }
}