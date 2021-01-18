package com.lucas.omnia.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class Comment {
    var uid: String? = null
    var author: String? = null
    var timestamp: Long = 0
    var body: String? = null
    var edited = false
    var upVoteCount = 0
    var downVoteCount = 0
    var votesBalance = 0
    var replyCount = 0
    var upVotes: Map<String, Boolean> = HashMap()
    var downVotes: Map<String, Boolean> = HashMap()

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    constructor(uid: String?, author: String?, body: String?) {
        this.uid = uid
        this.author = author
        this.body = body
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()
        result["uid"] = uid
        result["author"] = author
        result["timestamp"] = timestamp
        result["body"] = body
        result["upVoteCount"] = upVoteCount
        result["downVoteCount"] = downVoteCount
        result["votesBalance"] = votesBalance
        result["replyCount"] = replyCount
        return result
    }
}