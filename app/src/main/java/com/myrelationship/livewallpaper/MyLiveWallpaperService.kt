package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var isRunning = false
        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
        }

        // ------------------------------------------------------------
        // IMPORTANT DATES
        // ------------------------------------------------------------

        private val relationshipStart = Calendar.getInstance().apply {
            set(2006, Calendar.FEBRUARY, 14, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        private val marriageStart = Calendar.getInstance().apply {
            set(2025, Calendar.APRIL, 9, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // ------------------------------------------------------------
        // COLORS
        // ------------------------------------------------------------

        private val backgroundColor = Color.rgb(9, 11, 17)
        private val primaryText = Color.rgb(245, 245, 248)
        private val secondaryText = Color.rgb(155, 158, 168)
        private val mutedText = Color.rgb(105, 108, 118)
        private val heartColor = Color.rgb(255, 55, 80)
        private val goldColor = Color.rgb(255, 190, 55)
        private val dividerColor = Color.rgb(42, 45, 53)

        // ------------------------------------------------------------
        // WALLPAPER VISIBILITY
        // ------------------------------------------------------------

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

            if (isVisible) {
                startDrawing()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            if (isVisible) {
                startDrawing()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopDrawing()
            super.onSurfaceDestroyed(holder)
        }

        // ------------------------------------------------------------
        // DRAWING LOOP
        // ------------------------------------------------------------

        private fun startDrawing() {

            if (isRunning) return

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

        // ------------------------------------------------------------
        // MAIN DRAW
        // ------------------------------------------------------------

        private fun drawWallpaper() {

            val holder = surfaceHolder

            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                // Background
                canvas.drawColor(backgroundColor)

                // Scale based on phone width.
                // 1080px is our reference width.
                val scale = min(width / 1080f, height / 2400f)

                drawContent(
                    canvas,
                    width,
                    height,
                    scale
                )

            } catch (_: Exception) {

                // Prevent wallpaper thread from crashing
                // because of temporary SurfaceHolder issues.

            } finally {

                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        // ------------------------------------------------------------
        // CONTENT
        // ------------------------------------------------------------

        private fun drawContent(
            canvas: Canvas,
            width: Float,
            height: Float,
            scale: Float
        ) {

            val centerX = width / 2f

            // Keep content in the upper section.
            // This leaves clean empty space below.
            val top = height * 0.115f

            // --------------------------------------------------------
            // SMALL LOCK ICON
            // --------------------------------------------------------

            drawLockIcon(
                canvas,
                centerX,
                top + 22f * scale,
                20f * scale
            )

            // --------------------------------------------------------
            // LOOK DOWN TO UNLOCK
            // --------------------------------------------------------

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                top + 58f * scale,
                17f * scale,
                mutedText
            )

            // --------------------------------------------------------
            // CURRENT TIME
            // --------------------------------------------------------

            val currentTime = SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

            drawText(
                canvas,
                currentTime,
                centerX,
                top + 112f * scale,
                43f * scale,
                primaryText,
                bold = true
            )

            // --------------------------------------------------------
            // TOGETHER FOR
            // --------------------------------------------------------

            drawHeartLabel(
                canvas,
                centerX,
                top + 158f * scale,
                "TOGETHER FOR",
                scale
            )

            // --------------------------------------------------------
            // RELATIONSHIP COUNTER
            // --------------------------------------------------------

            val relationship = calculateDuration(
                relationshipStart
            )

            drawText(
                canvas,
                "${relationship.years} Years  •  " +
                        "${relationship.months} Months  •  " +
                        "${relationship.days} Days",
                centerX,
                top + 202f * scale,
                29f * scale,
                primaryText,
                bold = true
            )

            // --------------------------------------------------------
            // RELATIONSHIP START DATE
            // --------------------------------------------------------

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                top + 234f * scale,
                15f * scale,
                secondaryText
            )

            // --------------------------------------------------------
            // DIVIDER
            // --------------------------------------------------------

            drawDivider(
                canvas,
                centerX,
                top + 268f * scale,
                width * 0.66f
            )

            // --------------------------------------------------------
            // TOTAL RELATIONSHIP DAYS
            // --------------------------------------------------------

            val relationshipDays =
                daysBetween(
                    relationshipStart.timeInMillis,
                    System.currentTimeMillis()
                )

            drawText(
                canvas,
                relationshipDays.toString(),
                centerX,
                top + 320f * scale,
                37f * scale,
                primaryText,
                bold = true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                top + 344f * scale,
                12f * scale,
                mutedText
            )

            // --------------------------------------------------------
            // MARRIAGE SECTION
            // --------------------------------------------------------

            drawMarriageIcon(
                canvas,
                centerX,
                top + 395f * scale,
                19f * scale
            )

            drawHeartLabel(
                canvas,
                centerX,
                top + 435f * scale,
                "MARRIED FOR",
                scale,
                useGold = true
            )

            // --------------------------------------------------------
            // MARRIAGE COUNTER
            // --------------------------------------------------------

            val marriage = calculateDuration(
                marriageStart
            )

            drawText(
                canvas,
                "${marriage.years} Year" +
                        if (marriage.years == 1) "" else "s" +
                        "  •  " +
                        "${marriage.months} Month" +
                        if (marriage.months == 1) "" else "s" +
                        "  •  " +
                        "${marriage.days} Day" +
                        if (marriage.days == 1) "" else "s",
                centerX,
                top + 480f * scale,
                27f * scale,
                primaryText,
                bold = true
            )

            // --------------------------------------------------------
            // MARRIAGE DATE
            // --------------------------------------------------------

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                top + 512f * scale,
                15f * scale,
                secondaryText
            )

            // --------------------------------------------------------
            // MARRIAGE DIVIDER
            // --------------------------------------------------------

            drawDivider(
                canvas,
                centerX,
                top + 546f * scale,
                width * 0.66f
            )

            // --------------------------------------------------------
            // TOTAL MARRIED DAYS
            // --------------------------------------------------------

            val marriedDays =
                daysBetween(
                    marriageStart.timeInMillis,
                    System.currentTimeMillis()
                )

            drawText(
                canvas,
                marriedDays.toString(),
                centerX,
                top + 598f * scale,
                35f * scale,
                primaryText,
                bold = true
            )

            drawText(
                canvas,
                "MARRIED DAYS",
                centerX,
                top + 622f * scale,
                12f * scale,
                mutedText
            )

            // --------------------------------------------------------
            // LIVE TIME SINCE MARRIAGE
            // --------------------------------------------------------

            val marriageTime =
                calculateTimeParts(
                    marriageStart.timeInMillis
                )

            val columnY = top + 684f * scale

            val columnSpacing = width * 0.22f

            drawText(
                canvas,
                twoDigits(marriageTime.hours),
                centerX - columnSpacing,
                columnY,
                25f * scale,
                primaryText,
                bold = true
            )

            drawText(
                canvas,
                twoDigits(marriageTime.minutes),
                centerX,
                columnY,
                25f * scale,
                primaryText,
                bold = true
            )

            drawText(
                canvas,
                twoDigits(marriageTime.seconds),
                centerX + columnSpacing,
                columnY,
                25f * scale,
                primaryText,
                bold = true
            )

            drawText(
                canvas,
                "HOURS",
                centerX - columnSpacing,
                columnY + 25f * scale,
                10f * scale,
                mutedText
            )

            drawText(
                canvas,
                "MINUTES",
                centerX,
                columnY + 25f * scale,
                10f * scale,
                mutedText
            )

            drawText(
                canvas,
                "SECONDS",
                centerX + columnSpacing,
                columnY + 25f * scale,
                10f * scale,
                mutedText
            )

            // --------------------------------------------------------
            // FINAL MESSAGE
            // --------------------------------------------------------

            drawText(
                canvas,
                "♥  Same People • Same Dreams  ♥",
                centerX,
                top + 775f * scale,
                16f * scale,
                heartColor,
                bold = true
            )
        }

        // ------------------------------------------------------------
        // TEXT HELPER
        // ------------------------------------------------------------

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            bold: Boolean = false
        ) {

            paint.reset()

            paint.isAntiAlias = true
            paint.isSubpixelText = true
            paint.color = color
            paint.textSize = size
            paint.textAlign = Paint.Align.CENTER

            paint.strokeWidth = 0f

            // Use Android's default sans-serif family
            // without importing Typeface.
            paint.typeface = android.graphics.Typeface.create(
                "sans-serif",
                if (bold) {
                    android.graphics.Typeface.BOLD
                } else {
                    android.graphics.Typeface.NORMAL
                }
            )

            canvas.drawText(
                text,
                x,
                y,
                paint
            )
        }

        // ------------------------------------------------------------
        // HEART LABEL
        // ------------------------------------------------------------

        private fun drawHeartLabel(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            label: String,
            scale: Float,
            useGold: Boolean = false
        ) {

            val color =
                if (useGold) goldColor else heartColor

            drawText(
                canvas,
                "♥  $label  ♥",
                centerX,
                y,
                14f * scale,
                color,
                bold = true
            )
        }

        // ------------------------------------------------------------
        // DIVIDER
        // ------------------------------------------------------------

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            dividerWidth: Float
        ) {

            paint.reset()

            paint.isAntiAlias = true
            paint.color = dividerColor
            paint.strokeWidth = 1f

            canvas.drawLine(
                centerX - dividerWidth / 2f,
                y,
                centerX + dividerWidth / 2f,
                y,
                paint
            )
        }

        // ------------------------------------------------------------
        // LOCK ICON
        // ------------------------------------------------------------

        private fun drawLockIcon(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            size: Float
        ) {

            paint.reset()
            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = size * 0.11f
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = goldColor

            val bodyWidth = size * 1.25f
            val bodyHeight = size * 0.9f

            val left = centerX - bodyWidth / 2f
            val top = centerY
            val right = centerX + bodyWidth / 2f
            val bottom = centerY + bodyHeight

            val body = RectF(
                left,
                top,
                right,
                bottom
            )

            canvas.drawRoundRect(
                body,
                size * 0.12f,
                size * 0.12f,
                paint
            )

            val shackle = RectF(
                centerX - size * 0.42f,
                centerY - size * 0.55f,
                centerX + size * 0.42f,
                centerY + size * 0.25f
            )

            canvas.drawArc(
                shackle,
                180f,
                180f,
                false,
                paint
            )

            paint.style = Paint.Style.FILL

            canvas.drawCircle(
                centerX,
                centerY + size * 0.45f,
                size * 0.09f,
                paint
            )
        }

        // ------------------------------------------------------------
        // MARRIAGE ICON
        // ------------------------------------------------------------

        private fun drawMarriageIcon(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            size: Float
        ) {

            paint.reset()

            paint.isAntiAlias = true
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = size * 0.12f
            paint.color = goldColor

            val ringSize = size * 0.48f

            canvas.drawCircle(
                centerX - ringSize * 0.55f,
                centerY,
                ringSize,
                paint
            )

            canvas.drawCircle(
                centerX + ringSize * 0.55f,
                centerY,
                ringSize,
                paint
            )

            paint.style = Paint.Style.FILL
        }

        // ------------------------------------------------------------
        // DURATION CALCULATOR
        // ------------------------------------------------------------

        private fun calculateDuration(
            start: Calendar
        ): DurationParts {

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
                    Calendar.getInstance()

                previousMonth.timeInMillis =
                    now.timeInMillis

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

            return DurationParts(
                years,
                months,
                days
            )
        }

        // ------------------------------------------------------------
        // TOTAL DAYS
        // ------------------------------------------------------------

        private fun daysBetween(
            startMillis: Long,
            endMillis: Long
        ): Long {

            val difference =
                endMillis - startMillis

            return difference /
                    (24L * 60L * 60L * 1000L)
        }

        // ------------------------------------------------------------
        // HOURS / MINUTES / SECONDS
        // ------------------------------------------------------------

        private fun calculateTimeParts(
            startMillis: Long
        ): TimeParts {

            val difference =
                System.currentTimeMillis() -
                        startMillis

            val totalSeconds =
                difference / 1000L

            val seconds =
                (totalSeconds % 60L).toInt()

            val minutesTotal =
                totalSeconds / 60L

            val minutes =
                (minutesTotal % 60L).toInt()

            val hoursTotal =
                minutesTotal / 60L

            val hours =
                (hoursTotal % 24L).toInt()

            return TimeParts(
                hours,
                minutes,
                seconds
            )
        }

        // ------------------------------------------------------------
        // TWO DIGIT FORMAT
        // ------------------------------------------------------------

        private fun twoDigits(
            value: Int
        ): String {

            return if (value < 10) {
                "0$value"
            } else {
                value.toString()
            }
        }

        // ------------------------------------------------------------
        // DATA CLASSES
        // ------------------------------------------------------------

        private data class DurationParts(
            val years: Int,
            val months: Int,
            val days: Int
        )

        private data class TimeParts(
            val hours: Int,
            val minutes: Int,
            val seconds: Int
        )
    }
}
