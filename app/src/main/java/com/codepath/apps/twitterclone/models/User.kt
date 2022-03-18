package com.codepath.apps.twitterclone.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
@Entity
data class User (
    @ColumnInfo
    @PrimaryKey
    val id: Long,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val screenName: String,
    @ColumnInfo
    val profileImageUrl: String,
    var verified: Boolean
) : Parcelable {

    companion object {
        fun fromJson(jsonObject: JSONObject): User {
            return User (
                id = jsonObject.getLong("id"),
                name = jsonObject.getString("name"),
                screenName = jsonObject.getString("screen_name"),
                profileImageUrl = jsonObject.getString("profile_image_url_https"),
                verified = jsonObject.getBoolean("verified")
            )
        }

        fun fromTweetArray(tweetsFromNetwork: List<Tweet>): List<User> {
            val users = ArrayList<User>()
            for (tweet in tweetsFromNetwork) {
                tweet.user?.let { users.add(it) }
            }
            return users
        }
    }
}