package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

import databasePart1.QAHelper;

public class StartCSE360 extends Application {
	private static final QAHelper qaHelper = new QAHelper();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			qaHelper.connectToDatabase(); // Connect to the database
			new QuestionPage(qaHelper).show(primaryStage);

			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		// Removes icon from title bar in alert window		
		//primaryStage.getIcons().clear();
		
		primaryStage.setTitle("");
	}
}
