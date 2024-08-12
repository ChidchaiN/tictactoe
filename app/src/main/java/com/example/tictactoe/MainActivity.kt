package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sizeSpinner: Spinner
    private lateinit var gridLayout: GridLayout
    private lateinit var rematchButton: Button
    private lateinit var gameResult: TextView
    private lateinit var gameViewModel: GameViewModel
    private var currentGameId: Int? = null
    private lateinit var gameState: GameState
    private lateinit var viewHistoryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sizeSpinner = findViewById(R.id.size_spinner)
        gridLayout = findViewById(R.id.grid_layout)
        rematchButton = findViewById(R.id.rematch_button)
        gameResult = findViewById(R.id.game_result)
        viewHistoryButton = findViewById(R.id.history_button) // Moved this after setContentView

        // Initialize the ViewModel
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        setupTableSizeSpinner()
        setupBoard()

        rematchButton.setOnClickListener {
            handleRematch()
        }

        // Observe LiveData for moves if needed
        gameViewModel.moves.observe(this) { moves ->
            // Update UI with moves if needed
        }

        viewHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTableSizeSpinner() {
        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val size = when (position) {
                    0 -> 3
                    1 -> 4
                    2 -> 5
                    3 -> 6
                    else -> 3
                }
                Log.d("MainActivity", "Table size: $size")
                updateBoardSize(size)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where no item is selected if needed
            }
        }
    }

    private fun updateBoardSize(size: Int) {
        gameState = GameState(
            board = Array(size) { Array(size) { Player.NONE } },
            currentPlayer = Player.X
        )

        gridLayout.removeAllViews()
        gridLayout.rowCount = size
        gridLayout.columnCount = size

        for (i in 0 until size) {
            for (j in 0 until size) {
                val button = Button(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 0
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                        rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    }
                    setPadding(16, 16, 16, 16)
                    setBackgroundResource(R.drawable.button_background)
                    setOnClickListener { handleMove(i, j) }
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun setupBoard() {
        updateBoardSize(3)
        Log.d("MainActivity", "Board setup with size 3x3")
    }

    private fun handleMove(row: Int, col: Int) {
        if (gameState.board[row][col] == Player.NONE && gameState.winner == null) {
            val buttonIndex = row * gameState.board.size + col
            val button = gridLayout.getChildAt(buttonIndex) as Button

            val xIcon = ContextCompat.getDrawable(this, R.drawable.x_icon)
            val yIcon = ContextCompat.getDrawable(this, R.drawable.o_icon)
            val emptyIcon = ContextCompat.getDrawable(this, R.drawable.empty_icon)

            gameState.board[row][col] = gameState.currentPlayer
            val icon = when (gameState.currentPlayer) {
                Player.X -> xIcon
                Player.O -> yIcon
                else -> emptyIcon
            }
            button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)

            if (checkGameOver()) return

            gameState.currentPlayer = if (gameState.currentPlayer == Player.X) Player.O else Player.X

            if (gameState.currentPlayer == Player.O) {
                lifecycleScope.launch {
                    val move = TicTacToeGame.findBestMove(gameState.board, gameState.board.size)
                    val (aiRow, aiCol) = move

                    if (gameState.board[aiRow][aiCol] == Player.NONE) {
                        gameState.board[aiRow][aiCol] = Player.O

                        val aiButtonIndex = aiRow * gameState.board.size + aiCol
                        val aiButton = gridLayout.getChildAt(aiButtonIndex) as Button
                        aiButton.setCompoundDrawablesWithIntrinsicBounds(null, yIcon, null, null)

                        if (checkGameOver()) return@launch

                        gameState.currentPlayer = Player.X
                    }
                }
            }

            currentGameId?.let { gameId ->
                gameViewModel.insertMove(
                    gameId,
                    gameState.currentPlayer.name,
                    row,
                    col
                ) { result ->
                    if (result.isSuccess) {
                        Log.d("MainActivity", "Move recorded in the database successfully")
                    } else {
                        Log.e("MainActivity", "Failed to record move in the database", result.exceptionOrNull())
                    }
                }
            }
        }
    }

    private fun handleRematch() {
        val newSize = when (sizeSpinner.selectedItemPosition) {
            0 -> 3
            1 -> 4
            2 -> 5
            3 -> 6
            else -> 3
        }
        Log.d("MainActivity", "Rematch selected size: $newSize")
        updateBoardSize(newSize)
        startNewGame(newSize)
    }

    private fun startNewGame(size: Int) {
        gameViewModel.insertGame(size) { result ->
            if (result.isSuccess) {
                currentGameId = result.getOrNull()
                Log.d("MainActivity", "New game started with ID: $currentGameId")
            } else {
                Log.e("MainActivity", "Failed to start new game", result.exceptionOrNull())
            }
        }
    }

    private fun checkGameOver(): Boolean {
        val winner = TicTacToeGame.checkWinner(gameState.board)
        if (winner != null || TicTacToeGame.isBoardFull(gameState.board)) {
            gameState.winner = winner
            val winnerText = when (winner) {
                Player.X -> "Player X wins!"
                Player.O -> "Player O wins!"
                Player.NONE -> "No winner" // or handle this case appropriately
                null -> "It's a draw!"
            }
            gameResult.text = winnerText

            currentGameId?.let { gameId ->
                gameViewModel.updateGameWinner(gameId, winner?.name ?: "NONE") { result ->
                    if (result.isSuccess) {
                        Log.d("MainActivity", "Game winner updated successfully")
                    } else {
                        Log.e("MainActivity", "Failed to update game winner", result.exceptionOrNull())
                    }
                }
            }

            return true
        }
        return false
    }

}
