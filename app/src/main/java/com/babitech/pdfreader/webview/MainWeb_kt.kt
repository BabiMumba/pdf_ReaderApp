package com.babitech.pdfreader.webview

import android.Manifest
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar
import android.app.ProgressDialog
import android.widget.RelativeLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.os.Bundle
import android.view.WindowManager
import com.babitech.pdfreader.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.PermissionGrantedResponse
import android.app.DownloadManager
import android.os.Environment
import android.widget.Toast
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.PermissionToken
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Button
import com.karumi.dexter.listener.PermissionRequest

class MainWeb_kt : AppCompatActivity() {
    var webView: WebView? = null
    private val webUrl =
        "https://zestedesavoir.com/tutoriels/pdf/1140/communication-entre-android-et-php-mysql.pdf"
    var progressBarWeb: ProgressBar? = null
    var progressDialog: ProgressDialog? = null
    var relativeLayout: RelativeLayout? = null
    var btnNoInternetConnection: Button? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_web)
        webView = findViewById<View>(R.id.myWebView) as WebView
        progressBarWeb = findViewById<View>(R.id.progressBar) as ProgressBar
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading Please Wait")
        btnNoInternetConnection = findViewById<View>(R.id.btnNoConnection) as Button
        relativeLayout = findViewById<View>(R.id.relativeLayout) as RelativeLayout
        swipeRefreshLayout = findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout!!.setColorSchemeColors(Color.BLUE, Color.YELLOW, Color.GREEN)
        swipeRefreshLayout!!.setOnRefreshListener { webView!!.reload() }
        if (savedInstanceState != null) {
            webView!!.restoreState(savedInstanceState)
        } else {
            webView!!.settings.javaScriptEnabled = true
            webView!!.settings.loadWithOverviewMode = true
            webView!!.settings.useWideViewPort = true
            webView!!.settings.setSupportZoom(true)
            webView!!.settings.builtInZoomControls = true
            webView!!.settings.domStorageEnabled = true
            webView!!.settings.loadsImagesAutomatically = true
            checkConnection()
        }

        //Solved WebView SwipeUp Problem
        webView!!.viewTreeObserver.addOnScrollChangedListener {
            if (webView!!.scrollY == 0) {
                swipeRefreshLayout!!.isEnabled = true
            } else {
                swipeRefreshLayout!!.isEnabled = false
            }
        }
        webView!!.setDownloadListener { s: String?, s1: String?, s2: String?, s3: String?, l: Long ->
            Dexter.withActivity(this@MainWeb_kt)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val request = DownloadManager.Request(Uri.parse(s))
                        request.setMimeType(s3)
                        val cookies = CookieManager.getInstance().getCookie(s)
                        request.addRequestHeader("cookie", cookies)
                        request.addRequestHeader("User-Agent", s1)
                        request.setDescription("Downloading File.....")
                        request.setTitle(URLUtil.guessFileName(s, s2, s3))
                        request.allowScanningByMediaScanner()
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                s, s2, s3
                            )
                        )
                        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                        Toast.makeText(this@MainWeb_kt, "Downloading File..", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
        webView!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                swipeRefreshLayout!!.isRefreshing = false
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progressBarWeb!!.visibility = View.VISIBLE
                progressBarWeb!!.progress = newProgress
                title = "Loading..."
                progressDialog!!.show()
                if (newProgress == 100) {
                    progressBarWeb!!.visibility = View.GONE
                    title = view.title
                    progressDialog!!.dismiss()
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        btnNoInternetConnection!!.setOnClickListener { view: View? -> checkConnection() }
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure you want to Exit?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes") { dialogInterface: DialogInterface?, i: Int -> finishAffinity() }
                .show()
        }
    }

    fun checkConnection() {
        val connectivityManager = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifi!!.isConnected) {
            webView!!.loadUrl("https://docs.google.com/gview?embedded=true&url=$webUrl")
            webView!!.visibility = View.VISIBLE
            relativeLayout!!.visibility = View.GONE
        } else if (mobileNetwork!!.isConnected) {
            webView!!.loadUrl(webUrl)
            webView!!.visibility = View.VISIBLE
            relativeLayout!!.visibility = View.GONE
        } else {
            webView!!.visibility = View.GONE
            relativeLayout!!.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_previous -> onBackPressed()
            R.id.nav_next -> if (webView!!.canGoForward()) {
                webView!!.goForward()
            }
            R.id.nav_reload -> checkConnection()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView!!.saveState(outState)
    }
}