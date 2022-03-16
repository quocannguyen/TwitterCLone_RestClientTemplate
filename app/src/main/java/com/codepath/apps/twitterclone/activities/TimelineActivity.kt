package com.codepath.apps.twitterclone.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.twitterclone.*
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.apps.twitterclone.models.TweetDao
import com.codepath.apps.twitterclone.models.TweetWithUser
import com.codepath.apps.twitterclone.models.User
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    private val tweets = ArrayList<Tweet>()
    var minId: Long = 0
    lateinit var client: TwitterClient
    lateinit var rvTimeline: RecyclerView
    lateinit var adapter: TweetAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var swipeContainer: SwipeRefreshLayout
    // Store a member variable for the listener
    lateinit var scrollListener: EndlessRecyclerViewScrollListener
    lateinit var tweetDao: TweetDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        client = TwitterApplication.getTwitterClient(this)
        rvTimeline = findViewById(R.id.rvTimeline)
        adapter = TweetAdapter(tweets)
        linearLayoutManager = LinearLayoutManager(this)
        rvTimeline.layoutManager = linearLayoutManager
        rvTimeline.adapter = adapter

        try {
            tweetDao = (applicationContext as TwitterApplication).myDatabase?.tweetDao()!!
        } catch (e: Exception) {
            Log.e("peter", "TimelineActivity onCreate tweetDao: $e")
        }

        setUpScrollListener()
        setUpSwipeRefresh()
        setUpFloatingActionButtonAdd()

        populateHomeTimeline()
    }

    // Handles clicks on menu items
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.compose) {
            Toast.makeText(this, "Ready to compose tweet!", Toast.LENGTH_LONG).show()
            // Navigate to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, RC_COMPOSE)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == RC_COMPOSE) {
            // Extract name value from result extras
            val tweet = data?.getParcelableExtra<Tweet>("tweet")
            // Update timeline
            // Modifying the data source of tweets
            if (tweet != null) {
                tweets.add(0, tweet)
                // Update adapter
                adapter.notifyItemInserted(0)
                rvTimeline.smoothScrollToPosition(0)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUpSwipeRefresh() {
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer)
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here. Make sure you call swipeContainer.setRefreshing(false) once the network request has completed successfully.
            populateHomeTimeline()
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
    }

    private fun setUpScrollListener() {
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadMoreData()
            }
        }
        // Adds the scroll listener to RecyclerView
        rvTimeline.addOnScrollListener(scrollListener)
    }

    private fun setUpFloatingActionButtonAdd() {
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            Toast.makeText(this, "Ready to compose tweet!", Toast.LENGTH_LONG).show()
            // Navigate to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, RC_COMPOSE)
        }
    }

    private fun fetchTimelineAsync(page: Int) {
        try {
            if (internetIsConnected()) {
                // Send the network request to fetch the updated data `client` here is an instance of Android Async HTTP getHomeTimeline is an example endpoint.
                client.getHomeTimeline(object: JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        // Remember to CLEAR OUT old items before appending in the new ones
                        adapter.clear()
                        try {
                            // ...the data has come back, add new items to your adapter...
                            val jsonArray = json.jsonArray
                            val tweetsFromNetwork = Tweet.fromJsonArray(jsonArray)
                            adapter.addAll(tweetsFromNetwork)
                            minId = Tweet.getMinIdFromArray(tweetsFromNetwork, minId)
                            // Now we call setRefreshing(false) to signal refresh has finished
                            swipeContainer.isRefreshing = false

                            try {
                                AsyncTask.execute {
                                    Log.d("peter", "TimelineActivity fetchTimelineAsync onSuccess tweetDao: Saving data into database")
                                    // Insert users first
                                    val usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork)
                                    tweetDao.insertModel(*usersFromNetwork.toTypedArray())
                                    // Insert tweets next
                                    tweetDao.insertModel(*tweetsFromNetwork.toTypedArray())
                                }
                            } catch (e: Exception) {
                                Log.e("peter", "TimelineActivity fetchTimelineAsync onSuccess AsyncTask.execute { tweetDao}: $e", )
                            }
                        } catch (e: JSONException) {
                            Log.e("peter", "TimelineActivity fetchTimelineAsync onSuccess: $e")
                        }
                    }

                    override fun onFailure(
                        statusCode: Int, headers: Headers, response: String, throwable: Throwable
                    ) {
                        Log.e("peter", "Fetch timeline error $statusCode", throwable)
                    }
                })
            }
        } catch (e: Exception) {
            Log.e("peter", "TimelineActivity fetchTimelineAsync: $e")
        }
    }


    // this is where we will make another API call to get the next page of tweets and add the objects to our current list of tweets
    fun loadMoreData() {
        // 1. Send an API request to retrieve appropriate paginated data
        client.getNextPageOfTweets(object: JsonHttpResponseHandler() {
            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?
            ) {
                Log.e("peter", "TimelineActivity loadMoreData client.getNextPageOfTweets onFailure: $statusCode", throwable)
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                try {
                    val jsonArray = json.jsonArray
                    // 2. Deserialize and construct new model objects from the API response
                    val retrievedTweets = Tweet.fromJsonArray(jsonArray)
                    // 3. Append the new data objects to the existing set of items inside the array of items
                    // 4. Notify the adapter of the new items made with `notifyItemRangeInserted()`
                    adapter.addAll(retrievedTweets)
                    minId = Tweet.getMinIdFromArray(retrievedTweets, minId)
                } catch (e: JSONException) {
                    Log.e("peter", "TimelineActivity loadMoreData onSuccess: JSONException: $e")
                }
            }

        }, minId - 1)
    }

    fun showComposeDialog() {
        val composeDialogFragment = ComposeFragment.newInstance("Compose New Tweet")
        composeDialogFragment.show(supportFragmentManager, "peter")
    }

    fun internetIsConnected(): Boolean {
        return true
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: java.lang.Exception) {
            false
        }
    }

    fun populateFromDatabase() {
        // Query for existing tweets in the database
        try {
            AsyncTask.execute {
                Log.d("peter", "TimelineActivity onCreate tweetDao.recentItems: Showing data from database")
                val tweetsWithUsers = tweetDao.recentItems()
                val tweetsFromDB = TweetWithUser.getTweets(tweetsWithUsers)
                adapter.clear()
                adapter.addAll(tweetsFromDB)
            }
        } catch (e: Exception) {
            Log.e("peter", "TimelineActivity onCreate tweetDao.recentItems: $e", )
        }

    }

    fun populateHomeTimeline() {
        if (!internetIsConnected()) {
            Log.d("peter", "TimelineActivity populateHomeTimeline internetIsConnected: false")
            populateFromDatabase()
        } else {
            Log.d("peter", "TimelineActivity populateHomeTimeline internetIsConnected: true")
            fetchTimelineAsync(0)
        }
    }

    companion object {
        val RC_COMPOSE = 1
    }
}