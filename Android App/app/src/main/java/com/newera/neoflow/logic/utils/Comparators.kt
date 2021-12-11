package com.newera.neoflow.logic.utils

import androidx.recyclerview.widget.DiffUtil
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem

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