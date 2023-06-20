package Model;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;
import algorithms.mazeGenerators.Maze;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import algorithms.search.AState;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import java.util.Properties;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyModel extends Observable implements IModel
{
    private int[][] maze,solutionPath;
    private int currRow,currCol;
    private Position goalPosition;
    private Maze firstMaze;
    private boolean checkMazeSolved,playerFinishTheGame;
    public static final Logger GENERATE_LOG = LogManager.getLogger(MyModel.class);
    @Override
    public int[][] generateMaze(int row1, int col1) {
        Server  genreateServer = new Server(5400,1000, new ServerStrategyGenerateMaze());
        GENERATE_LOG.info(genreateServer.toString() + " Starting server at port = "  + 5400 );
        genreateServer.start();
        try
        {
            Client genreateClient = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inputStream, OutputStream outputStream)
                {
                    try
                    {
                        checkMazeSolved = false;playerFinishTheGame = false;
                        ObjectOutputStream toServer = new ObjectOutputStream(outputStream);
                        ObjectInputStream fromServer = new ObjectInputStream(inputStream);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row1,col1};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressMaze = (byte[]) fromServer.readObject();
                        InputStream inputStream1 = new MyDecompressorInputStream(new ByteArrayInputStream(compressMaze));
                        byte[] decompressedMaze = new byte[12+ mazeDimensions[0] * mazeDimensions[1]];
                        inputStream1.read(decompressedMaze);
                        Maze maze = new Maze(decompressedMaze);
                        Position UpdatePos;
                        UpdatePos = maze.getStartPosition();
                        firstMaze = maze;
                        currRow = UpdatePos.getRowIndex();
                        currCol = UpdatePos.getColumnIndex();
                        goalPosition = maze.getGoalPosition();
                        copyMaze(maze);
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            });
            genreateClient.communicateWithServer();
            GENERATE_LOG.info(genreateServer.toString() + " Client accepted: "  + genreateClient.toString() );
            GENERATE_LOG.info(genreateClient.toString() + " Generated a new maze with " + row1 + " rows and " + col1 +" cols");
        }
        catch (UnknownHostException e) {
            GENERATE_LOG.error(genreateServer.toString() + " UnknownHostException ",e );
            e.printStackTrace();
        }
        GENERATE_LOG.info(genreateServer.toString() + " Stopping server..." );
        genreateServer.stop();
        setChanged();
        notifyObservers();
        return maze;
    }
    @Override
    public int[][] solve(MyViewModel m, int charRow, int charCol, String hintOrSolution)
    {
        Server searchServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        GENERATE_LOG.info(searchServer.toString() + " Starting server at port = "  + 5401 );
        searchServer.start();
        try {

            Client searchClient = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        Maze maze = firstMaze;
                        toServer.writeObject(maze);
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject();
                        ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();
                        int sizeOfSolution = mazeSolutionSteps.size();
                        solutionPath = new int[2][sizeOfSolution];
                        if (hintOrSolution.equals("generateSolution"))
                        {
                            for (int i = 0; i < mazeSolutionSteps.size(); i++)
                            {
                                solutionPath[0][i] = ((MazeState)(mazeSolutionSteps.get(i))).getPosition().getRowIndex();
                                solutionPath[1][i] = ((MazeState)(mazeSolutionSteps.get(i))).getPosition().getColumnIndex();
                            }
                        }
                        else if (hintOrSolution.equals("generateHint"))
                        {
                            int pos = 1;
                            for (int i = 0; i < mazeSolutionSteps.size(); i++)
                            {
                                if (((MazeState) (mazeSolutionSteps.get(i))).getPosition().getRowIndex() == currRow
                                        && ((MazeState) (mazeSolutionSteps.get(i))).getPosition().getColumnIndex() == currCol)
                                {
                                    pos = i+1;}
                            }
                            for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                                solutionPath[0][i] = ((MazeState)(mazeSolutionSteps.get(pos))).getPosition().getRowIndex();
                                solutionPath[1][i] = ((MazeState)(mazeSolutionSteps.get(pos))).getPosition().getColumnIndex();
                            }
                        }
                        setChanged();
                        notifyObservers();
                    } catch (Exception e) {e.printStackTrace();}}});
            searchClient.communicateWithServer();
            GENERATE_LOG.info(searchServer.toString() + " Client accepted: "  + searchClient.toString() );
            InputStream inputFile = new FileInputStream("resources/config.properties");
            Properties properties = new Properties();
            properties.load(inputFile);
            if(hintOrSolution.equals("generateSolution")){GENERATE_LOG.info(searchClient.toString() + " Has requested a solution, maze solved with " + properties.getProperty("mazeSearchingAlgorithm") + " algorithm");}
            else if(hintOrSolution.equals("generateHint")){GENERATE_LOG.info(searchClient.toString() + " Has requested a hint ");}
        }
        catch (UnknownHostException e) {GENERATE_LOG.error(searchServer.toString() + " UnknownHostException ",e );e.printStackTrace();}
        catch (FileNotFoundException e) {GENERATE_LOG.error(searchServer.toString() + " FileNotFoundException ",e );throw new RuntimeException(e);}
        catch (IOException e) {GENERATE_LOG.error(searchServer.toString() + " IOException ",e );throw new RuntimeException(e);}
        GENERATE_LOG.info(searchServer.toString() + " Stopping server..." );
        searchServer.stop();
        return solutionPath;
    }
    private void copyMaze(Maze toCopyMaze)
    {
        int row = toCopyMaze.getNumRows(),col = toCopyMaze.getNumColumns();
        maze = new int[row][col];
        for (int i = 0; i < row; i++) for (int j = 0; j < col; j++) maze[i][j] = toCopyMaze.getValueAt(i,j);
    }
    @Override
    public void move(KeyCode movement){
        int row = currRow,col = currCol;
        switch (movement) {
            case UP: case DIGIT8: case NUMPAD8:
                if (isValidMove(row - 1, col))
                    this.currRow--;
                break;
            case DOWN: case DIGIT2: case NUMPAD2:
                if (isValidMove(row + 1, col))
                    this.currRow++;
                break;
            case RIGHT: case DIGIT6: case NUMPAD6:
                if (isValidMove(row, col + 1))
                    this.currCol++;
                break;
            case LEFT: case DIGIT4: case NUMPAD4:
                if (isValidMove(row, col - 1))
                    this.currCol--;
                break;
            case DIGIT3: case NUMPAD3:
                if (isValidMove(row + 1, col + 1))
                    if (isValidMove(row, col + 1) || isValidMove(row + 1, col))
                    {
                        this.currRow++;
                        this.currCol++;
                    }
                break;
            case DIGIT1 :case NUMPAD1:
                if (isValidMove(row + 1, col - 1))
                    if (isValidMove(row, col - 1) || isValidMove(row + 1, col))
                    {
                        this.currRow++;
                        this.currCol--;
                    }
                break;
            case DIGIT9 :case NUMPAD9:
                if (isValidMove(row - 1, col + 1))
                    if (isValidMove(row - 1, col) || isValidMove(row, col + 1))
                    {
                        this.currRow--;
                        this.currCol++;
                    }break;
            case DIGIT7 :case NUMPAD7:
                if (isValidMove(row - 1, col - 1))
                    if (isValidMove(row, col - 1) || isValidMove(row - 1, col))
                    {
                        this.currRow--;
                        this.currCol--;
                    }break;
        }
        if (goalPosition.getColumnIndex() == getPositionCol() && goalPosition.getRowIndex() == getPositionRow()){ playerFinishTheGame = true;}
        setChanged();
        notifyObservers();
    }
    private double dragStartX; // Starting X-coordinate of the drag
    private double dragStartY; // Starting Y-coordinate of the drag
