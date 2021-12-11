package com.newera.neoflow.logic.repository

import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.logic.dao.TodoDao
import javax.inject.Inject

/**
 * Todo repository
 * Interact with the database using data access objects
 * Available for dependency injection
 * Contain all methods for AllTodoViewModel as well as EditTodoViewModel
 *
 * @property todoDao
 * @constructor Create empty Todo repository
 */
class TodoRepository @Inject constructor(private val todoDao: TodoDao)
{

    suspend fun addTodo(todoItem: TodoItem) = todoDao.addTodo(todoItem)

    suspend fun updateTodo(todoItem: TodoItem) = todoDao.updateTodo(todoItem)

    suspend fun updateTodoChecked(todoItemId: Int, completed: Boolean) =
        todoDao.updateTodoChecked(todoItemId, completed)

    suspend fun updateTodoImportant(todoItemId: Int, important: Boolean) =
        todoDao.updateTodoImportant(todoItemId, important)

    suspend fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        todoDao.updateTodoTasks(todoItemId, tasks)

    suspend fun updateTodoTime(todoItemId: Int, remainderTime: Long) =
        todoDao.updateTodoTime(todoItemId, remainderTime)

    suspend fun updateTodoDueDate(todoItemId: Int, dueDate: Long) =
        todoDao.updateTodoDueDate(todoItemId, dueDate)

    suspend fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) =
        todoDao.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)

    suspend fun removeTodo(todoItem: TodoItem) = todoDao.removeTodo(todoItem)

    fun getTodoById(todoItemId: Int) = todoDao.getTodoById(todoItemId)

    fun getAllTodos() = todoDao.getAllTodos()

}
