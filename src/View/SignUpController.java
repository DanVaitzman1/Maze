package View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;

public class SignUpController {
    @FXML
    public TextField usernameSignUpField;
    @FXML
    public PasswordField passwordSignUpField;
    @FXML
    public Button signUpButtonSignUpView;
    @FXML
    private void handleSignUpSignUpView() throws Exception
    {
        String username = usernameSignUpField.getText();
        String password = passwordSignUpField.getText();
        if (username.length() < 2 )
        {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username must be at least two characters long.");
            return;
        }
        if (password.length() < 5)
        {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "The length of the password must be at least five characters.");
            return;
        }
        if (checkUserName(username)) {
            showAlert(Alert.AlertType.ERROR, "Sign Up Failed", "Username and password already exist.");
        }
        else {
            addUser(username, password);
            showAlert(Alert.AlertType.INFORMATION, "Sign Up Successful", "You have successfully signed up.");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("LogInView.fxml").openStream());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Log In");
            Image icon = new Image(new FileInputStream("resources/MazeImages/Icon.jpg"));
            stage.getIcons().add(icon);
            stage.setScene(scene);
            stage.show();
            Stage previousStage = (Stage) signUpButtonSignUpView.getScene().getWindow();
            previousStage.close();
        }
    }
    private boolean checkUserName(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/Users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String storedUsername = line.substring(line.indexOf(":") + 1).trim();
                if (storedUsername.equals(username) ) {return true;}
            }
        } catch (IOException e) {e.printStackTrace();}
        return false;
    }
    private void addUser(String username, String password) {
        System.out.println("user name : " + username.length() + "password : " + password.length());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/Users.txt", true))) {
            writer.newLine();
            writer.write("UserName: " + username);
            writer.newLine();
            writer.write("Password: " + password);
        } catch (IOException e) {
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


