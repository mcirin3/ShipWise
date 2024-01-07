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
@SuppressWarnings("ALL")
public class ActivityLogController {

    private Stage primaryStage;
    private Main main;
    private String loggedInUser;

    private Button backButton;
    public ActivityLogController(Main main, String loggedInUser){
        this.main = main;
        this.loggedInUser = loggedInUser;
        this.primaryStage = main.primaryStage;

    }

    void showActivityLog(String loggedInUser) {
        primaryStage.setTitle("Activity Log");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setStyle("-fx-background-color: lightcoral; -fx-padding: 10px;");

        Label activityLogLabel = new Label("Activity Log");
        activityLogLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        activityLogLabel.setTextFill(Color.WHITE);

        TextArea logTextArea = new TextArea();
        logTextArea.setEditable(false);

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

        // Read contents of activity_log.txt and set it to logTextArea
        try (BufferedReader reader = new BufferedReader(new FileReader("activity_log.txt"))) {
            StringBuilder logContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }

            logTextArea.setText(logContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        vbox.getChildren().addAll(
                topBar,
                activityLogLabel,
                logTextArea
        );

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);



        // Log initial activity or load previous logs...
        main.logActivity("Activity Log opened by: " + loggedInUser);
    }
}
