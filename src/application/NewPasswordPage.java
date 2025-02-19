package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Pos;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class NewPasswordPage {
	private final DatabaseHelper databaseHelper;

	public NewPasswordPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		// Input field for the user's userName, password
		PasswordField passwordField = new PasswordField();
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setPromptText("Enter New Password");
		passwordField.setMaxWidth(200);
		passwordField.setAlignment(Pos.CENTER);

		PasswordField passwordValidField = new PasswordField();
		passwordValidField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordValidField.setPromptText("Confirm New Password");
		passwordValidField.setMaxWidth(200);
		passwordValidField.setAlignment(Pos.CENTER);

		// Label to display title to user
		Label prompt = new Label("Password Reset");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Label to display error messages
		Label errorLabel = new Label("");
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// Button to save password change
		Button saveChanges = new Button("Save");
		saveChanges.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Set saveChanges button as default to allow pressing Enter to activate
		saveChanges.setDefaultButton(true);

		saveChanges.setOnAction(a -> {
			// Retrieve user inputs
			String password = passwordField.getText();
			String passwordValid = passwordValidField.getText();

			String passwordValidate = PasswordEvaluator.evaluatePassword(password);

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText("***ERROR*** Invalid password");
				return;
			} else {
				if (passwordValid.equals(password)) {

					errorLabel.setText("***Success*** Password Changed");
					databaseHelper.updatePassword(user.getUsername(), password);

					databaseHelper.updateOTPFlag(user.getUsername(), false);
					
					// Create new stage to get rid of transparency for following pages
					Stage newStage = new Stage();
					newStage.initStyle(StageStyle.TRANSPARENT);
					
					// Close the existing stage
					primaryStage.close();

					new UserLoginPage(databaseHelper).show(newStage);
					return;
				}
			}
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(prompt, passwordField, passwordValidField, saveChanges, errorLabel);
		layout.setMinSize(400, 220);
		layout.setMaxSize(400, 220);
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layout.setAlignment(Pos.CENTER);

		StackPane root = new StackPane(layout);

		// Removes icon from title bar in alert window		
		primaryStage.getIcons().clear();
		
		primaryStage.setScene(new Scene(root, 940, 400));
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
