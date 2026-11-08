package com.example

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    finish()
                }
            }
        })

        setContent {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                factory = { context ->
                    WebView(context).apply {
                        webView = this
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true

                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false
                                if (url.startsWith("whatsapp://") || url.startsWith("https://wa.me/")) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                        return true
                                    } catch (_: Exception) {
                                        try {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(browserIntent)
                                            return true
                                        } catch (_: Exception) {
                                            return false
                                        }
                                    }
                                }
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }

                        loadUrl("file:///android_asset/index.html")
                    }
                }
            )
        }
    }
}
