import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
public class ItemTrackController {
    private Main main;
    private static Stage primaryStage;
    private String loggedInUser;
    private Button backButton;

    private int itemRecordNum;
    private OrderTrackController orderTrackController;

    private LoginController login;
    private MainMenuController mainMenu;

    public ItemTrackController(Main main, String loggedInUser){
        this.main = main;
        this.loggedInUser = loggedInUser;
        this.primaryStage = main.primaryStage;

    }

    void showItemTrackMenu(String loggedInUser) {
        primaryStage.setTitle("Item Management Services");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");

        Label itemTrackingLabel = new Label("Item Tracking");
        itemTrackingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        itemTrackingLabel.setTextFill(Color.WHITE);

        Button createItemRecordButton = new Button("Create Item Record");
        createItemRecordButton.setOnAction(e -> showCreateItemRecord(loggedInUser));
        Button viewInventoryButton = new Button("View Inventory");
        viewInventoryButton.setOnAction(e -> viewInventoryRecord(loggedInUser));
        Button logoutButton = new Button("Logout");
        LoginController loginController = new LoginController(main);
        logoutButton.setOnAction(e -> loginController.showLoginWindow());

        backButton = new Button("Back");
        MainMenuController mainMenuController = new MainMenuController(main, loggedInUser);
        backButton.setOnAction(e -> mainMenuController.showMainMenuWindow(loggedInUser));

        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        if (loggedInUser.equals("admin")) {
            Button modifyInventoryButton = new Button("Modify Inventory");
            vbox.getChildren().addAll(
                    topBar,
                    itemTrackingLabel,
                    createItemRecordButton,
                    viewInventoryButton,
                    modifyInventoryButton
            );
            main.logActivity("Item tracking opened by: " + loggedInUser);
            Scene scene = new Scene(vbox, 400, 300);
            primaryStage.setScene(scene);
        }

        vbox.getChildren().addAll(
                topBar,
                itemTrackingLabel,
                createItemRecordButton,
                viewInventoryButton
        );

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
    }

    private void showCreateItemRecord(String loggedInUser){
        primaryStage.setTitle("Create Item Record");

        VBox createOrderVBox = new VBox();
        createOrderVBox.setSpacing(10);
        createOrderVBox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");

        TextField itemNameTextField = new TextField();
        TextField itemQuantityTextField = new TextField();
        TextField containerIDTextField = new TextField();
        TextField warehouseLocTextField = new TextField();
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> login.showLoginWindow());

        backButton = new Button("Back");
        backButton.setOnAction(e -> showItemTrackMenu(loggedInUser));

        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        Button createItemRecordButton = new Button("Add Record to Inventory");

        createItemRecordButton.setOnAction(e -> { writeToCSV2(
                loggedInUser,
                itemNameTextField.getText(),
                itemQuantityTextField.getText(),
                containerIDTextField.getText(),
                warehouseLocTextField.getText()

        );


            login.showAlert("Inventory Record Created", "Your record is logged in!");
            showItemTrackMenu(loggedInUser);
            itemRecordNum++;
        });


        createOrderVBox.getChildren().addAll(
                topBar,
                new Label("Item Name:"), itemNameTextField,
                new Label("Item Quantity:"), itemQuantityTextField,
                new Label("Container ID#:"), containerIDTextField,
                new Label("Located At Warehouse:"), warehouseLocTextField,
                createItemRecordButton
        );

        Scene scene = new Scene(createOrderVBox, 500, 500);
        primaryStage.setScene(scene);

        main.logActivity("Item Record #"+itemRecordNum+" recorded by "+loggedInUser);




    }

    private void writeToCSV2(String loggedInUser, String itemName, String itemQuantity, String containerID, String warehouseLocation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("inventory.csv", true))) {
            // Use a timestamp along with the activity message
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String csvLine = String.format("%s,%s,%s,%s,%s,%s%n", timestamp, loggedInUser, itemName, itemQuantity, containerID, warehouseLocation);

            //Sanity check
            // Print the components before writing to CSV
            System.out.println("Writing to CSV with the following components:");
            System.out.println("Timestamp: " + timestamp);
            System.out.println("User: " + loggedInUser);
            System.out.println("Order ID: " + itemName);
            System.out.println("Order Name: " + itemQuantity);
            System.out.println("Order Date: " + containerID);
            System.out.println("Expected Date: " + warehouseLocation);


            // Write the CSV line to the file
            writer.write(csvLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viewInventoryRecord(String loggedInUser) {
        primaryStage.setTitle("View Inventory");

        // Create a TableView
        TableView<String[]> tableView2 = new TableView<>();
        ObservableList<String[]> data = FXCollections.observableArrayList();

        // Read contents of order_records.csv and populate the TableView
        try (BufferedReader reader = new BufferedReader(new FileReader("inventory.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                data.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create TableColumn objects
        String[] columnNames = {"Timestamp", "User", "Item Name", "Item Quantity", "Container ID", "Warehouse Location"};

        for (int i = 0; i < columnNames.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(columnNames[i]);
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex]));
            tableView2.getColumns().add(column);
        }

        tableView2.setItems(data);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");
        backButton = new Button("Back");
        backButton.setOnAction(e -> showItemTrackMenu(loggedInUser));
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> login.showLoginWindow());
        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        vbox.getChildren().addAll(
                topBar,
                tableView2
        );

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
    }
}
