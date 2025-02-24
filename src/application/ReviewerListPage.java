package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ReviewerListPage {
	
	private final DatabaseHelper databaseHelper;

	private Stage primaryStage; 
	
	public ReviewerListPage(Stage primaryStage, DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.primaryStage = primaryStage;	
	}

	public void show( User user) {
		
		TableView<User> userTable = createUserTable(user);
		TableView<User> reviewerTable = createReviewerTable(user);
		
		Button backButton = new Button("Back");
		backButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		backButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new StudentHomePage(databaseHelper).show(newStage);
		});

		
		
		HBox hbox = new HBox(5, backButton);
		VBox vbox = new VBox(userTable, reviewerTable);
		vbox.getChildren().addAll(hbox);
		Scene scene = new Scene(vbox, 940, 400);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.show();
		
		
		
		
	}

	public void displayAddPopup(User user, User newReviewer) {
	        Stage popupStage = new Stage();
	        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with the main window
	        popupStage.setTitle("User Information");

	        // User Information
	        Label nameLabel = new Label("Name: " + user.getName());
	        Label emailLabel = new Label("Email: " + user.getEmail());

	        // Dropdown for assigning weight (1-6)
	        ComboBox<Integer> weightDropdown = new ComboBox<>();
	        weightDropdown.getItems().addAll(1, 2, 3, 4, 5);
	        weightDropdown.setValue(1); // Default selection

	        // Submit button
	        Button submitButton = new Button("Assign Weight");
	        submitButton.setOnAction(e -> {
	        	
	            int weight = weightDropdown.getValue();
	            
	            databaseHelper.addReviewer(user.getUserId(), newReviewer, weight);
	            
	            System.out.println("Assigned weight to " + user.getName() + ": " + weight);
	            popupStage.close(); // Close the popup
	            new ReviewerListPage(primaryStage, databaseHelper).show(user);
	        });

	        VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, submitButton);
	        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

	        Scene scene = new Scene(layout, 300, 200);
	        popupStage.setScene(scene);
	        popupStage.showAndWait(); // Blocks execution until popup is closed
	    }
	
	
	public void displayManagePopup(User user, User reviewer) {
		Stage popupStage = new Stage();
	    popupStage.initModality(Modality.APPLICATION_MODAL);
	    popupStage.setTitle("Manage User");

	    // User Information Labels
	    Label nameLabel = new Label("Name: " + user.getName());
	    Label emailLabel = new Label("Email: " + user.getEmail());

	    // Dropdown for reassigning weight (1-6)
	    ComboBox<Integer> weightDropdown = new ComboBox<>();
	    weightDropdown.getItems().addAll(1, 2, 3, 4, 5, 6);
	    weightDropdown.setValue(1); // Default selection

	    // Reassign Weight Button
	    Button reassignButton = new Button("Reassign Weight");
	    reassignButton.setOnAction(e -> {
	        int weight = weightDropdown.getValue();
	        databaseHelper.updateReviewerWeight(user.getUserId(), reviewer, weight);
	        System.out.println("Reassigned weight to " + user.getName() + ": " + weight);
	        popupStage.close();
	        new ReviewerListPage(primaryStage, databaseHelper).show(user);
	    });

	    // Delete User Button
	    Button deleteButton = new Button("Delete Reviewer");
	    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Red delete button for emphasis
	    deleteButton.setOnAction(e -> {
	        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
	                "Are you sure you want to remove " + user.getName() + "as a reviewer?", 
	                ButtonType.YES, ButtonType.NO);
	        confirmation.showAndWait().ifPresent(response -> {
	            if (response == ButtonType.YES) {
	                databaseHelper.removeReviewer(user.getUserId(), reviewer);
	                System.out.println("Removed Reviewer: " + user.getName());
	                popupStage.close();
	                new ReviewerListPage(primaryStage, databaseHelper).show(user);
	            }
	        });
	    });

	    // Layout
	    VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, reassignButton, deleteButton);
	    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

	    Scene scene = new Scene(layout, 300, 250);
	    popupStage.setScene(scene);
	    popupStage.showAndWait();
    }
	
	public TableView<User> createUserTable(User user) {
		TableView<User> table = new TableView<>();
		List<User> users = new ArrayList<>();

		try {
			
			///// for debugging, use the getAllUsers()
			///// for actual use the getAllUsersWithRole
			
			users = databaseHelper.getAllUsersWithRole("reviewer");
			//users = databaseHelper.getAllUsers();
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

		System.out.println("USERS:" + users.toString()); // debug


		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

		
		TableColumn<User, Void> addReviewer = new TableColumn<>("Add Reviewer");
		addReviewer.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Add Reviewer");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User newReviewer = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					displayAddPopup(user, newReviewer);
					
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
		return table;

	}

	public TableView<User> createReviewerTable(User user) {
		TableView<User> table = new TableView<>();
		Map<User, Integer> reviewersPlaceholder = new HashMap<>();
				
		// Label to display title to user
		Label prompt = new Label("Welcome, Administrator!");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		try {
			reviewersPlaceholder = databaseHelper.getAllReviewersForUser(user.getUserId());
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all users" + e.getMessage());
		}
		
		final Map<User, Integer> finalReviewers = reviewersPlaceholder;
		
		
		// setting up the table columns for both Column name 
		// also setting up where it will get its data from the ObservableList
		// automatically finds the variables based on variable name 
		
		TableColumn<User, String> usernames = new TableColumn<>("Username");
		usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
		

		TableColumn<User, String> names = new TableColumn<>("Name");
		names.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<User, String> emails = new TableColumn<>("Email");
		emails.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<User, Integer> weights = new TableColumn<>("Weight");
		weights.setCellValueFactory(cellData -> {
            User reviewer = cellData.getValue();
            Integer weight = finalReviewers.getOrDefault(reviewer, 0);            
            return new SimpleObjectProperty<>(weight); 
        });

        TableColumn<User, Void> manageReviewer = new TableColumn<>("Manage Reviewer");
        manageReviewer.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Manage Reviewer");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User newReviewer = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					displayManagePopup(user, newReviewer);
					
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
		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails, weights);

		System.out.println("REVIEWERS:" + finalReviewers.toString()); // debug
		

		List<User> users = new ArrayList<>(finalReviewers.keySet());
		
		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

	
		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");

		table.getColumns().add(manageReviewer);
		return table;

	}
	
}