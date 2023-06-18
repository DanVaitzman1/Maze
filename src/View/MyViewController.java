package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.Image;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class MyViewController implements IView, Observer {
    private MyViewModel viewModel = new MyViewModel(new MyModel());
    public MazeDisplayer mazeDisplayer = new MazeDisplayer();
    boolean showOnce = false,mazeAlreadyCreated = false;
    @FXML
    public TextField rowsNumberField,colsNumberField;
    @FXML
    public Label rowsNumberLabel, colsNumberLabel,myCurrRow,myCurrCol;
    @FXML
    public Button createMazeButton, solveMazeButton,loadMazeButton,saveMazeButton,hintButton;
    public StringProperty characterPositionRow = new SimpleStringProperty(),characterPositionColumn = new SimpleStringProperty(),selectedOptionGeneration = new SimpleStringProperty();
    @FXML
    public  ImageView imageView1,imageView2,imageView3;
    @FXML
    public VBox playersVbox;
    public MediaPlayer inGameMediaPlayer,inSolutionMediaPlayer;
    public Media mediaInGame,mediaInSolution;
    public void initialize()
    {
        String filePathInGame = "resources/Music/gamesong.mp3";
        mediaInGame = new Media(new File(filePathInGame).toURI().toString());
        inGameMediaPlayer = new MediaPlayer(mediaInGame);
        inGameMediaPlayer.setOnEndOfMedia(() -> {
            inGameMediaPlayer.seek(inGameMediaPlayer.getStartTime());
        });
        String filePathInSolution = "resources/Music/afterGoal.mp3";
        mediaInSolution = new Media(new File(filePathInSolution).toURI().toString());
        inSolutionMediaPlayer = new MediaPlayer(mediaInSolution);
        try {imageView1.setImage(new Image(new FileInputStream("resources/MazeImages/ballImage1.png")));
            imageView2.setImage(new Image(new FileInputStream("resources/MazeImages/ballImage2.png")));
            imageView3.setImage(new Image(new FileInputStream("resources/MazeImages/ballImage3.png")));}
        catch (FileNotFoundException e) {throw new RuntimeException(e);}
        mazeDisplayer.playerImage=imageView1.getImage();
        RadioButton radioButton1 = new RadioButton("Ball 1");
        playersVbox.setMargin(radioButton1,new Insets(-185,0,0,5));
        playersVbox.getChildren().addAll(radioButton1);
        RadioButton radioButton2 = new RadioButton("Ball 2");
        playersVbox.setMargin(radioButton2, new Insets(50, 0, 0, 5));
        playersVbox.getChildren().addAll( radioButton2);
        RadioButton radioButton3 = new RadioButton("Ball 3");
        playersVbox.setMargin(radioButton3, new Insets(50, 0, 0, 5));
        playersVbox.getChildren().addAll(radioButton3);
        radioButton1.setOnAction(event -> handleCheckBoxSelectionGeneration(radioButton1));
        radioButton2.setOnAction(event -> handleCheckBoxSelectionGeneration(radioButton2));
        radioButton3.setOnAction(event -> handleCheckBoxSelectionGeneration(radioButton3));
        radioButton1.setSelected(true);
    }
    private void handleCheckBoxSelectionGeneration(RadioButton radioButtonSelected) {
        if (radioButtonSelected.isSelected()) {
            String selectedOptionText = radioButtonSelected.getText();
            selectedOptionGeneration.set(selectedOptionText);
            playersVbox.getChildren().stream()
                    .filter(node -> node instanceof RadioButton && node != radioButtonSelected)
                    .map(node -> (RadioButton) node)
                    .forEach(RadioButton -> RadioButton.setSelected(false));
                if (selectedOptionText.equals("Ball 1")) {mazeDisplayer.playerImage = imageView1.getImage();this.mazeDisplayer.requestFocus();}
                if (selectedOptionText.equals("Ball 2")) {mazeDisplayer.playerImage = imageView2.getImage();this.mazeDisplayer.requestFocus();}
                if (selectedOptionText.equals("Ball 3")) {mazeDisplayer.playerImage = imageView3.getImage();this.mazeDisplayer.requestFocus();}
            }
        else {selectedOptionGeneration.set("Player 1");mazeDisplayer.playerImage = imageView1.getImage();}}
    public void setViewModel(MyViewModel M) {this.viewModel=M;bindProperties(viewModel);}
    private void bindProperties(MyViewModel viewModel) {myCurrRow.textProperty().bind(viewModel.row);myCurrCol.textProperty().bind(viewModel.col);}
    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            mazeDisplayer.goalPosition(viewModel.getEndPosition());
            mazeDisplayer.setMaze(viewModel.getMaze());
            mazeDisplayer.setCharacterPosition(viewModel.getPositionRow(), viewModel.getPositionCol());
            mazeDisplayer.goalPosition(viewModel.getEndPosition());
            displayMaze(viewModel.getMaze());
            createMazeButton.setDisable(false);
            if (viewModel.gameIsOver() && !showOnce) {
                if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}
                inSolutionMediaPlayer.play();
                showAlertDialog("Gooooooooooooooooooooooal !", "You managed to help Maor Malikson score! What a goall!","resources/MazeImages/finishImage.jpg");
                showOnce=true;}
            mazeDisplayer.drawMaze();}
    }
    @Override
    public void displayMaze(int[][] maze)
    {
        int characterPositionRow = viewModel.getPositionRow();
        int characterPositionColumn = viewModel.getPositionCol();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        mazeDisplayer.goalPosition(viewModel.getEndPosition());
        mazeDisplayer.Solved(viewModel.getSolutionArray());
        // The line below checks: if we are on the goal position - if we are thaen we finish the maze !
        mazeDisplayer.isSolved(viewModel.isSolved());
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        if (viewModel.isSolved()) mazeDisplayer.drawMaze();
    }
    @FXML
    public void createMazeAction ()  {
        mazeDisplayer.zoomFactor = 1.0;
        setCreatedOrNot(true);
        mazeDisplayer.isSolved(false);
        showOnce = false;
        int height = parseIntegerOrDefault(rowsNumberField.getText());
        int width = parseIntegerOrDefault(colsNumberField.getText());
        if (height < 2 || width < 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter positive integer values for rows and columns.");
            alert.showAndWait();return;}
        if (inSolutionMediaPlayer!=null && inSolutionMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inSolutionMediaPlayer.stop();}
        if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}
        inGameMediaPlayer.play();
        int[][] tempMaze = viewModel.generateMaze(height,width);
        mazeDisplayer.goalPosition(viewModel.getEndPosition());
        mazeDisplayer.setMaze(tempMaze);
        solveMazeButton.setVisible(true);
        hintButton.setVisible(true);
        saveMazeButton.setVisible(true);
        displayMaze(tempMaze);
        this.mazeDisplayer.requestFocus();
    }
    @FXML
    public void exitAction() {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setContentText("Are you sure you want to quit the game?");
        ButtonType quiteButton = new ButtonType("Quit");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationDialog.getButtonTypes().setAll(cancelButton, quiteButton);
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        if (confirmationResult.isPresent() && confirmationResult.get() == quiteButton)
        {
            if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}
            if (inSolutionMediaPlayer != null && inSolutionMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inSolutionMediaPlayer.stop();}
            Platform.exit();
        }
    }
    @FXML
    public void aboutActionMenu()
    {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {e.printStackTrace();}}
    @FXML
    public void helpAction()
    {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {e.printStackTrace();}}
    @FXML
    public void PropertiesAction()
    {
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {e.printStackTrace();}}
    @FXML
    public void solveMazeAction(){
        int[][] temp = viewModel.getMaze();
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setContentText("Are you sure you want to solve the maze?");
        ButtonType solveButton = new ButtonType("Solve");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationDialog.getButtonTypes().setAll(solveButton, cancelButton);
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        if (confirmationResult.isPresent() && confirmationResult.get() == solveButton) {
            if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}inSolutionMediaPlayer.play();
            int[][] sol = viewModel.solve(this.viewModel, this.viewModel.getPositionRow(), this.viewModel.getPositionCol(), "generateSolution");
            mazeDisplayer.setSolution(sol);
            mazeDisplayer.isSolved(true);
            mazeDisplayer.setMaze(temp);
            solveMazeButton.setVisible(false);
            Alert createMazeDialog = new Alert(Alert.AlertType.CONFIRMATION);
            createMazeDialog.setContentText("Do you want to create a new maze?");
            ButtonType createButton = new ButtonType("Create Maze");
            ButtonType cancelButton2 = new ButtonType("Cancel");
            createMazeDialog.getButtonTypes().setAll(createButton, cancelButton2);
            Optional<ButtonType> createMazeResult = createMazeDialog.showAndWait();
            if (createMazeResult.isPresent() && createMazeResult.get() == createButton) {createMazeAction();}}
    }
    @FXML
    public void loadMazeAction() {
        FileChooser chooseFile = new FileChooser();
        chooseFile.setTitle("Open Loaded Maze");
        File filePath = new File("resources/MazesFile");
        if (filePath.exists() && isDirectoryEmpty(filePath)) {showAlertDialog("No Mazes Found !","Sorry, but we currently have no saved mazes...",null);return;}
        chooseFile.setInitialDirectory(filePath);
        Stage stage = (Stage) mazeDisplayer.getScene().getWindow();
        File file = chooseFile.showOpenDialog(stage);
        if (file != null && file.exists() && !file.isDirectory()) {viewModel.load(file);mazeDisplayer.drawMaze();}
    }
    @FXML
    public void saveMazeAction() {
        if (!getCreatedOrNot()) {showAlertDialog("No Maze To Save !","If you want to save a maze, you need to create a maze first.",null);return;}
        FileChooser chooseFile = new FileChooser();
        File filePath = new File("resources/MazesFile");
        if (!filePath.exists() && !filePath.mkdirs()) {showAlertDialog("Error Creating Directory", "Failed to create the directory for saving the maze.",null);return;}
        chooseFile.setInitialDirectory(filePath);
        Stage stage = (Stage) mazeDisplayer.getScene().getWindow();
        File file = chooseFile.showSaveDialog(stage);
        if (file != null) {viewModel.save(file);}
    }
    @FXML
    public void hintAction()
    {
        int[][] maze = viewModel.getMaze();
        int [][] solution =viewModel.solve(this.viewModel, this.viewModel.getPositionRow(), this.viewModel.getPositionCol(), "generateHint");
        mazeDisplayer.setSolution(solution);
        mazeDisplayer.isSolved(true);
        mazeDisplayer.setMaze(maze);
        solveMazeButton.setVisible(true);
    }
    @FXML
    public void logOutAction() throws IOException {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setContentText("Are you sure you want to Log out ?");
        ButtonType LogButton = new ButtonType("Log Out");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationDialog.getButtonTypes().setAll(LogButton, cancelButton);
        Optional<ButtonType> confirmationResult = confirmationDialog.showAndWait();
        if (confirmationResult.isPresent() && confirmationResult.get() == LogButton)
        {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("LogInView.fxml").openStream());
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Log In");
            Image icon = new Image(new FileInputStream("resources/MazeImages/Icon.jpg"));
            stage.getIcons().add(icon);
            stage.show();
            Stage previousStage = (Stage) createMazeButton.getScene().getWindow();
            previousStage.close();
            if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}
            if (inSolutionMediaPlayer != null && inSolutionMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inSolutionMediaPlayer.stop();}
        }

    }
    /**
        /////////////// Help functions ///////////
     **/
    private void showAlertDialog(String title, String message, String imagePath) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.getDialogPane().lookupButton(ButtonType.CLOSE).setStyle("-fx-font-size: 15px;" + "-fx-padding: 5px 10px;");
        alert.getDialogPane().setStyle("-fx-background-color: #f2f2f2;" + "-fx-font: 20 Arial;");
        alert.getDialogPane().lookup(".dialog-pane").setStyle("-fx-background-color: #f2f2f2;" + "-fx-font-size: 16px;");
        alert.getDialogPane().lookup(".dialog-pane > *.label").setStyle("-fx-font-size: 18px;");
        if (title.equals("Gooooooooooooooooooooooal !"))
        {
            alert.getDialogPane().lookup(".dialog-pane > *.label").setStyle("-fx-font-size: 30px;");
        }
        alert.setContentText(message);
        alert.setTitle(title);
        if (imagePath != null) {try {
                Image backgroundImage = new Image(new FileInputStream(imagePath));
                ImageView imageView = new ImageView(backgroundImage);
                imageView.setFitHeight(300);
                imageView.setFitWidth(300);
                alert.setGraphic(imageView);
            } catch (FileNotFoundException e) {e.printStackTrace();}}
        alert.show();
        alert.getDialogPane().lookupButton(ButtonType.CLOSE).addEventFilter(ActionEvent.ACTION, event -> {
                if (inGameMediaPlayer != null && inGameMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inGameMediaPlayer.stop();}
                if (inSolutionMediaPlayer != null && inSolutionMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {inSolutionMediaPlayer.stop();}
            }

        );
    }
    public void KeyPressed(KeyEvent key) { viewModel.move(key.getCode()); key.consume();}
    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> mazeDisplayer.drawMaze());
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> mazeDisplayer.drawMaze());}
    private boolean isDirectoryEmpty(File directory) {File[] files = directory.listFiles();return files != null && files.length == 0;}
    public void setCreatedOrNot(boolean createOrNot) {mazeAlreadyCreated = createOrNot;}
    public boolean getCreatedOrNot() {return mazeAlreadyCreated;}
//    @FXML
    public void mouseClicked() {this.mazeDisplayer.requestFocus();}
    private int parseIntegerOrDefault(String value) {try {return Integer.parseInt(value);} catch (NumberFormatException e) {return 0;}}
//    public void stopSound() {
//        if (inGameMediaPlayer != null) {
//            inGameMediaPlayer.stop();
//            inGameMediaPlayer.dispose();
//        }
//    }
}
