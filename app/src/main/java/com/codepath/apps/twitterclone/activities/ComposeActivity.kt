package com.codepath.apps.twitterclone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.twitterclone.R
import com.codepath.apps.twitterclone.TwitterApplication
import com.codepath.apps.twitterclone.TwitterClient
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.lang.Exception

class ComposeActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        client = TwitterApplication.getTwitterClient(this)

        etCompose = findViewById(R.id.etCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharacterCount = findViewById(R.id.tvCharacterCount)

        setButtonTweet()
        addCharacterCounter()
    }

    private fun setButtonTweet() {
        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {
            // Grab the content of editText (etCompose)
            val tweetContent = etCompose.text.toString()

            client.publishTweet(object: JsonHttpResponseHandler() {
                override fun onFailure(
                    statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
                ) {
                    Log.e("peter", "ComposeActivity setOnClickListener publishTweet onFailure: $statusCode", throwable)
                }

                override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                    val tweet = Tweet.fromJsonObject(json.jsonObject)

                    val intent = Intent()
                    intent.putExtra("tweet", tweet)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }, tweetContent)
        }
    }

    private fun addCharacterCounter() {
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