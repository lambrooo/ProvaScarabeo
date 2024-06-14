package com.example.provascarabeo



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT id, username, score FROM users ORDER BY score DESC")
    suspend fun getAllUsers(): List<UserResult>
}