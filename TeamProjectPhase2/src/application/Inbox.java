package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.Priority;

/** 
 * The inbox class shows all user generated message that are related to the
 * target question.
 */

public class Inbox {
    private final DatabaseHelper databaseHelper;

    public Inbox(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        TableView<Message> table = new TableView<>();
        List<Message> messageList = new ArrayList<>();

        // Label to display title to user
        
        try {
            messageList = databaseHelper.qaHelper.retrieveMessagesByUserId(databaseHelper.currentUser.getUserId());
        } catch (SQLException e) {
            System.out.println("Error fetching messages from the database.");
            e.printStackTrace();
        }

        // TableColumn for Message ID
        TableColumn<Message, Integer> messageIdColumn = new TableColumn<>("MsgID");
        messageIdColumn.setCellValueFactory(new PropertyValueFactory<>("messageID"));
        messageIdColumn.setPrefWidth(50);

        // TableColumn for Sender
        TableColumn<Message, String> senderColumn = new TableColumn<>("From");
        senderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getSender() != null ? cellData.getValue().getSender().getUsername() : "Unknown"
        ));

        // TableColumn for Recipient
        TableColumn<Message, String> recipientColumn = new TableColumn<>("To");
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getRecipient() != null ? cellData.getValue().getRecipient().getUsername() : "Unknown"
        ));
        recipientColumn.setVisible(false); // Hide column

        // TableColumn for Subject
        TableColumn<Message, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectColumn.setPrefWidth(175);

        // TableColumn for Message Content
        TableColumn<Message, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(618);

        // Fixed row height
        table.setFixedCellSize(70);  

        // Add columns to the table
        table.getColumns().addAll(messageIdColumn, senderColumn, recipientColumn, subjectColumn, messageColumn);

        ObservableList<Message> messageObservableList = FXCollections.observableArrayList(messageList);
        table.setItems(messageObservableList);

        // Buttons
        Button readButton = new Button("Read");
        readButton.setStyle("-fx-text-fill: white; -fx-background-color: green; -fx-font-weight: bold;");
        
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold;");
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");

        // Disable buttons by default until a message is selected
        readButton.setDisable(true);
        replyButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable Buttons on Selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            readButton.setDisable(!hasSelection);
            replyButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        // Read Button Action
        readButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage detailsStage = new Stage();
                new ReadMessagePage(databaseHelper, selectedMessage).show(detailsStage);
            }
        });

        // Reply Button Action
        replyButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage replyStage = new Stage();
                new CreateMessagePage(databaseHelper, selectedMessage.getSenderID()).show(replyStage);
            }
        });

        // Delete Button Action
        deleteButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Are you sure you want to delete this message?");
                alert.setContentText("Message ID: " + selectedMessage.getMessageID());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        if (databaseHelper.qaHelper.deleteMessage(selectedMessage.getMessageID())) {
                            table.getItems().remove(selectedMessage);
                            System.out.println("Message deleted: " + selectedMessage.getMessageID());
                        } else {
                            System.out.println("Failed to delete message.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Layout
        HBox buttonBox = new HBox(10, readButton, replyButton, deleteButton);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox vbox = new VBox(10, table, buttonBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(vbox, 940, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inbox");
        primaryStage.show();
    }
}
