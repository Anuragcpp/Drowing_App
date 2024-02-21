package com.example.kidsdroingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrowingView (contex : Context, attrs : AttributeSet) : View(contex,attrs) {

    private lateinit var mDrowPath : CustomPath
    private lateinit var mCanvasBitmp : Bitmap
    private lateinit var mDrowPaint : Paint
    private lateinit var mCanvasPaint : Paint
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private lateinit var canvas : Canvas
    private val mPaths = ArrayList<CustomPath>()

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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmp = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmp)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmp,0f,0f,mCanvasPaint)
         //This loop is to hold all the stork that are drown in the screen, witheout
        // this we can  only drow one line at time and when we lift our hand it vanish
        for (path in mPaths){
            mDrowPaint.strokeWidth = path.brushThickness
            mDrowPaint.color = path.color
            canvas.drawPath(path,mDrowPaint)
        }

        if (!mDrowPath.isEmpty){
            mDrowPaint.strokeWidth = mDrowPath.brushThickness
            mDrowPaint.color =mDrowPaint.color
            canvas.drawPath(mDrowPath,mDrowPaint)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){

            //when we put our finger in the screen then this fuction will be called
            MotionEvent.ACTION_DOWN -> {
                mDrowPath.color = color
                mDrowPath.brushThickness= mBrushSize

                mDrowPath.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrowPath.moveTo(touchX,touchY)
                    }
                }
            }
            //when we move our finger in the screen then this action will perform
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrowPath.lineTo(touchX,touchY)
                    }
                }
            }

            //when we remove out finger from the screen then this action will be called
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrowPath)
                mDrowPath = CustomPath(color,mBrushSize)
            }

            else -> return false

        }

        invalidate()

        return true
    }

    internal inner class CustomPath (
        var color: Int,
        var brushThickness : Float)
        : Path(){

    }

}