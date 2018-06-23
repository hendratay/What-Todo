package com.example.user.whattodo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.grocery_list_item.view.*

class GroceryAdapter(private val groceryList: List<Todo>,
                     private val changeListener: (Todo) -> Unit,
                     private val deleteListener: (List<Int>) -> Unit):
        RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    private var actionMode: ActionMode? = null
    private var multiSelect: Boolean = false
    private var selectedItems: HashMap<Int, Todo> = hashMapOf()

    private var actionModeCallbacks: ActionMode.Callback = object: ActionMode.Callback {
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
            notifyDataSetChanged()
        }
    }

    inner class GroceryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(todo: Todo, position: Int, changeListener: (Todo) -> Unit) {
            itemView.check_box_grocery.isChecked = todo.done
            itemView.text_view_grocery.text = "${position + 1}. ${todo.todoText.capitalize()}"
            itemView.text_view_grocery.paintFlags = if(todo.done) (Paint.STRIKE_THRU_TEXT_FLAG) else 0
            itemView.check_box_grocery.setOnCheckedChangeListener { _, _ ->
                changeListener(todo)
            }
            update(adapterPosition)
        }

        private fun selectItem(item: Int) {
            if(multiSelect) {
                if (selectedItems.containsKey(item)) {
                    selectedItems.remove(item)
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedItems[item] = groceryList[item]
                    itemView.setBackgroundColor(Color.LTGRAY)
                }
            }
        }

        private fun update(value: Int) {
            if(selectedItems.containsKey(value)) {
                itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnLongClickListener {
                actionMode = (it.context as AppCompatActivity).startSupportActionMode(actionModeCallbacks)
                selectItem(value)
                true
            }
            itemView.setOnClickListener {
                if(actionMode == null) itemView.check_box_grocery.isChecked = !itemView.check_box_grocery.isChecked else selectItem(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        return GroceryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grocery_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.bind(groceryList[position], position, changeListener)
    }

    override fun getItemCount() = groceryList.size

}