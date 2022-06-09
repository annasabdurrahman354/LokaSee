package com.bangkit.lokasee.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.User
import com.bangkit.lokasee.data.store.UserStore
import com.bangkit.lokasee.ui.main.MainActivity
import com.bangkit.lokasee.util.AppPreferences
import com.bangkit.lokasee.util.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app")

class AuthActivity : AppCompatActivity() {
    private lateinit var pref: AppPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = AppPreferences.getInstance(dataStore)
        runBlocking {
            if (pref.getUserToken().first() != "") {
                UserStore.currentUser = User(
                    pref.getUserID().first(),
                    pref.getUserName().first(),
                    pref.getUserEmail().first(),
                    pref.getUserPhone().first(),
                    pref.getUserAvatarUrl().first(),
                    null,
                    null,
                    null
                )
                UserStore.currentUserToken = pref.getUserToken().first()
                ApiConfig.TOKEN = UserStore.currentUserToken!!
            }
        }
        if (UserStore.currentUserToken != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        setContentView(R.layout.activity_auth)
    }
}