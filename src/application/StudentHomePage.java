package application;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tests.PopulateQADatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user.
 */
public class StudentHomePage {
	private final DatabaseHelper databaseHelper;
	private Question question;
	private List<Question> questions;
	private List<Answer> answers;
	private List<QuestionsSet> questionsSet;
	private List<AnswersSet> answersSet;

	public int currentUser = 1;

	public StudentHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {

		// Use statement below to populate databases
		// new PopulateQADatabase(databaseHelper.qaHelper).execute();

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

		/*
		 * // Debug to check database contents try {
		 * System.out.println("Fetching all questions..."); List<Question>
		 * questionsFromDb = databaseHelper.getAllQuestions(); if (questionsFromDb ==
		 * null || questionsFromDb.isEmpty()) {
		 * System.out.println("No questions found in the database."); } else {
		 * System.out.println("Questions found: " + questionsFromDb.size());
		 * System.out.println(questionsFromDb); }
		 * 
		 * System.out.println("Fetching all answers..."); List<Answer> answersFromDb =
		 * databaseHelper.getAllAnswers(); if (answersFromDb == null ||
		 * answersFromDb.isEmpty()) {
		 * System.out.println("No answers found in the database."); } else {
		 * System.out.println("Answers found: " + answersFromDb.size());
		 * System.out.println(answersFromDb); } } catch (SQLException e) {
		 * e.printStackTrace();
		 * System.err.println("Error trying to check contents of databases"); ; return;
		 * }
		 */

		// Label to display title of area to the user
		Label prompt = new Label("Entry Box");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to hold and position the title
		HBox titleBox = new HBox(prompt);
		// Set alignment of box
		titleBox.setAlignment(Pos.CENTER);

		// Input box for a title
		TextArea titleField = new TextArea();
		titleField.setPromptText("Enter a title for your question. This should be a question");
		// Styling for the titleField
		titleField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		titleField.setMaxWidth(600);
		titleField.setPrefWidth(600);
		titleField.setMaxHeight(20);

		// Input box for body of question
		TextArea inputField = new TextArea();
		inputField.setPromptText("Ask your question in more detail");
		// Styling for the inputField
		inputField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		inputField.setMaxWidth(600);
		inputField.setPrefWidth(600);
		inputField.setMaxHeight(300);
		inputField.setPrefHeight(300);
		inputField.setWrapText(true);

		// Button search using text in input fields
		Button deleteButton = new Button("Delete");
		deleteButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Button to submit question text in input fields to database
		Button submitButton = new Button("Submit Question");
		submitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Button to submit answer text in input fields to database
		Button submitAnswerButton = new Button("Submit Answer");
		submitAnswerButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Button to return to the login screen
		Button quitButton = new Button("Back to login");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Container to hold search and submit boxes horizontally to each other
		HBox buttonBox = new HBox(5, submitButton, submitAnswerButton, deleteButton);
		buttonBox.setAlignment(Pos.CENTER);

		// Container to hold the two input boxes for questions and their title together
		VBox questionInputBox = new VBox(5, titleBox, titleField, inputField, buttonBox);

		// Table display of the question database

		// Label to display title to user
		Label prompt2 = new Label("Question Database");
		prompt2.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to position the title
		HBox titleBox2 = new HBox(prompt2);
		titleBox2.setAlignment(Pos.CENTER);

		// Create table to display the question database within
		TableView<Question> qTable = new TableView<>();
		// Styling for the table
		qTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
		qTable.setPrefWidth(600);
		qTable.setFixedCellSize(-1);

		// Create an observable list of questions and assign to the table
		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		// Create, assign, and associate values to table
		TableColumn<Question, Integer> idColumn = new TableColumn<>("Question ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		// Create a title column
		TableColumn<Question, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

		// Create a text column
		TableColumn<Question, String> textColumn = new TableColumn<>("Question");
		textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
		textColumn.setPrefWidth(200);

		// Create an userID column
		TableColumn<Question, Integer> authorColumn = new TableColumn<>("Author ID");
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

		// Create a createdOn column
		TableColumn<Question, String> createdColumn = new TableColumn<>("Created On");
		createdColumn.setCellValueFactory(new PropertyValueFactory<>("createdOn"));

		// Create an updatedOn column
		TableColumn<Question, String> updatedColumn = new TableColumn<>("Updated On");
		updatedColumn.setCellValueFactory(new PropertyValueFactory<>("updatedOn"));

		qTable.getColumns().addAll(idColumn, titleColumn, textColumn, authorColumn, createdColumn, updatedColumn);

		// Container to hold the table
		VBox questionDB = new VBox(5, titleBox2, qTable);

		// Table display of the answer database

		// Label to display title to user
		Label prompt3 = new Label("Answer Database");
		prompt3.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to position the title
		HBox titleBox3 = new HBox(prompt3);
		titleBox3.setAlignment(Pos.CENTER);

		// Create table to display the answer database
		TableView<Answer> aTable = new TableView<>();
		aTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
		aTable.setPrefWidth(600);

		// if answers is null then initialize as an empty list
		if (answers == null) {
			answers = new ArrayList<>();
		}

		// Create an observable list and assign it to the table
		ObservableList<Answer> answerObservableList = FXCollections.observableArrayList(answers);
		aTable.setItems(answerObservableList);

		// Create, assign, and associate values to table
		TableColumn<Answer, Integer> idColumn2 = new TableColumn<>("Answer ID");
		idColumn2.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));

