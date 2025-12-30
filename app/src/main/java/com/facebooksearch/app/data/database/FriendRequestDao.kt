package com.facebooksearch.app.data.database

import androidx.room.*
import com.facebooksearch.app.data.model.FriendRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRequestDao {
    @Query("SELECT * FROM friend_requests ORDER BY requestDate DESC")
    fun getAllFriendRequests(): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE isHidden = 0 ORDER BY requestDate DESC")
    fun getVisibleFriendRequests(): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE isFavorited = 1 ORDER BY requestDate DESC")
    fun getFavoritedFriendRequests(): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE mutualFriendsCount >= :minCount ORDER BY mutualFriendsCount DESC")
    fun getByMinMutualFriends(minCount: Int): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE city = :city ORDER BY requestDate DESC")
    fun getByCity(city: String): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE city IN (:cities) ORDER BY requestDate DESC")
    fun getByCities(cities: List<String>): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE messageStatus = :status ORDER BY requestDate DESC")
    fun getByMessageStatus(status: String): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE messageStatus != 'NO_MESSAGE' ORDER BY requestDate DESC")
    fun getWithMessages(): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE hasCommentedOnMutualFriends = 1 ORDER BY requestDate DESC")
    fun getWhoCommentedOnFriends(): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY requestDate DESC")
    fun search(query: String): Flow<List<FriendRequest>>

    @Query("SELECT * FROM friend_requests WHERE id = :id")
    suspend fun getById(id: String): FriendRequest?

    @Query("SELECT COUNT(*) FROM friend_requests")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM friend_requests WHERE mutualFriendsCount > 0")
    fun getCountWithMutualFriends(): Flow<Int>

    @Query("SELECT COUNT(*) FROM friend_requests WHERE messageStatus != 'NO_MESSAGE'")
    fun getCountWithMessages(): Flow<Int>

    @Query("SELECT COUNT(*) FROM friend_requests WHERE requestDate > :timestamp")
    fun getCountSince(timestamp: Long): Flow<Int>

    @Query("SELECT DISTINCT city FROM friend_requests WHERE city IS NOT NULL ORDER BY city")
    fun getAllCities(): Flow<List<String>>

    @Query("SELECT DISTINCT location FROM friend_requests WHERE location IS NOT NULL ORDER BY location")
    fun getAllLocations(): Flow<List<String>>

    @Query("SELECT AVG(mutualFriendsCount) FROM friend_requests")
    fun getAverageMutualFriends(): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friendRequest: FriendRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(friendRequests: List<FriendRequest>)

    @Update
    suspend fun update(friendRequest: FriendRequest)

    @Query("UPDATE friend_requests SET isFavorited = :isFavorited WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorited: Boolean)

    @Query("UPDATE friend_requests SET isHidden = :isHidden WHERE id = :id")
    suspend fun updateHidden(id: String, isHidden: Boolean)

    @Query("UPDATE friend_requests SET notes = :notes WHERE id = :id")
    suspend fun updateNotes(id: String, notes: String?)

    @Delete
    suspend fun delete(friendRequest: FriendRequest)

    @Query("DELETE FROM friend_requests WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM friend_requests")
    suspend fun deleteAll()
}
