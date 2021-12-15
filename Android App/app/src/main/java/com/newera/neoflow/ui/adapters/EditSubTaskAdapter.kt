package com.newera.neoflow.ui.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newera.neoflow.R
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.databinding.ItemEditSubTaskBinding
import com.newera.neoflow.logic.utils.Comparators.SUBTASK_COMPARATOR

class EditSubTaskAdapter(private val listener: OnTaskChanged) :
    ListAdapter<Task, EditSubTaskAdapter.EditSubTaskViewHolder>(SUBTASK_COMPARATOR)
{

    /**
     * Edit Sub task ViewHolder
     *
     * @property binding view binging of item_edit_sub_task.xml
     * @constructor Create empty Sub task view holder
     */
    inner class EditSubTaskViewHolder(private val binding: ItemEditSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bindSubTask(subTask: Task)
        {

            binding.apply {
                subTaskCheckbox.isChecked = subTask.isCompleted
                subTaskTitle.setText(subTask.title)
                subTaskTitle.paint.isStrikeThruText = subTask.isCompleted
            }

        }
    }

    /**
     * When EditSubTaskViewHolder is created
     *
     * Listener needed:
     * 1. completion checkbox
     * 2. tiny image when focus change
     * 3. text change
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditSubTaskViewHolder
    {
        val binding =
            ItemEditSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = EditSubTaskViewHolder(binding)

        binding.apply {

            subTaskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                listener.updateSubTaskCompletion(addTodoViewHolder.adapterPosition, isChecked)
            }

            subTaskTitle.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    subTaskSort.background = null
                    subTaskSort.setImageResource(R.drawable.ic_close)
                    subTaskSort.setOnClickListener {
                        listener.removeSubTask(addTodoViewHolder.adapterPosition)
                    }
                } else {
                    subTaskSort.background = null
                    subTaskSort.setImageResource(R.drawable.ic_sort)
                }
            }

            subTaskTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(newText: Editable?)
                {
                    listener.onSubTaskTitleChanged(
                        addTodoViewHolder.adapterPosition,
                        newText.toString()
                    )
                }
            })
        }
        return addTodoViewHolder
    }

    /**
     * Bind view holder
     * In ListAdapter , use getItem(position) to get a particular element
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: EditSubTaskViewHolder, position: Int)
    {
        val item = getItem(position)
        holder.bindSubTask(item)
    }
}

/**
 * Create interface for methods used in EditSubTaskAdapter
 * Implementation in EditTodoFragment
 *
 * @constructor Create empty On task changed
 */
interface OnTaskChanged
{
    fun onSubTaskTitleChanged(position: Int, newTitle: String)
    fun updateSubTaskCompletion(position: Int, isCompleted: Boolean)
    fun removeSubTask(position: Int)
}
