package com.lucas.omnia.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(var uid: String?, var username: String?) {
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

    constructor(username: String?, email: String?, subCount: Int) : this() {
        this.username = username
        this.email = email
        this.subCount = subCount
    }
}