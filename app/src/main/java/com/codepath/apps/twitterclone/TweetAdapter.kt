package com.codepath.apps.twitterclone

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.twitterclone.activities.TweetDetailActivity
import com.codepath.apps.twitterclone.models.Media
import com.codepath.apps.twitterclone.models.Tweet

const val TWEET_EXTRA = "tweet_extra"
class TweetAdapter(private val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate item layout
        val view = inflater.inflate(R.layout.item_tweet, parent, false)
        return TweetViewHolder(view, context)
    }

    // Populate data into the item view through holder
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Get data model based on position
        val tweet = tweets[position]
        val tweetViewHolder = holder as TweetViewHolder
        tweetViewHolder.bindTweet(tweet)
    }

    override fun getItemCount(): Int {
        return tweets.size
    }

    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }
}