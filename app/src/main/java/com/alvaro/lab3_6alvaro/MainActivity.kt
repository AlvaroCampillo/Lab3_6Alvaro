package com.alvaro.lab3_6alvaro

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import org.w3c.dom.Text
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var tvResult:TextView
    lateinit var buttonchoose: Button
    var intentActivityResultLauncher:ActivityResultLauncher<Intent>?=null

    lateinit var inputImage:InputImage
    lateinit var textRecognizer:TextRecognizer


    private val STORAGE_PERMISSION_CODE=110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult=findViewById(R.id.tvResult)
        buttonchoose=findViewById(R.id.buttonchoose)

        textRecognizer=TextRecognition.getClient{TextRecognizerOptions.DEFAULT_OPTIONS}

        intentActivityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback{
                //result
                val data=it.data
                val imageUri=data?.data

                convertImageToText(imageUri)
            }

        )

        buttonchoose.setOnClickListener{
            val chooseIntent= Intent()
            chooseIntent.type="image/*"
            chooseIntent.action=Intent.ACTION_GET_CONTENT
            intentActivityResultLauncher?.launch(chooseIntent)
        }
    }

    private fun convertImageToText(imageUri: Uri?) {
        try{
        //input image
            inputImage=InputImage.fromFilePath(applicationContext,imageUri)

            // get text from the picture
            val result: Task<Text>=textRecognizer.process(inputImage)
                .addOnSuccessListener{
                    tvResult.text=it.text
                }.addOnFailureListener{
                    tvResult.text="Error: ${it.message}"
            }
        }catch (e:Exception){

        }
    }

    override fun onResume() {
        super.onResume()
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
    }
    private fun checkPermission(permission:String,requestCode:Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        else {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Storage perm. granted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Storage perm. denied",Toast.LENGTH_SHORT).show()
            }
        }
    }


}