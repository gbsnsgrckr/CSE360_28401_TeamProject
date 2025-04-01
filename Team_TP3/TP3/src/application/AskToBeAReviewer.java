package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;

/**
 * The AskToBeAReviewer class provides a user interface that allows a user
 * to submit a request to become a reviewer. It displays a text area for the user
 * to enter a request message and handles the submission process.
 */
public class AskToBeAReviewer {
    private final DatabaseHelper databaseHelper;
    private User user;

    /**
     * Constructs an AskToBeAReviewer instance with the specified DatabaseHelper.
     *
     * @param databaseHelper the DatabaseHelper used for database operations.
     */
    public AskToBeAReviewer(DatabaseHelper databaseHelper) { //, user user
        this.databaseHelper = databaseHelper;
        //this.user = user;
    }
        
    /**
     * Displays the reviewer request form on the provided stage.
     * <p>
     * The form includes a text area for entering the request, a request button,
     * and a cancel button. When the request is submitted, it validates the input and,
     * if valid, registers the request in the database.
     * </p>
     *
     * @param primaryStage the stage where the form is displayed.
     */
    public void show(Stage primaryStage) {
        // Label to display the welcome message for the student
        Label userLabel = new Label("Tell Us why you want to be a reviewer in the box below.");
        userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");

        // Input field for the reviewer's request
        TextArea request = new TextArea();
        request.setPromptText("Tell us why you want to be a reviewer");
        request.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 5, 1; -fx-border-inset: 0, 4;");
        request.setMaxWidth(300);
        request.setPrefHeight(100);
        request.setWrapText(true);

        // Button to submit the request
        Button setupButton = new Button("Request");
        setupButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        setupButton.setDefaultButton(true);

        // Button to cancel the request
        Button cancel = new Button("Cancel");
        cancel.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

        setupButton.setOnAction(a -> {
            // Retrieve user input
            String requestA = request.getText();

            if (requestA.isEmpty() || requestA.length() <= 30 | requestA.length() >= 500) {
                errorLabel.setText("Enter a reequest with atleast 30 characters and less than 500 characters");
                return;
            }
            errorLabel.setText("");
            try {
                databaseHelper.register(requestA);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            primaryStage.close();
            return;
        });
            
        cancel.setOnAction(a -> {
            primaryStage.close();
        });

        VBox layout = new VBox(10, userLabel, request, setupButton, cancel, errorLabel);
        layout.setMinSize(500, 340);
        layout.setMaxSize(500, 340);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
        layout.setAlignment(Pos.CENTER);
            
        // StackPane to control layout sizing
        StackPane root = new StackPane(layout);

        // Removes icon from title bar in alert window		
        primaryStage.getIcons().clear();
            
        primaryStage.setScene(new Scene(root, 940, 400));
        primaryStage.setTitle("");
        primaryStage.show();
    }
}
