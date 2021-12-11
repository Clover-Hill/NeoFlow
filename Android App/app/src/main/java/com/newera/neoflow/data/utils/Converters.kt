package com.newera.neoflow.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.newera.neoflow.data.models.Task
import java.util.*

/**
 * Converters
 * Realize data conversion for Database
 * @constructor Create empty Converters
 */
class Converters
{

    /**
     * List<Task> to String
     *
     * @param value,List<Task>
     * @return String
     */
    @TypeConverter
    fun taskToJson(value: List<Task>?): String = Gson().toJson(value)

    /**
     * String to List<Task>
     *
     * @param value,String
     * @return List<Task>
     */
    @TypeConverter
    fun jsonToTask(value: String) = Gson().fromJson(value, Array<Task>::class.java).toList()

}
