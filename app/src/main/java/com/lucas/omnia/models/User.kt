package com.lucas.omnia.models

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
class User {
    var uid: String? = null
    var username: String? = null
    var email: String? = null
    var hasPhoto = false
    var city: String? = null
    var about: String? = null
    var subCount = 0
    var subs: Map<String, String> = HashMap()

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(uid: String?, username: String?) {
        this.uid = uid
        this.username = username
    }

    constructor(username: String?, email: String?, subCount: Int) {
        this.username = username
        this.email = email
        this.subCount = subCount
    }

    fun setUsername(username: String?) {
        this.username = username
    }

    fun setHasPhoto(hasPhoto: Boolean) {
        this.hasPhoto = hasPhoto
    }

    fun setCity(city: String?) {
        this.city = city
    }

    fun setAbout(about: String?) {
        this.about = about
    }

    fun setSubCount(subCount: Int) {
        this.subCount = subCount
    }
}