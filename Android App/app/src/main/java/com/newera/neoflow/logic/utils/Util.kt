package com.newera.neoflow.logic.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.View
import androidx.core.content.ContextCompat
import com.newera.neoflow.R
import com.google.android.material.snackbar.Snackbar
import com.newera.neoflow.data.models.TodoItem
import com.newera.neoflow.logic.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utils Functions
 * Mainly include useful functions for time and calendar
 *
 * @constructor Create empty Util
 */
object Util
{

    /**
     * Format time into 12-hour format
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: Long): String
    {
        val dateFormat = SimpleDateFormat("hh:mm a")
        return dateFormat.format(time)
    }

    /**
     * Format time into 24-hour format
     *
     * @param time
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun formatTimePicker(time: Long): String
    {
        val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(time)
    }

    /**
     * Format date to day/month/year
     *
     * @param dueDate
     * @return
     */
    fun formatDate(dueDate: Long): String
    {
        val newDay = DateFormat.format("dd", dueDate)
        val monthNumber = DateFormat.format("MM", dueDate)
        val newYear = DateFormat.format("yyyy", dueDate)

        return "$newDay/$monthNumber/$newYear"
    }

    /**
     * Check if dueDate has already passed
     *
     * @param dueDate
     * @return 1 : dueDate has already passed
     *         0 : dueDate hasn't passed
     */
    fun isTodoDateLessOrEqual(dueDate: Long): Boolean
    {
        val calendar = Calendar.getInstance()
        val alarm = Calendar.getInstance()
        alarm.timeInMillis = dueDate

        return calendar.timeInMillis >= alarm.timeInMillis
    }

    /**
     * Show date picker
     *
     * @param todoItem current todoItem
     * @param context
     * @param datePickerListener pre-defined listener after the date has been set
     */
    fun showDatePicker(todoItem: TodoItem?, context: Context, datePickerListener: DatePickerDialog.OnDateSetListener)
    {
        val calendar = Calendar.getInstance()
        var mDay = calendar.get(Calendar.DAY_OF_MONTH)
        var mMonth = calendar.get(Calendar.MONTH)
        var mYear = calendar.get(Calendar.YEAR)

        if (todoItem != null) {
            mYear = DateFormat.format("yyyy", todoItem.dueDate).toString().toInt()
            mMonth = DateFormat.format("MM", todoItem.dueDate).toString().toInt()-1
            mDay = DateFormat.format("dd", todoItem.dueDate).toString().toInt()
        }
        val datePickerDialog = DatePickerDialog(context, R.style.MyDatePicker,
            datePickerListener, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.light_blue))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.light_blue))
    }

    /**
     * Show time picker
     *
     * @param todoItem current todoItem
     * @param context
     * @param timePickerListener pre-defined listener after the time has been set
     */
    fun showTimePicker(todoItem: TodoItem?, context: Context, timePickerListener: TimePickerDialog.OnTimeSetListener)
    {
        val mCalendar = Calendar.getInstance()
        var pickerHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        var pickerMinute = mCalendar.get(Calendar.MINUTE)
        if (todoItem != null) {
            val formattedTime = formatTimePicker(todoItem.remainderTime)
            pickerHour = formattedTime.substring(0, 2).toInt()
            pickerMinute = formattedTime.substring(3, 5).toInt()
        }

        val timePicker = TimePickerDialog(context, timePickerListener,
            pickerHour, pickerMinute, true
        )
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.light_blue))
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.light_blue))
    }

    /**
     * Set alarm
     *
     * @param todoItem
     * @param context
     * @param alarmCalendar
     * @param view
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun setAlarm(todoItem: TodoItem, context: Context, alarmCalendar: Calendar, view: View)
    {
        val new = Calendar.getInstance()
        if (alarmCalendar.timeInMillis < new.timeInMillis)
        {
            Snackbar.make(view, "Set Correct Alarm Time", Snackbar.LENGTH_SHORT).show()
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoItem.title)

        val intentId = todoItem.createdAt
        val pendingIntent = PendingIntent.getBroadcast(context, intentId.toInt(),
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIntent)
    }

    /**
     * Cancel alarm
     *
     * @param todoItem
     * @param context
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun cancelAlarm(todoItem: TodoItem, context: Context)
    {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoItem.title)
        val intentId = todoItem.createdAt
        val pendingIntent = PendingIntent.getBroadcast(context, intentId.toInt(),
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

}
