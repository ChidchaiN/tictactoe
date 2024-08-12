package com.example.tictactoe

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameRepository {

    private val apiService = RetrofitClient.api

    fun insertGame(size: Int, callback: (Result<Int>) -> Unit) {
        val game = Game(id = 0, size = size, winner = "None", title = "None")
        apiService.insertGame(game).enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        callback(Result.success(result.id))
                    } ?: run {
                        callback(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    callback(Result.failure(Exception("Failed to insert game: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun insertMove(move: Move, callback: (Result<Unit>) -> Unit) {
        apiService.insertMove(move).enqueue(object : Callback<MoveResponse> {
            override fun onResponse(call: Call<MoveResponse>, response: Response<MoveResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    callback(Result.failure(Exception("Failed to insert move: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<MoveResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getGames(callback: (Result<List<GameDetail>>) -> Unit) {
        apiService.getGames().enqueue(object : Callback<List<GameDetail>> {
            override fun onResponse(call: Call<List<GameDetail>>, response: Response<List<GameDetail>>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body() ?: emptyList()))
                } else {
                    callback(Result.failure(Exception("Failed to fetch games")))
                }
            }

            override fun onFailure(call: Call<List<GameDetail>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun updateGameWinner(gameId: Int, winner: String?, callback: (Result<Unit>) -> Unit) {
        val update = GameUpdate(game_id = gameId, status = "COMPLETED", winner = winner)
        apiService.updateGame(update).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback(Result.success(Unit))
                } else {
                    callback(Result.failure(Exception("Failed to update game winner: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    fun getMoves(gameId: Int, callback: (Result<List<MoveDetail>>) -> Unit) {
        apiService.getMoves(gameId).enqueue(object : Callback<List<MoveDetail>> {
            override fun onResponse(call: Call<List<MoveDetail>>, response: Response<List<MoveDetail>>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body() ?: emptyList()))
                } else {
                    callback(Result.failure(Exception("Failed to fetch moves")))
                }
            }

            override fun onFailure(call: Call<List<MoveDetail>>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
