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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainMenuController {
    private Main main;
    private Stage primaryStage;
    private String loggedInUser;

    private Set<String> generatedStrings = new HashSet<>();

    private Button backButton;


    public MainMenuController(Main main,String loggedInUser){
        this.main = main;
        this.primaryStage = main.primaryStage;
        this.loggedInUser = loggedInUser;
    }

    public void showMainMenuWindow(String loggedInUser) {
        primaryStage.setTitle("Main Menu");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: #f1efe7; -fx-padding: 10px;");
        vbox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome to the Logistics App!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        welcomeLabel.setTextFill(Color.WHITE);

        Label loggedInLabel = new Label("Logged in as: " + loggedInUser);
        loggedInLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        loggedInLabel.setTextFill(Color.WHITE);

        Button orderTrackingButton = createStyledButton("Order Tracking");
        Button itemTrackingButton = createStyledButton("Item Tracking");
        Button activityLogButton = createStyledButton("Activity Log");
        Button generateTrackingIDButton = createStyledButton("Generate New Tracking ID");

        TextField generatedIDTextField = new TextField();

        generateTrackingIDButton.setOnAction(e -> {
            String generatedString = generateRandomString();
            generatedIDTextField.setText(generatedString);
        });

        OrderTrackController orderTrackController1 = new OrderTrackController(main, loggedInUser);
        ItemTrackController itemTrackController1 = new ItemTrackController(main, loggedInUser);
        ActivityLogController activityLogController1 = new ActivityLogController(main, loggedInUser);

        orderTrackingButton.setOnAction(e -> orderTrackController1.showOrderTrackMenu(loggedInUser));
        itemTrackingButton.setOnAction(e -> itemTrackController1.showItemTrackMenu(loggedInUser));
        activityLogButton.setOnAction(e -> activityLogController1.showActivityLog(loggedInUser));

        Button logoutButton = createStyledButton("Logout");
        LoginController loginController1 = new LoginController(main);
        logoutButton.setOnAction(e -> loginController1.showLoginWindow());

        backButton = createStyledButton("Back");
        backButton.setDisable(true);  // Initially disable the back button

        HBox generateStringBox = new HBox(generateTrackingIDButton, generatedIDTextField);
        generateStringBox.setAlignment(Pos.BOTTOM_LEFT);
        generateStringBox.setSpacing(10);
        HBox topBar = new HBox(backButton, loggedInLabel, logoutButton);
        topBar.setAlignment(Pos.BOTTOM_LEFT);
        topBar.setSpacing(10);
        topBar.setPadding(new Insets(5));

        vbox.getChildren().addAll(
                topBar,
                welcomeLabel,
                generateStringBox,
                orderTrackingButton,
                itemTrackingButton,
                activityLogButton
        );

        Scene scene = new Scene(vbox, 500, 500);
        primaryStage.setScene(scene);
    }

    private String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String generatedString;

        do {
            // Generate a random string of 10 characters
            generatedString = random.ints(10, 0, characters.length())
                    .mapToObj(characters::charAt)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
        } while (generatedStrings.contains(generatedString));

        // Store the generated string in the set and file
        generatedStrings.add(generatedString);
        storeGeneratedStringToFile(generatedString);

        return generatedString;
    }

    private void storeGeneratedStringToFile(String generatedString) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("generatedStrings.txt", true))) {
            writer.write(generatedString);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-radius: 10;");
        return button;
    }
}
