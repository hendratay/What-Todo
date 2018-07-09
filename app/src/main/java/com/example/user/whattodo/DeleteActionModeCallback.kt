package com.example.user.whattodo

import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.example.user.whattodo.model.Todo

class DeleteActionModeCallback(var actionMode: ActionMode?,
                               var multiSelect: Boolean,
                               var selectedItems: HashMap<Int, Todo>,
                               private val deleteListener: (List<Int>) -> Unit,
                               var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>):
        ActionMode.Callback {

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        multiSelect = true
        menu?.add("Delete")
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        deleteListener(selectedItems.keys.toList())
        mode?.finish()
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        multiSelect = false
        selectedItems.clear()
        adapter.notifyDataSetChanged()
    }

}