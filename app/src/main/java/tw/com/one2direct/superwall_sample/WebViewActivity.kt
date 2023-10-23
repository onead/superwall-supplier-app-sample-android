package tw.com.one2direct.superwall_sample
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
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
        webView.loadUrl(url)
    }
}