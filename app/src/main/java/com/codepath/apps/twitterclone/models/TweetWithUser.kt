package com.codepath.apps.twitterclone.models

import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity

class TweetWithUser {
    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation
    @Embedded(prefix = "")
    lateinit var user: User
    @Embedded(prefix = "tweet_")
    lateinit var tweet: Tweet

    companion object {
        fun getTweets(tweetsWithUsers: List<TweetWithUser>) : List<Tweet> {
            val tweets = ArrayList<Tweet>()

            for (tweetWithUser in tweetsWithUsers) {
                val tweet = tweetWithUser.tweet
                try {
                    tweet.user = tweetWithUser.user
                } catch (e: Exception) {
                    Log.e("peter", "TweetWithUser getTweets getUser: $e", )
                }
                tweets.add(tweet)
            }

            return tweets
        }
    }
}