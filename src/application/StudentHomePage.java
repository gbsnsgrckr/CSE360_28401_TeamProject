package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This page displays a simple welcome message for the user.
 */
public class StudentHomePage {
	// Class to allow for dynamic rows within a table
	public class QATableRow {
		public enum RowType {
			QUESTION, ANSWER
		}

		private final RowType type;
		private final String text;
		private final Integer contentId;
		private final Integer authorId;
		private final List<String> relatedId;

		// Wrapper class for resultsTable to allow for dynamic rows
		public QATableRow(RowType type, String text, Integer contentId, List<String> relatedId) {
			this.type = type;
			this.text = text;
			this.contentId = contentId;
			this.authorId = null;
			this.relatedId = relatedId;

		}

		public QATableRow(RowType type, String text, Integer contentId, Integer authorId, List<String> relatedId) {
			this.type = type;
			this.text = text;
			this.contentId = contentId;
			this.authorId = authorId;
			this.relatedId = relatedId;

		}

		public Integer getQuestionId() {
			return contentId;
		}

		public Integer getAnswerId() {
			return contentId;
		}

		public Integer getAuthorId() {
			return authorId;
		}

		public String getText() {
			return text;
		}

		public RowType getType() {
			return type;
		}

		public List<String> getRelatedId() {
			return relatedId;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	private final DatabaseHelper databaseHelper;
	private Question question;
	private Answer answer;
	private List<Question> questions;
	private List<Answer> answers;
	private List<QuestionsSet> questionsSet;
	private List<AnswersSet> answersSet;
	private ObservableList<QATableRow> resultsObservableList = FXCollections.observableArrayList();
	private TableView<Question> qTable;
	private List<Question> sortedList;
	private boolean updatingAnswer = false;
	private boolean updatingQuestion = false;

	private TableView<QATableRow> resultsTable;

	private int indent = 0;

	public StudentHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };

		try {
			questions = databaseHelper.qaHelper.getAllQuestions();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all QUESTIONS");
			e.printStackTrace();
		}

		try {
			answers = databaseHelper.qaHelper.getAllAnswers();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all ANSWERS");
			e.printStackTrace();
		}

		// Label to display title of the page to the user
		Label topLabel = new Label("Student Home Page");
		topLabel.setStyle(
				"-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: derive(gray, -20%)");

		VBox topLabelBox = new VBox(topLabel);
		topLabelBox.setStyle(" -fx-background-color: derive(gray, -20%)");

		// Label to display title of area to the user
		Label prompt = new Label("Entry Box");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Input box for a title
		TextArea titleField = new TextArea();
		titleField.setPromptText("Enter a title for your question.");
		// Styling for the titleField
		titleField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		titleField.setMaxWidth(600);
		titleField.setPrefWidth(600);
		titleField.setMaxHeight(60);
		titleField.setPrefHeight(60);

		// Input box for body of question
		TextArea inputField = new TextArea();
		inputField.setPromptText("Ask your question in more detail");
		// Styling for the inputField
		inputField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		inputField.setMaxWidth(600);
		inputField.setPrefWidth(600);
		inputField.setMaxHeight(900);
		inputField.setPrefHeight(900);
		inputField.setWrapText(true);

		// Button to submit question text in input fields to database
		Button submitButton = new Button("Submit Question");
		submitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to return to the login screen
		Button quitButton = new Button("Back to login");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to find reviewers for your questions
		Button findReviewerButton = new Button("Find Reviewer");
		findReviewerButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		Button manageReviewersButton = new Button("Manage my Reviewers");
		manageReviewersButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to open the ui to submit a new question
		Button newQuestionButton = new Button("New");
		newQuestionButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
		newQuestionButton.setMinWidth(20);
		;

		// Button to open the ui to submit a new question
		Button questionCloseButton = new Button("Close");
		questionCloseButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Table display of the question database
		// Create table to display the question database within
		TableView<Question> qTable = new TableView<>();

