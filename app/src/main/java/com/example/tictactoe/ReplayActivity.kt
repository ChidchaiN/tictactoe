package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.coroutines.DelicateCoroutinesApi

class ReplayActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var gameViewModel: GameViewModel
    private var gameId: Int? = null
    private var gameSize: Int = 3
    private lateinit var newGame: Button

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replay)

        gridLayout = findViewById(R.id.grid_layout)
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]
        newGame = findViewById(R.id.newGame_button)

        gameId = intent.getIntExtra("GAME_ID", -1)  // Retrieve game ID
        gameSize = intent.getIntExtra("GAME_SIZE", 3)

        Log.d("ReplayActivity", "Received game ID: $gameId, size: $gameSize")

        if (gameId != -1) {
            setupBoard(gameSize)
            loadGameMoves()
        } else {
            // Handle case where game ID is invalid or not passed
            Log.e("ReplayActivity", "Invalid game ID received")
        }

        newGame.setOnClickListener {
            val intent = Intent(this@ReplayActivity, MainActivity::class.java)
            intent.putExtra("GAME_SIZE", gameSize)
            startActivity(intent)
        }
    }

    private fun setupBoard(size: Int) {
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
                }
                gridLayout.addView(button)
            }
        }
    }

    private fun loadGameMoves() {
        gameId?.let { id ->
            Log.d("ReplayActivity", "Loading moves for game ID: $id")
            gameViewModel.fetchMoves(id)
            gameViewModel.moves.observe(this) { moves ->
                Log.d("ReplayActivity", "Moves observed: $moves")
                replayMoves(moves)
            }
        }
    }

    private fun replayMoves(moves: List<MoveDetail>) {
        val xIcon = ContextCompat.getDrawable(this, R.drawable.x_icon)
        val oIcon = ContextCompat.getDrawable(this, R.drawable.o_icon)

        moves.forEachIndexed { index, move ->
            val row = move.row
            val col = move.col
            val player = if (move.player == "X") Player.X else Player.O

            val buttonIndex = row * gameSize + col
            val button = gridLayout.getChildAt(buttonIndex) as Button

            val icon = when (player) {
                Player.X -> xIcon
                Player.O -> oIcon
                else -> null
            }

            Log.d("ReplayActivity", "Replay move $index: row=$row, col=$col, player=${player.name}, buttonIndex=$buttonIndex")

            button.postDelayed({
                Log.d("ReplayActivity", "Setting icon for move $index: row=$row, col=$col")
                button.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
            }, (index * 1000).toLong()) // Delay for replay effect
        }
    }

}
