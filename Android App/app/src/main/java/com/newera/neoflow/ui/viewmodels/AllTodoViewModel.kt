package com.newera.neoflow.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.newera.neoflow.data.models.Filter
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.logic.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTodoViewModel @Inject constructor(private val todoRepository: TodoRepository) : ViewModel() {

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.addTodo(todoItem)
    }

    /**
     * Flow for all Todos
     */
    private val allTodos = todoRepository.getAllTodos()

    /**
     * Flow for completed Todos
     */
    private val completedTodo = todoRepository.getAllTodos().map { list ->
        list.filter { todoItem -> todoItem.completed }
    }

    /**
     * Flow for important Todos
     */
    private val importantTodo = todoRepository.getAllTodos().map { list ->
        list.filter { todoItem -> todoItem.important }
    }

    /**
     * Filter for todoItems
     * Possible State:
     * 1. Filter.ALL
     * 2. Filter.COMPLETED
     * 3. Filter.IMPORTANT
     */
    val todoFilter = MutableStateFlow(Filter.ALL)

    @ExperimentalCoroutinesApi
    private val todoListFlow = todoFilter.flatMapLatest { filter ->
        when (filter) {
            Filter.ALL -> allTodos
            Filter.COMPLETED -> completedTodo
            Filter.IMPORTANT -> importantTodo
        }
    }

    /**
     * Interface for fragments
     * In fragments, first set todoFilter, then call this todoList to get the filtered list
     */
    @ExperimentalCoroutinesApi
    val todoList = todoListFlow.asLiveData()

    fun updateTodoTime(todoItemId: Int, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)
    }

    fun updateTodoCompletion(todoItemId: Int, completed: Boolean) =
        viewModelScope.launch {
            todoRepository.updateTodoChecked(todoItemId, completed)
        }

    fun updateTodoImportance(todoItemId: Int, important: Boolean) =
        viewModelScope.launch {
            todoRepository.updateTodoImportant(todoItemId, important)
        }

    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean) =
        viewModelScope.launch {
            val tasks = todoItem.tasks
            tasks[position].isCompleted = isChecked
            todoItem.tasks = tasks
            todoRepository.updateTodo(todoItem)
        }

    fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        viewModelScope.launch {
            todoRepository.updateTodoTasks(todoItemId, tasks)
        }

    fun removeTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.removeTodo(todoItem)
    }

}
