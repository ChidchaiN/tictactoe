
# Tic-Tac-Toe Game

This repository contains a complete Tic-Tac-Toe game with a backend API, Android app, and AI algorithm for optimal moves. The project includes features for game history, replay functionality, and basic AI for playing against the computer.

## Table of Contents

1.  [Project Overview](#project-overview)
2.  [Setup and Installation](#setup-and-installation)
3.  [Running the Application](#running-the-application)
4.  [Design and Architecture](#design-and-architecture)
5.  [Algorithms](#algorithms)
6.  [API Endpoints](#api-endpoints)
7.  [Contributing](#contributing)

## Project Overview

This project is designed to allow players to play Tic-Tac-Toe on an Android app against either another player or an AI. The backend is implemented using Flask and MySQL, providing an API for managing games and moves. The Android app interacts with this API to display game status and handle user interactions.

## Setup and Installation

### Prerequisites

-   Java 17 or higher
-   Android Studio
-   Python 3.x
-   MySQL Server

### Backend Setup

1.  **Clone the Repository:**
    
    ```bash
    git clone https://github.com/yourusername/tictactoe.git
    cd tictactoe` 
    ```
    
2.  **Set Up the Database:**
    
    -   Import the SQL schema into your MySQL database:
        
        sql
        
        ```bash
        CREATE DATABASE tic_tac_toe;
        USE tic_tac_toe;
        
        CREATE TABLE games (
            game_id INT AUTO_INCREMENT PRIMARY KEY,
            size INT,
            winner ENUM('X', 'O', 'DRAW') DEFAULT 'NONE',
            start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            end_time TIMESTAMP NULL,
            status ENUM('ONGOING', 'COMPLETED') DEFAULT 'ONGOING'
        );
        
        CREATE TABLE moves (
            move_id INT AUTO_INCREMENT PRIMARY KEY,
            game_id INT,
            player ENUM('X', 'O'),
            row INT,
            col INT,
            move_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (game_id) REFERENCES games(game_id) ON DELETE CASCADE
        );` 
        ```
3.  **Install Python Dependencies:**
    
    Navigate to the `backend` directory and install the required packages:
    
```bash
    pip install flask mysql-connector-python` 
   ```
4.  **Run the Flask API:**
    
```bash

    `python db_connect.py` 
```

### Android App Setup

1.  **Open the Project:**
    
    -   Open Android Studio and import the project.
2.  **Build and Run the Application:**
    
    -   Make sure you have the Android SDK installed.
    -   Build and run the app on an emulator or a physical device.

## Running the Application

1.  **Start the Backend API:**
    
    Ensure that the Flask API is running by executing `python db_connect.py` from the `backend` directory.
    
2.  **Run the Android App:**
    
    Build and run the app from Android Studio. It will communicate with the backend API to fetch and manage game data.
    

## Design and Architecture

### Backend (Flask & MySQL)

-   **Flask API**: Provides endpoints to insert games, insert moves, fetch games and moves, and update game status.
-   **MySQL Database**: Stores game and move data with relationships to ensure data integrity.

### Android App

-   **Activities**: Handles different views such as the main game screen, history, and replay.
-   **ViewModel**: Manages the game state and communicates with the repository to interact with the backend.
-   **AI Algorithm**: Uses the minimax algorithm for determining the best move for the AI.

## Algorithms

### Winner Check

The winner is determined by checking:

-   All rows, columns, and diagonals for a uniform player.
-   If a player occupies all cells in any of these lines, they are declared the winner.

### AI Algorithm

The AI uses the **Minimax Algorithm**:

-   **Minimax**: A recursive algorithm to determine the optimal move. It explores all possible moves, evaluates their outcomes, and selects the move with the highest score.
    
    -   **Maximizing Player**: Tries to maximize the score (AI player).
    -   **Minimizing Player**: Tries to minimize the score (opponent).
    
    The algorithm uses a scoring system where:
    
    -   Win: `+10 - depth`
    -   Loss: `-10 + depth`
    -   Draw or non-terminal state: `0`
    
    The depth limits recursion to prevent excessive computation.
    

## API Endpoints

-   **POST `/insert_game`**: Inserts a new game and returns the game ID.
-   **POST `/insert_move`**: Inserts a move into a game.
-   **GET `/get_moves/<game_id>`**: Retrieves all moves for a specific game.
-   **GET `/get_games`**: Retrieves all games.
-   **POST `/update_game`**: Updates the status and winner of a game.

## Contributing

Feel free to fork the repository and submit pull requests. For bug reports or feature requests, open an issue on GitHub.