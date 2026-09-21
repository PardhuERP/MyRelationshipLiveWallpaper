package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var thread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        }

        private val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

        private val startDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                startDrawing()
            } else {
                stopDrawing()
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)

            if (visible) {
                startDrawing()
            }
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

                // ------------------------------------------------
                // BACKGROUND
                // ------------------------------------------------

                canvas.drawColor(
                    Color.rgb(15, 18, 24)
                )

                // ------------------------------------------------
                // RESPONSIVE SCALE
                // ------------------------------------------------

                val scale = width / 1080f

                // Start lower to avoid Android wallpaper
                // preview/header overlap.
                var y = 190f * scale

                // ------------------------------------------------
                // LOCK
                // ------------------------------------------------

                boldPaint.color = Color.WHITE
                boldPaint.textAlign = Paint.Align.CENTER
                boldPaint.textSize = 26f * scale

                canvas.drawText(
                    "🔒",
                    centerX,
                    y,
                    boldPaint
                )

                y += 45f * scale

                // ------------------------------------------------
                // UNLOCK TEXT
                // ------------------------------------------------

                paint.color = Color.rgb(150, 155, 165)
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 17f * scale

                canvas.drawText(
                    "Look down to unlock",
                    centerX,
                    y,
                    paint
                )

                y += 75f * scale

                // ------------------------------------------------
                // CURRENT TIME
                // ------------------------------------------------

                val time = SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                boldPaint.color = Color.WHITE
                boldPaint.textSize = 48f * scale

                canvas.drawText(
                    time,
                    centerX,
                    y,
                    boldPaint
                )

                y += 62f * scale

                // ------------------------------------------------
                // TOGETHER FOR
                // ------------------------------------------------

                paint.color = Color.rgb(255, 55, 75)
                paint.textSize = 16f * scale

                canvas.drawText(
                    "♥  TOGETHER FOR  ♥",
                    centerX,
                    y,
                    paint
                )

                y += 48f * scale

                // ------------------------------------------------
                // RELATIONSHIP TIME
                // ------------------------------------------------

                val relationship = calculateRelationship()

                boldPaint.color = Color.WHITE
                boldPaint.textSize = 32f * scale

                canvas.drawText(
                    "${relationship.years} Years  •  " +
                            "${relationship.months} Months  •  " +
                            "${relationship.days} Days",
                    centerX,
                    y,
                    boldPaint
                )

                y += 38f * scale

                // ------------------------------------------------
                // START DATE
                // ------------------------------------------------

                paint.color = Color.rgb(130, 135, 145)
                paint.textSize = 15f * scale

                canvas.drawText(
                    "Since 15 January 2024",
                    centerX,
                    y,
                    paint
                )

                y += 35f * scale

                // ------------------------------------------------
                // DIVIDER
                // ------------------------------------------------

                paint.color = Color.rgb(50, 54, 62)
                paint.strokeWidth = 1f * scale

                canvas.drawLine(
                    width * 0.18f,
                    y,
                    width * 0.82f,
                    y,
                    paint
                )

                y += 58f * scale

                // ------------------------------------------------
                // TOTAL DAYS
                // ------------------------------------------------

                val elapsedMillis =
                    System.currentTimeMillis() -
                            startDate.timeInMillis

                val totalSeconds =
                    elapsedMillis / 1000

                val totalDays =
                    totalSeconds / 86400

                boldPaint.color = Color.WHITE
                boldPaint.textSize = 34f * scale

                canvas.drawText(
                    totalDays.toString(),
                    centerX,
                    y,
                    boldPaint
                )

                y += 28f * scale

                paint.color = Color.rgb(125, 130, 140)
                paint.textSize = 12f * scale

                canvas.drawText(
                    "TOTAL DAYS",
                    centerX,
                    y,
                    paint
                )

                y += 65f * scale

                // ------------------------------------------------
                // HOURS / MINUTES / SECONDS
                // ------------------------------------------------

                val hours =
                    (totalSeconds / 3600) % 24

                val minutes =
                    (totalSeconds / 60) % 60

                val seconds =
                    totalSeconds % 60

                val column1 = width * 0.27f
                val column2 = width * 0.50f
                val column3 = width * 0.73f

                boldPaint.color = Color.WHITE
                boldPaint.textSize = 25f * scale

                canvas.drawText(
                    hours.toString(),
                    column1,
                    y,
                    boldPaint
                )

                canvas.drawText(
                    minutes.toString(),
                    column2,
                    y,
                    boldPaint
                )

                canvas.drawText(
                    seconds.toString(),
                    column3,
                    y,
                    boldPaint
                )

                y += 25f * scale

                paint.color = Color.rgb(120, 125, 135)
                paint.textSize = 11f * scale

                canvas.drawText(
                    "HOURS",
                    column1,
                    y,
                    paint
                )

                canvas.drawText(
                    "MINUTES",
                    column2,
                    y,
                    paint
                )

                canvas.drawText(
                    "SECONDS",
                    column3,
                    y,
                    paint
                )

                y += 65f * scale

                // ------------------------------------------------
                // MESSAGE
                // ------------------------------------------------

                paint.color = Color.rgb(255, 55, 75)
                paint.textSize = 16f * scale

                canvas.drawText(
                    "♥ Same People • Same Dreams ♥",
                    centerX,
                    y,
                    paint
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // --------------------------------------------------------
        // RELATIONSHIP CALCULATION
        // --------------------------------------------------------

        private fun calculateRelationship(): Relationship {

            val start = startDate.clone() as Calendar
            val now = Calendar.getInstance()

            var years =
                now.get(Calendar.YEAR) -
                        start.get(Calendar.YEAR)

            var months =
                now.get(Calendar.MONTH) -
                        start.get(Calendar.MONTH)

            var days =
                now.get(Calendar.DAY_OF_MONTH) -
                        start.get(Calendar.DAY_OF_MONTH)

            if (days < 0) {

                months--

                val previousMonth =
                    now.clone() as Calendar

                previousMonth.add(
                    Calendar.MONTH,
                    -1
                )

                days += previousMonth.getActualMaximum(
                    Calendar.DAY_OF_MONTH
                )
            }

            if (months < 0) {
                years--
                months += 12
            }

            return Relationship(
                years,
                months,
                days
            )
        }
    }

    data class Relationship(
        val years: Int,
        val months: Int,
        val days: Int
    )
}
