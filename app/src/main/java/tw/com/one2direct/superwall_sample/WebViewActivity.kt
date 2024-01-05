package tw.com.one2direct.superwall_sample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


@SuppressLint("SetJavaScriptEnabled")
class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        intent.getStringExtra("url")?.let { webViewInit(it) }
        findViewById<android.widget.Button>(R.id.buttonClose).setOnClickListener { finish() }//請實作關閉任務牆
    }

    private fun webViewInit(url: String) {
        val webView= findViewById<WebView>(R.id.webView)
        webView.settings.javaScriptEnabled = true// 必須啟用JavaScript
        webView.settings.domStorageEnabled = true// 必須啟用domStorage
        webView.settings.javaScriptCanOpenWindowsAutomatically=true
        webView.settings.setSupportMultipleWindows(true)
        webView.settings.supportMultipleWindows()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false
                }
                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                startActivity(intent)
                return true
            }
        }
        webView.webChromeClient=object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@WebViewActivity)
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.startsWith("http:") || url.startsWith("https:")) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            return true
                        }
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        startActivity(intent)
                        return true
                    }
                }
                val transport = resultMsg.obj as WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        webView.loadUrl(url)
    }
}