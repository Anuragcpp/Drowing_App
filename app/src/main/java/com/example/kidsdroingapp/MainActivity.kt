package com.example.kidsdroingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var drowingView : DrowingView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drowingView = findViewById(R.id.drawing_view)
        drowingView.setSizeForBrush(20.toFloat())
    }
}