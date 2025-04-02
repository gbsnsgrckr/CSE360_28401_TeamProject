package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * AdminRequest class represents the user interface for handling admin requests.
 * It displays a table view of requests with options to accept or decline each request.
 */
public class AdminRequest {
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs an AdminRequest with the specified DatabaseHelper.
     *
     * @param databaseHelper The DatabaseHelper used to interact with the database.
     */
    public AdminRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the Admin Request window in the provided stage.
     *
     * @param primaryStage The stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUsername()));

        TableColumn<Request, String> request = new TableColumn<>("Request");
        request.setCellValueFactory(new PropertyValueFactory<>("request"));
        request.setPrefWidth(350); // Set preferred width to 350 pixels
        request.setMinWidth(200);  // Set a minimum width to prevent shrinking too much
        request.setMaxWidth(350);  // Set a maximum width to prevent expansion

        request.setCellFactory(tc -> new TableCell<Request, String>() {
            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(request.widthProperty()); // Wrap text to column width
                setGraphic(text);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    text.setText(null);
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                }
            }
        });

        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));

        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setPrefWidth(200);
        notesCol.setCellValueFactory(cellData -> 
            new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));

        TableColumn<Request, Void> accept = new TableColumn<>("Accept");

        accept.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button acceptButton = new Button("Accept");

            {
                acceptButton.setStyle(
                        "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1; "
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
                
                acceptButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    System.out.println("Accepted: " + request.getUserName());
                    try {
                        databaseHelper.addRoles(request.getUserName(), "Reviewer");
                        databaseHelper.deleteRequest(request.getUserName());
                        ObservableList<Request> updatedRequests = FXCollections.observableArrayList(databaseHelper.getAllRequests());
                        tableView.setItems(updatedRequests); 
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(acceptButton);
                }
            }
        });

        TableColumn<Request, Void> decline = new TableColumn<>("Decline");
        
        decline.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button declineButton = new Button("Decline");

            {
                declineButton.setStyle(
                        "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1; "
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
                
                declineButton.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    System.out.println("Accepted: " + request.getUserName());
                    try {
                        databaseHelper.deleteRequest(request.getUserName());
                        ObservableList<Request> updatedRequests = FXCollections.observableArrayList(databaseHelper.getAllRequests());
                        tableView.setItems(updatedRequests); 
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(declineButton);
                }
            }
        });

        TableColumn<Request, Void> closeCol = new TableColumn<>("Close");
        closeCol.setCellFactory(tc -> new TableCell<>() {
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
                    if ("OPEN".equalsIgnoreCase(r.getStatus()) || "REOPENED".equalsIgnoreCase(r.getStatus())) {
                        setGraphic(closeButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tableView.getColumns().addAll(usernames, request, statusCol, notesCol, accept, decline, closeCol);

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
            boolean result = switch (type.toLowerCase()) {
                case "question" -> databaseHelper.qaHelper.deleteQuestion(id);
                case "answer" -> databaseHelper.qaHelper.deleteAnswer(id);
                case "review" -> databaseHelper.qaHelper.deleteReview(id);
                default -> false;
            };
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Success", type + " with ID " + id + " was deleted successfully.");
                refreshTableData(tableView);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to delete the specified item.");
            }
        });

        HBox deleteBox = new HBox(10, typeComboBox, idInputField, deleteButton);

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; "
                + "-fx-border-width: 2, 1; -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        backButton.setOnAction(a -> {
            primaryStage.close();
            new AdminHomePage(databaseHelper).show(primaryStage, databaseHelper.currentUser);
        });

        HBox backBox = new HBox(5, backButton);

        refreshTableData(tableView);

        VBox root = new VBox(tableView, deleteBox, backBox);
        Scene scene = new Scene(root, 900, 500);
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

