package com.example.kidsdroingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.TtsSpan
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.kidsdroingapp.databinding.ActivityColorViewBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.AlphaSlideBar
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar

class ColorView : AppCompatActivity() {
    private lateinit var binding: ActivityColorViewBinding
    private lateinit var colorPickerView: ColorPickerView
    private lateinit var brightnessSlideBar: BrightnessSlideBar
    private lateinit var alphaSlideBar: AlphaSlideBar
    private lateinit var t1 : TextView
    private lateinit var linearLayout: LinearLayout
    private lateinit var doneBtn : Button
    private lateinit var colorCodeValue : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_color_view)
        binding = ActivityColorViewBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        colorPickerView = binding.colorPickerView

        //defing the brighness bar
        brightnessSlideBar = binding.brightnessSlide
        colorPickerView.attachBrightnessSlider(brightnessSlideBar)

        //defing the alpha betting
        alphaSlideBar = binding.alphaSlideBar
        colorPickerView.attachAlphaSlider(alphaSlideBar)
        t1 = binding.colorcode
        linearLayout = binding.colorLyout

        colorPickerView.setColorListener(object  : ColorEnvelopeListener {
            override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                envelope?.let {
                    linearLayout.setBackgroundColor(it.color)
                    t1.text = it.hexCode.toString()
                    colorCodeValue = it.hexCode.toString()
                }
            }
        })

        doneBtn = binding.doneBtn
        doneBtn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("colorCode",colorCodeValue)
            startActivity(intent)
            finish()
        }

    }
}