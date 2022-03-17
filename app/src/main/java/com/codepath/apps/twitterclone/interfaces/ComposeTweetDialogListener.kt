package com.codepath.apps.twitterclone.interfaces

import com.codepath.apps.twitterclone.models.Tweet

interface ComposeTweetDialogListener {
    fun onFinishComposeDialog(tweet: Tweet)
}