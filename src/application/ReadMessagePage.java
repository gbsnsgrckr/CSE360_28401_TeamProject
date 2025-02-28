package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ReadMessagePage {
    private final DatabaseHelper databaseHelper;
    private final Message message;
    private User sender;

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

    public void show(Stage stage) {
        Label senderLabel = new Label("From: " + (sender != null ? sender.getUsername() : "Unknown"));
        senderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label subjectLabel = new Label("Subject: " + message.getSubject());
        subjectLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label messageLabel = new Label("Message: " + message.getMessage());
        messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; " +
                             "-fx-border-color: black, gray; -fx-border-width: 2, 1; " +
                             "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        closeButton.setOnAction(a -> stage.close());

        VBox contentLayout = new VBox(10, senderLabel, subjectLabel, messageLabel);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(contentLayout);
        scrollPane.setFitToWidth(true);

        VBox mainLayout = new VBox(10, scrollPane, closeButton);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        Scene scene = new Scene(mainLayout, 600, 400);

        stage.setScene(scene);
        stage.setTitle("Message ID: " + message.getMessageID());
        stage.show();
    }
}

