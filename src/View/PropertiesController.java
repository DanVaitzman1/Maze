package View;
import Server.Configurations;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import java.io.*;
import java.net.URL;
import java.util.*;

//  TODO : check why it dosent changed values during the game ?
public class PropertiesController implements Initializable {
    @FXML
    private Pane pane;
    @FXML
    private VBox vBoxGenration;
    @FXML
    private VBox vBoxSeraching;
    @FXML
    private VBox vBoxPoolSize;

    @FXML
    public Button savePropertiesbutton;
    @FXML
    public Button closePropertiesButton;
    public int treadsNumberSpinnerValue;
    public static String searchingAlgorithm="BFS";
    public static String MazeGeneration="MyMazeGenerator";
    public static String treadPoolSize="2";
    private StringProperty selectedOptionGeneration = new SimpleStringProperty();
    private StringProperty selectedOptionSearching = new SimpleStringProperty();
    public StringProperty selectedOptionPropertyGeneration() {
        return selectedOptionGeneration;
    }
    public StringProperty selectedOptionPropertySearching() {
        return selectedOptionSearching;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Configurations.getInstance();
        
        Label vBoxGenrationLabel = new Label("Choose generation algorithm :");

        CheckBox checkBox1 = new CheckBox("SimpleMazeGenerator");
        CheckBox checkBox2 = new CheckBox("MyMazeGenerator");
        CheckBox checkBox3 = new CheckBox("EmptyMazeGenerator");
        checkBox1.setOnAction(event -> handleCheckBoxSelectionGeneration(checkBox1));
        checkBox2.setOnAction(event -> handleCheckBoxSelectionGeneration(checkBox2));
        checkBox3.setOnAction(event -> handleCheckBoxSelectionGeneration(checkBox3));
        vBoxGenration.getChildren().addAll(vBoxGenrationLabel,checkBox1, checkBox2, checkBox3);

        Label vBoxSerachingLabel = new Label("Choose searching algorithm :");
        CheckBox checkBox4 = new CheckBox("BreadthFirstSearch");
        CheckBox checkBox5 = new CheckBox("DepthFirstSearch");
        CheckBox checkBox6 = new CheckBox("BestFirstSearch");
        checkBox4.setOnAction(event -> handleCheckBoxSelectionSearching(checkBox4));
        checkBox5.setOnAction(event -> handleCheckBoxSelectionSearching(checkBox5));
        checkBox6.setOnAction(event -> handleCheckBoxSelectionSearching(checkBox6));
        vBoxSeraching.getChildren().addAll(vBoxSerachingLabel,checkBox4, checkBox5, checkBox6);

        Spinner<Integer> treadsNumberSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1000);
        valueFactory.setValue(1);
        treadsNumberSpinner.setValueFactory(valueFactory );
        treadsNumberSpinnerValue = treadsNumberSpinner.getValue();
        treadsNumberSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                treadsNumberSpinnerValue = treadsNumberSpinner.getValue();
            }
        });

        Label vBoxPoolSizeLabel = new Label("Choose the size of the tread pool :");
        vBoxPoolSize.getChildren().addAll(vBoxPoolSizeLabel,treadsNumberSpinner);
    }
    private void handleCheckBoxSelectionGeneration(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            String selectedOptionText = selectedCheckBox.getText();
            selectedOptionGeneration.set(selectedOptionText);
            vBoxGenration.getChildren().stream()
                    .filter(node -> node instanceof CheckBox && node != selectedCheckBox)
                    .map(node -> (CheckBox) node)
                    .forEach(checkBox -> checkBox.setSelected(false));
        } else {
            selectedOptionGeneration.set(null);
        }
    }
    private void handleCheckBoxSelectionSearching(CheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            String selectedOptionText = selectedCheckBox.getText();
            selectedOptionSearching.set(selectedOptionText);
            vBoxSeraching.getChildren().stream()
                    .filter(node -> node instanceof CheckBox && node != selectedCheckBox)
                    .map(node -> (CheckBox) node)
                    .forEach(checkBox -> checkBox.setSelected(false));
        } else {
            selectedOptionSearching.set(null);
        }
    }
    @FXML
    public void savePropertiesAction() throws IOException {
        OutputStream output=null;
        InputStream input=null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("resources/config.properties"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append(",");
                stringBuilder.append(System.lineSeparator());
                line = bufferedReader.readLine();
            }
            String text = stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException ignored) {
        }
        if (input == null) {
            output = new FileOutputStream("resources/config.properties");
            Properties prop = new Properties();
            switch (selectedOptionGeneration.getValue())
            {
                case "SimpleMazeGenerator" -> {MazeGeneration="SimpleMazeGenerator";}
                case "MyMazeGenerator" -> {MazeGeneration="MyMazeGenerator";}
                case "EmptyMazeGenerator" -> {MazeGeneration="EmptyMazeGenerator";}
            }

            switch (selectedOptionSearching.getValue()) {
                case "BreadthFirstSearch" -> {searchingAlgorithm = "BreadthFirstSearch";}
                case "DepthFirstSearch" -> {searchingAlgorithm = "DepthFirstSearch";}
                case "BestFirstSearch" -> {searchingAlgorithm = "BestFirstSearch";}
            }
            treadPoolSize= ""+treadsNumberSpinnerValue+"";
            prop.setProperty("threadPoolSize", treadPoolSize);
            prop.setProperty("mazeGeneratingAlgorithm", MazeGeneration);
            prop.setProperty("mazeSearchingAlgorithm", searchingAlgorithm);
            prop.store(output, null);
        }
        if (output != null) {
            try {output.close();}
            catch (IOException e) {e.printStackTrace();}
        }
        confirmAlert();
        closePropertiesAction();
    }
    @FXML
    public void closePropertiesAction()
    {
        Stage s = (Stage) closePropertiesButton.getScene().getWindow();
        s.close();
    }
    public void confirmAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("The following properties have been saved: \n" + "Maze Generating Algorithm = " + MazeGeneration +"\n"+ "Maze Searching Algorithm =  " +searchingAlgorithm +"\n" + "Thread Pool Size =  " + treadPoolSize );
        alert.show();
    }

}
