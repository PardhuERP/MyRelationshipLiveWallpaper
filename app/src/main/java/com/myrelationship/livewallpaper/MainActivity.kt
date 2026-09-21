package com.myrelationship.livewallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun roundedBackground(
        color: Int,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius.toInt()).toFloat()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(18, 21, 28)
        window.navigationBarColor = Color.rgb(18, 21, 28)

        // Main background
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(18, 21, 28))
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(40), dp(24), dp(25))
        }

        val title = TextView(this).apply {
            text = "❤️ My Relationship"
            textSize = 30f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "A little reminder of us, every day."
            textSize = 15f
            setTextColor(Color.rgb(175, 180, 190))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }

        header.addView(title)
        header.addView(subtitle)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // Scroll content
        val scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(10), dp(22), dp(30))
        }

        // Main relationship card
        val relationshipCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
            background = roundedBackground(
                Color.rgb(29, 33, 43),
                22f
            )
        }

        val togetherTitle = TextView(this).apply {
            text = "❤️  TOGETHER  ❤️"
            textSize = 14f
            setTextColor(Color.rgb(220, 100, 120))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val since = TextView(this).apply {
            text = "Since 15 January 2024"
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, 0)
        }

        val memoryText = TextView(this).apply {
            text = "Every second becomes a memory."
            textSize = 14f
            setTextColor(Color.rgb(165, 170, 180))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }

        relationshipCard.addView(togetherTitle)
        relationshipCard.addView(since)
        relationshipCard.addView(memoryText)

        content.addView(
            relationshipCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        // Features card
        val featuresCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(22))
            background = roundedBackground(
                Color.rgb(29, 33, 43),
                22f
            )
        }

        val featuresTitle = TextView(this).apply {
            text = "Your relationship wallpaper"
            textSize = 19f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }

        featuresCard.addView(featuresTitle)

        val features = arrayOf(
            "❤️ Live together-time counter",
            "⏱ Days, hours, minutes & seconds",
            "📅 Anniversary countdown",
            "💌 Personal messages",
            "🖼 Your favourite photo"
        )

        for (feature in features) {

            val item = TextView(this).apply {
                text = feature
                textSize = 16f
                setTextColor(Color.rgb(210, 214, 222))
                setPadding(0, dp(13), 0, dp(2))
            }

            featuresCard.addView(item)
        }

        content.addView(
            featuresCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(22)
            }
        )

        // Set wallpaper button
        val setWallpaperButton = Button(this).apply {
            text = "SET LIVE WALLPAPER"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            background = roundedBackground(
                Color.rgb(190, 65, 88),
                18f
            )
            isAllCaps = false
            setPadding(
                dp(10),
                dp(14),
                dp(10),
                dp(14)
            )

            setOnClickListener {
                openWallpaper()
            }
        }

        content.addView(
            setWallpaperButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            ).apply {
                bottomMargin = dp(18)
            }
        )

        // Footer
        val footer = TextView(this).apply {
            text = "❤️ Same People • Same Dreams ❤️"
            textSize = 14f
            setTextColor(Color.rgb(135, 140, 150))
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(10))
        }

        content.addView(footer)

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun openWallpaper() {

        try {

            val intent =
                Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)

            val component = ComponentName(
                this,
                MyLiveWallpaperService::class.java
            )

            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                component
            )

            startActivity(intent)

        } catch (e: Exception) {

            val intent =
                Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)

            startActivity(intent)
        }
    }
}
