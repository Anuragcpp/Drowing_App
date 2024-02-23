package com.example.kidsdroingapp

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import top.defaults.colorpicker.ColorPickerPopup
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var drowingView : DrowingView
    private lateinit var ib_brush : ImageButton
    private lateinit var smallBtn : ImageView
    private lateinit var mediumBtn : ImageView
    private lateinit var largeBtn : ImageView
    private lateinit var mImageButtonCurrentPaint : ImageButton
    private lateinit var linearLayoutPaintColor : LinearLayout


    //for the color picker
    private lateinit var mPickColorButton : ImageButton
    private var mDefaultColor by Delegates.notNull<Int>()
    private lateinit var mColorPreview : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drowingView = findViewById(R.id.drawing_view)
        drowingView.setSizeForBrush(5.toFloat())

        linearLayoutPaintColor = findViewById(R.id.ll_paint_color)
        mImageButtonCurrentPaint = linearLayoutPaintColor[1] as ImageButton
        mImageButtonCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_prassed)
        )

        ib_brush = findViewById(R.id.ib_brush)
        ib_brush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        //for the color picker
//        mColorPreview = findViewById(R.id.preview_selected_color)
//        mDefaultColor = 0
//        mPickColorButton = findViewById(R.id.ib_mPickColorButton)
//        mPickColorButton.setOnClickListener (
//            object  : OnClickListener {
//                override fun onClick(v: View?) {
//                    ColorPickerPopup.Builder(this@MainActivity).initialColor(
//                        Color.RED
//                    ) // set initial color
//                        // of the color
//                        // picker dialog
//                        .enableBrightness(
//                            true
//                        ) // enable color brightness
//                        // slider or not
//                        .enableAlpha(
//                            true
//                        ) // enable color alpha
//                        // changer on slider or
//                        // not
//                        .okTitle(
//                            "Choose"
//                        ) // this is top right
//                        // Choose button
//                        .cancelTitle(
//                            "Cancel"
//                        ) // this is top left
//                        // Cancel button which
//                        // closes the
//                        .showIndicator(
//                            true
//                        ) // this is the small box
//                        // which shows the chosen
//                        // color by user at the
//                        // bottom of the cancel
//                        // button
//                        .showValue(
//                            true
//                        ) // this is the value which
//                        // shows the selected
//                        // color hex code
//                        // the above all values can be made
//                        // false to disable them on the
//                        // color picker dialog.
//                        .build()
//                        .show(
//                            v,
//                            object : ColorPickerPopup.ColorPickerObserver() {
//                                override fun onColorPicked(color: Int) {
//                                    // set the color
//                                    // which is returned
//                                    // by the color
//                                    // picker
//                                    mDefaultColor = color
//
//                                    // now as soon as
//                                    // the dialog closes
//                                    // set the preview
//                                    // box to returned
//                                    // color
//                                    mColorPreview.setBackgroundColor(mDefaultColor)
//                                }
//                            })
//                }
//            }
//
//        )
    }

    private fun showBrushSizeChooserDialog (){
        //creating a Dialog class object , and passing the context to this
        val brushDialog = Dialog(this)

        //we are defining that the dialog_brush_size is the Dialog
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size")
        smallBtn = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drowingView.setSizeForBrush(5.toFloat())
            brushDialog.dismiss()
        }

        mediumBtn = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drowingView.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        largeBtn = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drowingView.setSizeForBrush(15.toFloat())
            brushDialog.dismiss()
        }

        //to display the dialog in the screen we need this code
        brushDialog.show()
    }

    fun paintclicked(view : View){
        if (view !== mImageButtonCurrentPaint){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drowingView.setColor(colorTag)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_prassed)
            )

            mImageButtonCurrentPaint.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.pallet_normal)
            )

            mImageButtonCurrentPaint = view
        }
    }
}