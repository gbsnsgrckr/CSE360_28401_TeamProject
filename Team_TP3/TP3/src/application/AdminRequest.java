package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AdminRequest {
    private final DatabaseHelper databaseHelper;

    public AdminRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        // Existing columns for "Username" and "Request"
        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getUserName()));

        TableColumn<Request, String> request = new TableColumn<>("Request");
        request.setCellValueFactory(new PropertyValueFactory<>("request"));
        request.setPrefWidth(300);

        // NEW: Show the status
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));

        // NEW: Show the notes (semicolon-delimited)
        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setPrefWidth(200);
        notesCol.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));

        // NEW: Close request
        TableColumn<Request, Void> closeCol = new TableColumn<>("Close");
        closeCol.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button closeButton = new Button("Close Request");
            {
                closeButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog("Add final admin notes...");
                    dialog.setTitle("Close Request");
                    dialog.setHeaderText("Optionally add notes before closing");
                    dialog.setContentText("Notes:");
                    dialog.showAndWait().ifPresent(note -> {
                        try {
                            databaseHelper.closeRequest(req.getId(), note);
                            refreshTableData(tableView);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request r = getTableView().getItems().get(getIndex());
                    // Show the button only if it's open or reopened
                    if ("OPEN".equalsIgnoreCase(r.getStatus()) || "REOPENED".equalsIgnoreCase(r.getStatus())) {
                        setGraphic(closeButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tableView.getColumns().addAll(usernames, request, statusCol, notesCol, closeCol);
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Question", "Answer", "Review");
        typeComboBox.setPromptText("Select Type");

        TextField idInputField = new TextField();
        idInputField.setPromptText("Enter ID to delete");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            String type = typeComboBox.getValue();
            String idText = idInputField.getText();

            if (type == null || idText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a type and enter a valid ID.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid ID", "ID must be an integer.");
                return;
            }

            boolean result = false;
            switch (type.toLowerCase()) {
                case "question":
                    result = databaseHelper.qaHelper.deleteQuestion(id);
                    break;
                case "answer":
                    result = databaseHelper.qaHelper.deleteAnswer(id);
                    break;
                case "review":
                    result = databaseHelper.qaHelper.deleteReview(id);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Invalid Type", "Unrecognized type selection.");
                    return;
            }

            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Success", type + " with ID " + id + " was deleted successfully.");
                refreshTableData(tableView);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to delete the specified item.");
            }
        });
        
        HBox deleteBox = new HBox(10, typeComboBox, idInputField, deleteButton);
        

        ObservableList<Request> data = FXCollections.observableArrayList();
        refreshTableData(tableView);

        // “Back” button to return to AdminHomePage
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            primaryStage.close();
            new AdminHomePage(databaseHelper).show(primaryStage, databaseHelper.currentUser);
        });

        HBox hbox = new HBox(5, backButton);
        VBox root = new VBox(tableView, deleteBox, hbox);
        Scene scene = new Scene(root, 900, 400);
        primaryStage.setTitle("Admin Requests");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTableData(TableView<Request> tableView) {
        try {
            List<Request> userRequests = databaseHelper.getAllRequests();
            tableView.setItems(FXCollections.observableArrayList(userRequests));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
