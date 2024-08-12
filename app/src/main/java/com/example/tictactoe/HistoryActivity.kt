package com.example.tictactoe

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class HistoryActivity : AppCompatActivity() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var historyLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        historyLayout = findViewById(R.id.history_layout)

        // Initialize the ViewModel
        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // Fetch game history and display it
        gameViewModel.getGames { result ->
            if (result.isSuccess) {
                val games = result.getOrNull() ?: emptyList()
                displayGameHistory(games)
            } else {
                // Handle error
            }
        }
    }

    private fun displayGameHistory(games: List<GameDetail>) {
        for (game in games) {
            val button = Button(this).apply {
                text = "Size: ${game.size}, Winner: ${game.winner}"
                setOnClickListener {
                    // Handle button click if needed, e.g., show game details or replay
                }
            }
            historyLayout.addView(button)
        }
    }
}
