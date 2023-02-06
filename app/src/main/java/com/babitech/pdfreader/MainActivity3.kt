package com.babitech.pdfreader

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.babitech.pdfreader.Pdf_listener_file
import com.babitech.pdfreader.pdfAdapter
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import com.babitech.pdfreader.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionDeniedResponse
import android.widget.Toast
import com.karumi.dexter.PermissionToken
import androidx.recyclerview.widget.GridLayoutManager
import android.os.Environment
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.babitech.pdfreader.DocumentActivity
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.ArrayList

class MainActivity3 : AppCompatActivity(), Pdf_listener_file {
    private var pdfAdapter: pdfAdapter? = null
    private var pdfList: MutableList<File>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        runtimePermissions()
    }

    private fun runtimePermissions() {
        Dexter.withContext(this@MainActivity3)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    displaypdf()
                }

                override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {
                    Toast.makeText(
                        this@MainActivity3,
                        "la permission est necessaire pour continuer",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissionRequest: PermissionRequest,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).check()
    }

    fun findpdf(file: File): ArrayList<File> {
        val arrayList = ArrayList<File>()
        val files = file.listFiles()
        for (singlefile in files) {
            if (singlefile.isDirectory && !singlefile.isHidden) {
                arrayList.addAll(findpdf(singlefile))
            } else {
                if (singlefile.name.endsWith(".pdf")) {
                    arrayList.add(singlefile)
                }
            }
        }
        return arrayList
    }

    private fun displaypdf() {
        my_recyclerview.setHasFixedSize(true)
        my_recyclerview.layoutManager = LinearLayoutManager(this, )
        pdfList = ArrayList()
        (pdfList as ArrayList<File>).addAll(
            findpdf(
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/" + Environment.DIRECTORY_DOWNLOADS + "/"
                )
            )
        )
        pdfAdapter = pdfAdapter(this, pdfList, this)
        my_recyclerview.adapter = pdfAdapter
    }

    override fun onSelected(file: File) {
        startActivity(
            Intent(this, DocumentActivity::class.java)
                .putExtra("path", file.absolutePath)
        )
    }
}