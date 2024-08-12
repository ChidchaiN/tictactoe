package com.example.tictactoe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class GameViewModel : ViewModel() {
    private val _games = MutableLiveData<List<GameDetail>>()
    val games: LiveData<List<GameDetail>> get() = _games

    private val _moves = MutableLiveData<List<MoveDetail>>()
    val moves: LiveData<List<MoveDetail>> get() = _moves

    private val repository = GameRepository()
    private val apiService = RetrofitClient.api

    fun insertGame(size: Int, onResult: (Result<Int>) -> Unit) {
        repository.insertGame(size, onResult)
    }

    fun insertMove(gameId: Int, player: String, row: Int, col: Int, onResult: (Result<Unit>) -> Unit) {
        val move = Move(game_id = gameId, player = player, row = row, col = col)
        repository.insertMove(move, onResult)
    }

    fun getGames(callback: (Result<List<GameDetail>>) -> Unit) {
        repository.getGames(callback)
    }

    fun updateGameWinner(gameId: Int, winner: String?, callback: (Result<Unit>) -> Unit) {
        repository.updateGameWinner(gameId, winner, callback)
    }

    fun fetchMoves(gameId: Int) {
        repository.getMoves(gameId) { result ->
            result.onSuccess { moves ->
                _moves.postValue(moves)
            }.onFailure { exception ->
                Log.e("GameViewModel", "Error fetching moves", exception)
                _moves.postValue(emptyList())
            }
        }
    }


}
