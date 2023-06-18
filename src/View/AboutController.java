package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class AboutController  extends Observable implements Initializable {
    @FXML
    private Label aboutUsLabel;
    @FXML
    private Button CloseButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        aboutUsLabel.setWrapText(true);
        aboutUsLabel.setText("""
                A game called a maze, the goal of which is to get from the starting position to the goal position.
                The maze is built using a Prim based algorithm.
                It is solved using one of three search algorithms:
                BFS, DFS and Best FS.
                This game is brought to you by Dan Vaitzman and Yuval Schwarts.""");
    }
    @FXML
    public void closeAbout()
    {
        Stage s = (Stage) CloseButton.getScene().getWindow();
        s.close();
    }
}
