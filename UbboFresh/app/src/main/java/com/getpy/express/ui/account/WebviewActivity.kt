package com.getpy.express.ui.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.getpy.express.R
import com.getpy.express.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityWebviewBinding=DataBindingUtil.setContentView(this, R.layout.activity_webview)
        val url=intent.extras?.getString("url")
        binding.back.setOnClickListener(View.OnClickListener {
            finish()
        })
        url?.let { binding.webivew.loadUrl(it) }
    }
}