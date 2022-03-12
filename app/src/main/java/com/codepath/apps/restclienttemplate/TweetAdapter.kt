package com.codepath.apps.restclienttemplate

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.activities.TweetDetailActivity
import com.codepath.apps.restclienttemplate.models.Tweet
import org.w3c.dom.Text

class TweetAdapter(val tweets: ArrayList<Tweet>) : RecyclerView.Adapter<TweetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
        val tvTimeStamp = itemView.findViewById<TextView>(R.id.tvTimeStamp)
        val tvId = itemView.findViewById<TextView>(R.id.tvId)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val intent = Intent(context, TweetDetailActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate item layout
        val view = inflater.inflate(R.layout.item_tweet, parent, false)

        return ViewHolder(view, context)
    }

    // Populate data into the item view through holder
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get data model based on position
        val tweet = tweets[position]
        //
        holder.tvUsername.text = tweet.user.name
        holder.tvTweetBody.text = tweet.body
        holder.tvTimeStamp.text = tweet.getFormattedTimestamp()
        holder.tvId.text = tweet.id.toString()
        Glide.with(holder.itemView).load(tweet.user.profileImageUrl).into(holder.ivProfileImage)
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