package com.newera.neoflow.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newera.neoflow.R
import com.newera.neoflow.databinding.FragmentAllTodoBinding
import com.newera.neoflow.logic.utils.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.newera.neoflow.data.models.Filter
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.ui.adapters.AddEditTask
import com.newera.neoflow.ui.adapters.TodoAdapter
import com.newera.neoflow.ui.viewmodels.AllTodoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags

import androidx.recyclerview.widget.RecyclerView.ViewHolder

@AndroidEntryPoint
class AllTodoFragment : Fragment(R.layout.fragment_all_todo), AddEditTask
{

    /**
     * Mutable binding
     */
    private var _binding: FragmentAllTodoBinding? = null

    /**
     * Immutable Binding
     */
    private val binding get() = _binding!!

    /**
     * All todo ViewModel
     */
    private val allTodoViewModel by viewModels<AllTodoViewModel>()

    /**
     * All todo adapter
     */
    private lateinit var allTodoAdapter: TodoAdapter

    /**
     * Bottom sheet behavior
     */
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    /**
     * Alarm calendar
     */
    private var alarmCalendar = Calendar.getInstance()

    /**
     * Due date
     */
    private var dueDate = Calendar.getInstance().timeInMillis

    /**
     * Remainder time
     */
    private var remainderTime = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)

        setUpTodoRecyclerview()
        // Initialize bottomSheetBehavior
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isDraggable = false

        // LiveData for filtered TodoItems
        // When it isn't empty, submit list to ListAdapter
        allTodoViewModel.todoList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noTaskTextview.visibility = View.VISIBLE
                binding.allTodoRecyclerview.visibility = View.GONE
            } else {
                binding.noTaskTextview.visibility = View.GONE
                binding.allTodoRecyclerview.visibility = View.VISIBLE
                allTodoAdapter.submitList(it)
            }
        }

        // LiveData only for Filter flag
        // To show different background text when there is no item
        allTodoViewModel.todoFilter.asLiveData().observe(viewLifecycleOwner) { filter ->
            when (filter) {
                Filter.ALL -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_all)
                Filter.COMPLETED -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_completed)
                Filter.IMPORTANT -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_important)
                else -> requireContext().resources.getString(R.string.no_task_completed)
            }
        }

        // Set listeners for this fragment
        binding.apply {

            // Implement actions when 'Create' button has been pressed
            addTodoButton.setOnClickListener{

                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    showBottomSheet()
                } else
                {
                    if (todoTitle.text.toString().trim().isNotEmpty())
                    {
                        val todoItem = TodoItem(
                            title = todoTitle.text.trim().toString(),
                            dueDate = dueDate, remainderTime = remainderTime
                        )
                        allTodoViewModel.addTodo(todoItem)
                        setAlarm(todoItem)
                        todoTitle.setText("")
                        dueDate = Calendar.getInstance().timeInMillis
                        remainderTime = System.currentTimeMillis()
                    } else
                    {
                        Snackbar.make(view, "Task can't be Empty!!", Snackbar.LENGTH_SHORT).show()
                    }
                }

            }

            todoCalendar.setOnClickListener {
                setDueDate(null)
            }

            todoAlarm.setOnClickListener {
                setRemainderTime(null)
            }

            screen.setOnClickListener {
                hideBottomSheet()
            }

            // Bind chipGroup to different filter value
            chipGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.all_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.ALL
                    }
                    R.id.completed_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.COMPLETED
                    }
                    R.id.important_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.IMPORTANT
                    }
                }
            }

        }

        // Using BackPressed in AllTodoFragment
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        hideBottomSheet()
                        return
                    }
                    if (isEnabled) {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
            )

    }

    /**
     * Set up todo recyclerview
     *
     * 1. Set up allTodoAdapter
     * 2. Set up ItemTouchHelper to enable swipe to delete
     *
     */
    private fun setUpTodoRecyclerview()
    {
        allTodoAdapter = TodoAdapter(this)

        binding.allTodoRecyclerview.apply {
            adapter = allTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // TODO(Figure out how to do drag while syncing with room database)
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean
            {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                val todoItem = allTodoAdapter.currentList[viewHolder.adapterPosition]
                allTodoViewModel.removeTodo(todoItem)
                showUndoSnackBar(todoItem)
            }
        }).attachToRecyclerView(binding.allTodoRecyclerview)
    }

    /**
     * Show undo snack bar when a todoItem has been swiped to delete
     *
     * @param todoItem
     */
    private fun showUndoSnackBar(todoItem: TodoItem)
    {
        val snackBar =
            Snackbar.make(binding.addTodoButton, "${todoItem.title} Removed", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    allTodoViewModel.addTodo(todoItem)
                }
        snackBar.anchorView = binding.addTodoButton

        snackBar.show()
    }

    private fun setDueDate(todoItem: TodoItem?)
    {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, day)

            alarmCalendar.set(Calendar.YEAR, year)
            alarmCalendar.set(Calendar.MONTH, month)
            alarmCalendar.set(Calendar.DAY_OF_MONTH, day)
            dueDate = alarmCalendar.timeInMillis

            if (todoItem != null) {
                allTodoViewModel.updateTodoDueDate(todoItem.id, dueDate)
                setAlarm(todoItem)
            }
        }

        Util.showDatePicker(
            todoItem = todoItem, context = requireContext(),
            datePickerListener = datePickerListener
        )
    }

    private fun setRemainderTime(todoItem: TodoItem?)
    {
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmCalendar.set(Calendar.MINUTE, minute)
            alarmCalendar.set(Calendar.SECOND, 0)

            remainderTime = alarmCalendar.timeInMillis
            dueDate = alarmCalendar.timeInMillis

            if (todoItem != null) {
                allTodoViewModel.updateTodoDueDateTime(todoItem.id, dueDate, remainderTime)
                if (!todoItem.completed) {
                    setAlarm(todoItem)
                }
            }
        }

        Util.showTimePicker(
            todoItem = todoItem, context = requireContext(),
            timePickerListener = timePickerListener
        )
    }

    private fun setAlarm(todoItem: TodoItem)
    {
        Util.setAlarm(
            todoItem = todoItem, context = requireContext(),
            alarmCalendar = alarmCalendar, view = binding.root
        )
    }

    private fun cancelAlarm(todoItem: TodoItem)
    {
        Util.cancelAlarm(todoItem = todoItem, context = requireContext())
    }

    /**
     * Show bottom sheet
     * Change addTodoButton style when bottom sheet has been opened
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showBottomSheet()
    {
        binding.apply {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            addTodoButton.text = requireContext().resources.getString(R.string.create)
            addTodoButton.gravity = Gravity.END
            addTodoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_submit, 0, 0, 0)
            screen.visibility = View.VISIBLE
        }
    }

    /**
     * Hide bottom sheet
     * Change addTodoButton style back
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun hideBottomSheet()
    {
        binding.apply {
            screen.visibility = View.GONE
            addTodoButton.text = requireContext().resources.getString(R.string.add_task)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            addTodoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
        }
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

//  ------------------Following Functions are override functions of AddEditTask

    override fun updateTodoDate(todoItem: TodoItem)
    {
        setDueDate(todoItem)
    }

    override fun updateTodoTime(todoItem: TodoItem)
    {
        setRemainderTime(todoItem)
    }

    /**
     * Update todo completion status
     * Remeber to update alarm when completion status has changed
     *
     * @param todoItem
     * @param completed
     */
    override fun updateTodoCompletion(todoItem: TodoItem, completed: Boolean)
    {
        allTodoViewModel.updateTodoCompletion(todoItem.id, completed)
        if (completed)
        {
            cancelAlarm(todoItem)
        } else
        {
            setAlarm(todoItem)
        }
    }

    override fun updateTodoImportance(todoItem: TodoItem, important: Boolean)
    {
        allTodoViewModel.updateTodoImportance(todoItem.id, important)
    }

    /**
     * Remove todo item
     * Remember to cancel alarm afterwards
     *
     * @param todoItem
     */
    override fun removeTodo(todoItem: TodoItem)
    {
        allTodoViewModel.removeTodo(todoItem)
        cancelAlarm(todoItem)
    }

    /**
     * Edit todo
     * Remember to send todoItem to EditTodoFragmentArgs
     *
     * @param todoItem
     */
    override fun editTodo(todoItem: TodoItem)
    {
        val action = AllTodoFragmentDirections.actionAllTodoFragmentToAddEditTodoFragment(todoItem)
        findNavController().navigate(action)
    }

    override fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean)
    {
        allTodoViewModel.updateSubTaskCompletion(todoItem, position, isChecked)
    }

    /**
     * Remove one sub-task by sending a whole new list
     *
     * @param todoItemId
     * @param tasks : Current sub-task list
     */
    override fun removeSubTask(todoItemId: Int, tasks: List<Task>)
    {
        allTodoViewModel.updateTodoTasks(todoItemId, tasks)
    }

}