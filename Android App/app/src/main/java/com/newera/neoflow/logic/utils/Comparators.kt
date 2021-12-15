package com.newera.neoflow.logic.utils

import androidx.recyclerview.widget.DiffUtil
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem

/**
 * Comparators
 * Set call backs for ListAdapters to use DiffUtil to get the difference between two lists
 * This can hugely improve the efficiency of RecyclerView and make it easier to delete items
 *
 * 1. TODO_COMPARATOR is for TodoAdapter
 * 2. SUBTASK_COMPARATOR is for SubTaskAdapter
 *
 * @constructor Create empty Comparators
 */
object Comparators
{

    val TODO_COMPARATOR = object : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean
        {
            return oldItem == newItem
        }
    }

    val SUBTASK_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean
        {
            return oldItem == newItem
        }
    }

}