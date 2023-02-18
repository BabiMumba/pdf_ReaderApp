package com.babitech.pdfreader.new_reader

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Environment
import android.widget.EditText
import android.os.Bundle
import com.babitech.pdfreader.R
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.view.View
import android.widget.Button
import java.io.File
import java.io.IOException

class Create_folder : AppCompatActivity() {
    lateinit var createFile: Button
    lateinit var createFolder: Button
    var theDir = Environment.getExternalStorageDirectory().toString() + "/Download/"
    private lateinit var one: EditText
    private lateinit var two: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        one = findViewById(R.id.fileName)
        two = findViewById(R.id.folderName)
        createFile = findViewById(R.id.createFile)
        createFolder = findViewById(R.id.createFolder)

        createFile.setOnClickListener(View.OnClickListener { v: View? ->
            if (one.getText().toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter a file name", Toast.LENGTH_SHORT)
                    .show()
            } else _createFile(File(theDir + one.getText().toString()))
        })
        createFolder.setOnClickListener(View.OnClickListener { v: View? ->
            if (two.getText().toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter a folder name", Toast.LENGTH_SHORT)
                    .show()
            } else _createFolder(File(theDir + two.getText().toString()))
        })

        //Check if app has permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestWrite()
        }
    }

    //Request permission
    fun requestWrite() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    fun _createFile(file: File) {
        if (!file.exists()) {
            try {
                val succ = file.createNewFile()
                if (succ) {
                    Toast.makeText(applicationContext, "File Created", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(applicationContext, "Error creating file", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (file.exists()) {
            Toast.makeText(applicationContext, "File already exists!", Toast.LENGTH_SHORT).show()
        }
    }

    fun _createFolder(file: File) {
        if (!file.exists()) {
            val succ = file.mkdir()
            if (succ) {
                Toast.makeText(applicationContext, "Folder Created", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(applicationContext, "Error creating folder", Toast.LENGTH_SHORT)
                .show()
        } else if (file.exists()) {
            Toast.makeText(applicationContext, "Folder already exists!", Toast.LENGTH_SHORT).show()
        }
    }
}