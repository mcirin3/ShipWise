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
public class OrderTrackController {
    private Main main;
    private static Stage primaryStage;
    private String loggedInUser;
    private Button backButton;

    private int orderNumber;

    private LoginController login;
    private MainMenuController mainMenu;


    public OrderTrackController(Main main, String loggedInUser){
        this.main = main;
        this.loggedInUser = loggedInUser;
        this.primaryStage = main.primaryStage;
    }

    void showOrderTrackMenu(String loggedInUser) {
        primaryStage.setTitle("Order Tracking Services");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");

        Label orderTrackingLabel = new Label("Order Tracking");
        orderTrackingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        orderTrackingLabel.setTextFill(Color.WHITE);

        Button createOrderButton = createStyledButton("Create Order");
        createOrderButton.setOnAction(e -> showCreateOrder(loggedInUser));
        Button viewOrderListButton = createStyledButton("View Orders");
        viewOrderListButton.setOnAction(e -> showViewOrderWindow(loggedInUser));
        Button logoutButton = createStyledButton("Logout");
        logoutButton.setOnAction(e -> login.showLoginWindow());


        backButton = createStyledButton("Back");
        MainMenuController mainMenuController = new MainMenuController(main, loggedInUser);
        backButton.setOnAction(e -> mainMenuController.showMainMenuWindow(loggedInUser));

        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        if (loggedInUser.equals("admin")) {
            Button modifyOrdersButton = new Button("Modify Orders");
            vbox.getChildren().addAll(
                    topBar,
                    orderTrackingLabel,
                    createOrderButton,
                    viewOrderListButton,
                    modifyOrdersButton
            );

            Scene scene = new Scene(vbox, 500, 500);
            primaryStage.setScene(scene);
        }

        vbox.getChildren().addAll(
                topBar,
                orderTrackingLabel,
                createOrderButton,
                viewOrderListButton
        );
        main.logActivity("Order tracking opened by: " + loggedInUser);
        Scene scene = new Scene(vbox, 500, 500);
        primaryStage.setScene(scene);
    }

    private void showCreateOrder(String loggedInUser) {
        primaryStage.setTitle("Create Order");

        VBox createOrderVBox = new VBox();
        createOrderVBox.setSpacing(10);
        createOrderVBox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");

        TextField orderIDTextField = new TextField();
        TextField orderNameTextField = new TextField();
        TextField orderDateTextField = new TextField();
        TextField orderExpectedDateTextField = new TextField();
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> login.showLoginWindow());

        backButton = new Button("Back");
        backButton.setOnAction(e -> showOrderTrackMenu(loggedInUser));

        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        Button createOrderRecordButton = new Button("Create Order Record");

        createOrderRecordButton.setOnAction(e -> {writeToCSV(
                loggedInUser,
                orderIDTextField.getText(),
                orderNameTextField.getText(),
                orderDateTextField.getText(),
                orderExpectedDateTextField.getText());

            login.showAlert("Order Successfully Created", "Your record is logged in!");
            showOrderTrackMenu(loggedInUser);
            orderNumber++;
        });


        createOrderVBox.getChildren().addAll(
                topBar,
                new Label("Order ID#:"), orderIDTextField,
                new Label("Order to:"), orderNameTextField,
                new Label("Date Order Placed:"), orderDateTextField,
                new Label("Order Date Arrival:"), orderExpectedDateTextField,
                createOrderRecordButton
        );

        Scene scene = new Scene(createOrderVBox, 500, 500);
        primaryStage.setScene(scene);

        main.logActivity("Order #"+orderIDTextField.getText()+" recorded by "+loggedInUser);

    }

    private void handleCreateOrderButton(String loggedInUser, String orderID, String orderName, String orderDate, String expectedDate){
        login.showAlert("Order Created", "The Order has been Successfully Created!");
        showOrderTrackMenu(loggedInUser);
        main.logActivity("Order Record Created by: " + loggedInUser);

        writeToCSV(loggedInUser, orderID, orderName, orderDate, expectedDate);


    }

    private void writeToCSV(String loggedInUser, String orderID, String orderName, String orderDate, String expectedDate) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("order_records.csv", true))) {
            // Use a timestamp along with the activity message
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String csvLine = String.format("%s,%s,%s,%s,%s,%s%n", timestamp, loggedInUser, orderID, orderName, orderDate, expectedDate);

            //Sanity check
            // Print the components before writing to CSV
            System.out.println("Writing to CSV with the following components:");
            System.out.println("Timestamp: " + timestamp);
            System.out.println("User: " + loggedInUser);
            System.out.println("Order ID: " + orderID);
            System.out.println("Order Name: " + orderName);
            System.out.println("Order Date: " + orderDate);
            System.out.println("Expected Date: " + expectedDate);


            // Write the CSV line to the file
            writer.write(csvLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showViewOrderWindow(String loggedInUser) {
        primaryStage.setTitle("View Orders");

        // Create a TableView
        TableView<String[]> tableView = new TableView<>();
        ObservableList<String[]> data = FXCollections.observableArrayList();

        // Read contents of order_records.csv and populate the TableView
        try (BufferedReader reader = new BufferedReader(new FileReader("order_records.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                data.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create TableColumn objects
        String[] columnNames = {"Timestamp", "User", "Order ID", "Order Name", "Order Date", "Expected Date"};

        for (int i = 0; i < columnNames.length; i++) {
            final int columnIndex = i;
            TableColumn<String[], String> column = new TableColumn<>(columnNames[i]);
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex]));
            tableView.getColumns().add(column);
        }

        tableView.setItems(data);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");
        backButton = new Button("Back");
        backButton.setOnAction(e -> showOrderTrackMenu(loggedInUser));
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> login.showLoginWindow());
        HBox topBar = new HBox(backButton, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        vbox.getChildren().addAll(
                topBar,
                tableView
        );

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-radius: 10;");
        return button;
    }

}
