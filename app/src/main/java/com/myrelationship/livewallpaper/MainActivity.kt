package com.myrelationship.livewallpaper

import android.app.Activity
import android.app.DatePickerDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {

    companion object {
        private const val PREFS = "relationship_settings"

        private const val KEY_TOGETHER_DATE = "together_date"
        private const val KEY_MARRIED_DATE = "married_date"
        private const val KEY_BACKGROUND_URI = "background_uri"

        private const val REQUEST_IMAGE = 1001

        private val DEFAULT_TOGETHER_DATE: Long =
            Calendar.getInstance().apply {
                set(2006, Calendar.FEBRUARY, 14, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

        private val DEFAULT_MARRIED_DATE: Long =
            Calendar.getInstance().apply {
                set(2025, Calendar.APRIL, 9, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
    }

    private val prefs by lazy {
        getSharedPreferences(PREFS, MODE_PRIVATE)
    }

    private lateinit var togetherText: TextView
    private lateinit var marriedText: TextView
    private lateinit var imageText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createSettingsScreen()
    }

    // ============================================================
    // SETTINGS SCREEN
    // ============================================================

    private fun createSettingsScreen() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.setPadding(40, 50, 40, 40)
        root.gravity = Gravity.CENTER_HORIZONTAL

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------

        val title = TextView(this)

        title.text = "❤️ UsForever wallpaper"
        title.textSize = 26f
        title.gravity = Gravity.CENTER

        root.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(root, 35)

        // --------------------------------------------------------
        // TOGETHER DATE
        // --------------------------------------------------------

        togetherText = TextView(this)

        togetherText.textSize = 18f
        togetherText.gravity = Gravity.CENTER

        updateTogetherText()

        root.addView(
            togetherText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        val togetherButton = Button(this)

        togetherButton.text = "Change Together Date ❤️"

        togetherButton.setOnClickListener {
            showDatePicker(true)
        }

        root.addView(
            togetherButton,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(root, 25)

        // --------------------------------------------------------
        // MARRIED DATE
        // --------------------------------------------------------

        marriedText = TextView(this)

        marriedText.textSize = 18f
        marriedText.gravity = Gravity.CENTER

        updateMarriedText()

        root.addView(
            marriedText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        val marriedButton = Button(this)

        marriedButton.text = "Change Married Date 💍"

        marriedButton.setOnClickListener {
            showDatePicker(false)
        }

        root.addView(
            marriedButton,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        addSpace(root, 25)


// --------------------------------------------------------
// BACKGROUND IMAGE
// --------------------------------------------------------

imageText = TextView(this)

imageText.textSize = 18f
imageText.gravity = Gravity.CENTER

updateImageText()

root.addView(
    imageText,
    LinearLayout.LayoutParams(
        -1,
        -2
    )
)

val imageButton = Button(this)

imageButton.text = "Choose Background Image 🖼️"

imageButton.setOnClickListener {
    chooseBackgroundImage()
}

root.addView(
    imageButton,
    LinearLayout.LayoutParams(
        -1,
        -2
    )
)

// --------------------------------------------------------
// DEFAULT BACKGROUND
// --------------------------------------------------------

val defaultBackgroundButton = Button(this)

defaultBackgroundButton.text =
    "Use Default Background ❤️"

defaultBackgroundButton.setOnClickListener {

    getSharedPreferences(
        "relationship_settings",
        MODE_PRIVATE
    )
        .edit()
        .remove("background_uri")
        .apply()

    updateImageText()
}

root.addView(
    defaultBackgroundButton,
    LinearLayout.LayoutParams(
        -1,
        -2
    )
)

addSpace(root, 40)

// --------------------------------------------------------
// SET WALLPAPER
// --------------------------------------------------------

val wallpaperButton = Button(this)

wallpaperButton.text =
    "Set Live Wallpaper ❤️"

wallpaperButton.setOnClickListener {
    openWallpaperPicker()
}

root.addView(
    wallpaperButton,
    LinearLayout.LayoutParams(
        -1,
        -2
    )
)

setContentView(root)


    // ============================================================
    // DATE PICKER
    // ============================================================

    private fun showDatePicker(isTogether: Boolean) {

        val currentDate = Calendar.getInstance()

        val savedDate = if (isTogether) {
            prefs.getLong(
                KEY_TOGETHER_DATE,
                DEFAULT_TOGETHER_DATE
            )
        } else {
            prefs.getLong(
                KEY_MARRIED_DATE,
                DEFAULT_MARRIED_DATE
            )
        }

        currentDate.timeInMillis = savedDate

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->

                val selected = Calendar.getInstance()

                selected.set(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0
                )

                selected.set(
                    Calendar.MILLISECOND,
                    0
                )

                if (isTogether) {

                    prefs.edit()
                        .putLong(
                            KEY_TOGETHER_DATE,
                            selected.timeInMillis
                        )
                        .apply()

                    updateTogetherText()

                } else {

                    prefs.edit()
                        .putLong(
                            KEY_MARRIED_DATE,
                            selected.timeInMillis
                        )
                        .apply()

                    updateMarriedText()
                }

            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    // ============================================================
    // IMAGE PICKER
    // ============================================================

    private fun chooseBackgroundImage() {

        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT
        )

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        intent.type = "image/*"

        startActivityForResult(
            intent,
            REQUEST_IMAGE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == REQUEST_IMAGE &&
            resultCode == RESULT_OK
        ) {

            val uri = data?.data ?: return

            try {

                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

            } catch (_: Exception) {
                // Some gallery providers do not support persistable permission.
            }

            prefs.edit()
                .putString(
                    KEY_BACKGROUND_URI,
                    uri.toString()
                )
                .apply()

            updateImageText()
        }
    }

    // ============================================================
    // OPEN LIVE WALLPAPER PICKER
    // ============================================================

    private fun openWallpaperPicker() {

        try {

            val componentName = ComponentName(
                this,
                MyLiveWallpaperService::class.java
            )

            val intent = Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER
            )

            intent.putExtra(
                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                componentName
            )

            startActivity(intent)

        } catch (_: Exception) {

            startActivity(
                Intent(
                    WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER
                )
            )
        }
    }

    // ============================================================
    // UI TEXT
    // ============================================================

    private fun updateTogetherText() {

        val date = prefs.getLong(
            KEY_TOGETHER_DATE,
            DEFAULT_TOGETHER_DATE
        )

        togetherText.text =
            "Together since\n${formatDate(date)}"
    }

    private fun updateMarriedText() {

        val date = prefs.getLong(
            KEY_MARRIED_DATE,
            DEFAULT_MARRIED_DATE
        )

        marriedText.text =
            "Married since\n${formatDate(date)}"
    }

    private fun updateImageText() {

        val uri = prefs.getString(
            KEY_BACKGROUND_URI,
            null
        )

        imageText.text =
            if (uri.isNullOrEmpty()) {
                "Background image\nDefault background"
            } else {
                "Background image\nSelected ✓"
            }
    }

    private fun formatDate(
        millis: Long
    ): String {

        return SimpleDateFormat(
            "dd MMMM yyyy",
            Locale.getDefault()
        ).format(millis)
    }

    private fun addSpace(
        root: LinearLayout,
        height: Int
    ) {

        val space = TextView(this)

        root.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }
}
