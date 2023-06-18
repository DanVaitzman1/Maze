package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
public class LogInViewController {
    @FXML
    private Button loginButton,signUpButtonLogIn;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordFieldPass;
    MyModel model = new MyModel();
    MyViewModel viewModel = new MyViewModel(model);
    @FXML
    private void handleLogIn() throws Exception
    {
        String userName = usernameField.getText(),password = passwordFieldPass.getText();
        if (!isValidLogIn(userName,password)) {logInPopUpAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");}
        else
        {
            model.addObserver(viewModel);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
            MyViewController view = fxmlLoader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Maze - The Game ");
            Image icon = new Image(new FileInputStream("resources/MazeImages/Icon.jpg"));
            stage.getIcons().add(icon);
            view.setResizeEvent(scene);
            view.setViewModel(viewModel);
            viewModel.addObserver(view);
            CheckingOutOfTheGame(stage);
            stage.show();
            Stage previousStage = (Stage) loginButton.getScene().getWindow();
            previousStage.close();
        }
    }
    private void CheckingOutOfTheGame(Stage primaryStage) {
        primaryStage.setOnCloseRequest(windowEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Are you sure you want to quit the game?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {primaryStage.close();Platform.exit();}
            else {windowEvent.consume();}
        });
    }
    @FXML
    public void signUpInLogInAction() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("signUpView.fxml").openStream());
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sign Up");
        Image icon = new Image(new FileInputStream("resources/MazeImages/Icon.jpg"));
        stage.getIcons().add(icon);
        stage.show();
        Stage previousStage = (Stage) loginButton.getScene().getWindow();
        previousStage.close();
        stage.setOnCloseRequest(event -> {
            try
            {
                FXMLLoader fxmlLoader1 = new FXMLLoader();
                Parent root1 = fxmlLoader1.load(getClass().getResource("LogInView.fxml").openStream());
                Scene scene1 = new Scene(root1);
                Stage stage1 = new Stage();
                stage1.setScene(scene1);
                stage1.setTitle("Log In");
                stage1.getIcons().add(icon);
                stage1.setScene(scene1);
                stage1.show();
            }
            catch (IOException e) {e.printStackTrace();}

        });
    }

    public void logInPopUpAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public boolean isValidLogIn(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Users.txt"))) {
            String storedUsername = null, storedPassword = null,line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("UserName:")) {storedUsername = extractValue(line);}
                else if (line.startsWith("Password:")) {storedPassword = extractValue(line);}
                if (storedUsername != null && storedPassword != null) {
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {return true;}
                    storedUsername = null;storedPassword = null;}
            }
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }
    public String extractValue(String line) {
        int index = line.indexOf(":");
        if (index != -1 && index + 1 < line.length()) {return line.substring(index + 1).trim();}return null;
    }
}
