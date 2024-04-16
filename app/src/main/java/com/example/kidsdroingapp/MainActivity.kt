package com.example.kidsdroingapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
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
import androidx.lifecycle.lifecycleScope
import com.example.kidsdroingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    private lateinit var undoBtn : ImageButton
    private lateinit var redoBtn : ImageButton
    private lateinit var saveBtn : ImageButton
    private  var customProgressDialog : Dialog? = null


    private val colorViewReqCode : Int = 100

    // the app was crashing due to lateinit property not initialized so I declared it earlier
    private  var colorCode : String = "000000"

    private val openGalleryLoncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK && result.data?.data != null){
                val imageBackgroud : ImageView = findViewById(R.id.iv_background);
                imageBackgroud.setImageURI(result.data?.data)
            }
        }




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

                        val pickIntent : Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        openGalleryLoncher.launch(pickIntent)
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
//            openColorPicker.launch(intent)
        }



        /// for the gallery btn
        galleryBtn = binding.galleryBtn
        galleryBtn.setOnClickListener {
            requestStoragePermission()
        }


        // undo button
        undoBtn = binding.ibUndo
        undoBtn.setOnClickListener {
            drowingView.onclickUndo()
        }

        //redo button
        redoBtn = binding.ibRedo
        redoBtn.setOnClickListener {
            drowingView.onClickRedo()
        }

        //save button
        saveBtn = binding.ibSave
        saveBtn.setOnClickListener {

            if(isReadStorageAllowed()){
                //displaying a progress dialog to hide the backgrond process, we will dismiss it when saving is done , int eh saveBitmapFile function
                showProgressDialog()
                lifecycleScope.launch {
                    // flDrawingView is the view  where the background image and the canvas where we draw is availvle
                    val flDrawingView : FrameLayout = findViewById(R.id.fl_drawingView_container)

                    // now we are passing the view to the getBitmapFromView function to get the bitmap out of it then we are passing
                    //bitmap to the saveBitmapFile to save the bitmap on the local storage, image are in the form of bitmap
                    saveBitmapFile(getBitmapFromView(flDrawingView))
                    Toast.makeText(this@MainActivity,"clicked",Toast.LENGTH_SHORT).show()
                }
            }
        }





    }

    // converting the view in which we drawing to and bitmap so that we can stort it
    private fun getBitmapFromView(view: View) : Bitmap {

        val returnBitmap =  Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888)

        val canvas : Canvas = Canvas(returnBitmap)
        val bgDrawable = view.background

        if(bgDrawable != null){
            //if background is there then we draw that on the canvas
            bgDrawable.draw(canvas)
        }else{
            // if no background the we draw a white backgraond on the canvas
            canvas.drawColor(Color.WHITE)
        }

        //draw the view on the canvas
        view.draw(canvas)

        return returnBitmap
    }

    //TODO need  to creat a function to save the file or image
    private suspend fun saveBitmapFile(mBitmap: Bitmap?) : String {
        var result : String = ""
        withContext(Dispatchers.IO){
            if(mBitmap != null){
                try {
                    // create a new byte stream initializy its 32 byts
                    val bytes = ByteArrayOutputStream();

                    // here png is the output format , 90 is the quality rating ,90 means good , then we pass the output stream
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)

                    val f = File(
                        externalCacheDir?.absoluteFile.toString() +
                                File.separator +
                                "KidDrawingApp" +
                                System.currentTimeMillis()/1000 +
                                ".png"
                    )

                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = f.absolutePath

                    runOnUiThread{
                        // dismissing the progress dialog
                        cancleProgressBarDialog()

                        
                        if (result.isNotEmpty()){
                            Toast.makeText(this@MainActivity,
                                "File saved successfully : $result",
                                Toast.LENGTH_SHORT
                                ).show()
                        }else{
                            Toast.makeText(this@MainActivity,
                                "Something went wrong while saving the file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }catch (e : Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
        }

        return result

    }

    // custiom progress dialog to rum while something is going on the backgrond
    private fun showProgressDialog (){
        customProgressDialog = Dialog(this)

        // set the layout whithe the custom layout design
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress_bar)

        //show the progress bar
        customProgressDialog?.show()

    }

    private fun cancleProgressBarDialog (){
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }


    // a function to check that if the write external storage permission is granted or not
    private fun isReadStorageAllowed() : Boolean{
        var result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
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

    
    // Aleart dialog for custome massage
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
        if(colorCode != null){
            drowingView.setColor("#$colorCode")
        }

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