package com.codepath.apps.twitterclone

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.codepath.apps.twitterclone.activities.ComposeTweetDialogListener
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val FRAGMENT_TITLE = "title"

class ComposeFragment : DialogFragment() {

    lateinit var composeTweetDialogListener: ComposeTweetDialogListener
    lateinit var client: TwitterClient
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharacterCount: TextView
//    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            title = it.getString(FRAGMENT_TITLE)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get field from view
        client = TwitterApplication.getTwitterClient(view.context)
        etCompose = view.findViewById(R.id.etCompose)!!
        btnTweet = view.findViewById(R.id.btnTweet)!!
        tvCharacterCount = view.findViewById(R.id.tvCharacterCount)!!

        setButtonTweet()
        addCharacterCounter()
        // Fetch arguments from bundle and set title
        val title = arguments?.getString(FRAGMENT_TITLE, "Enter tweet")
        dialog?.setTitle(title)
        // Show soft keyboard automatically and request focus to field
        etCompose.requestFocus()
        dialog?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
        )
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
                    composeTweetDialogListener.onFinishComposeDialog(tweet)
                    dialog?.dismiss()
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

    companion object {
        fun newInstance(title: String) : ComposeFragment {
            val composeFragment = ComposeFragment()
//            val arguments = Bundle()
//            arguments.putString(FRAGMENT_TITLE, title)
//            composeFragment.arguments = arguments
//            composeFragment.setStyle(STYLE_NORMAL, R.style.Theme_RestClientTemplateKotlin)
            return composeFragment
        }
    }
}