		// Give qTable a bold outline
		qTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

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
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px; -fx-table-cell-border-color: black;");
				}
			}
		});

		// Create an observable list of questions and assign to the table
		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
		detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		detailsColumn.setCellFactory(a -> new TableCell<Question, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(false);
				textLabel.setEllipsisString("...");
				textLabel.setMaxWidth(Double.MAX_VALUE);
				textLabel.setStyle("-fx-text-overrun: ellipsis; -fx-padding: 2px;");
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

		/*
		 * Answer Database Table that we shouldn't need // Table display of the answer
		 * database // Label to display title to user Label prompt3 = new
		 * Label("Answer Database"); prompt3.
		 * setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;"
		 * );
		 * 
		 * // Hbox to position the title HBox titleBox3 = new HBox(prompt3);
		 * titleBox3.setAlignment(Pos.CENTER);
		 * 
		 * // Create table to display the answer database TableView<Answer> aTable = new
		 * TableView<>(); aTable.
		 * setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;"
		 * ); aTable.setPrefWidth(600);
		 * 
		 * // if answers is null then initialize as an empty list if (answers == null) {
		 * answers = new ArrayList<>(); }
		 * 
		 * // Create an observable list and assign it to the table
		 * ObservableList<Answer> answerObservableList =
		 * FXCollections.observableArrayList(answers);
		 * aTable.setItems(answerObservableList);
		 * 
		 * // Create, assign, and associate values to table TableColumn<Answer, Integer>
		 * idColumn2 = new TableColumn<>("Answer ID");
		 * idColumn2.setCellValueFactory(data -> new
		 * ReadOnlyObjectWrapper<>(data.getValue().getId()));
		 * 
		 * // Create a text column TableColumn<Answer, String> textColumn2 = new
		 * TableColumn<>("Answer"); textColumn2.setCellValueFactory(new
		 * PropertyValueFactory<>("text"));
		 * 
		 * // Create a userID column TableColumn<Answer, Integer> authorColumn2 = new
		 * TableColumn<>("Author ID"); authorColumn2.setCellValueFactory(new
		 * PropertyValueFactory<>("author"));
		 * 
		 * // Create a createOn column TableColumn<Answer, String> createdColumn2 = new
		 * TableColumn<>("Created On"); createdColumn2.setCellValueFactory(new
		 * PropertyValueFactory<>("createdOn"));
		 * 
		 * // Create an updatedOn column TableColumn<Answer, String> updatedColumn2 =
		 * new TableColumn<>("Updated On"); updatedColumn2.setCellValueFactory(new
		 * PropertyValueFactory<>("updatedOn"));
		 * 
		 * aTable.getColumns().addAll(idColumn2, textColumn2, authorColumn2,
		 * createdColumn2, updatedColumn2);
		 * 
		 * // Container to hold the table VBox answerDB = new VBox(5, titleBox3,
		 * aTable);
		 */

		// Label to display title to user
		Label prompt5 = new Label("Details");
		prompt5.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to position the title
		HBox titleBox5 = new HBox(prompt5);
		titleBox5.setAlignment(Pos.CENTER);

		resultsTable = new TableView<>();
		resultsTable.setItems(resultsObservableList);
		resultsTable.setRowFactory(a -> new TableRow<QATableRow>() {
			@Override
			protected void updateItem(QATableRow item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		// Hbox to hold and position the title
		HBox topTitleBox = new HBox(135, submitButton, questionCloseButton);
		// Set alignment of box
		questionCloseButton.setAlignment(Pos.TOP_RIGHT);
		submitButton.setAlignment(Pos.TOP_LEFT);

		// Container to hold the two input boxes for questions and their title together
		VBox questionInputBox = new VBox(5, topTitleBox, titleField, inputField);
		questionInputBox.setStyle("-fx-background-color: derive(gray, 80%)");

		VBox submitBox = new VBox(questionInputBox);

		TableColumn<QATableRow, String> contentColumn = new TableColumn<>("Results");
		contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		contentColumn.setCellFactory(a -> new TableCell<QATableRow, String>() {
			private final TextArea replyArea = new TextArea();
			private final Button submitReplyButton = new Button("Submit");
			private final VBox replyBox = new VBox(5, submitReplyButton, replyArea);
			private final VBox cellContent = new VBox(5);
			private final HBox cellBox = new HBox();

			{
				submitReplyButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
				replyBox.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
				replyBox.setStyle("-fx-padding: 1px;");

				// Set prompt text for replyArea
				replyArea.setPromptText("Enter your answer here...");
				replyArea.setPrefRowCount(3);

				submitReplyButton.setOnAction(a -> {
					String inputText = replyArea.getText().trim();
					QATableRow.RowType rowType = getTableView().getItems().get(getIndex()).getType();
					if (!inputText.isEmpty()) {
						try {
							if (rowType == QATableRow.RowType.QUESTION) {
								databaseHelper.qaHelper.registerAnswerWithQuestion(
										new Answer(inputText, databaseHelper.currentUser.getUserId()),
										getTableView().getItems().get(getIndex()).getAnswerId());
								replyArea.clear();
							} else {
								databaseHelper.qaHelper.registerAnswerWithAnswer(
										new Answer(inputText, databaseHelper.currentUser.getUserId()),
										getTableView().getItems().get(getIndex()).getAnswerId());
								replyArea.clear();
							}

						} catch (SQLException e) {
							e.printStackTrace();
							System.err
							.println("Error trying to register answer in results table via submitReplyButton");
						}
						// Update the table after submitting new answer
						updateResultsTableForQuestion(qTable.getSelectionModel().getSelectedItem());
					}
				});
				cellContent.getChildren().addAll(replyBox);
				cellContent.setAlignment(Pos.CENTER_LEFT);
			}

			@Override
			protected void updateItem(String item, boolean flag) {
				super.updateItem(item, flag);

				// Clear container
				cellContent.getChildren().clear();
				cellBox.getChildren().clear();

				// variable to compound indents
				// int indent = 2;

				// Clear graphic
				setGraphic(null);
				if (flag && item == null) {
					setText(null);
				} else {

					// Get current QATableRow
					QATableRow row = getTableView().getItems().get(getIndex());
					// Create a label to hold the text
					Label displayLabel = new Label(item);
					displayLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black;");
					displayLabel.setWrapText(true);

					displayLabel.maxWidthProperty().bind(contentColumn.widthProperty().subtract(50));

					// Set the preferred height of the cell
					displayLabel.setPrefHeight(225);

					// Make sure you're on at least the second row
					if (getIndex() > 0) {
						QATableRow currentRow = getTableView().getItems().get(getIndex());
						QATableRow previousRow = getTableView().getItems().get(getIndex() - 1);

						// Add a spacer for any thread below the main question
						Region spacer = new Region();
						spacer.setStyle("-fx-border-color: black;");
						spacer.setMinSize(50, 5);
						spacer.setMaxSize(50, 5);
						cellBox.getChildren().add(spacer);

						// Make sure you're at least on third row
						if (getIndex() > 1) {

							// Check if row item is in the relatedId list for the row item above
							if (previousRow.getRelatedId() != null
									&& previousRow.getRelatedId().contains(currentRow.getAnswerId().toString())) {

								// Trying to get compounding indentation to work here. private variable indent
								// already exists at class level

							} else {
								// Reset indent counter on an unrelated row
								indent = 0;
							}

						}
					}

					// Check if the currentUser matches the author of the answer in the cell
					if (row.getType() == QATableRow.RowType.ANSWER && row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						// Buttons to edit and delete the answer
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						// Styling for buttons
						editButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");

						editButton.setOnAction(a -> {
							// Set submit button text
							submitButton.setText("Update Answer");

							// Show the submitBox
							submitBox.setVisible(true);
							submitBox.setManaged(true);

							// Set updating flag to true
							updatingAnswer = true;

							try {
								// Set answer to current row object
								answer = databaseHelper.qaHelper.getAnswer(row.getAnswerId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println("Error trying to get answer in results table via editButton");
							}

							// Set inputField to existing answer text to update
							inputField.setText(answer.getText());

						});

						deleteButton.setOnAction(a -> {
							// Delete selected answer
							databaseHelper.qaHelper.deleteAnswer(row.getAnswerId());

							try {
								// Retrieve an updated list of questions from the database
								question = qTable.getSelectionModel().getSelectedItem();
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update answers object via getALLUsers() in resultsTable");
							}

							// Refresh contents of tables manually
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);

							// Set qTable to previous question
							qTable.getSelectionModel().select(question);

						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);

						cellContent.getChildren().addAll(displayLabel, buttonBox);

						setGraphic(cellBox);
						setText(null);
						// Check if row is a question and the author matches current user
					} else if (row.getType() == QATableRow.RowType.QUESTION && row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						// Buttons to edit and delete the answer
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						// Styling for buttons
						editButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");

						editButton.setOnAction(a -> {
							// Set submit button text
							submitButton.setText("Update Question");

							// Show the submitBox
							submitBox.setVisible(true);
							submitBox.setManaged(true);

							// Set updating flag to true
							updatingQuestion = true;

							try {
								// Set answer to current row object
								question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println("Error trying to get question in results table via editButton");
							}

							// Set titleField to existing answer text to update
							titleField.setText(question.getTitle());

							// Set inputField to existing answer text to update
							inputField.setText(question.getText());
						});

						deleteButton.setOnAction(a -> {
							// Delete selected question
							databaseHelper.qaHelper.deleteQuestion(row.getQuestionId());

							try {
								// Retrieve an updated list of questions from the database
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update answers object via getALLUsers() in resultsTable");
							}

							// Refresh contents of tables manually
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);

							// Set qTable to previous question
							qTable.getSelectionModel().select(question);

						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);

						cellContent.getChildren().addAll(displayLabel, buttonBox);

						setGraphic(cellBox);
						setText(null);
					} else if (row.getType() == QATableRow.RowType.QUESTION) {

						// This will make sure the first question in row 1 is saved to a question object
						// for updating an answer object later
						try {
							// Set question to current row object
							question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
						} catch (SQLException e) {
							e.printStackTrace();
							System.err.println("Error trying to get question in results table via editButton");
						}

						cellContent.getChildren().add(0, displayLabel);
						setGraphic(cellBox);
						setText(null);
					} else {
						cellContent.getChildren().add(0, displayLabel);
						setGraphic(cellBox);
						setText(null);
					}
					cellContent.getChildren().add(replyBox);
					cellBox.getChildren().add(cellContent);

				}
			}
		});

		// Set columns to resultsTable
		resultsTable.getColumns().setAll(contentColumn);

		// Give resultsTable a bold outline
		resultsTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

		// Create and place a placeholder for when resultsTable is empty(At the start of
		// the page)
		Label placeholderBox = new Label("◄ Question everything... ►");
		placeholderBox.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: derive(gray, 80%)");
		resultsTable.setPlaceholder(placeholderBox);

		// Hide header for resultsTable
		resultsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = resultsTable.lookup("TableHeaderRow");
			if (titleBar != null) {
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
		allButton.setSelected(true);

		RadioButton unansweredButton = new RadioButton("Unanswered");
		unansweredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		unansweredButton.setToggleGroup(filter);
		;

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

		// Hbox to position the filter button
		HBox titleBox2 = new HBox(30, newQuestionButton, filterBox);
		titleBox2.setAlignment(Pos.CENTER_RIGHT);

		// Container to hold the table
		VBox questionDB = new VBox(5, titleBox2, qTable);

		// Set height of table to adjust to container
		qTable.prefHeightProperty().bind(questionDB.heightProperty());

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
		errorLabel.setTranslateY(22);

		// "Back to login" button will bring user back to the login screen
		quitButton.setOnAction(a -> {

			// Create new stage to add transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(newStage);
		});

		// Create an inputField to initiate and type search
		TextField searchField = new TextField();
		searchField.setPromptText("Search questions...");
		searchField.setStyle(
				"-fx-alignment: center-left; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");
		searchField.setPrefWidth(250);

		// Create an observable list of questions and assign to the table
		ObservableList<Question> searchObservableList = FXCollections.observableArrayList(questions);

		// Create table to display the search results
		TableView<Question> searchTable = new TableView<>();
		searchTable.setItems(searchObservableList);
		searchTable.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Create container to hold searchTable and allow for hiding and showing of it
		HBox searchBox = new HBox(searchTable);

		// Create a column for the searchTable that will hold the content
		TableColumn<Question, String> searchColumn = new TableColumn<>();
		searchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplayWithText()));

		// Style the searchTable with bold lines and black fonts etc..
		searchTable.setRowFactory(a -> new TableRow<Question>() {
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

		// Add cell factory to deal with text wrapping and styling
		searchColumn.setCellFactory(a -> new TableCell<Question, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(true);
				textLabel.setStyle("-fx-padding: 1px;");
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

		// Add searchColumn to searchTable
		searchTable.getColumns().add(searchColumn);

		// Hide header for searchTable
		searchTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = searchTable.lookup("TableHeaderRow");
			if (titleBar != null) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Hide table until needed
		searchBox.setVisible(false);
		searchBox.setManaged(false);

		// Search question database for similar entries after/while typing
		searchField.setOnKeyReleased(a -> {
			String input = searchField.getText().trim();
			if (!input.isEmpty()) {

				searchField.setMinWidth(searchTable.getWidth());

				// Make table visible
				searchBox.setVisible(true);
				searchBox.setManaged(true);

				// Get list words from current text input string
				List<String> entry = databaseHelper.qaHelper.textDeserial(input);
				try {
					questions = databaseHelper.qaHelper.getAllQuestions();
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error trying to .getAllQuestions() within inputField .setOnKeyReleased()");
					return;
				}

				// Use Hashmap to remove duplicates
				Map<Question, Integer> similarity = new HashMap<>();

				for (Question question : questions) {
					// Get list of words to compare from current question
					List<String> compList = question.getComp();
					int count = 0;

					// Count the matches
					for (String word : entry) {
						if (compList.contains(word)) {
							count++;
						}
					}

					// Set initial threshold to add comp to map
					if (count > 2) {
						similarity.put(question, count);
					}
				}

				// Sort based on similarity score
				sortedList = similarity.entrySet().stream()
						.sorted(Map.Entry.<Question, Integer>comparingByValue().reversed()).map(Map.Entry::getKey)
						.collect(Collectors.toList());

				// Set the observable list
				searchObservableList.setAll(sortedList);

				// Make table visible if it isn't
				if (!searchBox.isVisible()) {
					searchBox.setVisible(true);
					searchBox.setManaged(true);
				}
			} else {
				// Clear searchObservableList and hide searchTable
				searchObservableList.clear();
				searchBox.setVisible(false);
				searchBox.setManaged(false);

				// Set searchField width to default
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);

				// Reset searchField position
				searchField.setAlignment(Pos.TOP_RIGHT);
			}
		});

		searchField.setOnMouseClicked(a -> {
			qTable.getSelectionModel().clearSelection();

			// Show searchTable
			searchBox.setVisible(true);
			searchBox.setManaged(true);
		});

		searchTable.setOnMouseClicked(a -> {
			Question selection = searchTable.getSelectionModel().getSelectedItem();
			if (selection != null) {

				// Clear questionObservableList
				questionObservableList.clear();

				// Set the sortedList which has the recent search results saved to the
				// questionObservableList
				questionObservableList.setAll(sortedList);

				// Set the observable list to the qTable
				qTable.setItems(questionObservableList);

				// Select the choice on the qTable
				qTable.getSelectionModel().select(selection);

				// Deselect filter buttons after sending custom search to table
				allButton.setSelected(false);
				unansweredButton.setSelected(false);
				answeredButton.setSelected(false);
			}
		});

		// Button to submit an answer or question from the input fields
		submitButton.setOnAction(a -> {
			String titleInput = titleField.getText();
			String textInput = inputField.getText();

			// Check if textInput is empty or null
			if (textInput == null || textInput.trim().isEmpty()) {
				errorLabel.setText("Error, question-body field is blank.");
				return;
			}
			try {
				if (!updatingQuestion && !updatingAnswer) {

					// Check if titleInput is empty or null
					if (titleInput == null || titleInput.trim().isEmpty()) {
						errorLabel.setText("Error, question-title field is blank.");
						return;
					}

					// Check if inputs are empty or null
					Question newQuestion;
					Question newSelection = qTable.getSelectionModel().getSelectedItem();

					if (newSelection != null) {
						// Update the currently selected question
						newSelection.setTitle(titleInput);
						newSelection.setText(textInput);
						databaseHelper.qaHelper.updateQuestion(newSelection);
						newQuestion = newSelection;
					} else {
						// Register a new question in the database
						newQuestion = new Question(titleInput, textInput, databaseHelper.currentUser.getUserId());
						databaseHelper.qaHelper.registerQuestion(newQuestion);

						// Retrieve full question object for new question
						newQuestion = databaseHelper.qaHelper.getQuestion(newQuestion.getTitle());
					}

					// Clear input fields for new inputs
					titleField.clear();
					inputField.clear();

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					qTable.getSelectionModel().select(newQuestion);

				} else if (updatingAnswer) {

					// Add updated text to answer object
					answer.setText(textInput);

					// Update answer
					databaseHelper.qaHelper.updateAnswer(answer);

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					// Set qTable selection to previous question
					qTable.getSelectionModel().select(question);

				} else if (updatingQuestion) {

					// Add updated title to question object
					question.setTitle(titleInput);

					// Add updated text to question object
					question.setText(textInput);

					// Update answer
					databaseHelper.qaHelper.updateQuestion(question);

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					// Set qTable selection to previous selection
					qTable.getSelectionModel().select(question);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error trying to register new question into database via submit button");
				;
				return;
			}

			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);

			// Set both updating flags to false
			updatingQuestion = false;
			updatingAnswer = false;

		});

		findReviewerButton.setOnAction(a -> {

			// Create a new stage in order to popup new window and keep this one
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			new FindReviewerForQuestionPage(databaseHelper).show(newStage);

		});

		manageReviewersButton.setOnAction(a -> {
			// Create a new stage in order to popup new window and keep this one

			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new ReviewerListPage(newStage, databaseHelper).show(databaseHelper.currentUser);
		});

		// Add listeners for the textArea title field
		titleField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
				// aTable.getSelectionModel().clearSelection();
			}
		});

		// Add listeners for the textArea input field
		inputField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
				// aTable.getSelectionModel().clearSelection();
			}
		});

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				// Clear selections on the other tables
				// aTable.getSelectionModel().clearSelection();

				// Hide the searchBox when using the qTable
				searchBox.setVisible(false);
				searchBox.setManaged(false);

				// Reset position of the searchField
				searchField.setAlignment(Pos.TOP_RIGHT);

				// Set searchField width back to default
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);

				this.question = newSelection;

				// Update text for buttons
				submitButton.setText("Update Question");

				// Update results table
				updateResultsTableForQuestion(newSelection);
			}
		});

		searchField.setAlignment(Pos.TOP_RIGHT);
		searchBox.setAlignment(Pos.BOTTOM_RIGHT);

		// Use containers to position and hold many different UI components
		HBox hboxTop = new HBox(10, searchField);
		hboxTop.setAlignment(Pos.CENTER_RIGHT);

		VBox vboxTop = new VBox(10, hboxTop);
		vboxTop.setAlignment(Pos.CENTER_RIGHT);

		StackPane root2 = new StackPane(resultsTable, searchBox);

		VBox vbox = new VBox(10, vboxTop, root2);
		vbox.setAlignment(Pos.CENTER);

		HBox reviewerButtonBox = new HBox(findReviewerButton);
		reviewerButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

		HBox manageReviewerButtonBox = new HBox(manageReviewersButton);
		manageReviewerButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

		HBox quitButtonBox = new HBox(quitButton);
		quitButtonBox.setAlignment(Pos.BOTTOM_LEFT);

		HBox buttonBox1 = new HBox(10, quitButtonBox, reviewerButtonBox, manageReviewerButtonBox);
		quitButton.setAlignment(Pos.BOTTOM_LEFT);
		findReviewerButton.setAlignment(Pos.BOTTOM_RIGHT);

		VBox vbox1 = new VBox(10, vbox, buttonBox1);
		vbox1.setAlignment(Pos.CENTER);

		// Hide submitBox unit needed
		submitBox.setVisible(false);
		submitBox.setManaged(false);

		newQuestionButton.setOnAction(a -> {
			// Set submit button text
			submitButton.setText("Submit Question");

			// Show submitBox
			submitBox.setVisible(true);
			submitBox.setManaged(true);

		});

		questionCloseButton.setOnAction(a -> {
			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);
		});

		StackPane root3 = new StackPane(questionDB, submitBox);

		HBox hbox1 = new HBox(5, root3, vbox1);

		VBox layout = new VBox(topLabelBox, hbox1, errorLabel);

		layout.setOnMousePressed(a -> {
			offsetX[0] = a.getSceneX();
			offsetY[0] = a.getSceneY();
		});

		layout.setOnMouseDragged(a -> {
			primaryStage.setX(a.getScreenX() - offsetX[0]);
			primaryStage.setY(a.getScreenY() - offsetY[0]);
		});

		// Container to hold the buttons and allow for click+drag
		// Button to replace X close button for transparent background
		Button closeButton = new Button("X");
		closeButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		closeButton.setMinSize(25, 25);
		closeButton.setMaxSize(25, 25);

		// Button to replace maximize button for transparent background
		Button maxButton = new Button("🗖");
		maxButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		maxButton.setMinSize(25, 25);
		maxButton.setMaxSize(25, 25);

		// Button to replace minimize button for transparent background
		Button minButton = new Button("_");
		minButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		minButton.setMinSize(25, 25);
		minButton.setMaxSize(25, 25);

		// Set onAction events for button
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

		// Set onAction events for button
		maxButton.setOnMouseEntered(a -> {
			maxButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});

		maxButton.setOnMouseExited(a -> {
			maxButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});

		maxButton.setOnAction(a -> {
			primaryStage.setMaximized(!primaryStage.isMaximized());
		});

		// Set onAction events for button
		minButton.setOnMouseEntered(a -> {
			minButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});

		minButton.setOnMouseExited(a -> {
			minButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});

		minButton.setOnAction(a -> {
			primaryStage.setIconified(true);
		});

		// Container to hold the three buttons min, max, and close
		HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		buttonBar.setPadding(new Insets(0));
		buttonBar.setMaxHeight(27);
		buttonBar.setMaxWidth(80);

		buttonBar.setAlignment(Pos.TOP_RIGHT);

		// StackPane to control layout sizing
		StackPane root = new StackPane(layout, buttonBar);
		root.setAlignment(buttonBar, Pos.TOP_RIGHT);
		root.setStyle("-fx-background-color: transparent;");
		root.setStyle("-fx-background-color: derive(gray, 60%);");
		root.setPadding(new Insets(0));

		Scene scene = new Scene(root, 1900, 1000);

		resultsTable.prefWidthProperty().bind(hbox1.widthProperty());
		resultsTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		qTable.prefWidthProperty().bind(root3.widthProperty());
		qTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		contentColumn.prefWidthProperty().bind(vbox1.widthProperty().subtract(19));
		detailsColumn.prefWidthProperty().bind(questionDB.widthProperty().subtract(19));
		searchTable.prefWidthProperty().bind(vbox.widthProperty());
		searchTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		searchBox.prefWidthProperty().bind(vbox.widthProperty());
		searchBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		submitBox.prefWidthProperty().bind(qTable.widthProperty());
		submitBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		questionInputBox.prefWidthProperty().bind(qTable.widthProperty());
		questionInputBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		topLabelBox.prefWidthProperty().bind(layout.widthProperty());
		buttonBox1.prefWidthProperty().bind(resultsTable.widthProperty());

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	// Helper class to update reultsTable contents
	private void updateResultsTableForQuestion(Question question) {
		// Clear the observable list
		resultsObservableList.clear();

		// Retrieve an updated answer list of answers related to selected question from
		// the qtable
		try {
			question = databaseHelper.qaHelper.getQuestion(question.getId());
			answers = databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId());

			// Put the selected question in the first row
			resultsObservableList.add(new QATableRow(QATableRow.RowType.QUESTION, question.toDisplayWithText(),
					question.getId(), question.getAuthorId(), question.getRelatedId()));

			// Check if selected question has a preferred answer and put that in row 2 if so
			// Check if question has a preferred answer
			if (question.getPreferredAnswer() > 0) {
				// Retrieve the preferred answer object
				answer = databaseHelper.qaHelper.getAnswer(question.getPreferredAnswer());
				// Remove the preferred answer from the answers list so it is not duplicated
				answers.remove(answer);
				// Put the preferred answer in the second row
				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(), answer.getId(),
						answer.getAuthorId(), answer.getRelatedId()));

				// Recursively call addRelatedAnswers and store the list thats left
				answers = addRelatedAnswers(answer.getId(), answers);
			}

			// After that, if there are any, add each answer as its own row
			for (Answer answer : answers) {
				// Add answer to the observable list
				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(), answer.getId(),
						answer.getAuthorId(), answer.getRelatedId()));

				// Recursively call addRelatedAnswers and store the list thats left
				answers = addRelatedAnswers(answer.getId(), answers);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error trying to .getAllAnswers() within updateResultsTableForQuestion() method");
			return;
		}

		resultsTable.setItems(resultsObservableList);
		resultsTable.refresh();
	}

	private List<Answer> addRelatedAnswers(int parentId, List<Answer> answers) {
		try {
			// Retrieve related answers
			List<Answer> relatedAnswers = databaseHelper.qaHelper.getAllAnswersForAnswer(parentId);

			// Iterate through each answer in relatedAnswers
			for (Answer subAnswer : relatedAnswers) {

				// Remove subAnswer from the list of answers
				answers.remove(subAnswer);

				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, subAnswer.toDisplay(),
						subAnswer.getId(), subAnswer.getAuthorId(), subAnswer.getRelatedId()));

				// Recursively call the function to process nested related answers
				addRelatedAnswers(subAnswer.getId(), answers);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error retrieving related answers in addRelatedAnswers()");
		}
		// Return list of answers that is left
		return answers;
	}

}
