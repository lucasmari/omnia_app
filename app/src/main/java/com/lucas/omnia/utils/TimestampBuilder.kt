package com.lucas.omnia.utils

import android.content.res.Resources
import com.lucas.omnia.R
import com.lucas.omnia.models.Comment
import com.lucas.omnia.models.Post
import com.lucas.omnia.models.Reply
import java.util.*

object TimestampBuilder {
    private var date = Date()

    fun getPostTime(post: Post): Long {
        return date.time - post.timestamp
    }

    fun getCommentTime(comment: Comment): Long {
        return date.time - comment.timestamp
    }

    fun getReplyTime(reply: Reply): Long {
        return date.time - reply.timestamp
    }

    fun getTimestamp(timeDiff: Long): String {
        val diffSeconds = timeDiff / 1000
        val diffMinutes = timeDiff / (60 * 1000)
        val diffHours = timeDiff / (60 * 60 * 1000)
        val diffDays = timeDiff / (60 * 60 * 1000 * 24)
        val diffWeeks = timeDiff / (60 * 60 * 1000 * 24 * 7)
        val diffMonths = (timeDiff / (60 * 60 * 1000 * 24 * 30.41666666)).toLong()
        val diffYears = timeDiff / (60.toLong() * 60 * 1000 * 24 * 365)
        return when {
            diffSeconds < 1 -> {
                App.context!!.resources.getString(R.string.timestamp_builder_less_text)
            }
            diffMinutes < 1 -> {
                "$diffSeconds " + App.context!!.resources.getString(R.string
                    .timestamp_builder_seconds_text)
            }
            diffHours < 1 -> {
                "$diffMinutes " + App.context!!.resources.getString(R.string
                    .timestamp_builder_minutes_text)
            }
            diffDays < 1 -> {
                "$diffHours " + App.context!!.resources.getString(R.string
                    .timestamp_builder_hours_text)
            }
            diffWeeks < 1 -> {
                "$diffDays " + App.context!!.resources.getString(R.string.timestamp_builder_days_text)
            }
            diffMonths < 1 -> {
                "$diffWeeks " + App.context!!.resources.getString(R.string
                    .timestamp_builder_weeks_text)
            }
            diffYears < 1 -> {
                "$diffMonths " + App.context!!.resources.getString(R.string
                    .timestamp_builder_months_text)
            }
            else -> {
                "$diffYears " + App.context!!.resources.getString(R.string
                    .timestamp_builder_years_text)
            }
        }
    }
}