		// Create a text column
		TableColumn<Answer, String> textColumn2 = new TableColumn<>("Answer");
		textColumn2.setCellValueFactory(new PropertyValueFactory<>("text"));

		// Create a userID column
		TableColumn<Answer, Integer> authorColumn2 = new TableColumn<>("Author ID");
		authorColumn2.setCellValueFactory(new PropertyValueFactory<>("author"));

		// Create a createOn column
		TableColumn<Answer, String> createdColumn2 = new TableColumn<>("Created On");
		createdColumn2.setCellValueFactory(new PropertyValueFactory<>("createdOn"));

		// Create an updatedOn column
		TableColumn<Answer, String> updatedColumn2 = new TableColumn<>("Updated On");
		updatedColumn2.setCellValueFactory(new PropertyValueFactory<>("updatedOn"));

		aTable.getColumns().addAll(idColumn2, textColumn2, authorColumn2, createdColumn2, updatedColumn2);

		// Container to hold the table
		VBox answerDB = new VBox(5, titleBox3, aTable);

		// Table display of the QArelation database

		/*
		 * // Label to display title to user Label prompt4 = new
		 * Label("Relation Database"); prompt4.
		 * setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;"
		 * );
		 * 
		 * 
		 * 
		 * // Hbox to position the title HBox titleBox4 = new HBox();
		 * titleBox4.setAlignment(Pos.CENTER);
		 * 
		 * TableView<AnswersSet> rTable = new TableView<>(); // Styling for the table
		 * rTable.
		 * setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;"
		 * ); rTable.setPrefWidth(900);
		 * 
		 * // Create an observable list ObservableList<AnswersSet>
		 * relationObservableList = FXCollections.observableArrayList();
		 * 
		 * // Update question list try { questions =
		 * databaseHelper.qaHelper.getAllQuestions(); } catch (SQLException e) {
		 * System.out.println("Should never reach here, can't get all QUESTIONS");
		 * e.printStackTrace(); }
		 * 
		 * // Iterate through each question for (Question question : questions) { try {
		 * // Create an AnswersSet for each question AnswersSet displayAnswersSet = new
		 * AnswersSet(); // Store all answers for this question in answers answers =
		 * databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId()); // Store
		 * all answers for this question in an AnswersSet
		 * displayAnswersSet.setQuestion(question);
		 * displayAnswersSet.setAnswers(answers);
		 * 
		 * // Add updated answersset to the observable list
		 * relationObservableList.add(displayAnswersSet); } catch (SQLException e) {
		 * e.printStackTrace();
		 * System.out.println("Error pulling answers for question: " +
		 * question.getId()); } }
		 * 
		 * // Assign observable list to the table
		 * rTable.setItems(relationObservableList); // Create, assign, and associate
		 * values to table // Create an id column TableColumn<AnswersSet, Integer>
		 * idColumn3 = new TableColumn<>("Question ID");
		 * idColumn3.setCellValueFactory(data -> new
		 * ReadOnlyObjectWrapper<>(data.getValue().getQuestion().getId()));
		 * 
		 * // Create a title column TableColumn<AnswersSet, String> titleColumn3 = new
		 * TableColumn<>("Question"); titleColumn3.setCellValueFactory(data -> new
		 * SimpleStringProperty(data.getValue().getQuestion().getTitle()));
		 * 
		 * // Create a text column TableColumn<AnswersSet, String> textColumn3 = new
		 * TableColumn<>("Answer(s)"); textColumn3.setCellValueFactory(data -> new
		 * SimpleStringProperty(data.getValue().toString()));
		 * textColumn3.setPrefWidth(200);
		 * 
		 * rTable.getColumns().addAll(idColumn3, titleColumn3, textColumn3);
		 * 
		 * // Container to hold the table VBox relationDB = new VBox(5, titleBox4,
		 * rTable);
		 */

