package com.codepath.apps.restclienttemplate.models

import android.os.Build
import androidx.annotation.RequiresApi
import com.codepath.apps.restclienttemplate.TimeFormatter
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.max

class Tweet {

    var id: Long = 0
    lateinit var body: String
    lateinit var createdAt: String
    lateinit var user: User

    companion object {
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()

            tweet.id = jsonObject.getLong("id")
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))

            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()

            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }

            return tweets
        }

        fun getMaxIdFromArray(tweets: List<Tweet>): Long {
            var maxId: Long = 0

            for (i in 0 until tweets.size) {
                val tweet = tweets[i]
                val id = tweet.id
                if (id > maxId) {
                    maxId = id
                }
            }

            return maxId
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getFormattedTimestamp(): String {
        return TimeFormatter.getTimeDifference(createdAt)
    }
}