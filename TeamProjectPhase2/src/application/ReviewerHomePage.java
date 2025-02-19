package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import databasePart1.DatabaseHelper;

/**
 * ReviewerHomePage class represents the user interface for the reviewer user.
 * This page displays a simple welcome message for the reviewer.
 */
public class ReviewerHomePage {
	/**
	 * Displays the reviewer page in the provided primary stage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	private final DatabaseHelper databaseHelper;

	public ReviewerHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage) {
		VBox layout = new VBox(40);
		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

		// Label to display the welcome message for the reviewer
		Label reviewerLabel = new Label("Hello, Reviewer!");
		reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Button to return to the login screen
		Button quitButton = new Button("Back to login");

		quitButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(newStage);
		});

		layout.getChildren().addAll(reviewerLabel, quitButton);
		Scene reviewerScene = new Scene(layout, 940, 400);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		// Set the scene to primary stage
		primaryStage.setScene(reviewerScene);
		primaryStage.setTitle("");
		primaryStage.show();
	}
}
