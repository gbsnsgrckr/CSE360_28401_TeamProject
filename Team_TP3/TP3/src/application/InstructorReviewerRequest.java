package application;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

public class InstructorReviewerRequest {

    private final DatabaseHelper databaseHelper;
    // Track processed request IDs without modifying the Request class.
    private final Set<Integer> processedRequestIds = new HashSet<>();

    public InstructorReviewerRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUsername()));

        TableColumn<Request, String> requestCol = new TableColumn<>("Request");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("request"));
        requestCol.setPrefWidth(300);

        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));

        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));
        notesCol.setPrefWidth(200);

        TableColumn<Request, Void> accept = new TableColumn<>("Accept");
        accept.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button acceptButton = new Button("Accept");

            {
                acceptButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    String username = req.getUser().getUsername();
                    try {
                        System.out.println("Accepting request for: " + username);

                        // Update the request status to accepted (assumed to update status to "CLOSED")
                        databaseHelper.updateRequestStatus(username, false, true);

                        // Add the Reviewer role (capitalized to match AdminHomePage)
                        boolean success = databaseHelper.addRoles(username, "Reviewer");
                        System.out.println("Reviewer role added? " + success);

                        // Close the request with an appropriate note; assume this sets status to "CLOSED"
                        databaseHelper.closeRequest(req.getId(), "Instructor accepted reviewer request.");

                        // Refresh the table to show updated requests
                        refreshTable(getTableView());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request req = getTableView().getItems().get(getIndex());
                    // Debug print to check what the status is coming in as:
                    System.out.println("Request ID: " + req.getId() + ", Status: " + req.getStatus());
                    // Show the button only if status is null or equals "OPEN" (ignoring case)
                    if (req.getStatus() == null || req.getStatus().equalsIgnoreCase("OPEN")) {
                        setGraphic(acceptButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });



        TableColumn<Request, Void> declineCol = new TableColumn<>("Decline");
        declineCol.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button declineButton = new Button("Decline");

            {
                declineButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    // Delete the request from the database (or update its status accordingly)
					databaseHelper.deleteRequest(req.getUserName());

					// Refresh the table to show updated requests
					refreshTable(getTableView());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request req = getTableView().getItems().get(getIndex());
                    // Only show the button if status is null or equals "OPEN" (ignoring case)
                    if (req.getStatus() == null || req.getStatus().equalsIgnoreCase("OPEN")) {
                        setGraphic(declineButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });



        tableView.getColumns().addAll(usernames, requestCol, statusCol, notesCol, accept, declineCol);
        refreshTable(tableView);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            primaryStage.close();
            new InstructorHomePage(databaseHelper).show(primaryStage);
        });

        VBox root = new VBox(10, tableView, new HBox(5, backButton));
        primaryStage.setTitle("Reviewer Requests");
        primaryStage.setScene(new Scene(root, 850, 450));
        primaryStage.show();
    }

    private void refreshTable(TableView<Request> tableView) {
        try {
            List<Request> reviewerRequests = databaseHelper.getAllReviewerRequests();
            ObservableList<Request> data = FXCollections.observableArrayList(reviewerRequests);
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
