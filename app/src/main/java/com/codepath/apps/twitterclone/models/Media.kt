package com.codepath.apps.twitterclone.models

import android.os.Parcelable
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject

@Parcelize
//@Entity
data class Media (
//    @ColumnInfo
//    @PrimaryKey
    val id: Long,
//    @ColumnInfo
    val mediaUrl: String,
//    @ColumnInfo
    val type: MediaType,
//    @ColumnInfo
    val videoVariants: List<VideoVariant>
) : Parcelable {

    companion object {
        enum class MediaType {
            ELSE, PHOTO, VIDEO
        }

        fun fromJsonObject(jsonObject: JSONObject): Media {
            Log.d("peter", "Media fromJsonObject: $jsonObject")
            val media = Media (
                id = jsonObject.getLong("id"),
                mediaUrl = jsonObject.getString("media_url_https"),
                type = when (jsonObject.getString("type")) {
                    "photo" -> MediaType.PHOTO
                    "video" -> MediaType.VIDEO
                    else -> {
                        Log.d("peter", "Media fromJsonObject type = else: $jsonObject")
                        MediaType.ELSE
                    }
                },
                videoVariants = ArrayList()
            )

            if (media.type == MediaType.VIDEO) {
                val videoInfoJsonObject = jsonObject.getJSONObject("video_info")
                val variantJsonArray = videoInfoJsonObject.getJSONArray("variants")
                (media.videoVariants as ArrayList).addAll(VideoVariant.fromJsonArray(variantJsonArray))
            }

            return media
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Media> {
            val mediaList = ArrayList<Media>()
            for (i in 0 until jsonArray.length()) {
                mediaList.add(fromJsonObject(jsonArray.getJSONObject(i)))
            }
            return mediaList
        }
    }
}