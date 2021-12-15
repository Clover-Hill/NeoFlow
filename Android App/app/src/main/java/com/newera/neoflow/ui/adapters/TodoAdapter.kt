package com.newera.neoflow.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newera.neoflow.R
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.databinding.ItemTodoBinding
import com.newera.neoflow.logic.utils.Comparators
import com.newera.neoflow.logic.utils.Util

class TodoAdapter(private val listener: AddEditTask):
    ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(Comparators.TODO_COMPARATOR)
{

    /**
     * Sub task adapter
     * (Nested Adaper)
     */
    private var subTaskAdapter: SubTaskAdapter?= null

    inner class TodoViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root)
    {

        /**
         * Bind todoItem to current ViewHolder
         *
         * @param todoItem
         */
        fun bindTodo(todoItem: TodoItem)
        {
            binding.apply {
                todoTitle.text = todoItem.title
                todoDateTextview.text = Util.formatDate(todoItem.dueDate)
                todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)
                todoCompletedCheckbox.isChecked = todoItem.completed
                todoImportantCheckbox.isChecked = todoItem.important

                // If todoItem remainderTime is less than actual time and date is equal (less isn't possible)
                if (Util.isTodoDateLessOrEqual(todoItem.dueDate))
                {
                    todoTimeTextview.setTextColor(binding.root.context.resources.getColor(R.color.red))
                }
                else
                {
                    todoTimeTextview.setTextColor(Color.WHITE)
                }
            }
        }

    }

    /**
     * When ViewHolder is created
     * Set ClickListeners and CheckedChangeListeners
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder
    {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = TodoViewHolder(binding)

        binding.todoEdit.setOnClickListener {
            listener.editTodo(getItem(addTodoViewHolder.adapterPosition))
        }

        binding.todoCompletedCheckbox.setOnCheckedChangeListener { _, checked ->
            listener.updateTodoCompletion(getItem(addTodoViewHolder.adapterPosition), checked)
        }

        binding.todoImportantCheckbox.setOnCheckedChangeListener { _, checked ->
            listener.updateTodoImportance(getItem(addTodoViewHolder.adapterPosition), checked)
        }

        binding.todoConstraint.setOnClickListener {
            val visibility = (binding.subTaskRecyclerview.visibility == View.VISIBLE)
            if (visibility) {
                binding.subTaskRecyclerview.visibility = View.GONE
            }
            else {
                binding.subTaskRecyclerview.visibility = View.VISIBLE
            }
        }

        binding.todoDateTextview.setOnClickListener {
            listener.updateTodoDate(getItem(addTodoViewHolder.adapterPosition))
        }

        binding.todoTimeTextview.setOnClickListener {
            listener.updateTodoTime(getItem(addTodoViewHolder.adapterPosition))
        }

        return addTodoViewHolder
    }

    /**
     * Bind ViewHolder
     * Remember to reset subTaskAdapter in onBindViewHolder rather than onCreateViewHolder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int)
    {
        val todoItem = getItem(position)
        holder.bindTodo(todoItem)

        subTaskAdapter = SubTaskAdapter(todoItem, listener)

        holder.binding.subTaskRecyclerview.apply {
            adapter = subTaskAdapter
            layoutManager = LinearLayoutManager(holder.binding.root.context)
        }
    }
}

/**
 * Create interface for update functions that need to be used in adapters
 *
 * Steps :
 * 1. Make fragments inheritance of AddEditTask
 * 2. Override abstract functions in fragments
 * 3. Use these functions in adapters
 *
 * @constructor Create empty Add edit task
 */
interface AddEditTask
{
    fun editTodo(todoItem: TodoItem)
    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean)
    fun removeSubTask(todoItemId: Int, tasks: List<Task>)
    fun removeTodo(todoItem: TodoItem)
    fun updateTodoCompletion(todoItem: TodoItem, completed: Boolean)
    fun updateTodoImportance(todoItem: TodoItem, important: Boolean)
    fun updateTodoDate(todoItem: TodoItem)
    fun updateTodoTime(todoItem: TodoItem)
}
