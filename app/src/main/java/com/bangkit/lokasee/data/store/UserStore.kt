package com.bangkit.lokasee.data.store

import com.bangkit.lokasee.data.User

object UserStore {
    var currentUser = User(-1, "", "", "", "", "", null, null, null, null, null, null, null, null, null)
    var currentUserToken: String = ""
}