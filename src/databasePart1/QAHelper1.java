package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import application.User;

// This is a database class used to create, read, update, and delete SQL databases among other managing functions in order
// to maintain a database of questions and answers and to perform various functions to them.

public class QAHelper1 {
    private DatabaseHelper databaseHelper;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/QADatabase";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    public Connection connection = null;
    public Statement statement = null;

    public QAHelper1(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    // Initialize connection to database
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to QA database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    // Create the tables that will be used to store the info
    public void createTables() throws SQLException {
        // Create the question database
        String questionTable = "CREATE TABLE IF NOT EXISTS cse360question ("
                + "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, "
                + "title VARCHAR(255), "
                + "text TEXT DEFAULT NULL, "
                + "author INT, "
                + "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "answer_id VARCHAR(MAX) DEFAULT NULL, "
                + "preferred_answer INT DEFAULT NULL)";
        statement.execute(questionTable);

        // Create the answer database
        String answerTable = "CREATE TABLE IF NOT EXISTS cse360answer ("
                + "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, "
                + "text TEXT DEFAULT NULL, "
                + "author INT, "
                + "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "answer_id VARCHAR(MAX) DEFAULT NULL)";
        statement.execute(answerTable);

        // *** ADDED: call createAnswerViewsTable() here so cse360answerviews is also created ***
        createAnswerViewsTable();
    }
    
    /**
     * Creates an auxiliary table tracking which answers each student has read.
     * We we can distinguish "unread potential answers."
     * 
     */
    public void createAnswerViewsTable() throws SQLException {
        // *** REMOVED RECURSIVE CALL TO createAnswerViewsTable() ***
        String answerViewsTable = "CREATE TABLE IF NOT EXISTS cse360answerviews ("
                + "answer_id INT NOT NULL, "
                + "user_id INT NOT NULL, "
                + "is_read BOOLEAN DEFAULT FALSE, "
                + "PRIMARY KEY (answer_id, user_id))";

        statement.execute(answerViewsTable);
    }

