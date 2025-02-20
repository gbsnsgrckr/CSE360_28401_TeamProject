//package tests;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import application.*;
//import databasePart1.*;
//
//public class HW2TestBed {
//	private static final QAHelper1 qaHelper = new QAHelper1();
//	static int numPassed = 0;
//	static int numFailed = 0;
//	static int size;
//	private static Question question;
//	private static Answer answer;
//	private static QuestionsSet questionsSet;
//	private static AnswersSet answersSet;
//	private static List<Question> questions;
//	private static List<Answer> answers;
//	private static List<AnswersSet> Aset;
//
//	public static void main(String[] args) {
//		// Initialize the test environment
//
//		// Connect to the database
//		try {
//			qaHelper.connectToDatabase();
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//
//		// Populate the databases
//		new PopulateQADatabase(qaHelper).execute();
//
//		// Test Case 1
//		// Checks the size of the question database after populating with 10 questions.
//		System.out.println("Starting TEST 1\n");
//		performTestCase(1, false);
//		System.out.println("\nTEST 1 has completed\n");
//
//		// Test Case 2
//		// Checks the size of the answer database after populating with 15 answers
//		System.out.println("Starting TEST 2\n");
//		performTestCase(2, false);
//		System.out.println("\nTEST 2 has completed\n");
//
//		// Test Case 3
//		// Checks the size of the relation database after populating with the above 15		
//		// answers
//		System.out.println("Starting TEST 3\n");
//		performTestCase(3, true);
//		System.out.println("\nTEST 3 has completed\n");
//
//		// Test Case 4
//		// Deletes 2 questions from the question database
//		System.out.println("Starting TEST 4\n");
//		performTestCase(4, true);
//		System.out.println("\nTEST 4 has completed\n");
//
//		// Test Case 5
//		// Deletes 3 answers from the answer database
//		System.out.println("Starting TEST 5\n");
//		performTestCase(5, true);
//		System.out.println("\nTEST 5 has completed\n");
//
//		// Test Case 6
//		// Checks size of relation database after removing previous questions and
//		// answers
//		System.out.println("Starting TEST 6\n");
//		performTestCase(6, false);
//		System.out.println("\nTEST 6 has completed\n");
//
//		// Test Case 7
//		// Add a question to the database
//		System.out.println("Starting TEST 7\n");
//		performTestCase(7, false);
//		System.out.println("\nTEST 7 has completed\n");
//
//		// Test Case 8
//		// Update the title of a question in the database
//		System.out.println("Starting TEST 8\n");
//		performTestCase(8, true);
//		System.out.println("\nTEST 8 has completed\n");
//
//		// Test Case 9
//		// Update the text of an answer
//		System.out.println("Starting TEST 9\n");
//		performTestCase(9, true);
//		System.out.println("\nTEST 9 has completed\n");
//
//		// Test Case 10
//		// Delete the contents of the question database
//		System.out.println("Starting TEST 10\n");
//		performTestCase(10, false);
//		System.out.println("\nTEST 10 has completed\n");
//
//		// Display the results of the series of tests
//		displayResults();
//	}
//
//	private static void performTestCase(int testNum, boolean flag) {
//
//		// Perform appropriate test according to testNum
//		switch (testNum) {
//
//		// Test the size of the question database after populating
//		case 1:
//			// Retrieve question database size
//			try {
//				size = qaHelper.getAllQuestions().size();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.err.println("Error calling .getAllQuestions().size() in TEST 1");
//				;
//				return;
//			}
//
//			// Check if size is equal to 10
//			if (size == 10 && flag == false) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out.println(
//						"\nFailed: Question database is expected to have 10 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		// Test the size of the answer database after populating
//		case 2:
//			// Retrieve size of the list of answers in the database
//			try {
//				size = qaHelper.getAllAnswers().size();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() in TEST 2");
//				return;
//			}
//
//			// Check if size is equal to 15
//			if (size == 15 && flag == false) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out.println(
//						"\nFailed: Relation database is expected to have 15 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		// Test the size of the relation database after populating
//		case 3:
//			// Retrieve a list of all current questions in the database
//			try {
//				questions = qaHelper.getAllQuestions();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() in TEST 3");
//				return;
//			}
//			
//			// Initialize Aset before using it
//			Aset = new ArrayList<>();
//			
//			// Iterate through each question
//			for (Question question : questions) {
//				try {
//					// Create an AnswersSet for each question
//					answersSet = new AnswersSet();					
//					// Store all answers for this question in answers
//					answers = qaHelper.getAllAnswersForQuestion(question.getId());
//					// Store all answers for this question in an AnswersSet
//					answersSet.setQuestion(question);
//					answersSet.setAnswers(answers);
//					
//					// Add answersSet to Aset
//					Aset.add(answersSet);
//
//				} catch (SQLException e) {
//					e.printStackTrace();
//					System.out.println("Error pulling answers for question: " + question.getId());
//				}
//			}
//
//			// Count objects in the list within the list
//			int countObjects = Aset.stream()
//								.mapToInt(AnswersSet::size)
//								.sum();
//
//			// Check if size is equal to 15
//			if (countObjects == 15 && flag == true) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out.println(
//						"\nFailed: Relation database is expected to have 15 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		case 4:
//
//			// Remove elements from question database
//			// Remove question 1 - has 1 answer related to it
//			qaHelper.deleteQuestion(1);
//			// Remove question 5 - has 2 answers related to it
//			qaHelper.deleteQuestion(5);
//
//			// Retrieve question database size
//			try {
//				size = qaHelper.getAllQuestions().size();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.err.println("Error calling .getAllQuestions().size() in TEST 4");
//				;
//				return;
//			}
//
//			// Check if size is equal to 8
//			if (size == 8 && flag == true) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out
//						.println("\nFailed: Question database is expected to have 8 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		case 5:
//
//			// Remove elements from answer database
//			// Remove answer 3
//			qaHelper.deleteAnswer(3);
//			// Remove answer 7
//			qaHelper.deleteAnswer(7);
//			// Remove answer 8
//			qaHelper.deleteAnswer(8);
//
//			// Retrieve size of the list of answers in the database
//			try {
//				size = qaHelper.getAllAnswers().size();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() in TEST 5");
//				return;
//			}
//
//			// Check if size is equal to 12
//			if (size == 12 && flag == true) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out
//						.println("\nFailed: Answer database is expected to have 12 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		case 6:
//
//			// Since elements have been deleted from question and answer database, check
//			// size of relation database again
//			// Retrieve a list of all current questions in the database
//			try {
//				questions = qaHelper.getAllQuestions();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() in TEST 6");
//				return;
//			}
//			
//			// Initialize Aset before using it
//			Aset = new ArrayList<>();
//
//			// Iterate through each question
//			for (Question question : questions) {
//				try {
//					// Create an AnswersSet for each question
//					answersSet = new AnswersSet();
//					// Store all answers for this question in answers
//					answers = qaHelper.getAllAnswersForQuestion(question.getId());
//					// Store all answers for this question in an AnswersSet
//					answersSet.setQuestion(question);
//					answersSet.setAnswers(answers);
//					// Add answersSet to Aset
//					Aset.add(answersSet);
//
//				} catch (SQLException e) {
//					e.printStackTrace();
//					System.out.println("Error pulling answers for question: " + question.getId());
//				}
//			}
//			
//			// Reset count before using in this test
//			countObjects = 0;
//			
//			// Count objects in the list within the list
//			countObjects = Aset.stream()
//								.mapToInt(AnswersSet::size)
//								.sum();
//
//			// Check if size is equal to 9
//			if (countObjects == 9 && flag == false) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out
//						.println("\nFailed: Relation database is expected to have 9 entries but actually has: " + size);
//				numFailed++;
//			}
//			break;
//
//		case 7:
//			// Add a question to the database
//			// Create new question object
//			question = new Question("What color is the sky?", "Is the sky really blue or is it just seemingly blue?",
//					1337);
//
//			// Register new question
//			try {
//				qaHelper.registerQuestion(question);
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error registering a new question for TEST 7");
//				return;
//			}
//
//			// Check if the newly added question can be pull from the database
//			try {		
//				question = qaHelper.getQuestion(11);
//			if (question != null && flag == false) {
//				numPassed++;
//				System.out.println("\n*** TEST PASSED ***");
//			} else {
//				System.out.println("\nFailed: Newly added question not found in the database of questions");
//				numFailed++;
//			}
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() for TEST 7");
//				return;
//			}
//			break;
//
//		case 8:
//
//			// Edit the title of a question
//			// Get question object of question we want to change(#11)
//			try {
//				question = qaHelper.getQuestion(11);
//
//				// Edit the title to something easily checked
//				question.setTitle("5");
//				qaHelper.updateQuestion(question);
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error trying to get and update question for TEST 8");
//				return;
//			}			
//			
//			try {
//				// Retrieve the updated question from the database that should be edited now
//			Question questionFromDatabase = qaHelper.getQuestion(11);			
//
//			// Check if question object is contained within the new list of questions
//			
//			if (questionFromDatabase.getTitle().equals(question.getTitle()) && flag == true) {
//				// Increment numPassed
//				numPassed++;
//				System.out.println("\n*** TEST PASSED ***");
//			} else {
//				System.out.println("\nFailed: Newly updated question not found in the database of questions");
//				numFailed++;
//			}
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getQuestion() for TEST 8");
//				return;
//			}
//			
//			break;
//
//		case 9:
//
//			// Edit the text of an answer
//			// Get answer object of answer we want to change(#11)
//			try {
//				answer = qaHelper.getAnswer(10);
//
//				// Edit the text to something easily checked
//				answer.setText("no");
//				qaHelper.updateAnswer(answer);
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error trying to get and update answer for TEST 9");
//				return;
//			}
//
//			try {
//				// Retrieve the updated question from the database that should be edited now
//			Answer answerFromDatabase = qaHelper.getAnswer(10);				
//			
//			if (answerFromDatabase.getText().equals(answer.getText()) && flag == true) {
//				// Increment numPassed
//				numPassed++;
//				System.out.println("\n*** TEST PASSED ***");
//			} else {
//				System.out.println("\nFailed: Newly updated answer not found in the database of answers");
//				numFailed++;
//			}
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAnswer() for TEST 9");
//				return;
//			}
//			break;
//
//		case 10:
//
//			// Clear the database
//			// Retrieve the list of questions from the database
//			try {
//				questions = qaHelper.getAllQuestions();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() for TEST 10");
//				return;
//			}
//
//			for (Question question : questions) {
//				qaHelper.deleteQuestion(question.getId());
//			}			
//
//			// Check the size of the question database (Should be empty)
//			try {
//			if (qaHelper.isDatabaseEmpty() && flag == false) {
//				System.out.println("\n*** TEST PASSED ***");
//				// Increment numPassed if it passes
//				numPassed++;
//			} else {
//				// Display error message if it fails
//				System.out
//						.println("\nFailed: Question database is expected to have 0 entries but actually has: " + size);
//				numFailed++;
//			}
//			} catch (SQLException e) {
//				e.printStackTrace();
//				System.out.println("Error calling .getAllQuestions() for TEST 10");
//				return;
//			}
//			break;
//		}
//		return;
//
//	}
//
//	private static void displayResults() {
//		
//		System.out.println("\n\nTesting has successfully completed. The results are...\n\n");
//		
//		System.out.println("The number of tests that passed is: 	" + numPassed + "\n\n");
//		
//		System.out.println("The number of tests that failed is: 	" + numFailed + "\n\n");
//		
//		System.out.println("Thank you");
//	}
//
//}
