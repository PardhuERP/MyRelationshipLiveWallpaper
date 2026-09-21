package com.myrelationship.livewallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 60, 40, 40)

        val title = TextView(this)
        title.text = "❤️ My Relationship"
        title.textSize = 28f
        title.setPadding(0, 0, 0, 40)

        val description = TextView(this)
        description.text =
            "Create your relationship live wallpaper\n\n" +
            "• Together time\n" +
            "• Days / Hours / Minutes / Seconds\n" +
            "• Your photo\n" +
            "• Anniversary countdown\n" +
            "• Custom messages"

        description.textSize = 18f
        description.setPadding(0, 0, 0, 40)

        val setWallpaperButton = Button(this)
        setWallpaperButton.text = "Set Live Wallpaper"

        setWallpaperButton.setOnClickListener {
            openWallpaper()
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(setWallpaperButton)

        setContentView(layout)
    }

    private fun openWallpaper() {
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)

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
