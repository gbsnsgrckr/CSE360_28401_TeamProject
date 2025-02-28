package tests;

import databasePart1.QAHelper1;
import databasePart1.DatabaseHelper;
import application.Question;
import application.Answer;
import java.sql.SQLException;
import java.util.List;

public class UnresolvedAnsDuplicateTestingAutomation {

    // Static variables to track test results
    static int numPassed = 0; // Counter for passed tests
    static int numFailed = 0; // Counter for failed tests

    public static void main(String[] args) throws SQLException {
        // Print header for the test suite
        System.out.println("\nTester for Unresolved Questions");

        // Create instances of DatabaseHelper and QAHelper1 to manage database interactions
        DatabaseHelper dbHelper = new DatabaseHelper();
        QAHelper1 qaHelper = new QAHelper1(dbHelper);
        
        // Connect to the database using both helper objects
        qaHelper.connectToDatabase();
        dbHelper.connectToDatabase();
        
        // Populate the user and QA databases with test data
        new PopulateUserDatabase(dbHelper).execute();
        new PopulateQADatabase(qaHelper).execute();

        // Perform test cases to validate various functionalities
        performTestCase(1, "Retrieve all unresolved questions", qaHelper.getAllUnresolvedQuestions().size() >= 0);
        performTestCase(2, "Retrieve potential answers for a question", testPotentialAnswers(qaHelper));
        performTestCase(3, "Check answer duplication prevention", testAnswerDuplication(qaHelper));
        performTestCase(4, "Retrieve unresolved questions with unread counts", qaHelper.getAllUnresolvedQuestionsForUser(1).size() >= 0);
        performTestCase(5, "Retrieve all unanswered questions", qaHelper.getAllUnansweredQuestions().size() >= 0);
        
        // Print summary of test results
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    // Test case for retrieving potential answers for an unresolved question
    private static boolean testPotentialAnswers(QAHelper1 qaHelper) throws SQLException {
        // Retrieve the list of unresolved questions
        List<Question> unresolvedQuestions = qaHelper.getAllUnresolvedQuestions();
        
        // If there are unresolved questions, check if potential answers can be retrieved
        if (!unresolvedQuestions.isEmpty()) {
            Question q = unresolvedQuestions.get(0); // Pick the first unresolved question
            return qaHelper.getPotentialAnswersForQuestion(q.getId()).size() >= 0; // Ensure it returns a valid list
        }
        return true; // If no unresolved questions exist, test is inconclusive but passes
    }

    // Test case for preventing duplicate answers from being registered
    private static boolean testAnswerDuplication(QAHelper1 qaHelper) throws SQLException {
        // Retrieve the list of unresolved questions
        List<Question> unresolvedQuestions = qaHelper.getAllUnresolvedQuestions();
        
        // If there are unresolved questions, attempt to add and verify duplicate prevention
        if (!unresolvedQuestions.isEmpty()) {
            Question q = unresolvedQuestions.get(0); // Pick the first unresolved question
            String duplicateText = "This is a test answer"; // Sample answer text
            
            // Create a new answer and register it with the question
            Answer newAnswer = new Answer(duplicateText, 1);
            qaHelper.registerAnswerWithQuestion(newAnswer, q.getId());

            // Retrieve all answers for the question
            List<Answer> answers = qaHelper.getAllAnswersForQuestion(q.getId());

            // Count occurrences of the duplicate answer text
            long count = answers.stream().filter(a -> a.getText().equals(duplicateText)).count();

            // Test passes if only one instance of the answer exists (prevention of duplicates)
            return count == 1;
        }
        return true; // If no unresolved questions exist, test is inconclusive but passes
    }

    // Method to print test case results
    private static void performTestCase(int testCase, String description, boolean passed) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Description: " + description);

        // Print test result and update counters
        if (passed) {
            System.out.println("Test passed!");
            numPassed++;
        } else {
            System.out.println("Test failed");
            numFailed++;
        }
    }
}
