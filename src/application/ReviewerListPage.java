package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ReviewerListPage {
	
	private final DatabaseHelper databaseHelper;

	public ReviewerListPage(DatabaseHelper databaseHelper) {
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

		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails);

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

		TableColumn<User, Void> addReviewer = new TableColumn<>("Add Reviewer");
		addReviewer.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Add Reviewer");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User user = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					
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
	
		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");

		table.getColumns().add(addReviewer);

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