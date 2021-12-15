package com.newera.neoflow.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.newera.neoflow.ui.fragments.EditTodoFragmentArgs
import com.newera.neoflow.R
import com.newera.neoflow.databinding.FragmentEditTodoBinding
import com.newera.neoflow.logic.utils.Util
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.ui.adapters.EditSubTaskAdapter
import com.newera.neoflow.ui.adapters.OnTaskChanged
import com.newera.neoflow.ui.viewmodels.EditTodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EditTodoFragment : Fragment(R.layout.fragment_edit_todo), OnTaskChanged
{

    /**
     * _binding mutable
     */
    private var _binding: FragmentEditTodoBinding? = null
    /**
     * binding immutable
     */
    private val binding get() = _binding!!

    /**
     * Edit todo view model
     */
    private val editTodoViewModel by viewModels<EditTodoViewModel>()

    /**
     * Data transferred from AllTodoFragment (TodoItem)
     */
    private val editTodoFragmentArgs: EditTodoFragmentArgs by navArgs()

    /**
     * Edit sub task adapter
     */
    private lateinit var editSubTaskAdapter: EditSubTaskAdapter

    /**
     * Current Edited todoItem
     */
    private lateinit var todoItem: TodoItem

    /**
     * Current Sub-Task list
     */
    private val tasks: MutableList<Task> = ArrayList()

    /**
     * Alarm calendar
     */
    private val alarmCalendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        // Initialization
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditTodoBinding.bind(view)
        setUpTasksRecyclerView()

        todoItem = editTodoFragmentArgs.todoItem
        tasks.addAll(todoItem.tasks)

        alarmCalendar.timeInMillis = todoItem.dueDate

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.addTodoToolbar)

        // Update todoItem when it's been changed
        editTodoViewModel.getTodoById(todoItem.id).asLiveData().observe(viewLifecycleOwner)
        {
            todoItem = it
            tasks.clear()
            tasks.addAll(it.tasks)
        }

        // Update editSubTaskAdapter when sub-task list has been changed
        editTodoViewModel.getTodoList(todoItem.id).asLiveData().observe(viewLifecycleOwner)
        {
            editSubTaskAdapter.submitList(it)
        }

        // Set listeners for UI elements
        binding.apply {

            todoDateTextview.text = Util.formatDate(dueDate = todoItem.dueDate)
            todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)

            todoDate.setOnClickListener {
                setDueDate()
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }

            todoTime.setOnClickListener {
                setRemainderTime()
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }

            backButton.setOnClickListener {
                updateTodoTasks()
                findNavController().navigateUp()
            }

            saveTodoButton.setOnClickListener {
                updateTodoTasks()
                findNavController().navigateUp()
            }

            addSubTaskButton.setOnClickListener {
                tasks.add(Task(id = tasks.size, title = ""))
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }
            todoTitle.setText(todoItem.title)
        }

        // When Back Button Pressed, makes sure that sub-tasks get updated
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true)
            {
                override fun handleOnBackPressed()
                {
                    updateTodoTasks()
                    if (isEnabled)
                    {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
            )

    }

    /**
     * Set up sub-task recycler view
     */
    private fun setUpTasksRecyclerView()
    {
        editSubTaskAdapter = EditSubTaskAdapter(this)
        binding.subTaskRecyclerview.apply {
            adapter = editSubTaskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Update sub-tasks and eliminate empty tasks
     */
    private fun updateTodoTasks()
    {
        var index = 0
        val subTasks: MutableList<Task> = ArrayList()
        subTasks.addAll(tasks)
        // Check if there are any empty tasks
        tasks.forEach { task ->
            if (task.title == "")
            {
                subTasks.remove(task)
            } else
            {
                task.id = index++
            }
        }
        editTodoViewModel.updateTodoTasks(todoItem.id, subTasks)
    }

    /**
     * Set due date
     * First create a listener when date has been set
     * Then send it to showDatePicker
     *
     */
    private fun setDueDate()
    {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, day)

            alarmCalendar.set(Calendar.YEAR, year)
            alarmCalendar.set(Calendar.MONTH, month)
            alarmCalendar.set(Calendar.DAY_OF_MONTH, day)

            todoItem.dueDate = alarmCalendar.timeInMillis

            binding.todoDateTextview.text = Util.formatDate(dueDate = todoItem.dueDate)
            editTodoViewModel.updateTodoDueDate(todoItem.id, todoItem.dueDate)
        }

        Util.showDatePicker(
            todoItem = todoItem, context = requireContext(),
            datePickerListener = datePickerListener
        )
    }

    /**
     * Set remainder time
     *
     */
    private fun setRemainderTime()
    {
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmCalendar.set(Calendar.MINUTE, minute)
            alarmCalendar.set(Calendar.SECOND, 0)

            todoItem.remainderTime = alarmCalendar.timeInMillis
            todoItem.dueDate = alarmCalendar.timeInMillis

            binding.todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)
            editTodoViewModel.updateTodoDueDateTime(
                todoItem.id, todoItem.dueDate, todoItem.remainderTime
            )
            setAlarm()
        }

        Util.showTimePicker(
            todoItem = todoItem, context = requireContext(),
            timePickerListener = timePickerListener
        )
    }

    /**
     * Set alarm
     *
     */
    private fun setAlarm()
    {
        Util.setAlarm(
            todoItem = todoItem, context = requireContext(),
            alarmCalendar = alarmCalendar, view = binding.root
        )
    }

    /**
     * On destroy view
     * Remember to set _binding to null so trying to call binding after the view has been destroyed will throw exception
     *
     */
    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    /**
     * On sub task title changed
     *
     * @param position
     * @param newTitle
     */
    override fun onSubTaskTitleChanged(position: Int, newTitle: String)
    {
        tasks[position].title = newTitle
    }

    /**
     * Update sub task completion
     *
     * @param position
     * @param isCompleted
     */
    override fun updateSubTaskCompletion(position: Int, isCompleted: Boolean)
    {
        tasks[position].isCompleted = isCompleted
        editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
    }

    /**
     * Remove sub task
     *
     * @param position
     */
    override fun removeSubTask(position: Int)
    {
        tasks.removeAt(position)
        editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
    }
}
