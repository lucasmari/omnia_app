package com.lucas.omnia.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*
import kotlinx.serialization.*

@IgnoreExtraProperties
@Serializable
data class User(var uid: String? = null, var username: String? = null) {
    @JvmField
    var email: String? = null
    var hasPhoto = false
    var about: String? = null
    @JvmField
    var city: String? = null
    @JvmField
    var subCount = 0
    @JvmField
    var subs: MutableMap<String, String> = HashMap()

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    constructor() : this("", "")

    constructor(username: String? = null, email: String? = null, subCount: Int) : this() {
        this.username = username
        this.email = email
        this.subCount = subCount
    }
}