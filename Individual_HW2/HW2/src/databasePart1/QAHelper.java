package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;
import application.Question;
import application.Answer;
import application.QuestionsSet;
import application.AnswersSet;
import tests.*;

// This is a database class used to create, read, update, and delete SQL databases among other managing functions in order
// to maintain a database of questions and answers and to perform various functions to them.

public class QAHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/QADatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;

	// Initialize connection to database
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			statement = connection.createStatement();
			// You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");
			// System.out.println("Database cleared successfully.");

			createTables();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	// Create the tables that will be used to store the info
	private void createTables() throws SQLException {
		// Create the question database
		String questionTable = "CREATE TABLE IF NOT EXISTS cse360question ("
				+ "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " + "title VARCHAR(255), " + "text TEXT DEFAULT NULL, "
				+ "author INT, " + "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP)";
//				+ "FOREIGN KEY (author) REFERENCES cse360users(id))";		// Currently not linked to user database
		statement.execute(questionTable);

		// Create the answer database
		String answerTable = "CREATE TABLE IF NOT EXISTS cse360answer ("
				+ "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " + "text TEXT DEFAULT NULL, " + "author INT, "
				+ "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " + "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP)";
//				+ "FOREIGN KEY (author) REFERENCES cse360users(id))";		// Currently not linked to user database
		statement.execute(answerTable);

		// Create the relation database
		String relationTable = "CREATE TABLE IF NOT EXISTS cse360relation (" + "question_id INT NOT NULL, "
				+ "answer_id INT NOT NULL, " + "preferred_answer BOOLEAN DEFAULT NULL, "
				+ "FOREIGN KEY (question_id) REFERENCES cse360question(id) ON DELETE CASCADE, " //
				+ "FOREIGN KEY (answer_id) REFERENCES cse360answer(id) ON DELETE CASCADE, " //
				+ "PRIMARY KEY (question_id, answer_id), " + "UNIQUE (question_id, preferred_answer))";
		statement.execute(relationTable);
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360question";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void registerQuestion(Question question) throws SQLException {
		String insertQuestion = "INSERT INTO cse360question (title, text, author) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(1, question.getTitle());
			pstmt.setString(2, question.getText());
			pstmt.setInt(3, question.getAuthor());
			pstmt.executeUpdate();
		}
		System.out.println("Question registered successfully.");
	}

	// Registers a new user in the database.
	public void registerAnswer(Answer answer, int questionID) throws SQLException {
		String insertAnswer = "INSERT INTO cse360answer (text, author) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getAuthor());
			pstmt.executeUpdate();

			ResultSet newID = pstmt.getGeneratedKeys();
			if (newID.next()) {
				int answerID = newID.getInt(1);
				addRelation(questionID, answerID);
			}

		}
		System.out.println("Answer registered successfully.");
	}

	// Deletes a question row from the SQL table
	public boolean deleteQuestion(int id) {
		String query = "DELETE FROM cse360question AS c WHERE c.id = ?"; // delete the correct question row from
																			// database
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-QUESTION: Question successfully deleted");
				// Remove answers related to question
				List<Answer> answers = getAllAnswersForQuestion(id);
				for (Answer answer : answers) {
					deleteAnswer(answer.getId());
				}
				return true;
			}
			System.out.println("DELETE-QUESTION: Question was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETE-QUESTION: SQL Error - " + e.getMessage());
			return false;
		}
	}

	// Deletes a question row from the SQL table
	public boolean deleteAnswer(int id) {
		String query = "DELETE FROM cse360answer AS c WHERE c.id = ?"; // delete the correct answer row from database
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-ANSWER: Answer successfully deleted");
				return true;
			}
			System.out.println("DELETE-ANSWER: Answer was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETE-ANSWER: SQL Error - " + e.getMessage());
			return false;
		}
	}

	// Add a relation to the relation database
	public void addRelation(int questionID, int answerID) {
		String insertRelation = "INSERT INTO cse360relation (question_id, answer_id) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertRelation)) {
			pstmt.setInt(1, questionID);
			pstmt.setInt(2, answerID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println(e.getMessage() + "\nERROR IN ADD-RELATION METHOD");
		}
	}

	// Delete a relation from the relation database
	public boolean deleteRelation(int questionID, int answerID) {
		String query = "DELETE FROM cse360relation AS c where c.question_id = ? AND c.answer_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			pstmt.setInt(2, answerID);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-RELATION: QA-Relation successfully deleted");
				return true;
			}
			System.out.println("DELETE-RELATION: QA-Relation was not found");
			return false;
		} catch (SQLException e) {
			System.err.println(e.getMessage() + "\nERROR IN DELETE-RELATION METHOD");
			return false;
		}
	}

	// Get a question object with a provided question id
	public Question getQuestion(Integer questionID) throws SQLException {
		// Search the question database for a match to the question id
		String query = "SELECT * FROM cse360question AS c WHERE c.id = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int author = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, author, createdOn, updatedOn);

				// Return the question object
				return question;
			}
		}
		return null;
	}

	// Get an answer object with a provided answer id
	public Answer getAnswer(Integer answerID) throws SQLException {
		// Search the answer database for a match to the answer id
		String query = "SELECT * FROM cse360answer AS c WHERE c.id = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int author = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id, text, author, createdOn, updatedOn);

				// Return the answer object
				return answer;
			}
		}
		return null;
	}

	// Retrieve all questions from the question database
	public List<Question> getAllQuestions() throws SQLException {
		String query = "SELECT * FROM cse360question"; // selecting all of the rows in the database
		List<Question> questions = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int author = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				List<String> comp = textDeserial(text);

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, author, createdOn, updatedOn, comp);

				// Add question object to the list questions
				questions.add(question);
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	// Retrieve all answers from the answer database
	public List<Answer> getAllAnswers() throws SQLException {
		String query = "SELECT * FROM cse360answer"; // selecting all of the rows in the database
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				// Pull the info from that row for one answer object
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int author = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id, text, author, createdOn, updatedOn);

				// Add the new answer object to the list of answer objects
				answers.add(answer);
			}
		}
		// Return the list of answer objects
		return answers;
	}

	// Retrieve all of the answers that are associated with a given question id from
	// the relation database
	public List<Answer> getAllAnswersForQuestion(int questionID) throws SQLException {
		String query = "SELECT a.id, a.text, a.author, a.created_On, a.updated_On FROM cse360answer a "
				+ "JOIN cse360relation r ON a.id = r.answer_id " + "WHERE r.question_id = ?";
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id2 = rs.getInt("id");
				String text = rs.getString("text");
				int author = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id2, text, author, createdOn, updatedOn);

				// Add the new answer object to the list of answer objects
				answers.add(answer);
			}
		}
		// Return the list of answer objects
		return answers;
	}

	// Method used to convert the text of a question into a list of unique words
	// without special characters for comparison to others
	public List<String> textDeserial(String text) {
		if (text == null || text.isEmpty()) {
			// If empty return new empty list
			return new ArrayList<>();
		} else {
			// Clean string of all but alpha-numeric characters and spaces
			String cleanText = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

			// Split the string using the spaces
			String[] wordsArray = cleanText.split("\\s+");

			// Use set to get rid of any duplicate words in the set
			Set<String> uniqueText = new HashSet<>(Arrays.asList(wordsArray));

			// Convert back to list and return
			return new ArrayList<>(uniqueText);
		}
	}

	// Update the contents of a question object with those of pass question object
	public void updateQuestion(Question question) {
		String query = "UPDATE cse360question Set title = ?, text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, question.getTitle());
			pstmt.setString(2, question.getText());
			pstmt.setInt(3, question.getId());

			int updated = pstmt.executeUpdate();
			// Check if any changes were made
			if (updated > 0) {
				System.out.println("Question has been updated."); // Debug
			} else {
				System.out.println("No matching question was found."); // Debug
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update question in updateQuestion method.");
		}
	}

	// Update the contents of a answer object with those of pass answer object
	public void updateAnswer(Answer answer) {
		String query = "UPDATE cse360answer Set text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getId());

			int updated = pstmt.executeUpdate();
			// Check if any changes were made
			if (updated > 0) {
				System.out.println("Answer has been updated."); // Debug
			} else {
				System.out.println("No matching answer was found."); // Debug
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update question in updateAnswer method.");
		}
	}

}
