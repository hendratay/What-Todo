package com.example.user.whattodo.adapter

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import kotlinx.android.synthetic.main.todo_list_item.view.*;

class TodoAdapter(val items : MutableList<Todo>,
                  val changeListener: ((Todo) -> Unit)?,
                  val deleteTodoListener: ((List<Todo>) -> Unit)?) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var multiSelect: Boolean = false
    private var selectedItems: ArrayList<Todo> = ArrayList()

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
            for(todo: Todo in selectedItems) {
                items.remove(todo)
            }
            deleteTodoListener?.invoke(selectedItems)
            mode?.finish()
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            multiSelect = false
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val tvTodo = itemView.tv_todo
        val cbTodo = itemView.cb_todo

        fun selectItem(item: Todo) {
            if(multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item)
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    selectedItems.add(item)
                    itemView.setBackgroundColor(Color.LTGRAY)
                }
            }
        }

        fun update(value: Todo) {
            if(selectedItems.contains(value)) {
                itemView.setBackgroundColor(Color.LTGRAY)
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnLongClickListener {
                var activity = it.context as AppCompatActivity
                activity.startSupportActionMode(actionModeCallbacks)
                selectItem(value)
                true
            }
            itemView.setOnClickListener {
                selectItem(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = items.get(position)
        if(todo.done) {
            holder.tvTodo.text = todo.todoText
            holder.tvTodo.paintFlags = (Paint.STRIKE_THRU_TEXT_FLAG)
        }
        holder.tvTodo.text = todo.todoText
        holder.cbTodo.isChecked = todo.done
        holder.update(todo)
        holder.cbTodo.setOnCheckedChangeListener{ buttonView, isChecked ->
            changeListener?.invoke(todo)
        }
    }

    override fun getItemCount() = items.size
}