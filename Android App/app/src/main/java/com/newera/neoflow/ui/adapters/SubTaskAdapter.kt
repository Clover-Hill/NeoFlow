package com.newera.neoflow.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.databinding.ItemSubTaskBinding

/**
 * Sub task adapter
 * Implemented with simple RecyclerView.Adapter
 * Used to show the subtasks of every todoItem in AllTodoFragment ( Nested Adapter )
 *
 * @property todoItem
 * @property listener
  @constructor Create empty Sub task adapter
 */
class SubTaskAdapter(private val todoItem: TodoItem, private val listener: AddEditTask) :
    RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>()
{

    /**
     * Sub task ViewHolder
     *
     * @property binding view binging of item_sub_task.xml
     * @constructor Create empty Sub task view holder
     */
    inner class SubTaskViewHolder(private val binding: ItemSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
    {

        /**
         * Bind the ViewHolder to a particular subTask
         * Set the MaterialCheckBox taskTitle accordingly
         *
         * @param subTask
         */
        fun bindSubTask(subTask: Task)
        {
            binding.apply {
                taskTitle.isChecked = subTask.isCompleted
                taskTitle.text = subTask.title
                taskTitle.paint.isStrikeThruText = subTask.isCompleted
            }
        }

    }

    /**
     * Actions to be done when viewHolder is created
     *
     * 1. Get a new ViewHolder
     * 2. Set listeners
     * Remember to use adapterPosition to get the position of a ViewHolder in a list
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder
    {
        val binding = ItemSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val subTaskViewHolder = SubTaskViewHolder(binding)

        binding.taskTitle.setOnClickListener {
            listener.updateSubTaskCompletion(todoItem, subTaskViewHolder.adapterPosition, binding.taskTitle.isChecked)
            notifyItemChanged(subTaskViewHolder.adapterPosition)
        }

        binding.taskRemove.setOnClickListener {
            val tasks = todoItem.tasks.toMutableList()
            tasks.removeAt(subTaskViewHolder.adapterPosition)

            listener.removeSubTask(todoItem.id, tasks)
            notifyItemChanged(subTaskViewHolder.adapterPosition)
        }

        return subTaskViewHolder
    }

    /**
     * Bind view holder
     * Simply use bindSubTask method in ViewHolder to bind an element in the list to a ViewHolder
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int)
    {
        val subTask = todoItem.tasks[position]
        holder.bindSubTask(subTask)
    }

    override fun getItemCount(): Int
    {
        return todoItem.tasks.size
    }
}
