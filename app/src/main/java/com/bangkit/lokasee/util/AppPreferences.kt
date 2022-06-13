package com.bangkit.lokasee.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bangkit.lokasee.data.store.UserStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_ID_KEY = intPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
    private val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
    private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")

    fun getUserId(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }
    }

    suspend fun saveUserId(userID: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userID
        }
    }

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun getUserLogin(): User{
        val currentUser = User(-1, "", "", "", "", "",null, null, null, null, null, null, null, null, null)
            currentUser.id = dataStore.data.map { preferences ->
                preferences[USER_ID_KEY] ?: -1
            }.first()
            currentUser.name = dataStore.data.map { preferences ->
                preferences[USER_NAME_KEY] ?: ""
            }.first()
            currentUser.email = dataStore.data.map { preferences ->
                preferences[USER_EMAIL_KEY] ?: ""
            }.first()
            currentUser.phoneNumber = dataStore.data.map { preferences ->
                preferences[USER_PHONE_KEY] ?: ""
            }.first()
            currentUser.address = dataStore.data.map { preferences ->
                preferences[USER_ADDRESS_KEY] ?: ""
            }.first()
            currentUser.avatarUrl = dataStore.data.map { preferences ->
                preferences[USER_AVATAR_KEY] ?: ""
            }.first()
        return currentUser
    }

    suspend fun saveUserLogin(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        address: String,
        avatarUrl: String,
        token: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_PHONE_KEY] = phoneNumber
            preferences[USER_ADDRESS_KEY] = address
            preferences[USER_AVATAR_KEY] = avatarUrl
            preferences[USER_TOKEN_KEY] = token
        }
        Log.e("Ini habis login", UserStore.currentUserToken)
    }

    suspend fun deleteUserLogin() {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = -1
            preferences[USER_NAME_KEY] = ""
            preferences[USER_EMAIL_KEY] = ""
            preferences[USER_PHONE_KEY] = ""
            preferences[USER_ADDRESS_KEY] = ""
            preferences[USER_AVATAR_KEY] = ""
            preferences[USER_TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}