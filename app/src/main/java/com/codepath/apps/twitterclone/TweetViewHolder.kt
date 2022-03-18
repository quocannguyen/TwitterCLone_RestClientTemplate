package com.codepath.apps.twitterclone

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.twitterclone.activities.TweetDetailActivity
import com.codepath.apps.twitterclone.models.Media
import com.codepath.apps.twitterclone.models.Tweet
import com.codepath.apps.twitterclone.models.VideoVariant
import java.lang.Exception

class TweetViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
    val tvUsername = itemView.findViewById<TextView>(R.id.tvUsername)
    val tvHandle = itemView.findViewById<TextView>(R.id.tvHandle)
    val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
    val tvTimeStamp = itemView.findViewById<TextView>(R.id.tvTimeStamp)
    val ivPhoto = itemView.findViewById<ImageView>(R.id.ivPhoto)
    val vvVideo = itemView.findViewById<VideoView>(R.id.vvVideo)
    lateinit var tweet: Tweet

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val intent = Intent(context, TweetDetailActivity::class.java)
        intent.putExtra(TWEET_EXTRA, tweet)
        context.startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun bindTweet(tweet: Tweet) {
        this.tweet = tweet
        tvUsername.text = tweet.user?.name
        tvHandle.text = "@${tweet.user?.screenName}"
        tvTweetBody.text = tweet.body
        tvTimeStamp.text = tweet.getFormattedTimestamp()
        Glide.with(itemView).load(tweet.user?.profileImageUrl).into(ivProfileImage)
        if (tweet.media.isNotEmpty()) {
            for (media in tweet.media) {
//                Glide.with(itemView).load(media.mediaUrl).into(ivPhoto)
//                when (media.type) {
//                    Media.Companion.MediaType.PHOTO -> {
////                        Log.d("peter", "TweetViewHolder bindTweet type==PHOTO tweet.body: ${tweet.body}")
////                        Glide.with(itemView).load(media.mediaUrl).into(ivPhoto)
//                    }
//                    Media.Companion.MediaType.VIDEO -> {
//                        Log.d("peter", "TweetViewHolder bindTweet: $tweet")
//                        try {
//                            bindVideo(VideoVariant.getHighestBitRateUrl(media.videoVariants))
//                        } catch (e: Exception) {
//                            Log.e("peter", "TweetViewHolder bindTweet: $e",)
//                        }
//                    }
//                    else -> {}
//                }
            }
        } else {
            ivPhoto.setImageDrawable(null)
        }
    }

    fun bindVideo(videoUrl: String) {
        Log.d("peter", "TweetViewHolder bindVideo: $videoUrl")

        // Uri object to refer the resource from the videoUrl
        val uri: Uri = Uri.parse(videoUrl)
        // sets the resource from the videoUrl to the videoView
        vvVideo.setVideoURI(uri)
        // creating object of media controller class
        val mediaController = MediaController(context)
        // sets the anchor view anchor view for the videoView
        mediaController.setAnchorView(vvVideo)
        // sets the media player to the videoView
        mediaController.setMediaPlayer(vvVideo)
        // sets the media controller to the videoView
        vvVideo.setMediaController(mediaController)
        // starts the video
        vvVideo.start()
    }
}