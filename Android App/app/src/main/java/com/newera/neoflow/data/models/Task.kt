package com.newera.neoflow.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Sub-Task for Todo Item
 *
 * @property id
 * @property title
 * @property isCompleted
 * @constructor Create empty Task
 */
@Parcelize
data class Task(
    var id: Int ?= 0,
    var title: String,
    var isCompleted: Boolean = false
) : Parcelable
