package com.facebooksearch.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.facebooksearch.app.data.model.*

@Database(
    entities = [
        FriendRequest::class,
        FilterPreset::class,
        User::class,
        ConnectedAccount::class,
        Extension::class,
        ExtensionSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun filterPresetDao(): FilterPresetDao
    abstract fun userDao(): UserDao
    abstract fun extensionDao(): ExtensionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "facebook_search_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
