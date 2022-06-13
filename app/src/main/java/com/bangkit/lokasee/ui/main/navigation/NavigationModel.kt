package com.bangkit.lokasee.ui.main.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.lokasee.R

object NavigationModel {

    const val HOME_ID = 0
    const val MAP_ID = 1
    const val SELLER_ID = 2
    const val ACCOUNT_ID = 3
    const val LOGOUT_ID = 4

    var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = HOME_ID,
            icon = R.drawable.ic_twotone_home,
            title = "Home",
            checked = false,
        ),
        NavigationModelItem.NavMenuItem(
            id = MAP_ID,
            icon = R.drawable.ic_twotone_map,
            title = "Map",
            checked = false,
        ),
        NavigationModelItem.NavMenuItem(
            id = SELLER_ID,
            icon = R.drawable.ic_twotone_assessment,
            title = "Management",
            checked = false,
        ),
        NavigationModelItem.NavMenuItem(
            id = ACCOUNT_ID,
            icon = R.drawable.ic_twotone_account,
            title = "Account",
            checked = false,
        ),
        NavigationModelItem.NavMenuItem(
            id = LOGOUT_ID,
            icon = R.drawable.ic_twotone_logout,
            title = "Logout",
            checked = false,
        )
    )

    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
        postListUpdate()
    }

    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }

    private fun postListUpdate() {
        _navigationList.value = navigationMenuItems // + (NavigationModelItem.NavDivider("Folders"))
    }
}

