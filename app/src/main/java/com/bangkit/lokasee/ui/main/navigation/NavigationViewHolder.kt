package com.bangkit.lokasee.ui.main.navigation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.lokasee.databinding.NavMenuItemLayoutBinding

sealed class NavigationViewHolder<T : NavigationModelItem>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(navItem : T)

    class NavMenuItemViewHolder(
        private val binding: NavMenuItemLayoutBinding,
        private val listener: NavigationAdapter.NavigationAdapterListener
    ) : NavigationViewHolder<NavigationModelItem.NavMenuItem>(binding.root) {

        override fun bind(navItem: NavigationModelItem.NavMenuItem) {
            binding.run {
                navItemTitle.text = navItem.title
                navItemTitle.isChecked = navItem.checked
                navItemTitle.setCompoundDrawablesWithIntrinsicBounds(navItem.icon, 0, 0, 0);
                navItemTitle.setOnClickListener{
                    listener.onNavMenuItemClicked(navItem)
                }
            }

        }
    }
}