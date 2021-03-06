package com.newera.neoflow.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.newera.neoflow.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
/**
 * Main activity
 * Just start the view here
 * Implementation in Fragments
 *
 * @constructor Create empty Main activity
 */
class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}