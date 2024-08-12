package com.example.tictactoe

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameRepository {

    private val apiService = RetrofitClient.api

    // Function to update game winner
    suspend fun updateGameWinner(gameId: Int, winner: String?) {
        val update = GameUpdate(game_id = gameId, status = "Completed", winner = winner)
        apiService.updateGame(update).execute() // Use execute for synchronous call
    }

    // You can add other repository functions here to handle data operations
}
