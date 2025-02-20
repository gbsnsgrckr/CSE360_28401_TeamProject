package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;

/**
 * RoleSelectPage class represents the user interface for the role-selection
 * page. This page displays only if a user has multiple roles and allows the
 * user to select which role they which to play.
 */
public class RoleSelectPage {
	private final DatabaseHelper databaseHelper;
	String selectedRole;
	User user;

	public RoleSelectPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		user = databaseHelper.currentUser;
	}

	/**
	 * Displays the role-selection page. *
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {

		// Create Next and Back buttons
		Button nextButton = new Button("Next");
		nextButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		Button quitButton = new Button("Back");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Set nextButton to default to allow Enter button to activate
		nextButton.setDefaultButton(true);

		// Create combobox for selecting roles
		ComboBox<String> comboBox = new ComboBox<>();

		// Add roles text to comboBox
		comboBox.getItems().addAll(user.getRoles());

		// Set default comboBox text
		comboBox.setPromptText("Please select a role");
		comboBox.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;"
				+ "-fx-prompt-text-fill: black;");				

		// ComboBox allows selection of roles and deactivates Next button until
		// selection
		// is made
		comboBox.setOnAction(a -> {
			selectedRole = comboBox.getValue();
			if (selectedRole != null) {
				nextButton.setDisable(false);
			} else {
				nextButton.setDisable(true);
			}
		});

		// Next button to proceed with selection. Is inactive unless selection is made
		// in ComboBox. Based on role selected, go to that role's home page.
		nextButton.setOnAction(a -> {
			// Set currentRole in databaseHelper
			databaseHelper.currentUser.setCurrentRole(selectedRole);

			// Direct user to role's home page based on user selection
			switch (selectedRole) {
			case "Admin":
				new AdminHomePage(databaseHelper).show(primaryStage, user);
				break;
			case "Student":
				new StudentHomePage(databaseHelper).show(primaryStage);
				break;
			case "Instructor":
				new InstructorHomePage(databaseHelper).show(primaryStage);
				break;
			case "Staff":
				new StaffHomePage(databaseHelper).show(primaryStage);
				break;
			case "Reviewer":
				new ReviewerHomePage(databaseHelper).show(primaryStage);
				break;
			}
		});

		// Quit button to return to User Login Screen
		quitButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(primaryStage);
		});

		// Center the text in the dropdown list of the comboBox
		comboBox.setCellFactory(a -> new ListCell<String>() {
			@Override
			protected void updateItem(String role, boolean flag) {
				super.updateItem(role, flag);

				// If not empty, capitalize first letter and center text
				if (!flag && role != null) {
					setText(role.substring(0, 1).toUpperCase() + role.substring(1));
					setAlignment(Pos.CENTER);
				}
			}
		});

		// Center the text in the comboBox selection
		comboBox.setButtonCell(new ListCell<String>() {
			@Override
			protected void updateItem(String role, boolean flag) {
				super.updateItem(role, flag);

				// If not empty, capitalize first letter and center text
				if (!flag && role != null) {
					setText(role.substring(0, 1).toUpperCase() + role.substring(1));
					setAlignment(Pos.CENTER);
				}
			}
		});

		// Label to display title to user
		Label prompt = new Label("Role-Select");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Create layout for buttons and combobox
		VBox layoutV = new VBox(10);
		layoutV.setMinSize(400, 220);
		layoutV.setMaxSize(400, 220);
		layoutV.setStyle("-fx-padding: 20; -fx-background-color: gray; -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layoutV.setAlignment(Pos.CENTER);

		HBox layoutH = new HBox(10);
		layoutH.setStyle("-fx-padding: 20;");
		layoutH.setAlignment(Pos.CENTER);

		// Attach nextButton and quitButton to the same container
		layoutH.getChildren().addAll(nextButton, quitButton);

		// Attach buttons and combobox to the same container
		layoutV.getChildren().addAll(prompt, comboBox, layoutH);

		StackPane root = new StackPane(layoutV);

		// Create scene to hold UI objects
		Scene roleSelectScene = new Scene(root, 940, 400);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		// Set the scene to primary stage
		primaryStage.setScene(roleSelectScene);
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
