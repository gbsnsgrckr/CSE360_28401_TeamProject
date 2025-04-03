package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The {@code InstructorRequest} class is responsible for displaying all user-submitted
 * requests to the instructor in a tabular format using JavaFX. This includes the username,
 * request message, current status, and optional notes.
 * <p>
 * The instructor can view this information and navigate back to the instructor's home page.
 *
 * This class depends on {@code DatabaseHelper} to retrieve request data from the database.
 * 
 * @author CSE 360 Team 8 
 */
public class InstructorRequest {

    private final DatabaseHelper databaseHelper;

    /**
     * Constructs an InstructorRequest with a given {@code DatabaseHelper} instance.
     *
     * @param databaseHelper the database helper used to fetch requests
     */
    public InstructorRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the JavaFX window showing all requests in a {@code TableView}.
     * Each row displays the user's name, request content, status, and notes.
     * The instructor can click "Back" to return to the home page.
     *
     * @param primaryStage the primary JavaFX stage to show the request window
     */
    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        // Username column
        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUsername()));

        // Request column
        TableColumn<Request, String> requestCol = new TableColumn<>("Request");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("request"));
        requestCol.setPrefWidth(350);

        // Status column
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        // Notes column
        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));
        notesCol.setPrefWidth(200);

        tableView.getColumns().addAll(usernames, requestCol, statusCol, notesCol);

        ObservableList<Request> data = FXCollections.observableArrayList();

        try {
            List<Request> userRequests = databaseHelper.getAllRequests();
            data.addAll(userRequests);
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging in production
        }

        tableView.setItems(data);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        backButton.setOnAction(a -> {
            primaryStage.close();
            new InstructorHomePage(databaseHelper).show(primaryStage);
        });

        HBox hbox = new HBox(5, backButton);
        VBox root = new VBox(tableView, hbox);
        Scene scene = new Scene(root, 700, 400);
        primaryStage.setTitle("My Requests");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