    /**
     * Inserts a row marking an answer as "read" by a given user, if it has not already been.
     * This helps us keep track of how many 'unread' answers remain for each question or user.
     * 
     *
     /**
      * 
      */
    public void markAnswerAsRead(int answerId, int userId) throws SQLException {
        // Check if record exists
        String checkQuery = "SELECT * FROM cse360answerviews WHERE answer_id = ? AND user_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, answerId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Insert new record
                String insertQuery = "INSERT INTO cse360answerviews (answer_id, user_id, is_read) VALUES (?, ?, TRUE)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, answerId);
                    insertStmt.setInt(2, userId);
                    insertStmt.executeUpdate();
                }
            } else {
                // If record is found but is_read is FALSE, update it
                boolean isRead = rs.getBoolean("is_read");
                if (!isRead) {
                    String updateQuery = "UPDATE cse360answerviews SET is_read = TRUE WHERE answer_id = ? AND user_id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, answerId);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

    //Retrieves how many answers a given student has not yet read across all questions
    public int getUnreadAnswersCountForUser(int userId, Integer questionId) throws SQLException {
        // We will gather all answers from cse360answer, then see which are not in cse360answerviews for is_read=TRUE.
        // If questionId != null, we only check answers linked to that question.

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(a.id) AS unreadCount ");
        sb.append("FROM cse360answer a ");

        if (questionId != null) {
            // Join to question for only relevant answers
            sb.append("JOIN cse360question q ON q.answer_id LIKE CONCAT('%', a.id, '%') OR q.answer_id = a.id ");
            sb.append("WHERE q.id = ? ");
            sb.append("AND a.id NOT IN (");
            sb.append(" SELECT answer_id FROM cse360answerviews WHERE user_id = ? AND is_read = TRUE ");
            sb.append(")");
        } else {
            // For all questions
            sb.append("WHERE a.id NOT IN (");
            sb.append(" SELECT answer_id FROM cse360answerviews WHERE user_id = ? AND is_read = TRUE ");
            sb.append(")");
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sb.toString())) {
            if (questionId != null) {
                pstmt.setInt(1, questionId);
                pstmt.setInt(2, userId);
            } else {
                pstmt.setInt(1, userId);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("unreadCount");
            }
        }
        return 0;
    }

    /**
     * Returns a list of all questions that are still "unresolved." Interpreted here
     * as having no preferred answer or preferred_answer == 0.
     *
     * @return List of unresolved Question objects
     */
    public List<Question> getAllUnresolvedQuestions() throws SQLException {
        // "preferred_answer" is the int column that can store a chosen "best" or "accepted" answer
        // We treat null or 0 as “unresolved.”
        String query = "SELECT * FROM cse360question WHERE preferred_answer IS NULL OR preferred_answer = 0";
        List<Question> questions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_on");
                LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_on");
                LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
                int preferredAnswer = rs.getInt("preferred_answer");

                User author = databaseHelper.getUser(authorId);
                String authorName = "User";
                if (author != null) {
                    authorName = author.getName();
                }
                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

                Question q = new Question(
                    id, title, text, authorId, createdOn, updatedOn,
                    textDeserial(title + text),
                    preferredAnswer, author, authorName, relatedId
                );
                questions.add(q);
            }
        }
        return questions;
    }
    
    /**
     * Returns a list of the current user's unresolved questions,
     * including the number of unread potential answers for each.
     *
     * @param userId The student’s user ID
     * @return List of unresolved Question objects with unread answer counts
     */
    public List<Question> getAllUnresolvedQuestionsForUser(int userId) throws SQLException {
        String query = "SELECT q.*, " +
                       "(SELECT COUNT(a.id) FROM cse360answer a " +
                       " WHERE q.answer_id LIKE CONCAT('%', a.id, '%') " +
                       " AND a.id NOT IN (SELECT answer_id FROM cse360answerviews " +
                       "                  WHERE user_id = ? AND is_read = TRUE)) AS unread_count " +
                       "FROM cse360question q " +
                       "WHERE (q.preferred_answer IS NULL OR q.preferred_answer = 0) " +
                       "AND q.author = ?";

        List<Question> questions = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId); // For unread answers
            pstmt.setInt(2, userId); // For questions posted by the user
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_on");
                LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_on");
                LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
                int preferredAnswer = rs.getInt("preferred_answer");
                int unreadCount = rs.getInt("unread_count"); // Store unread answer count

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";
                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                // Create Question object and set the unread count as metadata
                Question q = new Question(id, title, text, authorId, createdOn, updatedOn,
                                          textDeserial(title + text),
                                          preferredAnswer, author, authorName, relatedId);
                q.setUnreadCount(unreadCount); // Custom method in Question class to store unread count
                questions.add(q);
            }
        }
        return questions;
    }


    /**
     * Retrieves only those answers that are NOT the chosen "preferred answer"
     * for a given question, i.e. "potential" answers that have not been marked as accepted.
     *
     * @param questionId The question’s unique ID
     * @return List of "potential" Answer objects
     */
    /**
     * Retrieves only those answers that are NOT the chosen "preferred answer"
     * for a given question, i.e. "potential" answers that have not been marked as accepted.
     *
     * @param questionId The question’s unique ID
     * @return List of "potential" Answer objects
     */
    public List<Answer> getPotentialAnswersForQuestion(int questionId) throws SQLException {
        Question q = getQuestion(questionId);
        if (q == null) {
            return new ArrayList<>();
        }

        int preferredId = q.getPreferredAnswer();

        // Get all answers for the question
        List<Answer> allAnswers = getAllAnswersForQuestion(questionId);
        List<Answer> potentialAnswers = new ArrayList<>();

        // Filter out the preferred answer
        for (Answer ans : allAnswers) {
            if (ans.getId() != preferredId) {
                potentialAnswers.add(ans);
            }
        }

        return potentialAnswers;
    }

    // ===============================================================================
    // *** END OF ADDED CODE FOR USER STORIES ***
    // ===============================================================================

    // Check if the database is empty - Only checks the question database at the
    // moment
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360question";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    // Registers a new question in the database.
    public void registerQuestion(Question question) throws SQLException {
        String insertQuestion = "INSERT INTO cse360question (title, text, author) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getText());
            pstmt.setInt(3, question.getAuthorId());
            pstmt.executeUpdate();
        }
        System.out.println("Question registered successfully.");
    }

    // Registers a new answer in the database.
    public void registerAnswerWithQuestion(Answer answer, int relatedID) throws SQLException {
        String insertAnswer = "INSERT INTO cse360answer (text, author) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, answer.getText());
            pstmt.setInt(2, answer.getAuthorId());
            pstmt.executeUpdate();

            ResultSet newID = pstmt.getGeneratedKeys();
            if (newID.next()) {
                int answerID = newID.getInt(1);
                addRelationToQuestion(relatedID, answerID);
            }

        }
        System.out.println("Answer registered successfully.");
    }

    // Registers a new answer in the database.
    public void registerAnswerWithAnswer(Answer answer, int relatedID) throws SQLException {
        String insertAnswer = "INSERT INTO cse360answer (text, author) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, answer.getText());
            pstmt.setInt(2, answer.getAuthorId());
            pstmt.executeUpdate();

            ResultSet newID = pstmt.getGeneratedKeys();
            if (newID.next()) {
                int answerID = newID.getInt(1);
                addRelationToAnswer(relatedID, answerID);
            }

        }
        System.out.println("Answer registered successfully.");
    }

    // Deletes a question row from the SQL table
    public boolean deleteQuestion(int id) {
        String query = "DELETE FROM cse360question AS c WHERE c.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
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
        String query = "DELETE FROM cse360answer AS c WHERE c.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
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

    // Add a relation to the question database
    public void addRelationToQuestion(int questionID, int answerID) {
        String selectQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
        String updateQuery = "UPDATE cse360question SET answer_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setInt(1, questionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String answerIDs = rs.getString("answer_id");
                String newAnswerIDs;
                if (answerIDs == null || answerIDs.trim().isEmpty()) {
                    newAnswerIDs = String.valueOf(answerID);
                } else {
                    newAnswerIDs = answerIDs + ", " + answerID;
                }
                try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
                    upstmt.setString(1, newAnswerIDs);
                    upstmt.setInt(2, questionID);
                    upstmt.executeUpdate();
                }
            } else {
                System.err.println("Could not find a question with id: " + questionID);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nERROR IN ADD-RELATION METHOD");
        }
    }

    // Add a relation to the answer database
    public void addRelationToAnswer(int answerID, int relatedID) {
        String selectQuery = "SELECT answer_id FROM cse360answer WHERE id = ?";
        String updateQuery = "UPDATE cse360answer SET answer_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setInt(1, answerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String answerIDs = rs.getString("answer_id");
                String newAnswerIDs;
                if (answerIDs == null || answerIDs.trim().isEmpty()) {
                    newAnswerIDs = String.valueOf(relatedID);
                } else {
                    newAnswerIDs = answerIDs + ", " + relatedID;
                }
                try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
                    upstmt.setString(1, newAnswerIDs);
                    upstmt.setInt(2, answerID);
                    upstmt.executeUpdate();
                }
            } else {
                System.err.println("Could not find an answer with id: " + answerID);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nERROR IN ADD-RELATION METHOD");
        }
    }

    // Delete a relation from the relation database
    public boolean deleteRelation(int questionID, int answerID) {
        String selectQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
        String updateQuery = "UPDATE cse360question SET answer_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setInt(1, questionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String answerIDs = rs.getString("answer_id");
                if (answerIDs == null || answerIDs.trim().isEmpty()) {
                    System.err.println("Error: There are no answers related to this question.");
                    return false;
                }
                String[] answerIdArray = answerIDs.split(",\\s");
                List<String> answerIdList = new ArrayList<>(Arrays.asList(answerIdArray));
                boolean idFound = answerIdList.remove(String.valueOf(answerID));
                if (!idFound) {
                    System.out.println("Could not find answer id relation in provided question id: " + questionID);
                    return false;
                }
                String newAnswerIDs = String.join(", ", answerIdList);
                try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
                    upstmt.setString(1, newAnswerIDs.isEmpty() ? null : newAnswerIDs);
                    upstmt.setInt(2, questionID);
                    upstmt.executeUpdate();
                    System.out.println("Answer ID: " + answerID
                            + " was successfully removed from relation with question id: " + questionID);
                    return true;
                }
            } else {
                System.err.println("Could not find a question with id: " + questionID);
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "\nERROR IN DELETE-RELATION METHOD");
            return false;
        }
    }

    // Get a question object with a provided question id
    public Question getQuestion(Integer questionID) throws SQLException {
        String query = "SELECT * FROM cse360question AS c WHERE c.id = ?	";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
                List<String> comp = textDeserial(title + text);
                int preferredAnswer = rs.getInt("preferred_answer");

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

                return new Question(
                    id, title, text, authorId, createdOn, updatedOn,
                    comp, preferredAnswer, author, authorName, relatedId
                );
            }
        }
        return null;
    }

    // Get a question object with a provided question title
    public Question getQuestion(String questionTitle) throws SQLException {
        String query = "SELECT * FROM cse360question AS c WHERE c.title = ?	";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, questionTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
                List<String> comp = textDeserial(title + text);
                int preferredAnswer = rs.getInt("preferred_answer");

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

                return new Question(
                    id, title, text, authorId, createdOn, updatedOn,
                    comp, preferredAnswer, author, authorName, relatedId
                );
            }
        }
        return null;
    }

    // Get an answer object with a provided answer id
    public Answer getAnswer(Integer answerID) throws SQLException {
        String query = "SELECT * FROM cse360answer AS c WHERE c.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                return new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);
            }
        }
        return null;
    }

    // Get an answer object with a provided answer text
    public Answer getAnswer(String answerText) throws SQLException {
        String query = "SELECT * FROM cse360answer AS c WHERE c.text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answerText);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                return new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);
            }
        }
        return null;
    }

    // Retrieve all questions from the question database
    public List<Question> getAllQuestions() throws SQLException {
        String query = "SELECT * FROM cse360question";
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;
                int preferredAnswer = rs.getInt("preferred_answer");

                List<String> comp = textDeserial(title + text);
                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                Question question = new Question(id, title, text, authorId, createdOn, updatedOn,
                        comp, preferredAnswer, author, authorName, relatedId);
                questions.add(question);
            }
        }
        return questions;
    }

    public List<Question> getAllUnansweredQuestions() throws SQLException {
        String query = "SELECT * FROM cse360question WHERE answer_id IS NULL OR answer_id = ''";
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;
                int preferredAnswer = rs.getInt("preferred_answer");

                List<String> comp = textDeserial(text);
                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                Question question = new Question(
                        id, title, text, authorId, createdOn, updatedOn,
                        comp, preferredAnswer, author, authorName, relatedId);
                questions.add(question);
            }
        }
        return questions;
    }

    public List<Question> getAllAnsweredQuestions() throws SQLException {
        String query = "SELECT * FROM cse360question WHERE answer_id IS NOT NULL AND answer_id <> ''";
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;
                int preferredAnswer = rs.getInt("preferred_answer");

                List<String> comp = textDeserial(text);
                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                Question question = new Question(id, title, text, authorId, createdOn, updatedOn,
                        comp, preferredAnswer, author, authorName, relatedId);
                questions.add(question);
            }
        }
        return questions;
    }

    // Retrieve all answers from the answer database
    public List<Answer> getAllAnswers() throws SQLException {
        String query = "SELECT * FROM cse360answer";
        List<Answer> answers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                int authorId = rs.getInt("author");
                Timestamp created = rs.getTimestamp("created_On");
                LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                Timestamp updated = rs.getTimestamp("updated_On");
                LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;

                User author = databaseHelper.getUser(authorId);
                String authorName = (author != null) ? author.getName() : "User";

                String answerIDs = rs.getString("answer_id");
                List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

                Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);
                answers.add(answer);
            }
        }
        return answers;
    }

    // Retrieve all of the answers that are associated with a given question id
    public List<Answer> getAllAnswersForQuestion(int questionID) throws SQLException {
        String query = "SELECT answer_id FROM cse360question WHERE id = ?";
        List<Answer> answers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String answerIDs = rs.getString("answer_id");
                if (answerIDs == null || answerIDs.trim().isEmpty()) {
                    return answers;
                }
                String[] answerIdArray = answerIDs.split(",\\s");
                String temp = String.join(",", Collections.nCopies(answerIdArray.length, "?"));
                String newQuery = "SELECT id, text, author, created_On, updated_On, answer_id FROM cse360answer WHERE id IN ("
                        + temp + ")";
                try (PreparedStatement upstmt = connection.prepareStatement(newQuery)) {
                    for (int i = 0; i < answerIdArray.length; i++) {
                        upstmt.setInt(i + 1, Integer.parseInt(answerIdArray[i].trim()));
                    }
                    ResultSet newRs = upstmt.executeQuery();
                    while (newRs.next()) {
                        int id = newRs.getInt("id");
                        String text = newRs.getString("text");
                        int authorId = newRs.getInt("author");
                        Timestamp created = newRs.getTimestamp("created_On");
                        LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                        Timestamp updated = newRs.getTimestamp("updated_On");
                        LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;

                        User author = databaseHelper.getUser(authorId);
                        String authorName = (author != null) ? author.getName() : "User";

                        String subAnswerIDs = newRs.getString("answer_id");
                        List<String> relatedId = (subAnswerIDs != null) ? List.of(subAnswerIDs.split(",\\s")) : null;

                        Answer ans = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);
                        answers.add(ans);
                    }
                }
            } else {
                System.err.println("Error. Question id, " + questionID + ", was not found");
            }
        }
        return answers;
    }

    // Retrieve all of the answers that are associated with a given answer id
    public List<Answer> getAllAnswersForAnswer(int answerID) throws SQLException {
        String query = "SELECT answer_id FROM cse360answer WHERE id = ?";
        List<Answer> answers = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answerID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String answerIDs = rs.getString("answer_id");
                if (answerIDs == null || answerIDs.trim().isEmpty()) {
                    return answers;
                }
                String[] answerIdArray = answerIDs.split(",\\s");
                String temp = String.join(",", Collections.nCopies(answerIdArray.length, "?"));
                String newQuery = "SELECT id, text, author, created_On, updated_On, answer_id FROM cse360answer WHERE id IN ("
                        + temp + ")";
                try (PreparedStatement upstmt = connection.prepareStatement(newQuery)) {
                    for (int i = 0; i < answerIdArray.length; i++) {
                        upstmt.setInt(i + 1, Integer.parseInt(answerIdArray[i].trim()));
                    }
                    ResultSet newRs = upstmt.executeQuery();
                    while (newRs.next()) {
                        int id = newRs.getInt("id");
                        String text = newRs.getString("text");
                        int authorId = newRs.getInt("author");
                        Timestamp created = newRs.getTimestamp("created_On");
                        LocalDateTime createdOn = (created != null) ? created.toLocalDateTime() : null;
                        Timestamp updated = newRs.getTimestamp("updated_On");
                        LocalDateTime updatedOn = (updated != null) ? updated.toLocalDateTime() : null;

                        User author = databaseHelper.getUser(authorId);
                        String authorName = (author != null) ? author.getName() : "User";

                        String subAnswerIDs = newRs.getString("answer_id");
                        List<String> relatedId = (subAnswerIDs != null) ? List.of(subAnswerIDs.split(",\\s")) : null;

                        Answer ans = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);
                        answers.add(ans);
                    }
                }
            } else {
                System.err.println("Error. Answer id, " + answerID + ", was not found");
            }
        }
        return answers;
    }

    // Method used to convert the text of a question into a list of unique words
    // without special characters for comparison to others
    public List<String> textDeserial(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            String cleanText = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();
            String[] wordsArray = cleanText.split("\\s+");
            Set<String> uniqueText = new HashSet<>(Arrays.asList(wordsArray));
            return new ArrayList<>(uniqueText);
        }
    }

    // Update the contents of a question object with those of the passed question object
    public void updateQuestion(Question question) {
        String query = "UPDATE cse360question Set title = ?, text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question.getTitle());
            pstmt.setString(2, question.getText());
            pstmt.setInt(3, question.getId());

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Question has been updated.");
            } else {
                System.out.println("No matching question was found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error trying to update question in updateQuestion method.");
        }
    }

    // Update the contents of an answer object with those of the passed answer object
    public void updateAnswer(Answer answer) {
        String query = "UPDATE cse360answer Set text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answer.getText());
            pstmt.setInt(2, answer.getId());

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                System.out.println("Answer has been updated.");
            } else {
                System.out.println("No matching answer was found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error trying to update answer in updateAnswer method.");
        }
    }
}
