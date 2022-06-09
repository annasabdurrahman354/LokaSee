package com.bangkit.lokasee.data.store

import com.bangkit.lokasee.data.User

object UserStore {
    var currentUser: User? = null
    var currentUserToken: String? = null
}