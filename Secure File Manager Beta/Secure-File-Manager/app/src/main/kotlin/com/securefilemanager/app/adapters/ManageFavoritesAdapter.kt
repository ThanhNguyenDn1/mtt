package com.securefilemanager.app.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.securefilemanager.app.R
import com.securefilemanager.app.activities.BaseAbstractActivity
import com.securefilemanager.app.extensions.config
import com.securefilemanager.app.extensions.standardizePath
import com.securefilemanager.app.interfaces.RefreshRecyclerViewListener
import com.securefilemanager.app.views.MyRecyclerView
import kotlinx.android.synthetic.main.item_manage_favorite.view.*
import java.util.*

class ManageFavoritesAdapter(
    activity: BaseAbstractActivity,
    private var favorites: ArrayList<String>,
    private val listener: RefreshRecyclerViewListener?,
    recyclerView: MyRecyclerView,
    itemClick: (Any) -> Unit
) : RecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    private val config = activity.config

    init {
        setupDragListener()
    }

    override fun getActionMenuId() = R.menu.cab_remove_only

    override fun actionItemPressed(id: Int) {
        when (id) {
            R.id.cab_remove -> removeSelection()
        }
    }

    override fun getSelectableItemCount() = favorites.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = favorites.getOrNull(position)?.hashCode()

    override fun getItemKeyPosition(key: Int) = favorites.indexOfFirst { it.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun prepareActionMode(menu: Menu) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        createViewHolder(R.layout.item_manage_favorite, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favorite = favorites[position]
        holder.bindView(
            favorite,
            allowSingleClick = true,
            allowLongClick = true
        ) { itemView, _ ->
            setupView(
                itemView,
                activity.standardizePath(favorite),
                selectedKeys.contains(favorite.hashCode())
            )
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = favorites.size

    private fun setupView(view: View, favorite: String, isSelected: Boolean) {
        view.apply {
            manage_favorite_title.apply {
                text = favorite
            }

            manage_favorite_holder?.isSelected = isSelected
        }
    }

    private fun removeSelection() {
        val removeFavorites = ArrayList<String>(selectedKeys.size)
        val positions = ArrayList<Int>()
        selectedKeys.forEach { selectedKey ->
            val position = favorites.indexOfFirst { it.hashCode() == selectedKey }
            if (position != -1) {
                positions.add(position)

                val favorite = getItemWithKey(selectedKey)
                if (favorite != null) {
                    removeFavorites.add(favorite)
                    config.removeFavorite(favorite)
                }
            }
        }

        positions.sortDescending()
        removeSelectedItems(positions)

        favorites.removeAll(removeFavorites)
        if (favorites.isEmpty()) {
            listener?.refreshItems()
        }
    }

    private fun getItemWithKey(key: Int): String? = favorites.firstOrNull { it.hashCode() == key }
}
