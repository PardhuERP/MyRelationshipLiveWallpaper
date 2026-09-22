package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var isRunning = false
        private var drawingThread: Thread? = null

        private val backgroundColor = Color.rgb(10, 13, 19)
        private val white = Color.rgb(245, 245, 248)
        private val secondary = Color.rgb(145, 150, 160)
        private val muted = Color.rgb(95, 100, 110)
        private val red = Color.rgb(255, 55, 75)
        private val divider = Color.rgb(42, 46, 55)

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
            textAlign = Paint.Align.CENTER
        }

        /*
         * Relationship start
         */
        private val relationshipStart = LocalDate.of(
            2006,
            2,
            14
        )

        /*
         * Marriage start
         */
        private val marriageStart = LocalDate.of(
            2025,
            4,
            9
        )

        override fun onVisibilityChanged(isVisible: Boolean) {
            super.onVisibilityChanged(isVisible)

            if (isVisible) {
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
            super.onSurfaceChanged(
                holder,
                format,
                width,
                height
            )

            if (isRunning) {
                drawWallpaper()
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

            if (isRunning) {
                return
            }

            isRunning = true

            drawingThread = Thread {

                while (isRunning) {

                    drawWallpaper()

                    try {
                        Thread.sleep(1000L)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }

            drawingThread?.start()
        }

        private fun stopDrawing() {

            isRunning = false

            drawingThread?.interrupt()
            drawingThread = null
        }

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) {
                    return
                }

                drawBackground(canvas)

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                /*
                 * Scale everything according to screen width.
                 * This prevents the UI becoming too large on smaller phones.
                 */
                val scale = (width / 1080f).coerceIn(
                    0.72f,
                    1.15f
                )

                /*
                 * Top area.
                 */
                val centerX = width / 2f

                var y = 115f * scale

                /*
                 * Small heart / lock symbol
                 */
                drawText(
                    canvas,
                    "♥",
                    centerX,
                    y,
                    28f * scale,
                    red,
                    true
                )

                y += 38f * scale

                /*
                 * Unlock hint
                 */
                drawText(
                    canvas,
                    "Look down to unlock",
                    centerX,
                    y,
                    14f * scale,
                    secondary,
                    false
                )

                y += 58f * scale

                /*
                 * Current time
                 */
                val currentTime = SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                drawText(
                    canvas,
                    currentTime,
                    centerX,
                    y,
                    39f * scale,
                    white,
                    true
                )

                y += 62f * scale

                /*
                 * TOGETHER FOR
                 */
                drawText(
                    canvas,
                    "♥  TOGETHER FOR  ♥",
                    centerX,
                    y,
                    14f * scale,
                    red,
                    true
                )

                y += 43f * scale

                /*
                 * Relationship period
                 */
                val relationshipPeriod =
                    getPeriod(relationshipStart)

                val relationshipText =
                    "${relationshipPeriod.years} Years  •  " +
                    "${relationshipPeriod.months} Months  •  " +
                    "${relationshipPeriod.days} Days"

                drawText(
                    canvas,
                    relationshipText,
                    centerX,
                    y,
                    25f * scale,
                    white,
                    true
                )

                y += 32f * scale

                /*
                 * Relationship date
                 */
                drawText(
                    canvas,
                    "Since 14 February 2006",
                    centerX,
                    y,
                    12f * scale,
                    secondary,
                    false
                )

                y += 28f * scale

                /*
                 * Divider
                 */
                drawDivider(
                    canvas,
                    centerX,
                    y,
                    width * 0.60f
                )

                y += 45f * scale

                /*
                 * Relationship live elapsed time
                 */
                val relationshipTime =
                    getElapsedTime(relationshipStart)

                drawText(
                    canvas,
                    relationshipTime.totalDays.toString(),
                    centerX,
                    y,
                    25f * scale,
                    white,
                    true
                )

                y += 19f * scale

                drawText(
                    canvas,
                    "TOTAL DAYS",
                    centerX,
                    y,
                    9f * scale,
                    muted,
                    false
                )

                y += 58f * scale

                /*
                 * Three columns
                 */
                val column1 = width * 0.30f
                val column2 = width * 0.50f
                val column3 = width * 0.70f

                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        relationshipTime.hours
                    ),
                    column1,
                    y,
                    22f * scale,
                    white,
                    true
                )

                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        relationshipTime.minutes
                    ),
                    column2,
                    y,
                    22f * scale,
                    white,
                    true
                )

                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        relationshipTime.seconds
                    ),
                    column3,
                    y,
                    22f * scale,
                    white,
                    true
                )

                y += 21f * scale

                drawText(
                    canvas,
                    "HOURS",
                    column1,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                drawText(
                    canvas,
                    "MINUTES",
                    column2,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                drawText(
                    canvas,
                    "SECONDS",
                    column3,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                /*
                 * Space before marriage section
                 */
                y += 70f * scale

                /*
                 * Marriage heading
                 */
                drawText(
                    canvas,
                    "♥  MARRIED FOR  ♥",
                    centerX,
                    y,
                    14f * scale,
                    red,
                    true
                )

                y += 43f * scale

                /*
                 * Marriage period
                 */
                val marriagePeriod =
                    getPeriod(marriageStart)

                val marriageText =
                    "${marriagePeriod.years} Years  •  " +
                    "${marriagePeriod.months} Months  •  " +
                    "${marriagePeriod.days} Days"

                drawText(
                    canvas,
                    marriageText,
                    centerX,
                    y,
                    25f * scale,
                    white,
                    true
                )

                y += 32f * scale

                /*
                 * Marriage date
                 */
                drawText(
                    canvas,
                    "Since 09 April 2025",
                    centerX,
                    y,
                    12f * scale,
                    secondary,
                    false
                )

                y += 28f * scale

                /*
                 * Marriage divider
                 */
                drawDivider(
                    canvas,
                    centerX,
                    y,
                    width * 0.60f
                )

                y += 45f * scale

                /*
                 * Marriage live elapsed time
                 */
                val marriageTime =
                    getElapsedTime(marriageStart)

                drawText(
                    canvas,
                    marriageTime.totalDays.toString(),
                    centerX,
                    y,
                    25f * scale,
                    white,
                    true
                )

                y += 19f * scale

                drawText(
                    canvas,
                    "TOTAL DAYS",
                    centerX,
                    y,
                    9f * scale,
                    muted,
                    false
                )

                y += 58f * scale

                /*
                 * Marriage hours / minutes / seconds
                 */
                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        marriageTime.hours
                    ),
                    column1,
                    y,
                    22f * scale,
                    white,
                    true
                )

                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        marriageTime.minutes
                    ),
                    column2,
                    y,
                    22f * scale,
                    white,
                    true
                )

                drawText(
                    canvas,
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        marriageTime.seconds
                    ),
                    column3,
                    y,
                    22f * scale,
                    white,
                    true
                )

                y += 21f * scale

                drawText(
                    canvas,
                    "HOURS",
                    column1,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                drawText(
                    canvas,
                    "MINUTES",
                    column2,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                drawText(
                    canvas,
                    "SECONDS",
                    column3,
                    y,
                    8f * scale,
                    muted,
                    false
                )

                /*
                 * Final message
                 */
                y += 62f * scale

                drawText(
                    canvas,
                    "♥ Same People • Same Dreams ♥",
                    centerX,
                    y,
                    13f * scale,
                    red,
                    false
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        private fun drawBackground(canvas: Canvas) {

            canvas.drawColor(backgroundColor)
        }

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            lineWidth: Float
        ) {

            paint.color = divider
            paint.strokeWidth = 1f

            val left = centerX - lineWidth / 2f
            val right = centerX + lineWidth / 2f

            canvas.drawLine(
                left,
                y,
                right,
                y,
                paint
            )
        }

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            textSize: Float,
            color: Int,
            bold: Boolean
        ) {

            paint.color = color
            paint.textSize = textSize
            paint.textAlign = Paint.Align.CENTER

            paint.typeface = if (bold) {
                Typeface.create(
                    Typeface.DEFAULT,
                    Typeface.BOLD
                )
            } else {
                Typeface.create(
                    Typeface.DEFAULT,
                    Typeface.NORMAL
                )
            }

            canvas.drawText(
                text,
                x,
                y,
                paint
            )
        }

        private fun getPeriod(
            startDate: LocalDate
        ): java.time.Period {

            val now = LocalDate.now()

            return java.time.Period.between(
                startDate,
                now
            )
        }

        private fun getElapsedTime(
            startDate: LocalDate
        ): ElapsedTime {

            val zone = ZoneId.systemDefault()

            val startDateTime =
                startDate
                    .atStartOfDay(zone)

            val now =
                java.time.ZonedDateTime.now(zone)

            val duration =
                Duration.between(
                    startDateTime,
                    now
                )

            val totalSeconds =
                duration.seconds.coerceAtLeast(0L)

            val totalDays =
                totalSeconds / 86_400L

            val remainingAfterDays =
                totalSeconds % 86_400L

            val hours =
                remainingAfterDays / 3_600L

            val remainingAfterHours =
                remainingAfterDays % 3_600L

            val minutes =
                remainingAfterHours / 60L

            val seconds =
                remainingAfterHours % 60L

            return ElapsedTime(
                totalDays = totalDays,
                hours = hours,
                minutes = minutes,
                seconds = seconds
            )
        }
    }

    private data class ElapsedTime(
        val totalDays: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long
    )
}
