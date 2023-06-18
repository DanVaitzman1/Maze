package View;
import algorithms.mazeGenerators.Position;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
public class MazeDisplayer extends Canvas {
    private int[][] maze,solutionPath;
    private int playerRow,playerCol;
    private Position goalPosition;
    private boolean gameFinish;
    public Image playerImage;
    public double zoomFactor = 1.0;
    public MazeDisplayer() {
        setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY();
                if (event.isControlDown()) {
                    if (deltaY > 0) {zoomIn();}
                    else {zoomOut();}
                    event.consume();
                }}});
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown()) {
                    if (event.getText().equalsIgnoreCase("plus") || event.getText().equalsIgnoreCase("add")) {zoomIn();event.consume();}
                    else if (event.getText().equalsIgnoreCase("minus") || event.getText().equalsIgnoreCase("subtract")) {zoomOut();event.consume();}
                }}});
    }
    public void setMaze(int[][] maze) {this.maze = maze;drawMaze();}
    public void goalPosition(Position goal) {
        this.goalPosition = goal;
    }
    public void Solved(int[][] sol) {
        this.solutionPath = sol;
    }
    public void setCharacterPosition(int row, int column) {this.playerRow = row;this.playerCol = column;}
    public void isSolved(boolean s) {
        this.gameFinish = s;
    }
    public void setSolution(int[][] sol) {
        this.solutionPath = sol;
    }
    public void drawMaze() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / (maze.length*zoomFactor);
            double cellWidth = canvasWidth / (maze[0].length*zoomFactor);
            try {
                GraphicsContext graphicsContext = getGraphicsContext2D();
                graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
                Image backgroundImage = new Image(new FileInputStream("resources/MazeImages/football.jpeg"));
                graphicsContext.drawImage(backgroundImage, 0, 0, canvasWidth, canvasHeight);
                Image wallImage = new Image(new FileInputStream("resources/MazeImages/wallimage.png"));
                for (int i = 0; i < maze.length; i++) {
                    for (int j = 0; j < maze[0].length; j++) {
                        if (maze[i][j] == 1) {
                            double x = i * cellHeight;
                            double y = j * cellWidth;
                            graphicsContext.drawImage(wallImage, y, x, cellWidth, cellHeight);}}}
                graphicsContext.drawImage(playerImage, playerCol * cellWidth, playerRow * cellHeight, cellWidth, cellHeight);
                Image startPositionImage = new Image(new FileInputStream("resources/MazeImages/startPositionImage.png"));
                graphicsContext.drawImage(startPositionImage, 0 * cellWidth, 0 * cellHeight, cellWidth, cellHeight);
                Image goalPositionImage = new Image(new FileInputStream("resources/MazeImages/goalPositionImage.jpeg"));
                graphicsContext.drawImage(goalPositionImage, goalPosition.getColumnIndex() * cellWidth, goalPosition.getRowIndex() * cellHeight, cellWidth, cellHeight);
                if (gameFinish) {
                    Image SolutionImage = new Image(new FileInputStream("resources/MazeImages/solutionImage.png"));
                    for (int i = 1; i < solutionPath[0].length - 1; i++) {
                        int x = solutionPath[0][i];
                        int y = solutionPath[1][i];
                        graphicsContext.drawImage(SolutionImage, y * cellWidth, x * cellHeight, cellWidth, cellHeight);
                    }
                }
            } catch (FileNotFoundException e) {e.printStackTrace();}
        }
    }
    public void zoomIn() {zoomFactor *= 1.1;drawMaze();}
    public void zoomOut() {zoomFactor /= 1.1;drawMaze();}
}
