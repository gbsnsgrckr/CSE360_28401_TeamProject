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

	private Answer tempAnswer;

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
		titleField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		titleField.setMaxWidth(600);
		titleField.setPrefWidth(600);
		titleField.setMaxHeight(60);
		titleField.setPrefHeight(60);

		// Input box for body of question
		TextArea inputField = new TextArea();
		inputField.setPromptText("Ask your question in more detail");
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

		// Button to open the ui to submit a new question
		Button questionCloseButton = new Button("Close");
		questionCloseButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Table display of the question database
		TableView<Question> qTable = new TableView<>();

		qTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");
		qTable.setMinWidth(300);
		qTable.setFixedCellSize(-1);

		qTable.setRowFactory(a -> new TableRow<Question>() {
			@Override
			protected void updateItem(Question item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; "
							+ "-fx-border-width:  1px; -fx-table-cell-border-color: black;");
				}
			}
		});

		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
		detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

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

		qTable.getColumns().addAll(detailsColumn);

		// Hide the header row in the question table
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
					setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; "
							+ "-fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		HBox topTitleBox = new HBox(135, submitButton, questionCloseButton);
		questionCloseButton.setAlignment(Pos.TOP_RIGHT);
		submitButton.setAlignment(Pos.TOP_LEFT);

		VBox questionInputBox = new VBox(5, topTitleBox, titleField, inputField);
		questionInputBox.setStyle("-fx-background-color: derive(gray, 80%)");

		VBox submitBox = new VBox(questionInputBox);

		TableColumn<QATableRow, String> contentColumn = new TableColumn<>("Results");
		contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		contentColumn.setCellFactory(a -> new TableCell<QATableRow, String>() {
			private final TextArea replyArea = new TextArea();
			private final Button submitReplyButton = new Button("Submit");
			private final VBox replyBox = new VBox(5, submitReplyButton, replyArea);
			private final VBox cellContent = new VBox(5);
			private final HBox cellBox = new HBox();

			{
				submitReplyButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
				replyBox.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; "
						+ "-fx-border-width:  1; -fx-padding: 1px;");
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
							tempAnswer = databaseHelper.qaHelper.getAnswer(inputText);
						} catch (SQLException e) {
							e.printStackTrace();
							System.err.println("Error trying to register answer via submitReplyButton");
						}

						updateResultsTableForQuestion(qTable.getSelectionModel().getSelectedItem());

						resultsTable.getSelectionModel().select(new QATableRow(QATableRow.RowType.ANSWER,
								tempAnswer.toDisplay(), tempAnswer.getId(), tempAnswer.getAuthorId(),
								tempAnswer.getRelatedId()));
					}
				});
				cellContent.getChildren().addAll(replyBox);
				cellContent.setAlignment(Pos.CENTER_LEFT);
			}

			@Override
			protected void updateItem(String item, boolean flag) {
				super.updateItem(item, flag);
				cellContent.getChildren().clear();
				cellBox.getChildren().clear();
				setGraphic(null);

				if (flag && item == null) {
					setText(null);
				} else {
					QATableRow row = getTableView().getItems().get(getIndex());
					Label displayLabel = new Label(item);
					displayLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black;");
					displayLabel.setWrapText(true);
					displayLabel.maxWidthProperty().bind(contentColumn.widthProperty().subtract(50));
					displayLabel.setPrefHeight(225);

					// Indentation/spacer logic omitted for brevity

					// If current user authored an ANSWER => show Edit/Delete
					if (row.getType() == QATableRow.RowType.ANSWER
							&& row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						editButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; "
								+ "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; "
								+ "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 1px;");

						editButton.setOnAction(a -> {
							submitButton.setText("Update Answer");
							submitBox.setVisible(true);
							submitBox.setManaged(true);
							updatingAnswer = true;
							try {
								answer = databaseHelper.qaHelper.getAnswer(row.getAnswerId());
							} catch (SQLException e) {
								e.printStackTrace();
							}
							inputField.setText(answer.getText());
						});

						deleteButton.setOnAction(a -> {
							databaseHelper.qaHelper.deleteAnswer(row.getAnswerId());
							try {
								question = qTable.getSelectionModel().getSelectedItem();
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);
							qTable.getSelectionModel().select(question);
						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);
						cellContent.getChildren().addAll(displayLabel, buttonBox);
						setGraphic(cellBox);
						setText(null);
					}
					else if (row.getType() == QATableRow.RowType.QUESTION
							&& row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						// If current user authored the QUESTION => show Edit/Delete
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						editButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; "
								+ "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: black; "
								+ "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 1px;");

						editButton.setOnAction(a -> {
							submitButton.setText("Update Question");
							submitBox.setVisible(true);
							submitBox.setManaged(true);
							updatingQuestion = true;
							try {
								question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
							} catch (SQLException e) {
								e.printStackTrace();
							}
							titleField.setText(question.getTitle());
							inputField.setText(question.getText());
						});

						deleteButton.setOnAction(a -> {
							databaseHelper.qaHelper.deleteQuestion(row.getQuestionId());
							try {
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);
							qTable.getSelectionModel().select(question);
						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);
						cellContent.getChildren().addAll(displayLabel, buttonBox);
						setGraphic(cellBox);
						setText(null);
					}
					else if (row.getType() == QATableRow.RowType.QUESTION) {
						// Just show question text
						try {
							question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
						} catch (SQLException e) {
							e.printStackTrace();
						}
						cellContent.getChildren().add(displayLabel);
						setGraphic(cellBox);
						setText(null);
					} else {
						cellContent.getChildren().add(displayLabel);
						setGraphic(cellBox);
						setText(null);
					}
					cellContent.getChildren().add(replyBox);
					cellBox.getChildren().add(cellContent);
				}
			}
		});
		resultsTable.getColumns().setAll(contentColumn);
		resultsTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

		Label placeholderBox = new Label("◄ Question everything... ►");
		placeholderBox.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: derive(gray, 80%)");
		resultsTable.setPlaceholder(placeholderBox);

		// Hide the header row in the results table
		resultsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = resultsTable.lookup("TableHeaderRow");
			if (titleBar != null) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		ToggleGroup filter = new ToggleGroup();
		RadioButton allButton = new RadioButton("All");
		allButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		allButton.setToggleGroup(filter);
		allButton.setSelected(true);

		RadioButton unansweredButton = new RadioButton("Unanswered");
		unansweredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		unansweredButton.setToggleGroup(filter);

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
					return;
				}
				questionObservableList.setAll(questions);
				qTable.setItems(questionObservableList);
				qTable.refresh();
			}
		});

		HBox titleBox2 = new HBox(30, newQuestionButton, filterBox);
		titleBox2.setAlignment(Pos.CENTER_RIGHT);
		VBox questionDB = new VBox(5, titleBox2, qTable);
		qTable.prefHeightProperty().bind(questionDB.heightProperty());

		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
		errorLabel.setTranslateY(22);

		quitButton.setOnAction(a -> {
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.close();
			new UserLoginPage(databaseHelper).show(newStage);
		});

		TextField searchField = new TextField();
		searchField.setPromptText("Search questions...");
		searchField.setStyle("-fx-alignment: center-left; -fx-text-fill: black; -fx-font-weight: bold; "
				+ "-fx-border-color: black; -fx-border-width: 1;");
		searchField.setPrefWidth(250);

		ObservableList<Question> searchObservableList = FXCollections.observableArrayList(questions);
		TableView<Question> searchTable = new TableView<>();
		searchTable.setItems(searchObservableList);
		searchTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
		HBox searchBox = new HBox(searchTable);

		TableColumn<Question, String> searchColumn = new TableColumn<>();
		searchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplayWithText()));
		searchTable.setRowFactory(a -> new TableRow<Question>() {
			@Override
			protected void updateItem(Question item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; "
							+ "-fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

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
		searchTable.getColumns().add(searchColumn);

		searchTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = searchTable.lookup("TableHeaderRow");
			if (titleBar != null) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		searchBox.setVisible(false);
		searchBox.setManaged(false);

		searchField.setOnKeyReleased(a -> {
			String input = searchField.getText().trim();
			if (!input.isEmpty()) {
				searchField.setMinWidth(searchTable.getWidth());
				searchBox.setVisible(true);
				searchBox.setManaged(true);

				List<String> entry = databaseHelper.qaHelper.textDeserial(input);
				try {
					questions = databaseHelper.qaHelper.getAllQuestions();
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				Map<Question, Integer> similarity = new HashMap<>();
				for (Question question : questions) {
					List<String> compList = question.getComp();
					int count = 0;
					for (String word : entry) {
						if (compList.contains(word)) count++;
					}
					if (count > 2) similarity.put(question, count);
				}

				sortedList = similarity.entrySet().stream()
						.sorted(Map.Entry.<Question, Integer>comparingByValue().reversed())
						.map(Map.Entry::getKey)
						.collect(Collectors.toList());

				searchObservableList.setAll(sortedList);
				if (!searchBox.isVisible()) {
					searchBox.setVisible(true);
					searchBox.setManaged(true);
				}
			} else {
				searchObservableList.clear();
				searchBox.setVisible(false);
				searchBox.setManaged(false);
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);
				searchField.setAlignment(Pos.TOP_RIGHT);
			}
		});

		searchField.setOnMouseClicked(a -> {
			qTable.getSelectionModel().clearSelection();
			searchBox.setVisible(true);
			searchBox.setManaged(true);
		});

		searchTable.setOnMouseClicked(a -> {
			Question selection = searchTable.getSelectionModel().getSelectedItem();
			if (selection != null) {
				questionObservableList.clear();
				questionObservableList.setAll(sortedList);
				qTable.setItems(questionObservableList);
				qTable.getSelectionModel().select(selection);
				allButton.setSelected(false);
				unansweredButton.setSelected(false);
				answeredButton.setSelected(false);
			}
		});

		submitButton.setOnAction(a -> {
			String titleInput = titleField.getText();
			String textInput = inputField.getText();
			if (textInput == null || textInput.trim().isEmpty()) {
				errorLabel.setText("Error, question-body field is blank.");
				return;
			}
			try {
				if (!updatingQuestion && !updatingAnswer) {
					if (titleInput == null || titleInput.trim().isEmpty()) {
						errorLabel.setText("Error, question-title field is blank.");
						return;
					}
					Question newQuestion;
					Question newSelection = qTable.getSelectionModel().getSelectedItem();

					if (newSelection != null) {
						newSelection.setTitle(titleInput);
						newSelection.setText(textInput);
						databaseHelper.qaHelper.updateQuestion(newSelection);
						newQuestion = newSelection;
					} else {
						newQuestion = new Question(titleInput, textInput, databaseHelper.currentUser.getUserId());
						databaseHelper.qaHelper.registerQuestion(newQuestion);
						newQuestion = databaseHelper.qaHelper.getQuestion(newQuestion.getTitle());
					}

					titleField.clear();
					inputField.clear();
					questions = databaseHelper.qaHelper.getAllQuestions();
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);
					qTable.getSelectionModel().select(newQuestion);
				}
				else if (updatingAnswer) {
					answer.setText(textInput);
					databaseHelper.qaHelper.updateAnswer(answer);
					questions = databaseHelper.qaHelper.getAllQuestions();
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);
					qTable.getSelectionModel().select(question);
				}
				else if (updatingQuestion) {
					question.setTitle(titleInput);
					question.setText(textInput);
					databaseHelper.qaHelper.updateQuestion(question);
					questions = databaseHelper.qaHelper.getAllQuestions();
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);
					qTable.getSelectionModel().select(question);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
			submitBox.setVisible(false);
			submitBox.setManaged(false);
			updatingQuestion = false;
			updatingAnswer = false;
		});

		findReviewerButton.setOnAction(a -> {
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);
			new FindReviewerForQuestionPage(databaseHelper).show(newStage);
		});

		manageReviewersButton.setOnAction(a -> {
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.close();
			new ReviewerListPage(newStage, databaseHelper).show(databaseHelper.currentUser);
		});

		titleField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
			}
		});
		inputField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
			}
		});

		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				searchBox.setVisible(false);
				searchBox.setManaged(false);
				searchField.setAlignment(Pos.TOP_RIGHT);
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);
				this.question = newSelection;
				submitButton.setText("Update Question");
				updateResultsTableForQuestion(newSelection);
			}
		});

		searchField.setAlignment(Pos.TOP_RIGHT);
		searchBox.setAlignment(Pos.BOTTOM_RIGHT);

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

		// Already-existing "View My Unresolved" button
		Button viewUnresolvedBtn = createViewUnresolvedButton();

		// *** ADDED CODE: "View All Unresolved" button ***
		Button viewAllUnresolvedBtn = createViewAllUnresolvedButton();  // <--- New button

		// Place both buttons in the same HBox, side by side
		HBox buttonBox2 = new HBox(viewUnresolvedBtn, viewAllUnresolvedBtn);

		VBox vbox1 = new VBox(10, vbox, buttonBox1, buttonBox2);
		vbox1.setAlignment(Pos.CENTER);

		submitBox.setVisible(false);
		submitBox.setManaged(false);

		newQuestionButton.setOnAction(a -> {
			submitButton.setText("Submit Question");
			submitBox.setVisible(true);
			submitBox.setManaged(true);
		});

		questionCloseButton.setOnAction(a -> {
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

		Button closeButton = new Button("X");
		closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; "
				+ "-fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
				+ "-fx-font-weight: bold; -fx-padding: 0;");
		closeButton.setMinSize(25, 25);
		closeButton.setMaxSize(25, 25);

		Button maxButton = new Button("🗖");
		maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; "
				+ "-fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
				+ "-fx-font-weight: bold; -fx-padding: 0;");
		maxButton.setMinSize(25, 25);
		maxButton.setMaxSize(25, 25);

		Button minButton = new Button("_");
		minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; "
				+ "-fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
				+ "-fx-font-weight: bold; -fx-padding: 0;");
		minButton.setMinSize(25, 25);
		minButton.setMaxSize(25, 25);

		closeButton.setOnMouseEntered(a -> {
			closeButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
					+ "-fx-text-fill: red; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});
		closeButton.setOnMouseExited(a -> {
			closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; "
					+ "-fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});
		closeButton.setOnAction(a -> {
			primaryStage.close();
		});

		maxButton.setOnMouseEntered(a -> {
			maxButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
					+ "-fx-text-fill: red; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});
		maxButton.setOnMouseExited(a -> {
			maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
					+ "-fx-text-fill: black; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});
		maxButton.setOnAction(a -> {
			primaryStage.setMaximized(!primaryStage.isMaximized());
		});

		minButton.setOnMouseEntered(a -> {
			minButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
					+ "-fx-text-fill: red; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});
		minButton.setOnMouseExited(a -> {
			minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
					+ "-fx-text-fill: black; -fx-font-size: 12px;"
					+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});
		minButton.setOnAction(a -> {
			primaryStage.setIconified(true);
		});

		HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		buttonBar.setPadding(new Insets(0));
		buttonBar.setMaxHeight(27);
		buttonBar.setMaxWidth(80);

		StackPane root = new StackPane(layout, buttonBar);
		root.setAlignment(buttonBar, Pos.TOP_RIGHT);
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

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	private void updateResultsTableForQuestion(Question question) {
		resultsObservableList.clear();
		try {
			question = databaseHelper.qaHelper.getQuestion(question.getId());
			answers = databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId());

			resultsObservableList.add(new QATableRow(QATableRow.RowType.QUESTION,
					question.toDisplayWithText(),
					question.getId(),
					question.getAuthorId(),
					question.getRelatedId()));

			if (question.getPreferredAnswer() > 0) {
				answer = databaseHelper.qaHelper.getAnswer(question.getPreferredAnswer());
				answers.remove(answer);

				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER,
						answer.toDisplay(),
						answer.getId(),
						answer.getAuthorId(),
						answer.getRelatedId()));

				answers = addRelatedAnswers(answer.getId(), answers);
			}

			for (Answer ans : answers) {
				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER,
						ans.toDisplay(),
						ans.getId(),
						ans.getAuthorId(),
						ans.getRelatedId()));
				answers = addRelatedAnswers(ans.getId(), answers);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		resultsTable.setItems(resultsObservableList);
		resultsTable.refresh();
	}

	/****************************************************
	 * "As a student, I can see my list of unresolved
	 *  questions and the number of unread potential
	 *  answers received."
	 ****************************************************/

	/**
	 * This method displays the current user's unresolved questions
	 * and shows the number of unread potential answers for each.
	 * Double-clicking on a question opens a new window to view those
	 * potential answers. 
	 */
	@SuppressWarnings("deprecation")
	private void showUnresolvedQuestionsForCurrentUser() {
	    Stage stage = new Stage(); 
	    // Creates a new Stage to display the list of unresolved questions for the current user

	    stage.setTitle("My Unresolved Questions"); 
	    // Sets the window title to indicate these are the user's unresolved questions

	    Label heading = new Label("My Unresolved Questions (Unread Answers)");
	    // Creates a heading label describing the content of this window

	    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Applies bold styling and a larger font size to the heading label

	    TableView<Question> unresolvedTable = new TableView<>();
	    // A TableView to list the current user's unresolved questions

	    unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
	    // Gives the table a black border, bold text, etc., to match the look of your application

	    unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    // Ensures columns automatically resize to fit the table width

	    List<Question> myUnresolvedList;
	    // Will hold all unresolved questions for the current user

	    try {
	        myUnresolvedList = databaseHelper.qaHelper.getAllUnresolvedQuestionsForUser(
	            databaseHelper.currentUser.getUserId()
	        );
	        // Retrieves all unresolved questions specific to the current user from the database
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        myUnresolvedList = new ArrayList<>();
	        // In case of error, initializes the list as empty to avoid null issues
	    }

	    ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(myUnresolvedList);
	    // Wraps the retrieved list in an ObservableList for display in the TableView

	    unresolvedTable.setItems(unresolvedObs);
	    // Populates the table with the user's unresolved questions

	    TableColumn<Question, String> questCol = new TableColumn<>("Question Title");
	    // Creates a column to display the question title

	    questCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getTitle())
	    );
	    // Binds the column to each Question object's getTitle() method

	    TableColumn<Question, Number> unreadCol = new TableColumn<>("Unread Answers");
	    // Creates a column to display how many potential answers remain unread

	    unreadCol.setCellValueFactory(cellData -> 
	        new ReadOnlyObjectWrapper<>(cellData.getValue().getUnreadCount())
	    );
	    // Binds the column to each Question object's unreadCount property

	    unresolvedTable.getColumns().addAll(questCol, unreadCol);
	    // Adds both columns (title and unread count) to the table

	    unresolvedTable.setRowFactory(tv -> {
	        TableRow<Question> row = new TableRow<>();
	        // Creates a custom row to detect double-clicks

	        row.setOnMouseClicked(event -> {
	            if (!row.isEmpty() && event.getClickCount() == 2) {
	                // Checks if the row is not empty and has been double-clicked
	                Question question = row.getItem();
	                // Retrieves the Question object from that row
	                showPotentialAnswersWindow(question);
	                // Opens a new window showing potential (unread) answers for this question
	            }
	        });
	        return row;
	    });

	    VBox layout = new VBox(10, heading, unresolvedTable);
	    // A VBox container to vertically stack the heading and the table

	    layout.setAlignment(Pos.CENTER);
	    // Centers the layout content horizontally

	    layout.setPadding(new Insets(15, 15, 15, 15));
	    // Adds padding around the VBox for visual spacing

	    Scene scene = new Scene(layout, 700, 400);
	    // Creates a Scene with the specified width and height

	    stage.setScene(scene);
	    // Sets our new scene on the stage

	    stage.show();
	    // Displays this new window on the screen
	}

	/**
	 * Displays a pop-up window listing potential (unread) answers for the specified question.
	 * Once opened, these potential answers are marked as read in the database
	 * to update the user's unread count (User Story #1 continued).
	 */
	private void showPotentialAnswersWindow(Question question) {
	    Stage stage = new Stage();
	    // Creates a new window (Stage) to list potential answers

	    stage.setTitle("Potential Answers for: " + question.getTitle());
	    // Sets the window title dynamically to the question's title

	    Label heading = new Label("Potential Answers");
	    // Heading label indicating that these are possible/new answers

	    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Applies a larger, bold font to the heading

	    TableView<Answer> potentialAnswersTable = new TableView<>();
	    // A table to display each potential answer

	    potentialAnswersTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
	    // Matches styling used elsewhere in your application

	    potentialAnswersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    // Ensures columns fit within the table width

	    List<Answer> potentialAnswers = new ArrayList<>();
	    // Will hold potential answers for the given question

	    try {
	        potentialAnswers = databaseHelper.qaHelper.getPotentialAnswersForQuestion(question.getId());
	        // Retrieves any "potential" (i.e., not preferred) answers from the database
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }

	    ObservableList<Answer> potentialObs = FXCollections.observableArrayList(potentialAnswers);
	    // Wraps the potential answers list in an ObservableList

	    potentialAnswersTable.setItems(potentialObs);
	    // Populates the table with the newly retrieved potential answers

	    TableColumn<Answer, String> ansCol = new TableColumn<>("Answer Text");
	    // Column to display the actual text of each answer

	    ansCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getText())
	    );
	    // Binds the column to each Answer object's getText() method

	    potentialAnswersTable.getColumns().add(ansCol);
	    // Adds the "Answer Text" column to the table

	    // Mark all potential answers as read upon opening this window
	    for (Answer ans : potentialAnswers) {
	        try {
	            databaseHelper.qaHelper.markAnswerAsRead(ans.getId(), databaseHelper.currentUser.getUserId());
	            // Calls a helper method to update the 'read' status in the database
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    VBox layout = new VBox(10, heading, potentialAnswersTable);
	    // Stacks the heading label and the table inside a VBox

	    layout.setAlignment(Pos.CENTER);
	    // Centers them horizontally

	    layout.setPadding(new Insets(15, 15, 15, 15));
	    // Adds 15px padding on all sides for better spacing

	    Scene scene = new Scene(layout, 600, 300);
	    // Creates a scene with a smaller size (600x300) for a simple pop-up window

	    stage.setScene(scene);
	    // Assigns the scene to the stage

	    stage.show();
	    // Shows the stage to the user
	}

	/****************************************************
	 * "As a student, I can see a list of all unresolved
	 *  questions (for any user) and a list of the current
	 *  potential answers for each so I can evaluate them."
	 ****************************************************/

	/**
	 * Creates a button that shows all unresolved questions for the current user 
	 * Clicking on a question will open the pop-up 
	 * with potential answers. 
	 */
	private Button createViewUnresolvedButton() {
	    Button viewUnresolvedBtn = new Button("View My Unresolved");
	    // Button text instructs that we are viewing the current user's unresolved questions

	    viewUnresolvedBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
	    // Applies bold text and a black border, matching your UI design

	    viewUnresolvedBtn.setOnAction(e -> {
	        showUnresolvedQuestionsForCurrentUser();
	        // When clicked, calls the method that displays the user's unresolved questions
	    });
	    return viewUnresolvedBtn;
	    // Returns the newly-created button for "View My Unresolved"
	}

	// Creates a button that shows all unresolved questions for any user (User Story #2).
	private Button createViewAllUnresolvedButton() {
	    Button viewAllUnresolvedBtn = new Button("View All Unresolved");
	    // Button text indicates that this lists all unresolved questions (not just the current user's)

	    viewAllUnresolvedBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
	    // Same style as other buttons associated

	    viewAllUnresolvedBtn.setOnAction(e -> {
	        showAllUnresolvedQuestions();
	        // When clicked, calls the method that displays every unresolved question
	    });
	    return viewAllUnresolvedBtn;
	    // Returns the new button for "View All Unresolved"
	}

	/**
	 * Displays a pop-up window listing all unresolved questions for any user (User Story #2).
	 * Also shows how many potential answers (i.e., not chosen as the preferred answer) each has.
	 * Double-clicking on a question opens the pop-up window with its potential answers.
	 */
	private void showAllUnresolvedQuestions() {
	    Stage stage = new Stage(); 
	    // Creates a new Stage to list unresolved questions from all users

	    stage.setTitle("All Unresolved Questions"); 
	    // Sets the title to "All Unresolved Questions" for clarity

	    Label heading = new Label("All Unresolved Questions");
	    // Heading label that explains this list is for all users

	    heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    // Applies large, bold styling to the heading

	    TableView<Question> unresolvedTable = new TableView<>();
	    // A TableView to list all unresolved questions (regardless of user)

	    unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
	    // Matches your application's style: black border, bold text

	    unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	    // Automatically resizes columns to fit the table width

	    List<Question> allUnresolvedList;
	    // Will hold all unresolved questions for every user

	    try {
	        allUnresolvedList = databaseHelper.qaHelper.getAllUnresolvedQuestions();
	        // Retrieves all unresolved questions, not filtered by user
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        allUnresolvedList = new ArrayList<>();
	        // If the DB call fails, initializes an empty list to prevent null usage
	    }

	    ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(allUnresolvedList);
	    // Wraps the retrieved list in an ObservableList for display in the table

	    unresolvedTable.setItems(unresolvedObs);
	    // Sets the table's data source to our ObservableList

	    TableColumn<Question, String> questCol = new TableColumn<>("Question Title");
	    // Column to display the question title text

	    questCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getTitle())
	    );
	    // Binds the column to each Question's getTitle() method

	    TableColumn<Question, Number> potentialAnsCol = new TableColumn<>("Potential Answers");
	    // Column showing how many potential (unpreferred) answers each question has

	    potentialAnsCol.setCellValueFactory(cellData -> {
	        Question q = cellData.getValue();
	        int count = 0;
	        // Will track how many potential answers exist for this question

	        try {
	            List<Answer> potential = databaseHelper.qaHelper.getPotentialAnswersForQuestion(q.getId());
	            // Retrieves any answers that have not been chosen as the question's preferred answer
	            count = potential.size();
	            // Stores the number of those potential answers
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return new ReadOnlyObjectWrapper<>(count);
	        // Wraps the count in a ReadOnlyObjectWrapper so it can be displayed in the table cell
	    });

	    unresolvedTable.getColumns().addAll(questCol, potentialAnsCol);
	    // Adds columns for question title and potential answers to the table

	    // Allows double-clicking on a row to open that question's potential answers
	    unresolvedTable.setRowFactory(tv -> {
	        TableRow<Question> row = new TableRow<>();
	        row.setOnMouseClicked(event -> {
	            if (!row.isEmpty() && event.getClickCount() == 2) {
	                // Checks if we have a valid row and a double-click
	                Question clickedQ = row.getItem();
	                // Retrieves the Question from that row
	                showPotentialAnswersWindow(clickedQ);
	                // Opens a window showing potential answers for this question
	            }
	        });
	        return row;
	    });

	    VBox layout = new VBox(10, heading, unresolvedTable);
	    // A simple vertical box to hold the heading and the table

	    layout.setAlignment(Pos.CENTER);
	    // Centers the layout content

	    layout.setPadding(new Insets(15, 15, 15, 15));
	    // Adds padding of 15px on each side

	    Scene scene = new Scene(layout, 700, 400);
	    // Creates a scene sized 700x400 for the "All Unresolved" pop-up

	    stage.setScene(scene);
	    // Sets the newly created scene on the stage

	    stage.show();
	    // Displays this stage to the user
	}
	
	private List<Answer> addRelatedAnswers(int parentId, List<Answer> answers) {
		try {
			List<Answer> relatedAnswers = databaseHelper.qaHelper.getAllAnswersForAnswer(parentId);
			for (Answer subAnswer : relatedAnswers) {
				answers.remove(subAnswer);
				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER,
						subAnswer.toDisplay(),
						subAnswer.getId(),
						subAnswer.getAuthorId(),
						subAnswer.getRelatedId()));

				addRelatedAnswers(subAnswer.getId(), answers);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error retrieving related answers in addRelatedAnswers()");
		}
		return answers;
	}
}
