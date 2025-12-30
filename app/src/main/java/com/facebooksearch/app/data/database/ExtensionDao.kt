package com.facebooksearch.app.data.database

import androidx.room.*
import com.facebooksearch.app.data.model.Extension
import com.facebooksearch.app.data.model.ExtensionCategory
import com.facebooksearch.app.data.model.ExtensionSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtensionDao {
    @Query("SELECT * FROM extensions ORDER BY name ASC")
    fun getAllExtensions(): Flow<List<Extension>>

    @Query("SELECT * FROM extensions WHERE isInstalled = 1 ORDER BY name ASC")
    fun getInstalledExtensions(): Flow<List<Extension>>

    @Query("SELECT * FROM extensions WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledExtensions(): Flow<List<Extension>>

    @Query("SELECT * FROM extensions WHERE category = :category ORDER BY rating DESC")
    fun getByCategory(category: ExtensionCategory): Flow<List<Extension>>

    @Query("SELECT * FROM extensions WHERE id = :id")
    suspend fun getById(id: String): Extension?

    @Query("SELECT * FROM extensions WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Extension>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(extension: Extension)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(extensions: List<Extension>)

    @Update
    suspend fun update(extension: Extension)

    @Query("UPDATE extensions SET isInstalled = :isInstalled WHERE id = :id")
    suspend fun updateInstalled(id: String, isInstalled: Boolean)

    @Query("UPDATE extensions SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateEnabled(id: String, isEnabled: Boolean)

    @Delete
    suspend fun delete(extension: Extension)

    // Extension settings
    @Query("SELECT * FROM extension_settings WHERE extensionId = :extensionId")
    suspend fun getSettings(extensionId: String): ExtensionSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ExtensionSettings)

    @Query("DELETE FROM extension_settings WHERE extensionId = :extensionId")
    suspend fun deleteSettings(extensionId: String)
}
