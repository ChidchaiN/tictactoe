package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                text = "ID: ${game.game_id}, Size: ${game.size}, Winner: ${game.winner ?: "None"}"
                setOnClickListener {
                    val intent = Intent(this@HistoryActivity, ReplayActivity::class.java).apply {
                        putExtra("GAME_ID", game.game_id)  // Ensure game.id is correct here
                        putExtra("GAME_SIZE", game.size)
                    }
                    startActivity(intent)
                }
            }
            historyLayout.addView(button)
        }
    }

}
