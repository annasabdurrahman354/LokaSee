package com.bangkit.lokasee.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bangkit.lokasee.R
import com.bangkit.lokasee.data.Result
import com.bangkit.lokasee.data.store.UserStore.currentUser
import com.bangkit.lokasee.data.store.UserStore.currentUserToken
import com.bangkit.lokasee.databinding.ActivityMainBinding
import com.bangkit.lokasee.ui.ViewModelFactory
import com.bangkit.lokasee.ui.auth.AuthActivity
import com.bangkit.lokasee.ui.main.home.HomeFragmentDirections
import com.bangkit.lokasee.ui.main.map.MapFragmentDirections
import com.bangkit.lokasee.ui.main.navigation.BottomNavDrawerFragment
import com.bangkit.lokasee.ui.main.navigation.NavigationAdapter
import com.bangkit.lokasee.ui.main.navigation.NavigationModelItem
import com.bangkit.lokasee.ui.main.profile.ProfileFragmentDirections
import com.bangkit.lokasee.ui.main.search.SearchFragmentDirections
import com.bangkit.lokasee.ui.main.seller.SellerHomeFragmentDirections
import com.bangkit.lokasee.util.*
import com.bangkit.lokasee.util.ViewHelper.gone
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis


class MainActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener, NavController.OnDestinationChangedListener, NavigationAdapter.NavigationAdapterListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavDrawer: BottomNavDrawerFragment
    private lateinit var mainViewModel: MainViewModel

    val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        bottomNavDrawer = supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
        setUpBottomNavigationAndFab()

        binding.fabMain.setOnClickListener{
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_sellerCreateFragment)
        }
    }

    private fun setUpBottomNavigationAndFab() {
        // Wrap binding.run to ensure ContentViewBindingDelegate is calling this Activity's
        // setContentView before accessing views
        binding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
        }

        // Set a custom animation for showing and hiding the FAB
        binding.fabMain.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
            }
        }

        bottomNavDrawer.apply {
            addOnSlideAction(HalfClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addOnSlideAction(AlphaSlideAction(binding.bottomAppBarTitle, true))
            addOnStateChangedAction(ShowHideFabStateAction(binding.fabMain))
            addOnStateChangedAction(ChangeSettingsMenuStateAction { showSettings ->
                // Toggle between the current destination's BAB menu and the menu which should
                // be displayed when the BottomNavigationDrawer is open.
                binding.bottomAppBar.replaceMenu(if (showSettings) {
                    R.menu.bottom_app_bar_settings_menu
                } else {
                    getBottomAppBarMenuForDestination()
                })
            })

            addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(binding.bottomAppBarChevron))
            addNavigationListener(this@MainActivity)
        }

        // Set up the BottomAppBar menu
        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                bottomNavDrawer.toggle()
            }
            setOnMenuItemClickListener(this@MainActivity)
        }

        // Set up the BottomNavigationDrawer's open/close affordance
        binding.bottomAppBarContentContainer.setOnClickListener {
            bottomNavDrawer.toggle()
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                binding.bottomAppBarTitle.text = "Home"
                setBottomAppBarForHome(getBottomAppBarMenuForDestination(destination))
            }
            R.id.searchFragment -> {
                binding.bottomAppBarTitle.text = "Search"
                hideBottomAppBar()
                binding.fabMain.hide()
            }
            R.id.mapFragment ->{
                binding.bottomAppBarTitle.text = "Map"
                setBottomAppBarForMap(getBottomAppBarMenuForDestination(destination))
            }
            R.id.postFragment ->{
                hideBottomAppBar()
                binding.fabMain.hide()
            }
            R.id.profileFragment ->{
                binding.bottomAppBarTitle.text = "Profile"
                binding.bottomAppBar.performShow()
                binding.fabMain.show()
                binding.bottomAppBar.fabAlignmentMode =  FAB_ALIGNMENT_MODE_CENTER
            }
            R.id.sellerHomeFragment ->{
                binding.bottomAppBarTitle.text = "Management"
                setBottomAppBarForSellerHome()
            }
            R.id.sellerCreateFragment ->{
                hideBottomAppBar()
                binding.fabMain.hide()
            }
            R.id.sellerUpdateFragment ->{
                hideBottomAppBar()
                binding.fabMain.hide()
            }
            else -> {
                setBottomAppBarForOther()
                binding.bottomAppBarTitle.text = ""
            }
        }
    }

    @MenuRes
    private fun getBottomAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.nav_host_fragment).currentDestination
        return when (dest?.id) {
            R.id.homeFragment -> R.menu.bottom_app_bar_home_menu
            R.id.mapFragment -> R.menu.bottom_app_bar_map_menu
            else -> R.menu.bottom_app_bar_no_menu
        }
    }

    private fun setBottomAppBarForMap(@MenuRes menuRes: Int) {
        binding.run {
            fabMain.setImageResource(R.drawable.ic_add)
            fabMain.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fabMain.show()
            binding.bottomAppBar.fabAlignmentMode =  FAB_ALIGNMENT_MODE_CENTER

        }
    }

    private fun setBottomAppBarForHome(@MenuRes menuRes: Int) {
        binding.run {
            fabMain.setImageResource(R.drawable.ic_add)
            fabMain.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.replaceMenu(menuRes)
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fabMain.show()
            bottomAppBar.fabAlignmentMode =  FAB_ALIGNMENT_MODE_CENTER
        }
    }


    private fun setBottomAppBarForSellerHome() {
        binding.run {
            fabMain.setImageResource(R.drawable.ic_add)
            fabMain.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBarTitle.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fabMain.show()
            bottomAppBar.fabAlignmentMode =  FAB_ALIGNMENT_MODE_END

        }
    }

    private fun setBottomAppBarForOther() {
        binding.run {
            fabMain.setImageResource(R.drawable.ic_add)
            fabMain.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBarTitle.gone()
            bottomAppBar.performShow()
            fabMain.show()
            bottomAppBar.fabAlignmentMode =  FAB_ALIGNMENT_MODE_END
        }
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    bottomAppBar.visibility = View.GONE
                    fabMain.visibility = View.INVISIBLE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        when(item.id){
            0 -> navigateToHome()
            1 -> navigateToMap()
            2 -> {
                if (currentUserToken == "") {
                        val pDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                        pDialog.titleText = "Opps!"
                        pDialog.contentText = "You need to login first!"
                        pDialog.confirmText = "Login"
                        pDialog.setCancelable(true)
                        pDialog.setConfirmClickListener {
                            val intent = Intent(this, AuthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        pDialog.show()
                    }
                else navigateToSeller()
                }
            3 -> navigateToProfile()
            4 -> logout()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_settings -> {
                bottomNavDrawer.close()
            }
            R.id.menu_search -> navigateToSearch()
            R.id.menu_filter -> showFilterModal()
        }
        return true
    }

    private fun navigateToHome() {
        val directions = HomeFragmentDirections.actionGlobalHomeFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToSearch() {
        val directions = SearchFragmentDirections.actionGlobalSearchFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToMap() {
        val directions = MapFragmentDirections.actionGlobalMapFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToProfile() {
        val directions = ProfileFragmentDirections.actionGlobalProfileFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToSeller() {
        val directions = SellerHomeFragmentDirections.actionGlobalSellerHomeFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun showFilterModal() {
        FilterBottomSheet.newInstance().show(supportFragmentManager, null)
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.newInstance(this)
        mainViewModel = factory.create(MainViewModel::class.java)
    }

    private fun setupTransition(){
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(R.integer.lokasee_motion_duration_large).toLong()
            }
        }
    }

    private fun logout(){
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
            pDialog.titleText = "Logging Out"
            pDialog.setCancelable(false)
            pDialog.show()
        mainViewModel.deleteUser()
        mainViewModel.logout().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        pDialog.hide()
                        ViewModelFactory.newInstance(this)
                        val intent = Intent(this, AuthActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    is Result.Error -> {
                        pDialog.hide()
                        Toast.makeText(this, result.error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
