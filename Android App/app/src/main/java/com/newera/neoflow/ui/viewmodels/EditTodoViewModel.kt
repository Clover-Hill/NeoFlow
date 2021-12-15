package com.newera.neoflow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.logic.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
/**
 * Edit-todo viewmodel
 * 1. Caution that in the database-logic layer, we use flow to perform the transfer
 * However, in the logic-ui layer, we transform flow to liveData to have lifecycle awareness as well as better efficiency
 *
 * 2. In addition, all the update operation are realized by coroutines
 *
 * 3. This viewModel has been provided for dependency injection
 *
 * @property todoRepository
 * @constructor Create empty Edit todo view model
 */
class EditTodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel()
{

    fun getTodoById(todoItemId: Int) = todoRepository.getTodoById(todoItemId)

    /**
     * Get sub-task list
     * First we need to transform the flow for todoItem.tasks
     * Then we transform it to LiveData to observe it
     *
     * @param todoItemId
     */
    @ExperimentalCoroutinesApi
    fun getTodoList(todoItemId: Int) = getTodoById(todoItemId).flatMapLatest {
        val taskFlow = MutableStateFlow(it.tasks)
        taskFlow
    }

    fun updateTodoTasks(todoItemId: Int, subTasks: List<Task>) = viewModelScope.launch {
        todoRepository.updateTodoTasks(todoItemId, subTasks)
    }

    fun updateTodoTime(todoItemId: Int, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)
    }

    fun updateSubTaskCompletion(todoItemId: Int, position: Int, isChecked: Boolean, tasks: List<Task>) =
        viewModelScope.launch {
            tasks[position].isCompleted = isChecked
            todoRepository.updateTodoTasks(todoItemId = todoItemId, tasks = tasks)
        }

}
