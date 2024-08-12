//viewModel.kt
package com.example.tictactoe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameViewModel : ViewModel() {
    private val _games = MutableLiveData<List<GameDetail>>()
    val games: LiveData<List<GameDetail>> get() = _games

    private val _moves = MutableLiveData<List<MoveDetail>>()
    val moves: LiveData<List<MoveDetail>> get() = _moves


    private val apiService = RetrofitClient.api
    private val repository = GameRepository()

    fun insertGame(size: Int, onResult: (Result<Int>) -> Unit) {
        val game = Game(id = 0, size = size, winner = "None", title = "None") // id is not needed for POST, but required in data class
        Log.d("GameViewModel", "Inserting game with size: $size")

        apiService.insertGame(game).enqueue(object : Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { result ->
                        Log.d("GameViewModel", "Game inserted with ID: ${result.id}")
                        onResult(Result.success(result.id))
                    } ?: run {
                        Log.e("GameViewModel", "Response body is null")
                        onResult(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    Log.e("GameViewModel", "Failed to insert game: ${response.message()}")
                    onResult(Result.failure(Exception("Failed to insert game: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                Log.e("GameViewModel", "Failed to insert game: ${t.message}", t)
                onResult(Result.failure(t))
            }
        })
    }

    fun insertMove(gameId: Int, player: String, row: Int, col: Int, onResult: (Result<Unit>) -> Unit) {
        val move = Move(game_id = gameId, player = player, row = row, col = col)
        apiService.insertMove(move).enqueue(object : Callback<MoveResponse> {
            override fun onResponse(call: Call<MoveResponse>, response: Response<MoveResponse>) {
                if (response.isSuccessful) {
                    Log.d("GameViewModel", "Move inserted successfully for game ID: $gameId")
                    onResult(Result.success(Unit))
                } else {
                    Log.e("GameViewModel", "Failed to insert move: ${response.message()}")
                    onResult(Result.failure(Exception("Failed to insert move: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<MoveResponse>, t: Throwable) {
                Log.e("GameViewModel", "Failed to insert move: ${t.message}", t)
                onResult(Result.failure(t))
            }
        })
    }

    fun getGames(callback: (Result<List<GameDetail>>) -> Unit) {
        // Make API call to fetch game history using Retrofit
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
        viewModelScope.launch {
            try {
                repository.updateGameWinner(gameId, winner)
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

//    fun fetchGames() {
//        apiService.getGames().enqueue(object : Callback<List<GameDetail>> {
//            override fun onResponse(call: Call<List<GameDetail>>, response: Response<List<GameDetail>>) {
//                if (response.isSuccessful) {
//                    _games.value = response.body()
//                    Log.d("GameViewModel", "Games fetched successfully: ${response.body()}")
//                } else {
//                    Log.e("GameViewModel", "Failed to fetch games: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<GameDetail>>, t: Throwable) {
//                Log.e("GameViewModel", "Error fetching games", t)
//            }
//        })
//    }

    fun fetchMoves(gameId: Int) {
        apiService.getMoves(gameId).enqueue(object : Callback<List<MoveDetail>> {
            override fun onResponse(call: Call<List<MoveDetail>>, response: Response<List<MoveDetail>>) {
                if (response.isSuccessful) {
                    _moves.value = response.body()
                    Log.d("GameViewModel", "Moves fetched successfully: ${response.body()}")
                } else {
                    Log.e("GameViewModel", "Failed to fetch moves: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<MoveDetail>>, t: Throwable) {
                Log.e("GameViewModel", "Error fetching moves", t)
            }
        })
    }
}
