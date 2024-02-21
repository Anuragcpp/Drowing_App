package com.example.kidsdroingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class DrowingView (contex : Context, attrs : AttributeSet) : View(contex,attrs) {

    private lateinit var mDrowPath : CustomPath
    private lateinit var mCanvasBitmp : Bitmap
    private lateinit var mDrowPaint : Paint
    private lateinit var mCanvasPaint : Paint
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private lateinit var canvas : Canvas

    init {
        setUPDrowing()
    }

    private fun setUPDrowing() {
        mDrowPaint = Paint()
        mDrowPath = CustomPath(color,mBrushSize)
        mDrowPaint.color = color
        mDrowPaint.style = Paint.Style.STROKE
        mDrowPaint.strokeJoin = Paint.Join.ROUND
        mDrowPaint.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    internal inner class CustomPath (
        var color: Int,
        var brushThickness : Float)
        : Path(){

    }

}