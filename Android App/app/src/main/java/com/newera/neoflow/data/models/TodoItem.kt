package com.newera.neoflow.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

/**
 * Todo item
 *
 * @property id
 * @property title
 * @property createdAt
 * @property tasks
 * @property dueDate
 * @property remainderTime
 * @property completed
 * @property important
 * @constructor Create empty Todo item
 */
@Parcelize
@Entity(tableName = "todo_table")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String? = "",
    var createdAt: Long = System.currentTimeMillis(),
    var tasks: MutableList<Task> = ArrayList(),
    var dueDate: Long = Calendar.getInstance().timeInMillis,
    var remainderTime: Long = System.currentTimeMillis(),
    var completed: Boolean = false,
    var important: Boolean = false
): Parcelable

/**
 * Filter for TodoItems
 * Used in AllToDoFragment
 */
enum class Filter {
    ALL,
    COMPLETED,
    IMPORTANT
}
