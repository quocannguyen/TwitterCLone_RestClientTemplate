package com.codepath.apps.twitterclone

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codepath.apps.twitterclone.models.*

@Database(entities = [SampleModel::class, Tweet::class, User::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract fun sampleModelDao(): SampleModelDao?

    abstract fun tweetDao(): TweetDao

    companion object {
        // Database name to be used
        const val NAME = "MyDatabase"
    }
}