package com.codepath.apps.twitterclone.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.codepath.apps.twitterclone.R
import com.codepath.apps.twitterclone.TWEET_EXTRA
import com.codepath.apps.twitterclone.models.Media
import com.codepath.apps.twitterclone.models.Tweet

class TweetDetailActivity : AppCompatActivity() {

    lateinit var tweet: Tweet

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet_detail)

        tweet = intent.getParcelableExtra<Tweet>(TWEET_EXTRA)
        bindTweet()
        setButtonReply()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bindTweet() {
        findViewById<TextView>(R.id.tvUsername).text = tweet.user?.name
        findViewById<TextView>(R.id.tvHandle).text = '@' + tweet.user?.screenName.toString()
        findViewById<TextView>(R.id.tvTweetBody).text = tweet.body
        findViewById<TextView>(R.id.tvTimeStamp).text = tweet.getFormattedTimestamp()
        Glide.with(this).load(tweet.user?.profileImageUrl).into(findViewById(R.id.ivProfileImage))

        if (tweet.media.isNotEmpty()) {
            if (tweet.media[0].type == Media.Companion.MediaType.PHOTO) {
                Glide.with(this).load(tweet.media[0].mediaUrl).into(findViewById(R.id.ivPhoto))
            }
        }
    }

    private fun setButtonReply() {
        findViewById<Button>(R.id.btnTweet).setOnClickListener {
            val intent = Intent(this, ReplyActivity::class.java)
            intent.putExtra("tweet", tweet)
            startActivity(intent)
        }
    }
}