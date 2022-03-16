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
data class VideoVariant (
//    @ColumnInfo
    val bitRate: BitRate,
//    @ColumnInfo
    val url: String
) : Parcelable {

//    @ColumnInfo
//    @PrimaryKey(autoGenerate = true)
//    var id: Long? = null

    companion object {
        enum class BitRate {
            ELSE,
            _432,
            _832,
            _1280
        }

        fun fromJsonObject(jsonObject: JSONObject) : VideoVariant {
            Log.d("peter", "VideoVariant fromJsonObject: $jsonObject")
            return VideoVariant (
                bitRate = when (jsonObject.getInt("bitrate")) {
                    432000 -> BitRate._432
                    832000 -> BitRate._832
                    1280000 -> BitRate._1280
                    else -> BitRate.ELSE
                },
                url = jsonObject.getString("url")
            )
        }

        fun fromJsonArray(jsonArray: JSONArray) : List<VideoVariant> {
            val videoVariants = ArrayList<VideoVariant>()
            for (i in 0 until jsonArray.length()) {
                val videoVariantJsonObject = jsonArray.getJSONObject(i)
                if (videoVariantJsonObject.getString("content_type") == "video/mp4") {
                    val videoVariant = fromJsonObject(videoVariantJsonObject)
                    videoVariants.add(videoVariant)
                }
            }
            return videoVariants
        }

        fun getHighestBitRateUrl(videoVariants: List<VideoVariant>): String {
            var highestBitRateVariant = videoVariants[0]
            for (i in 1 until videoVariants.size) {
                val videoVariant = videoVariants[i]
                if (videoVariant.bitRate.ordinal > highestBitRateVariant.bitRate.ordinal) {
                    highestBitRateVariant = videoVariant
                }
            }
            return highestBitRateVariant.url
        }
    }
}