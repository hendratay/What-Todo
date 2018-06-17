package com.example.user.whattodo.fragment

import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import com.example.user.whattodo.MainActivity
import com.example.user.whattodo.R
import com.example.user.whattodo.model.Todo
import com.example.user.whattodo.adapter.TaskAdapter
import com.example.user.whattodo.db.TodoEntity
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_todo.*

class TaskFragment: TodoFragment() {

    private lateinit var adapter: TaskAdapter
    private var taskList: MutableList<Todo> = ArrayList()

    override fun onStart() {
        super.onStart()
        getTask()
    }

    override fun addTodoDialog() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val dialog = BottomSheetDialog(activity as MainActivity)
        dialog.setContentView(bottomSheet)
        dialog.button_save_task.setOnClickListener {
            if(dialog.edit_text_task.text.isNotBlank()) {
                insertTodo(TodoEntity(dialog.edit_text_task.text.toString(), false, "Task", null))
                getTask()
            }
            dialog.hide()
        }
        dialog.show()
    }

    override fun setupRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(taskList, { todo: Todo -> onItemChecked(todo) }, { todoList: List<Int> -> onItemDeleted(todoList) })
        recycler_view.adapter = adapter
    }

    private fun getTask() {
        getTodo("Task")
                .subscribe {
                    taskList.clear()
                    it.forEach { taskList.add(Todo(it.id, it.todo, it.done, it.type, it.dateTime)) }
                    adapter.notifyDataSetChanged()
                }
    }

    private fun onItemChecked(todo: Todo) {
        if(!recycler_view.isComputingLayout) {
            if(todo.done) updateTodo(todo, false) else updateTodo(todo, true)
        }
    }

    private fun onItemDeleted(selected: List<Int>) {
        deleteTodo(selected, taskList)
        getTask()
        val snackBar = Snackbar.make(coordinator_layout, "${selected.size} item deleted", Snackbar.LENGTH_SHORT)
        snackBar.show()
        snackBar.setAction("UNDO") {
            undoDeleteTodo()
            getTask()
        }
    }

}