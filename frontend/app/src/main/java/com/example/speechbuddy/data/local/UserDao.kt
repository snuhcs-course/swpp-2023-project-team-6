package com.example.speechbuddy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.speechbuddy.data.local.models.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * The Data Access Object for users.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): Flow<UserEntity?>

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)
}