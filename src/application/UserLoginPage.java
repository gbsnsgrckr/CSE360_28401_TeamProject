package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class UserLoginPage {
	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	// Method to include direction to the necessary role pages
	private void roleHomePage(User user, Stage newStage) {
		
		// Check if user has any roles, if so, send to that page
		if (user.getRoles().size() > 0) {
			switch (user.getRoles().get(0).toLowerCase()) {
			case "admin":
				databaseHelper.setUserCurrentRole("admin");
				new AdminHomePage(databaseHelper).show(newStage, user);
				break;

			case "student":
				databaseHelper.setUserCurrentRole("student");
				new StudentHomePage(databaseHelper).show(newStage);
				break;

			case "instructor":
				databaseHelper.setUserCurrentRole("instructor");
				new InstructorHomePage(databaseHelper).show(newStage);
				break;

			case "staff":
				databaseHelper.setUserCurrentRole("staff");
				new StaffHomePage(databaseHelper).show(newStage);
				break;

			case "reviewer":
				databaseHelper.setUserCurrentRole("reviewer");
				new ReviewerHomePage(databaseHelper).show(newStage);
				break;

			default:
				databaseHelper.setUserCurrentRole("user");
				new UserHomePage(databaseHelper).show(newStage);
				break;
			}
			// Else, send them to the UserHomePage
		} else {
			databaseHelper.setUserCurrentRole("user");
			new UserHomePage(databaseHelper).show(newStage);
		}
	}

	public void show(Stage primaryStage) {
		// Input field for the user's userName, password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Username");
		userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		userNameField.setMaxWidth(200);
		userNameField.setAlignment(Pos.CENTER);		

		PasswordField passwordField = new PasswordField();
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(200);
		passwordField.setAlignment(Pos.CENTER);

		// Label to display title to user
		Label prompt = new Label("Login");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

		// Button to login
		Button loginButton = new Button("Login");
		loginButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Button to register a new user
		Button setupButton = new Button("New User");
		setupButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Button to replace X close button for transparent background
		Button closeButton = new Button("X");
		closeButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		closeButton.setMinSize(25, 25);
		closeButton.setMaxSize(25, 25);

		closeButton.setOnMouseEntered(a -> {
			closeButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});

		closeButton.setOnMouseExited(a -> {
			closeButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});

		closeButton.setOnAction(a -> {
			primaryStage.close();
		});

		// Set login button as default to allow pressing Enter to activate
		loginButton.setDefaultButton(true);

		loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText();
			String password = passwordField.getText();

			String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);
			String passwordValidate = PasswordEvaluator.evaluatePassword(password);

			if (!userNameValidate.isEmpty()) {
				errorLabel.setText("Invalid username");
				return;
			}

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText("Invalid password");
				return;
			}
			
			try {
				User user = databaseHelper.login(userName, password);
				
				if(user == null) {
					errorLabel.setText("Error loggin in. Contact an Administrator.");
					return;
				}
				
				// Create new stage to get rid of transparency for following pages
				Stage newStage = new Stage();
				newStage.initStyle(StageStyle.DECORATED);				
				
				System.out.println(user.toString());	
				
				// Close primaryStage before moving forward
				primaryStage.close();

				System.out.print("\n The otp value is " + user.getOTPFlag() + "\n");// debug

				if (user.getOTPFlag()) {
					System.out.print("Your if statement works\n");// debug
					new NewPasswordPage(databaseHelper).show(newStage, user);					
					return;
				}

				// If user has more than one role, send them to RoleSelectPage otherwise call
				// roleHomePage() method
				if (user.getRoles().size() > 1) {
					System.out.println("MAde it to roleSelectPage");
					new RoleSelectPage(databaseHelper).show(newStage);//
					return;
				} else {
					roleHomePage(user, newStage);
					
					System.out.println("Made it to if-statement checking number of roles"); // Debug
					return;
				}				
				
			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		// Button to register a new account
		setupButton.setOnAction(a -> {
			
			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.DECORATED);
			
			// Close the existing stage
			primaryStage.close();
			
			new SetupAccountPage(databaseHelper).show(newStage);
		});

		HBox closeButtonBox = new HBox(closeButton);
		closeButtonBox.setAlignment(Pos.TOP_RIGHT);
		closeButtonBox.setTranslateX(-275);
		closeButtonBox.setTranslateY(65);
		closeButtonBox.setPadding(new Insets(0));

		HBox hbox = new HBox(5, loginButton, setupButton);
		hbox.setAlignment(Pos.CENTER);

		// Container to hold UI elements in a nice border
		VBox layout = new VBox(10);
		layout.setMinSize(400, 220);
		layout.setMaxSize(400, 220);
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layout.getChildren().addAll(prompt, userNameField, passwordField, hbox, errorLabel);
		layout.setAlignment(Pos.CENTER);

		// StackPane to control layout sizing
		StackPane root = new StackPane(closeButtonBox, layout);
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0));

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		primaryStage.setOnShown(a -> {
			userNameField.requestFocus();
		});

		Scene scene = new Scene(root, 940, 400);
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
