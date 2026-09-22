package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        // ------------------------------------------------------------
        // RELATIONSHIP DATES
        // ------------------------------------------------------------

        private val relationshipStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2006)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 14)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        private val marriageStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.APRIL)
            set(Calendar.DAY_OF_MONTH, 9)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // ------------------------------------------------------------
        // DRAWING
        // ------------------------------------------------------------

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var running = false
        private var animationThread: Thread? = null

        private var screenWidth = 1080f
        private var screenHeight = 2400f

        private var animationTime = 0L

        // ------------------------------------------------------------
        // COLORS
        // ------------------------------------------------------------

        private val backgroundColor = Color.rgb(8, 11, 17)

        private val white = Color.rgb(248, 248, 250)
        private val softWhite = Color.rgb(205, 207, 214)
        private val muted = Color.rgb(125, 128, 138)

        private val red = Color.rgb(255, 45, 80)
        private val pink = Color.rgb(255, 70, 105)

        private val gold = Color.rgb(255, 193, 70)

        // ------------------------------------------------------------
        // VISIBILITY
        // ------------------------------------------------------------

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
            super.onSurfaceChanged(holder, format, width, height)

            screenWidth = width.toFloat()
            screenHeight = height.toFloat()

            drawWallpaper()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopDrawing()
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            stopDrawing()
            super.onDestroy()
        }

        // ------------------------------------------------------------
        // ANIMATION LOOP
        // ------------------------------------------------------------

        private fun startDrawing() {

            if (running) return

            running = true

            animationThread = Thread {

                while (running) {

                    animationTime = System.currentTimeMillis()

                    drawWallpaper()

                    try {
                        Thread.sleep(50L)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }

            animationThread?.start()
        }

        private fun stopDrawing() {

            running = false

            animationThread?.interrupt()
            animationThread = null
        }

        // ------------------------------------------------------------
        // MAIN DRAW
        // ------------------------------------------------------------

        private fun drawWallpaper() {

            val holder = surfaceHolder

            if (!holder.surface.isValid) return

            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                screenWidth = canvas.width.toFloat()
                screenHeight = canvas.height.toFloat()

                canvas.drawColor(backgroundColor)

                drawBackgroundAnimation(canvas)

                drawMainContent(canvas)

            } catch (_: Exception) {

                // Prevent wallpaper from crashing because of a
                // temporary surface/system drawing issue.

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
        // BACKGROUND ANIMATION
        // ------------------------------------------------------------

        private fun drawBackgroundAnimation(canvas: Canvas) {

            val progress =
                (animationTime % 8000L).toFloat() / 8000f

            val wave = sin(progress * Math.PI * 2.0).toFloat()

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = scale(1.2f)
            paint.color = Color.argb(35, 255, 45, 80)

            path.reset()

            val left = -screenWidth * 0.18f
            val right = screenWidth * 1.18f

            val startY = screenHeight * 0.34f

            path.moveTo(left, startY)

            path.cubicTo(
                screenWidth * 0.08f,
                screenHeight * (0.25f + wave * 0.015f),
                screenWidth * 0.02f,
                screenHeight * (0.55f + wave * 0.02f),
                screenWidth * 0.27f,
                screenHeight * 0.72f
            )

            path.cubicTo(
                screenWidth * 0.44f,
                screenHeight * 0.85f,
                screenWidth * 0.50f,
                screenHeight * 0.90f,
                screenWidth * 0.62f,
                screenHeight * 0.88f
            )

            path.cubicTo(
                screenWidth * 0.85f,
                screenHeight * 0.84f,
                screenWidth * 0.88f,
                screenHeight * 0.58f,
                right,
                screenHeight * 0.45f
            )

            canvas.drawPath(path, paint)

            // Second subtle line

            paint.color = Color.argb(18, 255, 45, 80)
            paint.strokeWidth = scale(0.8f)

            path.reset()

            path.moveTo(
                -screenWidth * 0.1f,
                screenHeight * 0.52f
            )

            path.cubicTo(
                screenWidth * 0.20f,
                screenHeight * 0.80f,
                screenWidth * 0.55f,
                screenHeight * 0.93f,
                screenWidth * 1.08f,
                screenHeight * 0.35f
            )

            canvas.drawPath(path, paint)

            paint.style = Paint.Style.FILL
        }

        // ------------------------------------------------------------
        // MAIN CONTENT
        // ------------------------------------------------------------

        private fun drawMainContent(canvas: Canvas) {

            val centerX = screenWidth / 2f

            /*
             * Responsive top position.
             *
             * On most modern phones the status bar occupies
             * approximately 3–5% of the screen.
             */
            val top = screenHeight * 0.075f

            var y = top

            // --------------------------------------------------------
            // HEART
            // --------------------------------------------------------

            drawHeart(
                canvas,
                centerX,
                y + scale(28f),
                scale(15f)
            )

            y += scale(66f)

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                y,
                10f,
                muted,
                false
            )

            y += scale(32f)

            // --------------------------------------------------------
            // CURRENT TIME
            // --------------------------------------------------------

            val time = SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

            drawText(
                canvas,
                time,
                centerX,
                y,
                27f,
                white,
                true
            )

            y += scale(50f)

            // --------------------------------------------------------
            // RELATIONSHIP
            // --------------------------------------------------------

            drawSectionTitle(
                canvas,
                centerX,
                y,
                "TOGETHER FOR"
            )

            y += scale(35f)

            val relationshipAge =
                calculateCalendarDifference(
                    relationshipStart,
                    animationTime
                )

            drawText(
                canvas,
                "${relationshipAge.years} Years  •  " +
                        "${relationshipAge.months} Months  •  " +
                        "${relationshipAge.days} Days",
                centerX,
                y,
                20f,
                white,
                true
            )

            y += scale(29f)

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                y,
                9f,
                muted,
                false
            )

            y += scale(22f)

            drawDivider(canvas, y)

            y += scale(36f)

            // --------------------------------------------------------
            // RELATIONSHIP TOTAL DAYS
            // --------------------------------------------------------

            val relationshipElapsed =
                getElapsed(relationshipStart)

            drawText(
                canvas,
                relationshipElapsed.totalDays.toString(),
                centerX,
                y,
                23f,
                white,
                true
            )

            y += scale(17f)

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                y,
                7f,
                muted,
                false
            )

            y += scale(40f)

            drawTimeColumns(
                canvas,
                y,
                relationshipElapsed
            )

            y += scale(70f)

            // --------------------------------------------------------
            // MARRIAGE RINGS
            // --------------------------------------------------------

            drawMarriageRings(
                canvas,
                centerX,
                y
            )

            y += scale(66f)

            // --------------------------------------------------------
            // MARRIED FOR
            // --------------------------------------------------------

            drawSectionTitle(
                canvas,
                centerX,
                y,
                "MARRIED FOR"
            )

            y += scale(35f)

            val marriageAge =
                calculateCalendarDifference(
                    marriageStart,
                    animationTime
                )

            drawText(
                canvas,
                "${marriageAge.years} Years  •  " +
                        "${marriageAge.months} Months  •  " +
                        "${marriageAge.days} Days",
                centerX,
                y,
                20f,
                white,
                true
            )

            y += scale(29f)

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                y,
                9f,
                muted,
                false
            )

            y += scale(22f)

            drawDivider(canvas, y)

            y += scale(36f)

            // --------------------------------------------------------
            // MARRIAGE TOTAL DAYS
            // --------------------------------------------------------

            val marriageElapsed =
                getElapsed(marriageStart)

            drawText(
                canvas,
                marriageElapsed.totalDays.toString(),
                centerX,
                y,
                23f,
                white,
                true
            )

            y += scale(17f)

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                y,
                7f,
                muted,
                false
            )

            y += scale(40f)

            drawTimeColumns(
                canvas,
                y,
                marriageElapsed
            )

            y += scale(66f)

            // --------------------------------------------------------
            // FINAL MESSAGE
            // --------------------------------------------------------

            drawHeart(
                canvas,
                centerX - scale(78f),
                y - scale(5f),
                scale(5f)
            )

            drawText(
                canvas,
                "Same People • Same Dreams",
                centerX,
                y,
                11f,
                pink,
                true
            )

            drawHeart(
                canvas,
                centerX + scale(78f),
                y - scale(5f),
                scale(5f)
            )
        }

        // ------------------------------------------------------------
        // SECTION TITLE
        // ------------------------------------------------------------

        private fun drawSectionTitle(
            canvas: Canvas,
            centerX: Float,
            baseline: Float,
            title: String
        ) {

            val pulse =
                1f + 0.08f *
                        sin(
                            animationTime / 450.0
                        ).toFloat()

            drawHeart(
                canvas,
                centerX - scale(64f),
                baseline - scale(5f),
                scale(5f) * pulse
            )

            drawText(
                canvas,
                title,
                centerX,
                baseline,
                10f,
                pink,
                true
            )

            drawHeart(
                canvas,
                centerX + scale(64f),
                baseline - scale(5f),
                scale(5f) * pulse
            )
        }

        // ------------------------------------------------------------
        // TIME COLUMNS
        // ------------------------------------------------------------

        private fun drawTimeColumns(
            canvas: Canvas,
            y: Float,
            elapsed: ElapsedTime
        ) {

            val column1 = screenWidth * 0.30f
            val column2 = screenWidth * 0.50f
            val column3 = screenWidth * 0.70f

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.hours
                ),
                column1,
                y,
                18f,
                white,
                true
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.minutes
                ),
                column2,
                y,
                18f,
                white,
                true
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.seconds
                ),
                column3,
                y,
                18f,
                white,
                true
            )

            drawText(
                canvas,
                "HOURS",
                column1,
                y + scale(22f),
                6f,
                muted,
                false
            )

            drawText(
                canvas,
                "MINUTES",
                column2,
                y + scale(22f),
                6f,
                muted,
                false
            )

            drawText(
                canvas,
                "SECONDS",
                column3,
                y + scale(22f),
                6f,
                muted,
                false
            )

            // Small vertical separators

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = scale(0.7f)
            paint.color = Color.rgb(45, 48, 56)

            canvas.drawLine(
                screenWidth * 0.40f,
                y - scale(8f),
                screenWidth * 0.40f,
                y + scale(15f),
                paint
            )

            canvas.drawLine(
                screenWidth * 0.60f,
                y - scale(8f),
                screenWidth * 0.60f,
                y + scale(15f),
                paint
            )

            paint.style = Paint.Style.FILL
        }

        // ------------------------------------------------------------
        // DIVIDER
        // ------------------------------------------------------------

        private fun drawDivider(
            canvas: Canvas,
            y: Float
        ) {

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = scale(0.7f)
            paint.color = Color.rgb(40, 43, 51)

            canvas.drawLine(
                screenWidth * 0.15f,
                y,
                screenWidth * 0.85f,
                y,
                paint
            )

            paint.style = Paint.Style.FILL
        }

        // ------------------------------------------------------------
        // HEART
        // ------------------------------------------------------------

        private fun drawHeart(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            size: Float
        ) {

            val pulse =
                1f + 0.07f *
                        sin(
                            animationTime / 420.0
                        ).toFloat()

            val s = size * pulse

            path.reset()

            path.moveTo(cx, cy + s)

            path.cubicTo(
                cx - s * 1.8f,
                cy - s * 0.1f,
                cx - s * 1.25f,
                cy - s * 1.5f,
                cx - s * 0.55f,
                cy - s * 1.05f
            )

            path.cubicTo(
                cx - s * 0.2f,
                cy - s * 1.55f,
                cx + s * 0.2f,
                cy - s * 1.55f,
                cx + s * 0.55f,
                cy - s * 1.05f
            )

            path.cubicTo(
                cx + s * 1.25f,
                cy - s * 1.5f,
                cx + s * 1.8f,
                cy - s * 0.1f,
                cx,
                cy + s
            )

            paint.style = Paint.Style.FILL
            paint.color = red

            canvas.drawPath(path, paint)
        }

        // ------------------------------------------------------------
        // MARRIAGE RINGS
        // ------------------------------------------------------------

        private fun drawMarriageRings(
            canvas: Canvas,
            centerX: Float,
            centerY: Float
        ) {

            val pulse =
                1f + 0.04f *
                        sin(
                            animationTime / 700.0
                        ).toFloat()

            val ringSize = scale(23f) * pulse

            // Glow

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = scale(3f)
            paint.color = Color.argb(35, 255, 193, 70)

            canvas.drawCircle(
                centerX - scale(9f),
                centerY,
                ringSize,
                paint
            )

            canvas.drawCircle(
                centerX + scale(9f),
                centerY,
                ringSize,
                paint
            )

            // Main rings

            paint.strokeWidth = scale(2.8f)
            paint.color = gold

            canvas.drawCircle(
                centerX - scale(9f),
                centerY,
                ringSize,
                paint
            )

            canvas.drawCircle(
                centerX + scale(9f),
                centerY,
                ringSize,
                paint
            )

            // Tiny heart above rings

            drawHeart(
                canvas,
                centerX,
                centerY - scale(31f),
                scale(4f)
            )

            paint.style = Paint.Style.FILL
        }

        // ------------------------------------------------------------
        // TEXT
        // ------------------------------------------------------------

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            baseline: Float,
            size: Float,
            color: Int,
            bold: Boolean
        ) {

            paint.style = Paint.Style.FILL
            paint.color = color
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = scale(size)

            // Avoid Typeface completely.
            paint.isFakeBoldText = bold

            canvas.drawText(
                text,
                x,
                baseline,
                paint
            )

            paint.isFakeBoldText = false
        }

        // ------------------------------------------------------------
        // SCALE
        // ------------------------------------------------------------

        private fun scale(value: Float): Float {

            /*
             * 1080px width is our design reference.
             *
             * The minimum scale keeps the design readable on
             * narrower devices.
             */

            val widthScale =
                screenWidth / 1080f

            return value * widthScale.coerceAtLeast(0.72f)
        }

        // ------------------------------------------------------------
        // ELAPSED TIME
        // ------------------------------------------------------------

        private fun getElapsed(
            startMillis: Long
        ): ElapsedTime {

            val now = System.currentTimeMillis()

            var difference =
                now - startMillis

            if (difference < 0L) {
                difference = 0L
            }

            val totalSeconds =
                difference / 1000L

            val totalDays =
                totalSeconds / 86400L

            val hours =
                (totalSeconds % 86400L) / 3600L

            val minutes =
                (totalSeconds % 3600L) / 60L

            val seconds =
                totalSeconds % 60L

            return ElapsedTime(
                totalDays = totalDays,
                hours = hours,
                minutes = minutes,
                seconds = seconds
            )
        }

        // ------------------------------------------------------------
        // CALENDAR DIFFERENCE
        // ------------------------------------------------------------

        private fun calculateCalendarDifference(
            startMillis: Long,
            endMillis: Long
        ): CalendarAge {

            val start = Calendar.getInstance()
            start.timeInMillis = startMillis

            val end = Calendar.getInstance()
            end.timeInMillis = endMillis

            var years =
                end.get(Calendar.YEAR) -
                        start.get(Calendar.YEAR)

            var months =
                end.get(Calendar.MONTH) -
                        start.get(Calendar.MONTH)

            var days =
                end.get(Calendar.DAY_OF_MONTH) -
                        start.get(Calendar.DAY_OF_MONTH)

            if (days < 0) {

                months--

                val previousMonth =
                    Calendar.getInstance()

                previousMonth.timeInMillis =
                    end.timeInMillis

                previousMonth.add(
                    Calendar.MONTH,
                    -1
                )

                days +=
                    previousMonth.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                    )
            }

            if (months < 0) {

                years--
                months += 12
            }

            if (years < 0) {
                years = 0
                months = 0
                days = 0
            }

            return CalendarAge(
                years = years,
                months = months,
                days = days
            )
        }

        // ------------------------------------------------------------
        // DATA CLASSES
        // ------------------------------------------------------------

        private data class ElapsedTime(
            val totalDays: Long,
            val hours: Long,
            val minutes: Long,
            val seconds: Long
        )

        private data class CalendarAge(
            val years: Int,
            val months: Int,
            val days: Int
        )
    }
}
