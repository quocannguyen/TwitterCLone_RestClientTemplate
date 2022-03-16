package com.codepath.apps.twitterclone.models

import androidx.room.Embedded

class TweetWithUser {
    // @Embedded notation flattens the properties of the User object into the object, preserving encapsulation
    @Embedded(prefix = "user_")
    lateinit var user: User
    @Embedded(prefix = "tweet_")
    lateinit var tweet: Tweet

    companion object {
        fun getTweets(tweetsWithUsers: List<TweetWithUser>) : List<Tweet> {
            val tweets = ArrayList<Tweet>()

            for (tweetWithUser in tweetsWithUsers) {
                val tweet = tweetWithUser.tweet
                tweet.user = tweetWithUser.user
                tweets.add(tweet)
            }

            return tweets
        }
    }
}