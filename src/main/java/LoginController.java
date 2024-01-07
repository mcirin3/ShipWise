import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class LoginController{
    private Main main;
    private Stage primaryStage;

    public LoginController(Main main){
        this.main = main;
        this.primaryStage = main.primaryStage;
    }

    void showLoginWindow() {
        primaryStage.setTitle("Login");

        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-background-color: #f1efe7; -fx-padding: 10px;");

        // Load your logo from the resources
        ImageView logoImageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/shipwise2.png"))));
        logoImageView.setPreserveRatio(true);
        logoImageView.fitWidthProperty().bind(stackPane.widthProperty().multiply(0.8)); // Adjust the scaling factor as needed

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Hyperlink createAccountLink = new Hyperlink("Create Account");

        // Apply styles to the button
        loginButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-radius: 10;");

        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));
        createAccountLink.setOnAction(e -> showCreateAccountWindow());

        // Set alignments
        StackPane.setAlignment(logoImageView, Pos.TOP_CENTER);

        VBox loginBox = new VBox(10, // spacing
                new Label("Username:"),
                usernameField,
                new Label("Password:"),
                passwordField,
                loginButton,
                createAccountLink);
        loginBox.setAlignment(Pos.BOTTOM_LEFT);

        stackPane.getChildren().addAll(
                logoImageView,
                loginBox
        );

        Scene scene = new Scene(stackPane, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    void handleLogin(String username, String password) {
        // Implement login logic
        if (validateLogin(username, password)) {
            // After successful login, you can open the main menu or another window
            main.showMainMenuWindow(username);
        } else {
            showCautionAlert("Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private boolean validateLogin(String username, String password) {
        try (InputStream inputStream = getClass().getResourceAsStream("/authority.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void showCreateAccountWindow() {
        primaryStage.setTitle("Create Account");

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        TextField newUsernameField = new TextField();
        PasswordField newPasswordField = new PasswordField();
        Button createAccountButton = new Button("Create Account");

        createAccountButton.setOnAction(e -> handleCreateAccount(newUsernameField.getText(), newPasswordField.getText()));

        vbox.getChildren().addAll(
                new Label("New Username:"),
                newUsernameField,
                new Label("New Password:"),
                newPasswordField,
                createAccountButton
        );

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
    }

    private void handleCreateAccount(String newUsername, String newPassword) {
        // Implement account creation logic
        try {
            // Create a temporary file for writing
            File tempFile = File.createTempFile("authority_temp", ".txt");
            tempFile.deleteOnExit();

            // Read the existing content from the resource file
            InputStream inputStream = getClass().getResourceAsStream("/authority.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true))) {

                // Copy existing content to the temporary file
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }

                // Append the new account information
                writer.write(newUsername + "," + newPassword);

                showAlert("Account Created", "Account created successfully!");
                showLoginWindow();

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Replace the original file with the temporary file
            File originalFile = new File(Objects.requireNonNull(getClass().getResource("/authority.txt")).toURI());
            tempFile.renameTo(originalFile);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showCautionAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}