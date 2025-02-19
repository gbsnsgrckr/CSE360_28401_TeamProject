package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator
 * account. This is intended to be used by the first user to initialize the
 * system with admin credentials.
 */
public class AdminSetupPage {
	private final DatabaseHelper databaseHelper;

	public AdminSetupPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {

		// Label to explain the first page to the user
		Label userLabel = new Label("             Welcome..You are the first person here."
				+ "\nPlease register an Administrator account to continue");
		userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");

		// Input fields for account info
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Admin Username");
		userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		userNameField.setMaxWidth(200);

		TextField nameField = new TextField();
		nameField.setPromptText("Enter Admin Name");
		nameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		nameField.setMaxWidth(200);

		TextField emailField = new TextField();
		emailField.setPromptText("Enter Admin Email");
		emailField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		emailField.setMaxWidth(200);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setMaxWidth(200);

		// Button to register account
		Button setupButton = new Button("Setup");
		setupButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		setupButton.setDefaultButton(true);
		
		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

		setupButton.setOnAction(a -> {
			
			// Retrieve user input
			String userName = userNameField.getText();
			String name = nameField.getText();
			String email = emailField.getText();
			String password = passwordField.getText();
			
			// Using FSM to validate
			String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);
			
			// Using FSM to validate name
			String nameValidate = NameValidator.checkForValidName(name);
			
			// Using FSM to validate Password
			String passwordValidate = PasswordEvaluator.evaluatePassword(password);
			
			// Using FSM to validate email syntax
			String emailValidate = EmailValidator.checkForValidEmail(email);

			if (!userNameValidate.isEmpty()) {
				errorLabel.setText(userNameValidate);
				return;
			}

			if (!nameValidate.isEmpty()) {
				errorLabel.setText(nameValidate);
				return;
			}

			if (!emailValidate.isEmpty()) {
				errorLabel.setText(emailValidate);
				return;
			}

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText(passwordValidate);
				return;
			}

			try {
				// Create a new User object with admin role and register in the database
				List<String> roles = new ArrayList<>();
				roles.add("Admin");

				User user = new User(userName, name, password, email, roles, false);
				databaseHelper.register(user);
				System.out.println("Administrator setup completed.");

				// Create new stage to allow transparency for following page
				Stage newStage = new Stage();
				newStage.initStyle(StageStyle.TRANSPARENT);
				
				// Close the existing stage
				primaryStage.close();
				
				// Navigate to the Welcome Login Page
				new UserLoginPage(databaseHelper).show(newStage);
			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		VBox layout = new VBox(10, userLabel, userNameField, nameField, emailField, passwordField, setupButton,
				errorLabel);
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
