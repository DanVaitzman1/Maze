
# Maze Project

## Overview
The Maze Project is a multi-part development exercise that focuses on various software development techniques and principles. The project involves building a maze application with a focus on the following components:
- **Algorithmic design and implementation** for maze generation and solving.
- **Client-Server architecture** using Java, JavaFX, and Java Streams.
- **3D Maze extension** for advanced functionalities (optional).
- **Testing and version control** using JUnit and Git.

## Features
1. **Maze Generation**:
   - Generate 2D and 3D mazes using efficient algorithms.
   - Support for different maze generation algorithms, including random and structured approaches.

2. **Maze Solving**:
   - Implement search algorithms like Breadth-First Search (BFS), Depth-First Search (DFS), and Best-First Search.
   - Efficiently find and display paths through the maze.

3. **Compression and Decompression**:
   - Reduce maze data size using custom compression algorithms.
   - Support decompression for reconstruction and visualization.

4. **Client-Server Interaction**:
   - Client requests maze generation and solving from the server.
   - Multi-threaded server processes requests concurrently.

5. **Graphical User Interface**:
   - Intuitive GUI for interacting with mazes, built using JavaFX.
   - MVVM architecture ensures separation of concerns and maintainability.

6. **Testing and Validation**:
   - Comprehensive unit testing using JUnit 5.
   - Automatic validation of maze generation and solving functionalities.

## Technology Stack
- **Programming Language**: Java (version 15+)
- **GUI Framework**: JavaFX
- **Design Patterns**: MVVM, Singleton, Strategy, Decorator
- **Testing Framework**: JUnit 5
- **Version Control**: Git

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 15 or higher.
- IntelliJ IDEA (recommended IDE).
- Maven for dependency management.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/<username>/Maze-Project
   cd Maze-Project
   ```
2. Open the project in IntelliJ IDEA and ensure all dependencies are resolved via Maven.

3. Configure the `config.properties` file in the `resources` directory:
   ```properties
   threadPoolSize=4
   mazeAlgorithm=MyMazeGenerator
   solverAlgorithm=BestFirstSearch
   ```

4. Compile and run the application:
   ```bash
   mvn compile
   mvn exec:java
   ```

## Usage
- **Generate a Maze**: Select the maze dimensions and generation algorithm.
- **Solve a Maze**: Choose a solving algorithm and visualize the solution.
- **3D Mazes**: Explore the optional 3D maze functionality.

## Testing
Run unit tests using Maven:
```bash
mvn test
```

## License
This project is licensed under the MIT License. See the LICENSE file for more details.
