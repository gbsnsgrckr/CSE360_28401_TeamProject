package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.ClipboardContent;

/**
 * AdminPage class represents the user interface for the admin user. This page
 * displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
	 * Displays the admin page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	private final DatabaseHelper databaseHelper;

	public AdminHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		TableView<User> table = new TableView<>();
		List<User> users = new ArrayList<>();

		// Label to display title to user
		Label prompt = new Label("Welcome, Administrator!");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		try {
			users = databaseHelper.getAllUsers();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all users");
		}
		
		// setting up the table columns for both Column name 
		// also setting up where it will get its data from the ObservableList
		// automatically finds the variables based on variable name 
		
		TableColumn<User, String> usernames = new TableColumn<>("Username");
		usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
		

		TableColumn<User, String> names = new TableColumn<>("Name");
		names.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<User, String> emails = new TableColumn<>("Email");
		emails.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<User, String> roles = new TableColumn<>("Roles");
		// roles.setCellValueFactory(new PropertyValueFactory<>("roles"));

		roles.setCellValueFactory(cellData -> {
			List<String> listOfRoles = cellData.getValue().getRoles();

			String displayRoles;
			if (listOfRoles == null || listOfRoles.isEmpty()) {
				displayRoles = "";
			} else {
				displayRoles = String.join(", ", listOfRoles);
			}
			return new SimpleStringProperty(displayRoles);
		});

		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails, roles);

		System.out.println("USERS:" + users); // debug
		
		// list that takes the columns cellValueFactory values that correspond to user variables
		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

		// Create backButton to return to login screen
		Button backButton = new Button("Back to login");
		backButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		backButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(newStage);
		});

		// Create inviteButton for admin to generate invitation codes
		Button inviteButton = new Button("Invite");
		inviteButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		inviteButton.setOnAction(a -> {
			new InvitationPage(databaseHelper).show(primaryStage);
		});

		TableColumn<User, Void> deleteColumn = new TableColumn<>("Delete User");
		deleteColumn.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Delete");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);  // confirmation alert box will show to confirm deletion
					alert.setTitle("Confirm Deletion");
					alert.setHeaderText("Are you sure you want to delete this user?");
					alert.setContentText("User: " + user.getUsername());

					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.OK) {  // making sure optional has data

						if (databaseHelper.deleteUser(user.getUsername())) {
							System.out.println("User deleted: " + user.getUsername());
							getTableView().getItems().remove(user);
						}
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {  // generic update method to update tableView
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(button);
				}
			}
		});

		// Add and populate Change Role column with comboBox and button to add/remove
		// roles
		TableColumn<User, Void> changeRole = new TableColumn<>("Change Role");    
		changeRole.setCellFactory(tc -> new TableCell<>() {
			private final ComboBox<String> comboBox = new ComboBox<>();
			private final Button addOrRemove = new Button("Add/Remove");
			private final HBox hbox = new HBox(5, comboBox, addOrRemove);

			{
				// Style addOrRemove button
				addOrRemove.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				// Style comboBox
				comboBox.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
						+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;"
						+ "-fx-prompt-text-fill: black;");

				// Populate comboBox
				comboBox.getItems().addAll("Admin", "Student", "Instructor", "Staff", "Reviewer");
				comboBox.setPromptText("Select a role");

				// Center the text in the dropdown list of the comboBox
				comboBox.setCellFactory(a -> new ListCell<String>() {
					@Override
					protected void updateItem(String role, boolean flag) {
						super.updateItem(role, flag);

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

						if (!flag && role != null) {
							setText(role.substring(0, 1).toUpperCase() + role.substring(1));
							setAlignment(Pos.CENTER);
						}
					}
				});

				// Disable add/remove button unless a role selection is made
				comboBox.setOnAction(a -> {
					String check = comboBox.getValue();
					addOrRemove.setDisable(check == null);

				});

				// addOrRemove button will remove role if user has it or add it if they don't
				addOrRemove.setOnAction(a -> {
					try {
						User user = getTableView().getItems().get(getIndex());        // get the correct user from the table
						String roleToAdd = comboBox.getSelectionModel().getSelectedItem();  // get the correct role to add or remove
						if (roleToAdd == null) {
							return;
						}
						if (!user.getRoles().contains(roleToAdd)) {                     // also checked in database, double checking
							if (user.getRoles() == null || user.getRoles().isEmpty()) {
								databaseHelper.updateRoles(user.getUsername(), roleToAdd);
							}
							System.out.println(user.getRoles() + "\n" + roleToAdd);
							// System.out.println(!user.getRoles().contains(roleToAdd));
							databaseHelper.addRoles(user.getUsername(), roleToAdd);
							new AdminHomePage(databaseHelper).show(primaryStage, user);
						} else {
							if (roleToAdd.equals("Admin")) {                             // 
								Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
								alert.setTitle("STOP!!");
								alert.setHeaderText("You cannot delete the 'Admin' role");
								alert.setContentText("An admin role cannot be deleted");

								Optional<ButtonType> result = alert.showAndWait();
							} else {
								System.out.println("User has this role, deleting role. ");
								databaseHelper.removeRoles(user.getUsername(), roleToAdd);
								new AdminHomePage(databaseHelper).show(primaryStage, user);
							}
						}
					} catch (SQLException e) {
						System.out.println(
								"Should print this after trying to add the role since I don't know the format for role");
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(hbox);
				}
			}
		});

		// Enable pushing to users clipboard for copy/paste functionality
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();

		// Add and populate Forgot Password column
		TableColumn<User, Void> tempPassword = new TableColumn<>("Forgot Password");
		tempPassword.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("One Time Password");  // adding button for generating OTP

			{
				// Style addOrRemove button
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

					// Removes icon from title bar in alert window
					Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
					stage.getIcons().clear();

					// Removes title text for cleaner UI
					alert.setTitle("");

					// Label for center alert text
					Label headerText = new Label();
					headerText.setText("\nYou are about to generate a One Time Password for this user!");
					headerText.setAlignment(Pos.CENTER);

					// Label for center alert text
					Label contentText = new Label();
					contentText.setText("  User:\n" + user.getUsername());
					contentText.setAlignment(Pos.CENTER);

					alert.getDialogPane().setHeader(headerText);
					alert.getDialogPane().setContent(contentText);

					// Center buttons in confirmation window
					alert.getDialogPane().lookup(".button-bar").setStyle("-fx-alignment: center;");

					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.OK) {
						String OTP = databaseHelper.generateOneTimePassword();

						Alert otpConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

						// Removes icon from title bar in alert window
						Stage stage1 = (Stage) otpConfirmationAlert.getDialogPane().getScene().getWindow();
						stage1.getIcons().clear();

						// Removes title text for cleaner UI
						otpConfirmationAlert.setTitle("");

						// Label for center alert text
						Label headerSubText = new Label();
						headerSubText.setText("\nYour ONE TIME PASSWORD is " + OTP);
						headerSubText.setStyle("-fx-font-weight: bold;");
						headerSubText.setAlignment(Pos.CENTER);

						// Label for center alert text
						Label contentSubText = new Label();
						contentSubText.setText("  Please write this down and do not share it with anyone!"
								+ "\nIf the OTP is lost or expires, another OTP must be generated"
								+ "\n\n     This will be automatically copied to your clipboard!");
						contentSubText.setAlignment(Pos.CENTER);

						// Put labels into alert
						otpConfirmationAlert.getDialogPane().setHeader(headerSubText);
						otpConfirmationAlert.getDialogPane().setContent(contentSubText);

						// Center buttons in cofirmation window
						otpConfirmationAlert.getDialogPane().lookup(".button-bar").setStyle("-fx-alignment: center;");

						Optional<ButtonType> otpResult = otpConfirmationAlert.showAndWait();

						if (otpResult.isPresent() && otpResult.get() == ButtonType.OK) {
							user.setPassword(OTP);
							databaseHelper.updatePassword(user.getUsername(), OTP);

							// Push OTP to users clipboard so they may easily copy/paste
							content.putString(OTP);
							clipboard.setContent(content);

							user.setOTPFlag(true);
							databaseHelper.updateOTPFlag(user.getUsername(), true);
							System.out.println(user.getPassword()); // Debug
						}
					}
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(button);
				}
			}
		});
		
		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");

		table.getColumns().add(changeRole);
		table.getColumns().add(deleteColumn);
		table.getColumns().add(tempPassword);

		HBox hbox = new HBox(5, backButton, inviteButton);
		HBox header = new HBox(5, prompt);
		header.setAlignment(Pos.CENTER);
		VBox vbox = new VBox(header, table);
		vbox.getChildren().addAll(hbox);
		Scene scene = new Scene(vbox, 940, 400);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
