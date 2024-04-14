package com.example.kidsdroingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.example.kidsdroingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drowingView : DrowingView
    private lateinit var ib_brush : ImageButton
    private lateinit var smallBtn : ImageView
    private lateinit var mediumBtn : ImageView
    private lateinit var largeBtn : ImageView
    private lateinit var mImageButtonCurrentPaint : ImageButton
    private lateinit var linearLayoutPaintColor : LinearLayout
    private lateinit var ib_brush_color : ImageButton
    private lateinit var binding: ActivityMainBinding
    private lateinit var galleryBtn : ImageButton

    private val colorViewReqCode : Int = 100
    private lateinit var colorCode : String

    //camera and gallery loncher
    private val cameraAndGalleryResultLoncher : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()){

            permissions ->
            //2nd forEach loog
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted){
                    //if permission is grante the we have to check which one is granted
                    if (permissionName == Manifest.permission.CAMERA){
                        Toast.makeText(this,
                            "Permission granted for camera",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else if (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                        //TODO don't know what permission is needed for pick an image form the gallery

                        Toast.makeText(this,
                            "Permission granted for external storage",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }else{
                    //is permission is denied the we have to check which one is been denied
                    if (permissionName == Manifest.permission.CAMERA){
                        Toast.makeText(this,
                            "Permission Denied for camera",
                            Toast.LENGTH_SHORT)
                            .show()
                    } else if (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE){
                        //TODO don't know what permission is needed for pick an image form the gallery

                        Toast.makeText(this,
                            "Permission Denied for external storage",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)


        drowingView = binding.drawingView
        drowingView.setSizeForBrush(5.toFloat())

        linearLayoutPaintColor = binding.llPaintColor
        mImageButtonCurrentPaint = linearLayoutPaintColor[1] as ImageButton
        mImageButtonCurrentPaint.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_prassed)
        )

        ib_brush = binding.ibBrush
        ib_brush.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        ib_brush_color = binding.ibBrushColor
        ib_brush_color.setOnClickListener {
            val intent = Intent(this,ColorView::class.java)
            startActivityForResult(intent,colorViewReqCode)
        }

        /// for the gallery btn
        galleryBtn = binding.galleryBtn
        galleryBtn.setOnClickListener {
            requestStoragePermission()
//            cameraAndGalleryResultLoncher.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//            )
        }





    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )){
            showRationalDialog("Kids drawing App", "Kids Drawing App Needs  to access your external storage")
        }else{
            cameraAndGalleryResultLoncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun showRationalDialog(title : String, message : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setNeutralButton("Cancle"){dialog , _->
            dialog.dismiss()
        }
        builder.setPositiveButton("Ok"){dialog, _->
            dialog.dismiss()
        }
    }


    // I have declared the setColor in this method as when the activity will restart then only we have to set the colour
    override fun onRestart() {
        super.onRestart()
        // setting the color
        drowingView.setColor("#$colorCode")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == colorViewReqCode){
            if (resultCode == Activity.RESULT_OK){
                // initializing the color code from the data recived from the colorActivity
                //to use to set the color in the onRestart method
                colorCode  = data!!.getStringExtra("colorCode").toString()
            }
        }
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