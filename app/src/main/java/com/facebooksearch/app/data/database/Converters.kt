package com.facebooksearch.app.data.database

import androidx.room.TypeConverter
import com.facebooksearch.app.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(value: String): MessageStatus = MessageStatus.valueOf(value)

    @TypeConverter
    fun fromSocialPlatform(platform: SocialPlatform): String = platform.name

    @TypeConverter
    fun toSocialPlatform(value: String): SocialPlatform = SocialPlatform.valueOf(value)

    @TypeConverter
    fun fromSocialPlatformList(platforms: List<SocialPlatform>): String = gson.toJson(platforms.map { it.name })

    @TypeConverter
    fun toSocialPlatformList(value: String): List<SocialPlatform> {
        val type = object : TypeToken<List<String>>() {}.type
        val names: List<String> = gson.fromJson(value, type) ?: emptyList()
        return names.map { SocialPlatform.valueOf(it) }
    }

    @TypeConverter
    fun fromExtensionCategory(category: ExtensionCategory): String = category.name

    @TypeConverter
    fun toExtensionCategory(value: String): ExtensionCategory = ExtensionCategory.valueOf(value)

    @TypeConverter
    fun fromFriendRequestFilter(filter: FriendRequestFilter): String = gson.toJson(filter)

    @TypeConverter
    fun toFriendRequestFilter(value: String): FriendRequestFilter {
        return gson.fromJson(value, FriendRequestFilter::class.java) ?: FriendRequestFilter()
    }

    @TypeConverter
    fun fromSortOption(option: SortOption): String = option.name

    @TypeConverter
    fun toSortOption(value: String): SortOption = SortOption.valueOf(value)
}
