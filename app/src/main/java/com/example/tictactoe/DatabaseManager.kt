//DatabaseManager.kt
package com.example.tictactoe

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Data classes for API requests and responses
data class Game(
    val id: Int,
    val size: Int,
    val winner: String,
    val title: String // Ensure this property exists
)

data class Move(
    val game_id: Int,
    val player: String,
    val row: Int,
    val col: Int
)

data class GameResponse(
    val id: Int
)

data class MoveResponse(
    val message: String
)

data class GameDetail(
    val id: Int,
    val size: Int,
    val board: Array<Array<Player>>,
    val currentPlayer: Player,
    val winner: Player? = null,
    val title: String
)

data class MoveDetail(
    val game_id: Int,
    val player: String,
    val row: Int,
    val col: Int
)

data class GameUpdate(
    val game_id: Int,
    val status: String,
    val winner: String?
)


interface ApiService {
    @POST("insert_game")
    fun insertGame(@Body game: Game): Call<GameResponse>

    @POST("insert_move")
    fun insertMove(@Body move: Move): Call<MoveResponse>

    @GET("get_moves/{gameId}")
    fun getMoves(@Path("gameId") gameId: Int): Call<List<MoveDetail>>

    @GET("get_games")
    fun getGames(): Call<List<GameDetail>>

    @GET("get_game_details/{gameId}")
    fun getGameDetails(@Path("gameId") gameId: Int): Call<GameDetail>

    @POST("update_game")
    fun updateGame(@Body update: GameUpdate): Call<Void>
}




object RetrofitClient {
    private const val BASE_URL = "http://192.168.172.70:5000"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
