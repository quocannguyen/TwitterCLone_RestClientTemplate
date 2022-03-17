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
import com.codepath.apps.twitterclone.fragments.SaveFragment
import com.codepath.apps.twitterclone.interfaces.SaveTweetDialogListener
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.io.*

const val FILE_NAME = "saved_draft"
class ComposeActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount: TextView
    var tweeted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        client = TwitterApplication.getTwitterClient(this)
        etCompose = findViewById(R.id.etCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharacterCount = findViewById(R.id.tvCharacterCount)

        setButtonTweet()
        addCharacterCounter()
        loadDraft()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (etCompose.text.isNotEmpty() && !tweeted) {
            getSaveDialogFragment()
        } else {
            saveDraft("")
            finish()
        }
    }

    private fun getSaveDialogFragment() {
        val saveDialogFragment = SaveFragment.newInstance()
        saveDialogFragment.saveTweetDialogListener = object: SaveTweetDialogListener {
            override fun onSaveDialog(isSaving: Boolean) {
                if (isSaving) {
                    saveDraft(etCompose.text.toString())
                } else {
                    saveDraft("")
                }
                finish()
            }
        }
        saveDialogFragment.show(supportFragmentManager, "peter")
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
                    tweeted = true
                    val tweet = Tweet.fromJsonObject(json.jsonObject)

                    val intent = Intent()
                    intent.putExtra("tweet", tweet)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }, tweetContent)
        }
    }

    fun saveDraft(draft: String) {
        val fileOutputStream: FileOutputStream = openFileOutput(FILE_NAME, MODE_PRIVATE)
        // Create buffered writer
        val writer = BufferedWriter(OutputStreamWriter(fileOutputStream))
        fileOutputStream.write(draft.toByteArray())
//        writer.write(etCompose.text.toString())
        writer.close()
        Toast.makeText(this, "Draft saved!", Toast.LENGTH_LONG).show()
    }

    fun loadDraft() {
        try {
            val fileInputStream = openFileInput(FILE_NAME)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while (run {
                    text = bufferedReader.readLine()
                    text
                } != null) {
                stringBuilder.append(text)
            }
            //Displaying data on EditText
            etCompose.setText(stringBuilder.toString()).toString()
        } catch (e: Exception) {
            Log.e("peter", "ComposeActivity loadDraft: $e", )
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