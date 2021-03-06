package com.newera.neoflow.logic.dao

import androidx.room.*
import com.newera.neoflow.data.models.Task
import com.newera.neoflow.data.models.TodoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Dao
/**
 * Data access objects
 * Define all logic to interact with TodoDatabase
 * @constructor Create empty Todo dao
 */
interface TodoDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todoItem: TodoItem)

    @Update
    suspend fun updateTodo(todoItem: TodoItem)

    @Query("UPDATE todo_table SET title =:title WHERE id = :todoItemId")
    suspend fun updateTodoTitle(todoItemId: Int, title: String)

    @Query("UPDATE todo_table SET completed = :completed WHERE id = :todoItemId")
    suspend fun updateTodoChecked(todoItemId: Int, completed: Boolean)

    @Query("UPDATE todo_table SET important = :important WHERE id = :todoItemId")
    suspend fun updateTodoImportant(todoItemId: Int, important: Boolean)

    @Query("UPDATE todo_table SET tasks =:tasks WHERE id = :todoItemId")
    suspend fun updateTodoTasks(todoItemId: Int, tasks: List<Task>)

    @Query("UPDATE todo_table SET remainderTime = :remainderTime WHERE id = :todoItemId")
    suspend fun updateTodoTime(todoItemId: Int, remainderTime: Long)

    @Query("UPDATE todo_table SET dueDate = :dueDate WHERE id = :todoItemId")
    suspend fun updateTodoDueDate(todoItemId: Int, dueDate: Long)

    @Query("UPDATE todo_table SET dueDate = :dueDate, remainderTime = :remainderTime WHERE id = :todoItemId")
    suspend fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long)

    @Delete
    suspend fun removeTodo(todoItem: TodoItem)

    @Query("SELECT * from todo_table WHERE id = :todoId")
    fun getTodoById(todoId: Int): Flow<TodoItem>

    @Query("SELECT * from todo_table ORDER BY important DESC, createdAt")
    fun getAllTodos(): Flow<List<TodoItem>>

}
