package com.example.ittalian.shaketrain

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Course : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var departStaion: String = ""
    var arriveStation: String = ""
}