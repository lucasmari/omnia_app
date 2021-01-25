package com.lucas.omnia.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Post(var uid: String? = null, var author: String? = null, var title: String? = null, var body: String? = null) {
    @JvmField
    var timestamp: Long = 0
    @JvmField
    var edited = false
    @JvmField
    var hasImage = false
    @JvmField
    var upVoteCount = 0
    @JvmField
    var downVoteCount = 0
    @JvmField
    var votesBalance = 0
    @JvmField
    var commentCount = 0
    @JvmField
    var upVotes: MutableMap<String, Boolean> = HashMap()
    @JvmField
    var downVotes: MutableMap<String, Boolean> = HashMap()

    // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    constructor() : this("", "", "", "")

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["uid"] = uid
        result["author"] = author
        result["timestamp"] = timestamp
        result["title"] = title
        result["body"] = body
        result["upVoteCount"] = upVoteCount
        result["downVoteCount"] = downVoteCount
        result["votesBalance"] = votesBalance
        result["commentCount"] = commentCount
        return result
    }
}