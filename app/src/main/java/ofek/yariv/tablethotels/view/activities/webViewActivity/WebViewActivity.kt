package ofek.yariv.tablethotels.view.activities.webViewActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.Constants

class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView: WebView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        val query = intent.getStringExtra(Constants.SEARCH_QUERY)
        webView.loadUrl("https://www.tablethotels.com/")
    }
}
