package com.codepath.apps.twitterclone.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.codepath.apps.twitterclone.R
import com.codepath.apps.twitterclone.TwitterApplication
import com.codepath.apps.twitterclone.TwitterClient
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.lang.Exception

class ReplyActivity : AppCompatActivity() {
    lateinit var tweet: Tweet
    lateinit var client: TwitterClient
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        tweet = intent.getParcelableExtra("tweet")

        client = TwitterApplication.getTwitterClient(this)

        etCompose = findViewById(R.id.etCompose)
        btnTweet = findViewById(R.id.btnTweet)

        bindTweet()
        setButtonTweet()
        addCharacterCounter()
    }

    private fun setButtonTweet() {
        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {
            // Grab the content of editTxt (etCompose)
            val tweetContent = etCompose.text.toString()

            client.publishTweet(object: JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                ) {
                    Log.e("peter", "ReplyActivity setOnClickListener publishTweet onFailure: $statusCode", throwable)
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    Log.d("peter", "ReplyActivity setOnClickListener publishTweet onSuccess: Successfully published tweet!", )
                    val tweet = Tweet.fromJsonObject(json.jsonObject)

                    val intent = Intent()
                    intent.putExtra("tweet", tweet)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }, tweetContent, tweet.id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bindTweet() {
        findViewById<TextView>(R.id.tvUsername).text = tweet.user?.name
        findViewById<TextView>(R.id.tvHandle).text = "@${tweet.user?.screenName}"
        findViewById<TextView>(R.id.tvTweetBody).text = tweet.body
        findViewById<TextView>(R.id.tvTimeStamp).text = tweet.getFormattedTimestamp()
        findViewById<TextView>(R.id.tvReplyingTo).text = "Replying to @${tweet.user?.screenName}"
        Glide.with(this).load(tweet.user?.profileImageUrl).into(findViewById(R.id.ivProfileImage))
    }

    private fun addCharacterCounter() {
        val tvCharacterCount = findViewById<TextView>(R.id.tvCharacterCount)

        etCompose.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val characterCount = p0?.length
                tvCharacterCount.text = "$characterCount/280"
                if (characterCount != null) {
                    btnTweet.isEnabled = characterCount in 1..280
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })
    }
}