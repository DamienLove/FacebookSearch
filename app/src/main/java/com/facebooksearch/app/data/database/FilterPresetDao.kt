package com.facebooksearch.app.data.database

import androidx.room.*
import com.facebooksearch.app.data.model.FilterPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface FilterPresetDao {
    @Query("SELECT * FROM filter_presets ORDER BY createdAt DESC")
    fun getAllPresets(): Flow<List<FilterPreset>>

    @Query("SELECT * FROM filter_presets WHERE id = :id")
    suspend fun getById(id: Long): FilterPreset?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: FilterPreset): Long

    @Update
    suspend fun update(preset: FilterPreset)

    @Delete
    suspend fun delete(preset: FilterPreset)

    @Query("DELETE FROM filter_presets WHERE id = :id")
    suspend fun deleteById(id: Long)
}
