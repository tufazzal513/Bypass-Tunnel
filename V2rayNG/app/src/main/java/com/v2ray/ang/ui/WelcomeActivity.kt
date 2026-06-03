package com.v2ray.ang.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()

        if (MmkvManager.decodeSettingsBool(PREF_WELCOME_SHOW)) {
            navigateToMain()
            return
        }

        setContentView(R.layout.uwu_activity_welcome)
        setupViewsAndListeners()
    }

    private fun setupViewsAndListeners() {
        val page1 = findViewById<View>(R.id.page1)
        val page2 = findViewById<View>(R.id.page2)
        val page3 = findViewById<View>(R.id.page3)

        val insetListener = androidx.core.view.OnApplyWindowInsetsListener { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(page1, insetListener)
        ViewCompat.setOnApplyWindowInsetsListener(page2, insetListener)
        ViewCompat.setOnApplyWindowInsetsListener(page3, insetListener)

        page2.visibility = View.GONE
        page3.visibility = View.GONE

        findViewById<View>(R.id.page_1button).setOnClickListener {
            page1.visibility = View.GONE
            page2.visibility = View.VISIBLE
        }

        findViewById<View>(R.id.page_2button).setOnClickListener {
            page2.visibility = View.GONE
            page3.visibility = View.VISIBLE
        }

        val navigateAction = View.OnClickListener { navigateToMain() }
        
        findViewById<View>(R.id.page_3button).setOnClickListener(navigateAction)
        findViewById<View>(R.id.page_1_skip).setOnClickListener(navigateAction)
        findViewById<View>(R.id.page_2_skip).setOnClickListener(navigateAction)
    }

    private fun navigateToMain() {
        MmkvManager.encodeSettings(PREF_WELCOME_SHOW, true)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        const val PREF_WELCOME_SHOW = "pref_welcome_show"
    }
}
