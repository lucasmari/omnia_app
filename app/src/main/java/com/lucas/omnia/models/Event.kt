package com.lucas.omnia.models

data class Event(val date: String, val status: String, val type: String, val description: String,
                 val name: String,
            val building: String, val room: String, val floor: String)