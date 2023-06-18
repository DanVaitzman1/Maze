package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class HelpController extends Observable implements Initializable {
    @FXML
    private Label helpLabel;
    @FXML
    private Button ClosebuttonHelp;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        helpLabel.setWrapText(true);
        helpLabel.setText("""
                How to play :
                If you want to take a step up you have two options : 8/UP
                If you want to take a step down you have two options : 2/DOWN
                If you want to take a step left you have two options : 4/LEFT
                If you want to take a step right you have two options : 6/RIGHT
                If you want to make a diagonal step to the right up : 9
                If you want to make a diagonal step to the left up : 7
                If you want to make a diagonal step to the right down : 3
                If you want to make a diagonal step to the left down : 1
                
                Markings on the game board:
                Maor Melikson picture - this is the start Positon.
                Seck picture - this is the wall .
                Football gate - this is goal Position.
                Ball picture - this is the player Position.
                Another Ball (different) - is a hint or solution .
                
                Your goal :
                Your goal is to get from the start point to the end point in the maze while avoiding reaching a dead end.
                If you have reached a dead end you can go back and try again.
                
                Get Help :
                If you can't reach the finish line, you can always press the "Solve" button, which will show you the solution to the maze.
                If you are stuck on a certain bank but don't want to see the whole solution, you can click on the "Hint" button that will guide you in your next step.
                
                The menu bar :
                Our menu includes 5 options:
                1) File - include the follow buttons - New Maze,Save Maze,Load Maze,Hint.
                2) Options - include Properties - you can change the properties og the game.
                3) Help - this window.
                4) About - open new window that explain about us and about algorithms we used.
                5) Exit - inclide exit cloase all the program. 
                
                Buttons : 
                1) Create - generate maze with the rows number and columns number that enter above .
                2) Load - you can load maze file .
                3) Save - you can save yor maze .
                4) Hint - give you a hint .
                5) Solve - show you the solve of the maze .
                
                Change the ball:
                At each stage of the game you can choose one of the balls that appear next to the game board.
                """);
    }
    @FXML
    public void closeHelp()
    {
        Stage s = (Stage) ClosebuttonHelp.getScene().getWindow();
        s.close();
    }
}
