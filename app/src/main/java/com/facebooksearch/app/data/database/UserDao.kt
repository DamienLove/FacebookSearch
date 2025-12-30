package com.facebooksearch.app.data.database

import androidx.room.*
import com.facebooksearch.app.data.model.ConnectedAccount
import com.facebooksearch.app.data.model.SocialPlatform
import com.facebooksearch.app.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentUser(): Flow<User?>

    @Query("SELECT * FROM user_profile WHERE id = :id")
    suspend fun getUserById(id: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("UPDATE user_profile SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("DELETE FROM user_profile WHERE id = :id")
    suspend fun deleteById(id: String)

    // Connected accounts
    @Query("SELECT * FROM connected_accounts WHERE isConnected = 1")
    fun getConnectedAccounts(): Flow<List<ConnectedAccount>>

    @Query("SELECT * FROM connected_accounts WHERE platform = :platform")
    suspend fun getAccountByPlatform(platform: SocialPlatform): ConnectedAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: ConnectedAccount)

    @Query("UPDATE connected_accounts SET isConnected = 0 WHERE platform = :platform")
    suspend fun disconnectAccount(platform: SocialPlatform)

    @Query("DELETE FROM connected_accounts WHERE platform = :platform")
    suspend fun deleteAccount(platform: SocialPlatform)
}
