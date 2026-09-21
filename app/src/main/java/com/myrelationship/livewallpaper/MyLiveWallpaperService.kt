package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var thread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val startDate =
            LocalDateTime.of(2024, 1, 15, 0, 0)

        override fun onVisibilityChanged(isVisible: Boolean) {
            super.onVisibilityChanged(isVisible)

            if (isVisible) {
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

            thread = Thread {

                while (running) {

                    drawWallpaper()

                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }

            thread?.start()
        }

        private fun stopDrawing() {

            running = false

            thread?.interrupt()
            thread = null
        }

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                val centerX = width / 2f

                /*
                 * Responsive scaling.
                 * Designed around a 1080px wide phone.
                 */
                val scale = (width / 1080f).coerceAtLeast(0.7f)

                /*
                 * IMPORTANT:
                 * Keep content below Android's
                 * "Set wallpaper" preview header.
                 */
                val top = 270f * scale

                // --------------------------------
                // BACKGROUND
                // --------------------------------

                paint.shader = LinearGradient(
                    0f,
                    0f,
                    0f,
                    height,
                    Color.rgb(10, 13, 19),
                    Color.rgb(17, 20, 28),
                    Shader.TileMode.CLAMP
                )

                canvas.drawRect(
                    0f,
                    0f,
                    width,
                    height,
                    paint
                )

                paint.shader = null

                // --------------------------------
                // REAL TIME CALCULATION
                // --------------------------------

                val now = LocalDateTime.now()

                val period = Period.between(
                    startDate.toLocalDate(),
                    now.toLocalDate()
                )

                val duration = Duration.between(
                    startDate,
                    now
                )

                val totalSeconds = duration.seconds

                val totalDays = totalSeconds / 86400

                val hours =
                    (totalSeconds % 86400) / 3600

                val minutes =
                    (totalSeconds % 3600) / 60

                val seconds =
                    totalSeconds % 60

                // --------------------------------
                // LOCK ICON
                // --------------------------------

                drawLock(
                    canvas,
                    centerX,
                    top,
                    1.0f * scale
                )

                // --------------------------------
                // LOOK DOWN
                // --------------------------------

                paint.shader = null
                paint.textAlign = Paint.Align.CENTER
                paint.typeface = android.graphics.Typeface.DEFAULT
                paint.color = Color.rgb(145, 150, 160)
                paint.textSize = 17f * scale

                canvas.drawText(
                    "Look down to unlock",
                    centerX,
                    top + 55f * scale,
                    paint
                )

                // --------------------------------
                // CURRENT TIME
                // --------------------------------

                paint.color = Color.WHITE
                paint.typeface =
                    android.graphics.Typeface.DEFAULT_BOLD
                paint.textSize = 48f * scale

                val currentTime =
                    now.format(
                        DateTimeFormatter.ofPattern(
                            "HH:mm:ss",
                            Locale.getDefault()
                        )
                    )

                canvas.drawText(
                    currentTime,
                    centerX,
                    top + 115f * scale,
                    paint
                )

                // --------------------------------
                // TOGETHER FOR
                // --------------------------------

                paint.color = Color.rgb(255, 65, 85)
                paint.typeface =
                    android.graphics.Typeface.DEFAULT_BOLD
                paint.textSize = 17f * scale

                canvas.drawText(
                    "♥  TOGETHER FOR  ♥",
                    centerX,
                    top + 170f * scale,
                    paint
                )

                // --------------------------------
                // RELATIONSHIP PERIOD
                // --------------------------------

                paint.color = Color.WHITE
                paint.textSize = 31f * scale

                val relationship =
                    "${period.years} Years  •  " +
                    "${period.months} Months  •  " +
                    "${period.days} Days"

                canvas.drawText(
                    relationship,
                    centerX,
                    top + 220f * scale,
                    paint
                )

                // --------------------------------
                // SINCE
                // --------------------------------

                paint.color = Color.rgb(145, 150, 160)
                paint.typeface =
                    android.graphics.Typeface.DEFAULT
                paint.textSize = 14f * scale

                canvas.drawText(
                    "Since 15 January 2024",
                    centerX,
                    top + 252f * scale,
                    paint
                )

                // --------------------------------
                // DIVIDER
                // --------------------------------

                paint.color = Color.rgb(55, 60, 70)
                paint.strokeWidth = 1f

                canvas.drawLine(
                    centerX - 250f * scale,
                    top + 285f * scale,
                    centerX + 250f * scale,
                    top + 285f * scale,
                    paint
                )

                // --------------------------------
                // TOTAL DAYS
                // --------------------------------

                paint.color = Color.WHITE
                paint.typeface =
                    android.graphics.Typeface.DEFAULT_BOLD
                paint.textSize = 38f * scale

                canvas.drawText(
                    totalDays.toString(),
                    centerX,
                    top + 330f * scale,
                    paint
                )

                paint.color = Color.rgb(135, 140, 150)
                paint.typeface =
                    android.graphics.Typeface.DEFAULT
                paint.textSize = 12f * scale

                canvas.drawText(
                    "TOTAL DAYS",
                    centerX,
                    top + 355f * scale,
                    paint
                )

                // --------------------------------
                // LIVE COUNTERS
                // --------------------------------

                drawCounter(
                    canvas,
                    centerX - 190f * scale,
                    top + 415f * scale,
                    hours.toString().padStart(2, '0'),
                    "HOURS",
                    scale
                )

                drawCounter(
                    canvas,
                    centerX,
                    top + 415f * scale,
                    minutes.toString().padStart(2, '0'),
                    "MINUTES",
                    scale
                )

                drawCounter(
                    canvas,
                    centerX + 190f * scale,
                    top + 415f * scale,
                    seconds.toString().padStart(2, '0'),
                    "SECONDS",
                    scale
                )

                // --------------------------------
                // MESSAGE
                // --------------------------------

                paint.color = Color.rgb(255, 65, 85)
                paint.typeface =
                    android.graphics.Typeface.DEFAULT_BOLD
                paint.textSize = 16f * scale

                canvas.drawText(
                    "♥  Same People • Same Dreams  ♥",
                    centerX,
                    top + 500f * scale,
                    paint
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // --------------------------------
        // COUNTER
        // --------------------------------

        private fun drawCounter(
            canvas: Canvas,
            x: Float,
            y: Float,
            value: String,
            label: String,
            scale: Float
        ) {

            paint.textAlign = Paint.Align.CENTER

            paint.color = Color.WHITE
            paint.typeface =
                android.graphics.Typeface.DEFAULT_BOLD
            paint.textSize = 30f * scale

            canvas.drawText(
                value,
                x,
                y,
                paint
            )

            paint.color = Color.rgb(135, 140, 150)
            paint.typeface =
                android.graphics.Typeface.DEFAULT
            paint.textSize = 11f * scale

            canvas.drawText(
                label,
                x,
                y + 24f * scale,
                paint
            )
        }

        // --------------------------------
        // SIMPLE LOCK ICON
        // --------------------------------

        private fun drawLock(
            canvas: Canvas,
            x: Float,
            y: Float,
            scale: Float
        ) {

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f * scale
            paint.color = Color.rgb(255, 195, 60)

            val bodyWidth = 30f * scale
            val bodyHeight = 25f * scale

            canvas.drawRoundRect(
                x - bodyWidth / 2,
                y + 8f * scale,
                x + bodyWidth / 2,
                y + 8f * scale + bodyHeight,
                5f * scale,
                5f * scale,
                paint
            )

            canvas.drawArc(
                x - 10f * scale,
                y - 5f * scale,
                x + 10f * scale,
                y + 20f * scale,
                180f,
                -180f,
                false,
                paint
            )

            paint.style = Paint.Style.FILL

            canvas.drawCircle(
                x,
                y + 20f * scale,
                2.5f * scale,
                paint
            )

            paint.style = Paint.Style.FILL
        }
    }
}