@Override
public void mouseMove(MouseEvent event,double cellWidth1,double cellHeight1) {
    if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
        dragStartX = event.getX();
        dragStartY = event.getY();
    } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double cellWidth = cellWidth1 / maze[0].length;
        double cellHeight = cellHeight1 / maze.length;
        int newCurrRow = (int) (mouseY / cellHeight);
        int newCurrCol = (int) (mouseX / cellWidth);
        if (Math.abs(newCurrRow - currRow) <= 1 && Math.abs(newCurrCol - currCol) <= 1) {
            if (isValidMove(newCurrRow, newCurrCol)) {
                currRow = newCurrRow;
                currCol = newCurrCol;
            }
        }
        if (goalPosition.getColumnIndex() == getPositionCol() && goalPosition.getRowIndex() == getPositionRow()) {
            playerFinishTheGame = true;
        }
        setChanged();
        notifyObservers();
    }
}
    private boolean isValidMove(int row, int col) {return row >= 0 && col >= 0 && row <= maze.length - 1 && col <= maze[0].length - 1 && (maze[row][col] != 1);}
    @Override
    public boolean checkPlayerFinishTheGame() {
        return this.playerFinishTheGame;
    }
    @Override
    public void save(File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            this.checkMazeSolved = false;
            objectOutputStream.writeObject(this.firstMaze);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException ignored) {}
    }
    @Override
    public void load(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            ObjectInputStream outputStream = new ObjectInputStream(inputStream);
            Maze temp = (Maze)outputStream.readObject();
            setFirstMaze(temp);
            setMaze(getFirstMazeMaze().getMazeMatrix());
            setPositionRow(temp.getStartPosition().getRowIndex());
            setPositionCol(temp.getStartPosition().getColumnIndex());
            setGoalPosition(temp.getGoalPosition());
            this.checkMazeSolved =false;
            setChanged();
            notifyObservers();
            outputStream.close();}
        catch (IOException ignored) {}
        catch (ClassNotFoundException e) {e.printStackTrace();}
    }
    @Override
    public boolean mazeSolvedCheck() {
        return this.checkMazeSolved;
    }
    @Override
    public int[][] getMaze() {
        return this.maze;
    }
    @Override
    public Maze getFirstMazeMaze() {
        return this.firstMaze;
    }
    @Override
    public int[][] getSolutionArray() {
        return this.solutionPath;
    }
    @Override
    public int getPositionRow() {
        return this.currRow;
    }
    @Override
    public int getPositionCol() {
        return this.currCol;
    }
    @Override
    public Position getGoalPosition() {
        return this.goalPosition;
    }
    @Override
    public void setFirstMaze(Maze m) {
        int row = m.getNumRows(),col = m.getNumColumns();
        this.firstMaze = new Maze(row,col);
        for (int i = 0; i < row; i++) for (int j = 0; j < col; j++) firstMaze.setValueInPosition(i,j,m.getValueAt(i,j));
    }
    @Override
    public void setMaze(int[][] maze) {
        this.maze=maze;
    }
    @Override
    public void setPositionCol(int col) {
        this.currCol=col;
    }
    @Override
    public void setPositionRow(int row) {
        this.currRow=row;
    }
    @Override
    public void setGoalPosition(Position goalPosition) {
        this.goalPosition = goalPosition;
    }
}
