package com.lucas.omnia.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Reply(var uid: String? = null, var author: String? = null, var body: String? = null) {
    @JvmField
    var timestamp: Long = 0
    @JvmField
    var edited = false
    @JvmField
    var upVoteCount = 0
    @JvmField
    var downVoteCount = 0
    @JvmField
    var votesBalance = 0
    @JvmField
    var upVotes: MutableMap<String, Boolean> = HashMap()
    @JvmField
    var downVotes: MutableMap<String, Boolean> = HashMap()

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
        return result
    }
}