		// Area to display results of searches

		// Label to display title to user
		Label prompt5 = new Label("Details");
		prompt5.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to position the title
		HBox titleBox5 = new HBox(prompt5);
		titleBox5.setAlignment(Pos.CENTER);

		// Text area to display results for search/selection functions
		TextArea resultsBox = new TextArea();
		resultsBox.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
		resultsBox.setPrefWidth(900);
		resultsBox.setPrefHeight(400);
		resultsBox.setWrapText(true);

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
		errorLabel.setTranslateY(22);

		// "Back to login" button will bring user back to the login screen
		quitButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(newStage);
		});

		// Button to submit an answer or question from the input fields
		submitButton.setOnAction(a -> {
			String titleInput = titleField.getText();
			String textInput = inputField.getText();

			// Check if titleInput is empty or null
			if (titleInput == null || titleInput.isEmpty()) {
				errorLabel.setText("Error, question-title field is blank.");
				return;
			}

			// Check if textInput is empty or null
			if (textInput == null || textInput.isEmpty()) {
				errorLabel.setText("Error, question-body field is blank.");
				return;
			}

			// Check if inputs are empty or null
			try {
				if (titleInput != null && textInput != null && !titleInput.isEmpty() && !textInput.isEmpty()) {
					Question newQuestion = new Question(titleInput, textInput, databaseHelper.currentUser.getUserId());

					// Clear input fields for new inputs
					titleField.clear();
					inputField.clear();

					// Register the new question object into the database
					databaseHelper.qaHelper.registerQuestion(newQuestion);

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					/*
					 * // Clear Observable list before updating relationObservableList.clear();
					 */

					/*
					 * // Iterate through each question for (Question question : questions) { try {
					 * // Create an AnswersSet for each question AnswersSet displayAnswersSet = new
					 * AnswersSet(); // Store all answers for this question in answers answers =
					 * databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId()); // Store
					 * all answers for this question in an AnswersSet
					 * displayAnswersSet.setQuestion(question);
					 * displayAnswersSet.setAnswers(answers);
					 * 
					 * relationObservableList.addAll(displayAnswersSet); } catch (SQLException e) {
					 * e.printStackTrace();
					 * System.out.println("Error pulling answers for question: " +
					 * question.getId()); } }
					 */

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

				} else {
					System.out.println("Title and/or Question fields are blank");
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error trying to register new question into database via submit button");
				;
				return;
			}

		});

		// Button to submit an answer to answer database. Must be located after qtable
		// to allow selections of question to pass
		submitAnswerButton.setOnAction(a -> {
			titleField.clear();
			String textInput = inputField.getText();
			Question selectedQuestion = qTable.getSelectionModel().getSelectedItem();

			if (textInput == null || textInput.isEmpty()) {
				errorLabel.setText("Error, answer field is blank.");
				return;
			}

			// Check if inputs are empty or null
			try {
				if (textInput != null && !textInput.isEmpty()) {
					Answer newAnswer = new Answer(textInput, currentUser); // Placeholder no author is registered for

					// Clear input fields for new inputs
					titleField.clear();
					inputField.clear();

					// Register new answer into database with question relation
					databaseHelper.qaHelper.registerAnswer(newAnswer, selectedQuestion.getId());

					// Update objects with latest info
					answers = databaseHelper.qaHelper.getAllAnswers();
					questions = databaseHelper.qaHelper.getAllQuestions();

					/*
					 * // Clear observable list before updating it relationObservableList.clear();
					 * 
					 * // Iterate through each question for (Question question : questions) { try {
					 * // Create an AnswersSet for each question AnswersSet displayAnswersSet = new
					 * AnswersSet(); // Store all answers for this question in answers List<Answer>
					 * updateAnswers =
					 * databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId()); // Store
					 * all answers for this question in an AnswersSet
					 * displayAnswersSet.setQuestion(question);
					 * displayAnswersSet.setAnswers(updateAnswers);
					 * 
					 * relationObservableList.addAll(displayAnswersSet);
					 * 
					 * } catch (SQLException e) { e.printStackTrace();
					 * System.out.println("Error pulling answers for question: " +
					 * question.getId()); } }
					 */

					// Refresh contents of tables manually
					answerObservableList.clear();
					answerObservableList.addAll(answers);
					aTable.setItems(answerObservableList);

				} else {
					System.out.println("Answer fields is blank");
					return;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error trying to register new answer into database via submit button");
				return;
			}

		});

		// Event to allow searching of question database for similar entries while the
		// user types into the inputField
		inputField.setOnKeyReleased(a -> {
			String input = inputField.getText().trim();
			if (!input.isEmpty()) {
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
					if (count > 5) {
						similarity.put(question, count);
					}
				}

				// Sort based on similarity score
				List<Question> sortedList = similarity.entrySet().stream()
						.sorted(Map.Entry.<Question, Integer>comparingByValue().reversed()).map(Map.Entry::getKey)
						.collect(Collectors.toList());

				// Create QuestionsSet to hold sort list of questions
				QuestionsSet searchResults = new QuestionsSet();
				for (Question temp : sortedList) {
					searchResults.addQuestion(temp);
				}

				// Display results of updating similarity search in resultsBox
				resultsBox.setText(searchResults.toString());
			}
		});

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				// Clear selections on the other tables
				aTable.getSelectionModel().clearSelection();

				deleteButton.setText("Delete Question");

				// Modify setOnAction for deleteButton to delete selected Question from Question
				// database
				deleteButton.setOnAction(a -> {
					try {
						databaseHelper.qaHelper.deleteQuestion(newSelection.getId());

						questions = databaseHelper.qaHelper.getAllQuestions();
					} catch (SQLException e) {
						e.printStackTrace();
						System.err.println("Error trying update question object via getALLUsers() in qTable");
					}

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);
					// aTable.setItems(answerObservableList);
					// rTable.setItems(relationObservableList);

				});
				// Send the details of the selection to the resultsBox to display to the user

				try {
					System.out
							.println(databaseHelper.qaHelper.getAllAnswersForQuestion(newSelection.getId()).toString());
					resultsBox.setText(newSelection.toString() + "\n"
							+ databaseHelper.qaHelper.getAllAnswersForQuestion(newSelection.getId()).stream()
									.map(Object::toString).collect(Collectors.joining("\n")));
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error trying to populate resultsBox with results of selected question: "
							+ newSelection.getId());
				}
			}
		});

		// Add listeners for table selections to read selected objects from tables
		aTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				// Clear selections on the other tables
				qTable.getSelectionModel().clearSelection();

				deleteButton.setText("Delete Answer");

				// Modify setOnAction for deleteButton to delete selected Answer from Answer
				// database
				deleteButton.setOnAction(a -> {
					try {
						databaseHelper.qaHelper.deleteAnswer(newSelection.getId());

						answers = databaseHelper.qaHelper.getAllAnswers();
					} catch (SQLException e) {
						e.printStackTrace();
						System.err.println("Error trying to refresh answers within aTable listener calls");
					}

					// Refresh contents of tables manually
					answerObservableList.clear();
					answerObservableList.addAll(answers);
					// qTable.setItems(questionObservableList);
					aTable.setItems(answerObservableList);
					// rTable.setItems(relationObservableList);
				});
				// Send the details of the selection to the resultsBox to display to the user
				resultsBox.setText(newSelection.toString());
			}
		});

		/*
		 * // Add listeners for table selections to read selected objects from tables
		 * rTable.getSelectionModel().selectedItemProperty().addListener((obs,
		 * oldSelection, newSelection) -> { if (newSelection != null) { // Clear
		 * selections on the other tables qTable.getSelectionModel().clearSelection();
		 * aTable.getSelectionModel().clearSelection();
		 * 
		 * // Set delete button text to adjust when selecting a relation from the
		 * relation // table deleteButton.setText("Delete Relation");
		 * 
		 * deleteButton.setOnAction(a -> {
		 * 
		 * // Check input to make sure if (newSelection.getAnswers().isEmpty() ||
		 * newSelection.getAnswers() == null) { errorLabel.setText(
		 * "Error, there are no answers associated with the selected question in the relation table."
		 * ); return; }
		 * 
		 * try {
		 * databaseHelper.qaHelper.deleteRelation(newSelection.getQuestion().getId(),
		 * newSelection.getAnswers().get(0).getId());
		 * 
		 * questions = databaseHelper.qaHelper.getAllQuestions(); } catch (Exception e)
		 * { System.out.println(
		 * "There are no AnswersSets to delete from this question in the relation database"
		 * ); }
		 * 
		 * // Clear observable list before updating it relationObservableList.clear();
		 * 
		 * // Iterate through each question for (Question question : questions) { try {
		 * // Create an AnswersSet for each question AnswersSet displayAnswersSet = new
		 * AnswersSet(); // Store all answers for this question in answers answers =
		 * databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId()); // Store
		 * all answers for this question in an AnswersSet
		 * displayAnswersSet.setQuestion(question);
		 * displayAnswersSet.setAnswers(answers);
		 * 
		 * relationObservableList.addAll(displayAnswersSet); } catch (SQLException e) {
		 * e.printStackTrace();
		 * System.out.println("Error pulling answers for question: " +
		 * question.getId()); } }
		 * 
		 * // Refresh contents of tables manually //
		 * qTable.setItems(questionObservableList); //
		 * aTable.setItems(answerObservableList);
		 * rTable.setItems(relationObservableList);
		 * 
		 * }); // Send the details of the selection to the resultsBox to display to the
		 * user resultsBox.setText(newSelection.getQuestion().toString() + "\n\n" +
		 * newSelection.toString()); } });
		 */

		// Use containers to position and hold many different UI components

		// Container to hold the table
		VBox result = new VBox(5, titleBox5, resultsBox);

		HBox hboxTop = new HBox(10, questionInputBox, questionDB, answerDB);
		hboxTop.setAlignment(Pos.CENTER);

		VBox vboxTop = new VBox(10, hboxTop);
		vboxTop.setAlignment(Pos.TOP_CENTER);

		HBox hboxBottom = new HBox(10, result);
		hboxBottom.setAlignment(Pos.CENTER);

		VBox vboxBottom = new VBox(10, hboxBottom, errorLabel);
		vboxBottom.setAlignment(Pos.BOTTOM_CENTER);

		VBox vbox = new VBox(10, vboxTop, vboxBottom);
		vbox.setAlignment(Pos.CENTER);

		VBox vbox1 = new VBox(10, vbox, quitButton);
		vbox1.setAlignment(Pos.CENTER);

		StackPane root = new StackPane(vbox1);
		root.setStyle("-fx-background-color: derive(gray, 60%);");

		Scene scene = new Scene(root, 1900, 1000);

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
}
