package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.*;
import databasePart1.*;

public class QuestionAndAnswerAutomation {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	static int numPassed = 0;
	static int numFailed = 0;
	static int size;
	private static String testString = "This is the request";
	

	public static void main(String[] args) {
		// Initialize the test environment

		// Connect to the database
		try {
			databaseHelper.connectToDatabase();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		// Test Case 1
		//Test if we can set a question correctly
		System.out.println("Starting TEST 1\n");
		performTestCase(1, false);
		System.out.println("\nTEST 1 has completed\n");
		
		// Test Case 7
		//Test if we can update a question
		System.out.println("Starting TEST 2\n");
		performTestCase(2, false);
		System.out.println("\nTEST 2 has completed\n");
		
		// Test Case 8
		//Test if we can get an answer
		System.out.println("Starting TEST 3\n");
		performTestCase(3, false);
		System.out.println("\nTEST 3 has completed\n");
		
		// Test Case 9
		//Test if we can update an answer
		System.out.println("Starting TEST 4\n");
		performTestCase(4, false);
		System.out.println("\nTEST 4 has completed\n");
		
		// Test Case 10
		//Test if we can get all the answers by the question
		System.out.println("Starting TEST 5\n");
		performTestCase(5, false);
		System.out.println("\nTEST 5 has completed\n");
		

		// Display the results of the series of tests
		displayResults();
		List<String> role = new ArrayList<>();
		role.add("Admin");
		role.add("Student");
		User user = new User("darren", "Darren", "123456Ab.", "d@email.com", role, false);
		try {
			databaseHelper.register(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void performTestCase(int testNum, boolean flag) {
		
		// Perform appropriate test according to testNum
		switch (testNum) {

		case 1:
			PopulateQADatabase populate = new PopulateQADatabase(databaseHelper.qaHelper);
			populate.execute();
			
			try {
				String question1 = databaseHelper.qaHelper.getQuestion(1).getText();
				if(!question1.equals("Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?")){
					numFailed++;
					System.out.println("Question is wrong " + question1);
				}
			} catch (SQLException e) {
				System.out.println("Case 1 not working");
				numFailed++;
				e.printStackTrace();
			}
			numPassed++;
			break;
		
		case 2:
			try {
				Question question = databaseHelper.qaHelper.getQuestion(1);
				question.setText("It works");
				databaseHelper.qaHelper.updateQuestion(question);
				if(!databaseHelper.qaHelper.getQuestion(1).getText().equals("It works")) {
					System.out.println("Question is wrong");
					numFailed++;
				}
				question.setText("Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?");
				databaseHelper.qaHelper.updateQuestion(question);
			} catch (SQLException e) {
				System.out.println("Case 2 is not working");
				numFailed++;
				
				e.printStackTrace();
			}
			numPassed++;
			break;
			
		case 3:
			try {
				Answer answer = databaseHelper.qaHelper.getAnswer(3);
				if(!answer.getText().equals("Makes sense to me.")) {
					System.out.println("Answer is wrong" + answer.getText());
					numFailed++;
				}
			}
			catch(SQLException e){
				System.out.println("Case 3 is not working");
				numFailed++;
				e.printStackTrace();
			}
			numPassed++;
			break;
			
		case 4:
			try {
				Answer answer = databaseHelper.qaHelper.getAnswer(3);
				answer.setText("It works!");
				databaseHelper.qaHelper.updateAnswer(answer);
				if(!databaseHelper.qaHelper.getAnswer(3).getText().equals("It works!")) {
					numFailed++;
					System.out.println("Case 4 not working");
				}
				answer.setText("Makes sense to me.");
				databaseHelper.qaHelper.updateAnswer(answer);
			}
			catch(SQLException e){
				System.out.println("Case 4 is not working");
				numFailed++;
				e.printStackTrace();
			}
			numPassed++;
			break;
			
		case 5: 
			try {
				List<Answer> answer = databaseHelper.qaHelper.getAllAnswersForQuestion(1);
				if(answer.size() != 2) {
					System.out.println("Wrong number of answers");
					System.out.println(answer.size());
					numFailed++;
				}
				
				if(!answer.get(1).getText().equals("Oh well another test happened")) {
					System.out.println("this does not work" + answer.get(1).getText());
					numFailed++;
				}
				}
			catch(SQLException e) {
				System.out.println("Case 5 nor working");
				numFailed++;
			}
			numPassed++;
			break;
		}
	}

	private static void displayResults() {
		
		System.out.println("\n\nTesting has successfully completed. The results are...\n\n");
		
		System.out.println("The number of tests that passed is: 	" + numPassed + "\n\n");
		
		System.out.println("The number of tests that failed is: 	" + numFailed + "\n\n");
		
		System.out.println("Thank you");
	}
}