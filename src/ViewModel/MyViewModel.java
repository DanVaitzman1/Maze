package ViewModel;
import Model.IModel;
import algorithms.mazeGenerators.Position;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private final IModel model;
    private int rowIndex,colIndex;
    public StringProperty row,col;
    public MyViewModel(IModel model) {
        this.model = model;
        rowIndex=0;
        colIndex=0;
        row = new SimpleStringProperty("0");
        col = new SimpleStringProperty("0");
    }
    public int[][] generateMaze(int row, int col) {return model.generateMaze(row,col);}
    public void move(KeyCode movement) {model.move(movement);}
    public void mouseMove(MouseEvent event,double cellWidth1,double cellHeight1){model.mouseMove(event,cellWidth1,cellHeight1);}
    public int [][] solve(MyViewModel m, int charRow, int charCol, String hintOrSolution) {return model.solve(m,charRow,charCol,hintOrSolution);}
    public void save(File file) {
        model.save(file);
    }
    public void load(File file) {
        model.load(file);
    }
    public boolean isSolved() {
        return model.mazeSolvedCheck();
    }
    public boolean gameIsOver() {
        return model.checkPlayerFinishTheGame();
    }
    public int[][] getMaze() {return  model.getMaze();}
    public int[][] getSolutionArray() {
        return model.getSolutionArray();
    }
    public int getPositionRow() {
        return this.rowIndex;
    }
    public int getPositionCol() {
        return this.colIndex;
    }
    public Position getEndPosition() {
        return model.getGoalPosition();
    }
    public void setMaze(int[][] maze) {
        model.setMaze(maze);
    }
    @Override
    public void update(Observable obs, Object arg) {
        if (obs == model) {
            rowIndex = model.getPositionRow();row.set(rowIndex + "");
            colIndex = model.getPositionCol();col.set(colIndex + "");
        }setChanged();notifyObservers();}
}
