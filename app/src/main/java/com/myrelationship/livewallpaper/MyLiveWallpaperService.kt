package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false

        private val handler = Handler(Looper.getMainLooper())

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
        }

        private val startDateTime =
            LocalDateTime.of(2024, 1, 15, 0, 0, 0)

        private val timeFormatter =
            DateTimeFormatter.ofPattern(
                "HH:mm:ss",
                Locale.getDefault()
            )

        private val drawRunnable = object : Runnable {
            override fun run() {

                if (!running) return

                drawWallpaper()

                handler.postDelayed(this, 1000)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                startDrawing()
            } else {
                stopDrawing()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startDrawing()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopDrawing()
            super.onSurfaceDestroyed(holder)
        }

        private fun startDrawing() {

            if (running) return

            running = true

            handler.removeCallbacks(drawRunnable)
            handler.post(drawRunnable)
        }

        private fun stopDrawing() {

            running = false

            handler.removeCallbacks(drawRunnable)
        }

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                /*
                 * Responsive scaling.
                 *
                 * Designed around a 1080px wide phone,
                 * then automatically scales for other screens.
                 */
                val scale = width / 1080f

                // Background
                canvas.drawColor(
                    Color.rgb(15, 18, 25)
                )

                // Current time
                val now = LocalDateTime.now()

                // Relationship period
                val period = Period.between(
                    startDateTime.toLocalDate(),
                    now.toLocalDate()
                )

                // Total elapsed duration
                val startInstant =
                    startDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()

                val nowInstant =
                    now.atZone(ZoneId.systemDefault())
                        .toInstant()

                val duration =
                    Duration.between(
                        startInstant,
                        nowInstant
                    )

                val totalSeconds =
                    duration.seconds.coerceAtLeast(0)

                val totalDays =
                    totalSeconds / 86400

                val hours =
                    (totalSeconds % 86400) / 3600

                val minutes =
                    (totalSeconds % 3600) / 60

                val seconds =
                    totalSeconds % 60

                // Vertical layout based on screen height
                val top = height * 0.09f

                // ------------------------------------------------
                // LOCK
                // ------------------------------------------------

                paint.typeface = Typeface.DEFAULT
                paint.color = Color.rgb(210, 214, 222)
                paint.textSize = 30f * scale

                canvas.drawText(
                    "🔒",
                    width / 2f,
                    top,
                    paint
                )

                // ------------------------------------------------
                // UNLOCK TEXT
                // ------------------------------------------------

                paint.color = Color.rgb(150, 155, 165)
                paint.textSize = 17f * scale

                canvas.drawText(
                    "Look down to unlock",
                    width / 2f,
                    top + 42f * scale,
                    paint
                )

                // ------------------------------------------------
                // CURRENT TIME
                // ------------------------------------------------

                paint.typeface = Typeface.create(
                    Typeface.DEFAULT,
                    Typeface.NORMAL
                )

                paint.color = Color.WHITE
                paint.textSize = 52f * scale

                canvas.drawText(
                    now.format(timeFormatter),
                    width / 2f,
                    top + 115f * scale,
                    paint
                )

                // ------------------------------------------------
                // TOGETHER
                // ------------------------------------------------

                paint.color = Color.rgb(235, 90, 115)
                paint.textSize = 17f * scale
                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText(
                    "♥  TOGETHER FOR  ♥",
                    width / 2f,
                    top + 175f * scale,
                    paint
                )

                // ------------------------------------------------
                // YEARS / MONTHS / DAYS
                // ------------------------------------------------

                paint.color = Color.WHITE
                paint.textSize = 38f * scale
                paint.typeface = Typeface.DEFAULT_BOLD

                val relationshipText =
                    "${period.years} Years  •  " +
                    "${period.months} Months  •  " +
                    "${period.days} Days"

                canvas.drawText(
                    relationshipText,
                    width / 2f,
                    top + 225f * scale,
                    paint
                )

                // ------------------------------------------------
                // SINCE DATE
                // ------------------------------------------------

                paint.color = Color.rgb(145, 150, 160)
                paint.textSize = 16f * scale
                paint.typeface = Typeface.DEFAULT

                canvas.drawText(
                    "Since 15 January 2024",
                    width / 2f,
                    top + 262f * scale,
                    paint
                )

                // ------------------------------------------------
                // DIVIDER
                // ------------------------------------------------

                paint.color = Color.rgb(55, 60, 70)
                paint.strokeWidth = 1f * scale

                canvas.drawLine(
                    width * 0.18f,
                    top + 300f * scale,
                    width * 0.82f,
                    top + 300f * scale,
                    paint
                )

                // ------------------------------------------------
                // TOTAL DAYS
                // ------------------------------------------------

                paint.color = Color.WHITE
                paint.textSize = 40f * scale
                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText(
                    totalDays.toString(),
                    width / 2f,
                    top + 355f * scale,
                    paint
                )

                paint.color = Color.rgb(145, 150, 160)
                paint.textSize = 13f * scale

                canvas.drawText(
                    "TOTAL DAYS",
                    width / 2f,
                    top + 382f * scale,
                    paint
                )

                // ------------------------------------------------
                // HOURS / MINUTES / SECONDS
                // ------------------------------------------------

                val columnY = top + 445f * scale

                val leftX = width * 0.28f
                val centerX = width * 0.50f
                val rightX = width * 0.72f

                paint.color = Color.WHITE
                paint.textSize = 27f * scale
                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText(
                    String.format("%02d", hours),
                    leftX,
                    columnY,
                    paint
                )

                canvas.drawText(
                    String.format("%02d", minutes),
                    centerX,
                    columnY,
                    paint
                )

                canvas.drawText(
                    String.format("%02d", seconds),
                    rightX,
                    columnY,
                    paint
                )

                paint.color = Color.rgb(130, 135, 145)
                paint.textSize = 12f * scale
                paint.typeface = Typeface.DEFAULT

                canvas.drawText(
                    "HOURS",
                    leftX,
                    columnY + 25f * scale,
                    paint
                )

                canvas.drawText(
                    "MINUTES",
                    centerX,
                    columnY + 25f * scale,
                    paint
                )

                canvas.drawText(
                    "SECONDS",
                    rightX,
                    columnY + 25f * scale,
                    paint
                )

                // ------------------------------------------------
                // MESSAGE
                // ------------------------------------------------

                paint.color = Color.rgb(235, 90, 115)
                paint.textSize = 16f * scale
                paint.typeface = Typeface.DEFAULT_BOLD

                canvas.drawText(
                    "♥  Same People • Same Dreams  ♥",
                    width / 2f,
                    top + 535f * scale,
                    paint
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}
