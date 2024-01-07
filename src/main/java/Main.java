import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application{
    Stage primaryStage;
    private LoginController loginController;
    private MainMenuController mainMenu;

    public static void main(String[] args){
        launch(args);

    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        loginController = new LoginController(this);
       loginController.showLoginWindow();
    }

    public void showMainMenuWindow(String loggedInUser){
        MainMenuController mainMenuController = new MainMenuController(this,loggedInUser);
        mainMenuController.showMainMenuWindow(loggedInUser);
    }

    //For logging activity
    void logActivity(String activity) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("activity_log.txt", true))) {
            // Use a timestamp along with the activity message
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String logMessage = "[" + timestamp + "] " + activity;

            // Log the activity to a file
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







}