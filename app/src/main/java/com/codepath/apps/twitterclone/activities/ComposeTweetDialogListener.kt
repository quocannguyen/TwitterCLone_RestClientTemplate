package com.codepath.apps.twitterclone.activities

import com.codepath.apps.twitterclone.models.Tweet

interface ComposeTweetDialogListener {
    fun onFinishComposeDialog(tweet: Tweet)
}