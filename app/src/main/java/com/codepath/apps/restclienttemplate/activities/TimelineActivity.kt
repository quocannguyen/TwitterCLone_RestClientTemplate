package com.codepath.apps.restclienttemplate.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.R
import com.codepath.apps.restclienttemplate.TweetAdapter
import com.codepath.apps.restclienttemplate.TwitterApplication
import com.codepath.apps.restclienttemplate.TwitterClient
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener

class TimelineActivity : AppCompatActivity() {

    lateinit var client: TwitterClient
    lateinit var rvTimeline: RecyclerView
    lateinit var adapter: TweetAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    val tweets = ArrayList<Tweet>()
    lateinit var swipeContainer: SwipeRefreshLayout

    // Store a member variable for the listener
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    var maxId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getTwitterClient(this)

        rvTimeline = findViewById(R.id.rvTimeline)
        adapter = TweetAdapter(tweets)
        linearLayoutManager = LinearLayoutManager(this)
        rvTimeline.layoutManager = linearLayoutManager
        rvTimeline.adapter = adapter

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.d("peter", "onLoadMore: ")
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData()
            }
        }
        // Adds the scroll listener to RecyclerView
//        rvTimeline.addOnScrollListener(scrollListener)

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer)
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "onCreate: Refreshing timeline")

            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchTimelineAsync(0)
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);

//        populateHomeTimeline()
        fetchTimelineAsync(0)
    }

    fun fetchTimelineAsync(page: Int) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(object: JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: okhttp3.Headers, json: JSON) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear()
                // ...the data has come back, add new items to your adapter...
                try {
                    val jsonArray = json.jsonArray
                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    adapter.addAll(retrievedTweets)
                    maxId = Tweet.getMaxIdFromArray(retrievedTweets)
                    Log.d("peter", "onSuccess: $maxId")
                } catch (e: JSONException) {
                    Log.e(TAG, "onSuccess: $e")
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.isRefreshing = false
            }

            override fun onFailure(
                statusCode: Int, headers: okhttp3.Headers, response: String, throwable: Throwable
            ) {
                Log.d("DEBUG", "Fetch timeline error $statusCode", throwable)
            }
        })
    }


    // this is where we will make another API call to get the next page of tweets and add the objects to our current list of tweets
    fun loadMoreData() {
        Log.d("peter", "loadMoreData: ")
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(object: JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
            ) {
                Log.e(TAG, "onFailure: $statusCode", throwable)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                try {
                    val jsonArray = json.jsonArray
                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    adapter.addAll(retrievedTweets)
                    maxId = Tweet.getMaxIdFromArray(retrievedTweets)
                } catch (e: JSONException) {
                    Log.e(TAG, "onSuccess: $e")
                }
            }

        }, maxId)
        // 2. Deserialize and construct new model objects from the API response
        // 3. Append the new data objects to the existing set of items inside the array of items
        // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    companion object {
        const val TAG = "TimelineActivity"
    }
}