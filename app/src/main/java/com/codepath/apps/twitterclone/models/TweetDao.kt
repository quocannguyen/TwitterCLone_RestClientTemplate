package com.codepath.apps.twitterclone.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TweetDao {

    // @Query annotation requires knowing SQL syntax
    // See http://www.sqltutorial.org/
    @Query("SELECT * FROM SampleModel WHERE id = :id")
    fun byId(id: Long): SampleModel?

    @Query("SELECT Tweet.body AS tweet_body, Tweet.createdAt AS tweet_createdAt, Tweet.id AS tweet_id, User.* " +
            "FROM Tweet INNER JOIN User ON Tweet.userID = User.id " +
            "ORDER BY Tweet.createdAt DESC " +
            "LIMIT 5")
    fun recentItems(): List<TweetWithUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(vararg tweets: Tweet?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertModel(vararg users: User?)

    @Query("DELETE FROM Tweet")
    fun deleteAllTweets()
}