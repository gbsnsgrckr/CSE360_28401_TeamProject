package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class FindReviewerForQuestionPage {
	private final DatabaseHelper databaseHelper;
	private Question question;
	private List<Question> questions;
	private User user;
	private List<User> users;

	public FindReviewerForQuestionPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {

		// Label to display page title to the user
		Label titleLabel = new Label("Find Reviewers For Your Question");
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");

		// Button to return to the login screen
		Button quitButton = new Button("Close");
		quitButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");

		// Button to assign selected reviewer to selected question
		Button assignButton = new Button("Assign Reviewer");
		assignButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");

		quitButton.setOnAction(a -> {

			// Close the existing stage
			primaryStage.close();

		});

		// Table display of the question database
		// Create table to display the question database within
		TableView<Question> qTable = new TableView<>();
		// Styling for the table
		qTable.setMinWidth(300);
		qTable.setFixedCellSize(-1);

		qTable.setRowFactory(a -> new TableRow<Question>() {
			@Override
			protected void updateItem(Question item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		if (questions == null || questions.isEmpty()) {
			try {
				questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
			} catch (SQLException e) {
				e.printStackTrace();
				System.err
						.println("Error trying to update question table when initializing FindReviewerForQuestionPage");
				return;
			}
		}

		if (users == null || users.isEmpty()) {
			try {
				users = databaseHelper.getAllUsersWithRole("Reviewer");
			} catch (SQLException e) {
				e.printStackTrace();
				System.err
						.println("Error trying to update reviewer table when initializing FindReviewerForQuestionPage");
				return;
			}
		}

		// Create an observable list of questions and assign to the table
		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
		detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text wrapping
		detailsColumn.setCellFactory(a -> new TableCell<Question, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(true);
				textLabel.setMaxWidth(Double.MAX_VALUE);
				textLabel.setStyle("-fx-padding: 5px;");
			}

			@Override
			protected void updateItem(String item, boolean flag) {
				super.updateItem(item, flag);
				setGraphic(flag || item == null ? null : textLabel);
				if (!flag && item != null) {
					textLabel.setText(item);
					textLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
				}
			}
		});

		// Add columns to the qTable
		qTable.getColumns().addAll(detailsColumn);

		// Listener to dynamically hide the title bar of the Question Details table
		qTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = qTable.lookup("TableHeaderRow");
			if (titleBar != null && titleBar.isVisible()) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Table display of the users with the role of reviewer
		// Create table to display the reviewers to the user
		TableView<User> rTable = new TableView<>();
		// Styling for the table
		rTable.setMinWidth(300);
		rTable.setFixedCellSize(-1);

		rTable.setRowFactory(a -> new TableRow<User>() {
			@Override
			protected void updateItem(User item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		// Create an observable list of questions and assign to the table
		ObservableList<User> reviewerObservableList = FXCollections.observableArrayList(users);
		rTable.setItems(reviewerObservableList);

		TableColumn<User, String> reviewerColumn = new TableColumn<>("Reviewers");
		reviewerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text wrapping
		reviewerColumn.setCellFactory(a -> new TableCell<User, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(true);
				textLabel.setMaxWidth(Double.MAX_VALUE);
				textLabel.setStyle("-fx-padding: 5px;");
			}

			@Override
			protected void updateItem(String item, boolean flag) {
				super.updateItem(item, flag);
				setGraphic(flag || item == null ? null : textLabel);
				if (!flag && item != null) {
					textLabel.setText(item);
					textLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
				}
			}
		});

		// Add columns to the qTable
		rTable.getColumns().addAll(reviewerColumn);

		// Listener to dynamically hide the title bar of the Question Details table
		rTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = rTable.lookup("TableHeaderRow");
			if (titleBar != null && titleBar.isVisible()) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Create filter button to adjust question table database view
		ToggleGroup filter = new ToggleGroup();

		RadioButton allButton = new RadioButton("All");
		allButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		allButton.setToggleGroup(filter);

		RadioButton unansweredButton = new RadioButton("Unanswered");
		unansweredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		unansweredButton.setToggleGroup(filter);
		unansweredButton.setSelected(true);

		RadioButton answeredButton = new RadioButton("Answered");
		answeredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		answeredButton.setToggleGroup(filter);

		HBox filterBox = new HBox(10, allButton, unansweredButton, answeredButton);
		filterBox.setAlignment(Pos.CENTER);

		filter.selectedToggleProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				RadioButton selected = (RadioButton) newSelection;
				String selection = selected.getText();

				try {
					if (selection.equalsIgnoreCase("All")) {
						questions = databaseHelper.qaHelper.getAllQuestions();
					} else if (selection.equalsIgnoreCase("Unanswered")) {
						questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
					} else if (selection.equalsIgnoreCase("Answered")) {
						questions = databaseHelper.qaHelper.getAllAnsweredQuestions();
					}
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error trying to update question table via radio buttons");
					return;
				}
				questionObservableList.setAll(questions);
				qTable.setItems(questionObservableList);
				qTable.refresh();
			}
		});

		assignButton.setOnAction(a -> {
			Question qSelection = qTable.getSelectionModel().getSelectedItem();
			User rSelection = rTable.getSelectionModel().getSelectedItem();

			// Check if titleInput is empty or null
			if (qSelection == null || rSelection == null) {
				errorLabel.setText("Error, you must select a QUESTION AND a REVIEWER");
				return;
			} else {
				
				// Insert logic to assign a question to a reviewer
				
				
			}

		});

		// Create filter button to adjust reviewer table view
		ToggleGroup reviewerFilter = new ToggleGroup();

		RadioButton reviewerAllButton = new RadioButton("All");
		reviewerAllButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		reviewerAllButton.setToggleGroup(reviewerFilter);

		RadioButton reviewerPreferredButton = new RadioButton("Preferred");
		reviewerPreferredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		reviewerPreferredButton.setToggleGroup(reviewerFilter);
		reviewerPreferredButton.setSelected(true);

		HBox reviewerFilterBox = new HBox(10, reviewerAllButton, reviewerPreferredButton);
		reviewerFilterBox.setAlignment(Pos.CENTER);

		reviewerFilter.selectedToggleProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				RadioButton selected = (RadioButton) newSelection;
				String selection = selected.getText();

				try {
					if (selection.equalsIgnoreCase("All")) {
						users = databaseHelper.getAllUsersWithRole("Reviewer");
					} else if (selection.equalsIgnoreCase("Preferred")) {
						/*
						 * PLACEHOLDER FOR METHOD TO RETURN PREFERRED USERS FROM DATABASE users =
						 * databaseHelper.getPreferredReviewers(databaseHelper.currentUser);
						 */
						
						// REMOVE THIS WHEN YOU ADD PROPER LOGIC
						users = databaseHelper.getAllUsersWithRole("Reviewer");						
					}
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error trying to update reviewer table via radio buttons");
					return;
				}
				reviewerObservableList.setAll(users);
				rTable.setItems(reviewerObservableList);
				rTable.refresh();
			}
		});

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {

				// Store newSelection as question
				this.question = newSelection;
			} else {

			}
		});

		HBox topBox = new HBox(titleLabel);

		VBox buttonBox = new VBox(assignButton);

		VBox questionBox = new VBox(5, filterBox, qTable);

		VBox reviewerBox = new VBox(5, reviewerFilterBox, rTable);

		HBox middleBox = new HBox(200, questionBox, buttonBox, reviewerBox);

		VBox buttonBox2 = new VBox(5, quitButton, errorLabel);

		HBox bottomBox = new HBox(buttonBox2);

		VBox vbox = new VBox(5, topBox, middleBox, bottomBox);

		StackPane root = new StackPane(vbox);

		questionBox.setAlignment(Pos.CENTER_LEFT);
		reviewerBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.setAlignment(Pos.CENTER);
		quitButton.setAlignment(Pos.CENTER);
		titleLabel.setAlignment(Pos.CENTER);
		buttonBox2.setAlignment(Pos.CENTER);
		middleBox.setAlignment(Pos.CENTER);
		bottomBox.setAlignment(Pos.CENTER);
		vbox.setAlignment(Pos.CENTER);
		topBox.setAlignment(Pos.CENTER);		

		// Adjust errorLabel downward just a bit
		errorLabel.setTranslateY(22);

		// Set height of table to adjust to container
		qTable.prefHeightProperty().bind(middleBox.heightProperty());
		rTable.prefHeightProperty().bind(middleBox.heightProperty());
		detailsColumn.prefWidthProperty().bind(questionBox.widthProperty().subtract(19));
		reviewerColumn.prefWidthProperty().bind(reviewerBox.widthProperty().subtract(19));

		Scene scene = new Scene(root, 1600, 1000);

		middleBox.prefWidthProperty().bind(root.widthProperty());
		// searchBox.prefHeightProperty().bind(hbox1.heightProperty().subtract(50));

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.centerOnScreen();
		// primaryStage.setMaximized(true);
		primaryStage.show();
	}
}
