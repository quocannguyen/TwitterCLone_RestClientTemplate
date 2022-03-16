package com.codepath.apps.twitterclone.models

import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.*
import com.codepath.apps.twitterclone.TimeFormatter
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"])])
data class Tweet (
    @ColumnInfo
    @PrimaryKey
    val id: Long,
//    val favoriteCount: Int,
//    val retweetCount: Int,
//    val favorited: Boolean,
//    val retweeted: Boolean,
    @ColumnInfo
    val body: String,
    @ColumnInfo
    val createdAt: String,
    @Ignore
    var user: User? = null,
    @Ignore
    val media: List<Media>,
) : Parcelable {

    constructor(id: Long, body: String, createdAt: String) : this(id, body, createdAt, null, ArrayList<Media>())

    @ColumnInfo
    var userId: Long? = null

    companion object {
        fun fromJsonObject(jsonObject: JSONObject): Tweet {
            val tweet = Tweet (
                id = jsonObject.getLong("id"),
                body = jsonObject.getString("text"),
                createdAt = jsonObject.getString("created_at"),
                user = User.fromJson(jsonObject.getJSONObject("user")),
//                favoriteCount = jsonObject.getInt("favorite_count"),
//                retweetCount = jsonObject.getInt("retweet_count"),
//                favorited = jsonObject.getBoolean("favorited"),
//                retweeted = jsonObject.getBoolean("retweeted"),
                media = ArrayList()
            )

            tweet.userId = tweet.user?.id
            if (jsonObject.has("extended_entities")) {
                val mediaJsonArray = jsonObject.getJSONObject("extended_entities").getJSONArray("media")
                (tweet.media as ArrayList<Media>).addAll(Media.fromJsonArray(mediaJsonArray))
            }

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJsonObject(jsonArray.getJSONObject(i)))
            }
            return tweets
        }

        fun getMinIdFromArray(tweets: List<Tweet>, minId: Long): Long {
            return if (tweets.isEmpty()) {
                minId
            } else {
                var newMinId: Long = tweets[0].id
                for (tweet in tweets) {
                    if (tweet.id < newMinId) {
                        newMinId = tweet.id
                    }
                }
                newMinId
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getFormattedTimestamp(): String {
        return TimeFormatter.getTimeDifference(createdAt)
    }
}