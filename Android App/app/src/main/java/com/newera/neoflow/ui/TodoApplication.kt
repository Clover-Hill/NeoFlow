package com.newera.neoflow.ui

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
/**
 * Provide dependency injection for some objects that can't be set up in constructors
 *
 * @constructor Create empty Todo application
 */
class TodoApplication: Application()