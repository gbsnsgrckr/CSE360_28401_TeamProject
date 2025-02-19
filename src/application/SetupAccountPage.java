package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users. Users
 * provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {

	private final DatabaseHelper databaseHelper;

	// DatabaseHelper to handle database operations.
	public SetupAccountPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * Displays the Setup Account page in the provided stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {

		Label title = new Label("New User");
		title.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		title.setAlignment(Pos.CENTER);

		// Input fields for userName, password, and invitation code
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Username");
		userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		userNameField.setMaxWidth(200);

		TextField nameField = new TextField();
		nameField.setPromptText("Enter Name");
		nameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		nameField.setMaxWidth(200);

		TextField emailField = new TextField();
		emailField.setPromptText("Enter Email");
		emailField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		emailField.setMaxWidth(200);

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Enter Password");
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setMaxWidth(200);

		TextField inviteCodeField = new TextField();
		inviteCodeField.setPromptText("Enter Invitation Code");
		inviteCodeField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		inviteCodeField.setMaxWidth(200);

		// Label to display error messages for invalid input or registration issues
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
		errorLabel.setAlignment(Pos.CENTER);

		Button setupButton = new Button("Setup");
		setupButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		setupButton.setDefaultButton(true);

		// Create button to return to login screen
		Button quitButton = new Button("Back to login");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Quit button to return to User Login Screen
		quitButton.setOnAction(a -> {
			
			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.DECORATED);
			
			// Close the existing stage
			primaryStage.close();
			
			new UserLoginPage(databaseHelper).show(newStage);
		});

		setupButton.setOnAction(a -> {
			// Retrieve user input
			String userName = userNameField.getText();
			String name = nameField.getText();
			String email = emailField.getText();
			String password = passwordField.getText();
			String code = inviteCodeField.getText();

			// Using FSM to validate the username
			String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);

			// Using FSM to validate the name
			String nameValidate = NameValidator.checkForValidName(name);

			// Using FSM to validate the password
			String passwordValidate = PasswordEvaluator.evaluatePassword(password);

			// Using FSM to validate the email syntax
			String emailValidate = EmailValidator.checkForValidEmail(email);

			if (!userNameValidate.isEmpty()) {
				errorLabel.setText(userNameValidate);
				return;
			}

			if (!nameValidate.isEmpty()) {
				errorLabel.setText(nameValidate);
				return;
			}

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText(passwordValidate);
				return;
			}

			if (!emailValidate.isEmpty()) {
				errorLabel.setText(emailValidate);
				return;
			}

			try {
				// Check if the user already exists
				if (!databaseHelper.doesUserExist(userName)) {

					// Validate the invitation code
					if (databaseHelper.validateInvitationCode(code)) {

						// Create a new user and register them in the database
						List<String> roles = new ArrayList<>();

						User user = new User(userName, name, password, email, roles, false);
						databaseHelper.register(user);
						
						// Create new stage to get rid of transparency for following pages
						Stage newStage = new Stage();
						newStage.initStyle(StageStyle.TRANSPARENT);
						
						// Close the existing stage
						primaryStage.close();

						// Navigate to the Welcome Login Page
						new UserLoginPage(databaseHelper).show(newStage);
					} else {
						errorLabel.setText("Please enter a valid invitation code.");
					}
				} else {
					errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
				}

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		HBox hbox = new HBox(5, setupButton, quitButton);
		hbox.setStyle("-fx-padding: 20; -fx-alignment: center;");
		hbox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10);
		layout.setMinSize(500, 380);
		layout.setMaxSize(500, 380);
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layout.getChildren().addAll(title, userNameField, nameField, emailField, passwordField, inviteCodeField, hbox,
				errorLabel);
		layout.setAlignment(Pos.CENTER);

		VBox.setMargin(layout, new Insets(0));
		VBox.setMargin(errorLabel, new Insets(0));
		VBox.setVgrow(hbox, Priority.NEVER);

		// StackPane to control layout sizing
		StackPane root = new StackPane(layout);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.setScene(new Scene(root, 940, 400));
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
