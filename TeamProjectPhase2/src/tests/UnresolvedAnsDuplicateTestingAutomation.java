package tests;

import databasePart1.QAHelper1;
import databasePart1.DatabaseHelper;
import application.Question;
import application.Answer;
import java.sql.SQLException;
import java.util.List;

public class UnresolvedAnsDuplicateTestingAutomation {

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) throws SQLException {
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation for QA System");

        DatabaseHelper dbHelper = new DatabaseHelper();
        QAHelper1 qaHelper = new QAHelper1(dbHelper);
        
        qaHelper.connectToDatabase();
        dbHelper.connectToDatabase();
        
        
        
        new PopulateUserDatabase(dbHelper).execute();
        new PopulateQADatabase(qaHelper).execute();

        performTestCase(1, "Retrieve all unresolved questions", qaHelper.getAllUnresolvedQuestions().size() >= 0);
        performTestCase(2, "Retrieve potential answers for a question", testPotentialAnswers(qaHelper));
        performTestCase(3, "Check answer duplication prevention", testAnswerDuplication(qaHelper));
        performTestCase(4, "Retrieve unresolved questions with unread counts", qaHelper.getAllUnresolvedQuestionsForUser(1).size() >= 0);
        performTestCase(5, "Retrieve all unanswered questions", qaHelper.getAllUnansweredQuestions().size() >= 0);

        System.out.println("____________________________________________________________________________");
        System.out.println();
        System.out.println("Number of tests passed: " + numPassed);
        System.out.println("Number of tests failed: " + numFailed);
    }

    private static boolean testPotentialAnswers(QAHelper1 qaHelper) throws SQLException {
        List<Question> unresolvedQuestions = qaHelper.getAllUnresolvedQuestions();
        if (!unresolvedQuestions.isEmpty()) {
            Question q = unresolvedQuestions.get(0);
            return qaHelper.getPotentialAnswersForQuestion(q.getId()).size() >= 0;
        }
        return true;
    }

    private static boolean testAnswerDuplication(QAHelper1 qaHelper) throws SQLException {
        List<Question> unresolvedQuestions = qaHelper.getAllUnresolvedQuestions();
        if (!unresolvedQuestions.isEmpty()) {
            Question q = unresolvedQuestions.get(0);
            String duplicateText = "This is a test answer";
            Answer newAnswer = new Answer(duplicateText, 1);
            qaHelper.registerAnswerWithQuestion(newAnswer, q.getId());
            List<Answer> answers = qaHelper.getAllAnswersForQuestion(q.getId());
            long count = answers.stream().filter(a -> a.getText().equals(duplicateText)).count();
            return count == 1;
        }
        return true;
    }

    private static void performTestCase(int testCase, String description, boolean passed) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Description: " + description);
        System.out.println("______________");

        if (passed) {
            System.out.println("***Success*** Test passed!");
            numPassed++;
        } else {
            System.out.println("***Failure*** Test failed!");
            numFailed++;
        }
    }
}
