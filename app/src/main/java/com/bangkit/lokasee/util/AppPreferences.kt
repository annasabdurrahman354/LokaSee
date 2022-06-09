package com.bangkit.lokasee.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    private val USER_ID_KEY = intPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
    private val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")

    fun getUserID(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: -1
        }
    }

    suspend fun saveUserID(userID: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userID
        }
    }

    fun getUserName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY] ?: ""
        }
    }

    suspend fun saveUserLogin(
        userId: Int,
        userName: String,
        email: String,
        phoneNumber: String,
        avatarUrl: String,
        token: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_NAME_KEY] = userName
            preferences[USER_EMAIL_KEY] = userName
            preferences[USER_PHONE_KEY] = userName
            preferences[USER_AVATAR_KEY] = userName
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = userName
        }
    }

    fun getUserEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY] ?: ""
        }
    }

    suspend fun saveUserEmail(userEmail: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = userEmail
        }
    }

    fun getUserPhone(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_PHONE_KEY] ?: ""
        }
    }

    suspend fun saveUserPhone(userPhone: String) {
        dataStore.edit { preferences ->
            preferences[USER_PHONE_KEY] = userPhone
        }
    }

    fun getUserAvatarUrl(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USER_AVATAR_KEY] ?: ""
        }
    }

    suspend fun saveUserAvatarUrl(userAvatarUrl: String) {
        dataStore.edit { preferences ->
            preferences[USER_AVATAR_KEY] = userAvatarUrl
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