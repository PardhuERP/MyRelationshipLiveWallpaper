package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var thread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val heartPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val togetherDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2006)
            set(Calendar.MONTH, Calendar.FEBRUARY)
            set(Calendar.DAY_OF_MONTH, 14)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        private val marriageDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2025)
            set(Calendar.MONTH, Calendar.APRIL)
            set(Calendar.DAY_OF_MONTH, 9)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
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

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startDrawing()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)

            if (visible) {
                drawWallpaper()
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
                        Thread.sleep(1000L)
                    } catch (_: InterruptedException) {
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

                /*
                 * Responsive scale.
                 *
                 * Designed around a 1080 x 2400 reference screen.
                 */
                val sx = width / 1080f
                val sy = height / 2400f
                val scale = minOf(sx, sy)

                canvas.drawColor(Color.rgb(8, 11, 19))

                drawBackground(canvas, width, height)

                drawContent(
                    canvas = canvas,
                    width = width,
                    height = height,
                    scale = scale
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // ------------------------------------------------------------
        // BACKGROUND
        // ------------------------------------------------------------

        private fun drawBackground(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val backgroundShader = LinearGradient(
                0f,
                0f,
                width,
                height,
                Color.rgb(7, 10, 18),
                Color.rgb(15, 16, 27),
                Shader.TileMode.CLAMP
            )

            paint.shader = backgroundShader
            canvas.drawRect(
                0f,
                0f,
                width,
                height,
                paint
            )

            paint.shader = null

            /*
             * Very subtle pink glow.
             */
            val glow = RadialGradient(
                width * 0.50f,
                height * 0.48f,
                width * 0.60f,
                intArrayOf(
                    Color.argb(28, 255, 45, 95),
                    Color.argb(8, 255, 45, 95),
                    Color.TRANSPARENT
                ),
                floatArrayOf(
                    0f,
                    0.45f,
                    1f
                ),
                Shader.TileMode.CLAMP
            )

            paint.shader = glow

            canvas.drawCircle(
                width * 0.50f,
                height * 0.48f,
                width * 0.60f,
                paint
            )

            paint.shader = null
        }

        // ------------------------------------------------------------
        // MAIN CONTENT
        // ------------------------------------------------------------

        private fun drawContent(
            canvas: Canvas,
            width: Float,
            height: Float,
            scale: Float
        ) {

            val centerX = width / 2f

            /*
             * Top content position.
             *
             * Keeps the design safely away from the status-bar area
             * while leaving enough room at the bottom for launcher icons.
             */
            val top = height * 0.075f

            // --------------------------------------------------------
            // LOCK
            // --------------------------------------------------------

            drawLock(
                canvas,
                centerX,
                top + 30f * scale,
                20f * scale
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                top + 78f * scale,
                18f * scale,
                Color.rgb(145, 151, 170),
                Paint.Align.CENTER,
                Paint.Typeface.create("sans-serif", Paint.NORMAL)
            )

            // --------------------------------------------------------
            // CURRENT TIME
            // --------------------------------------------------------

            val now = Date()

            val time = SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(now)

            drawText(
                canvas,
                time,
                centerX,
                top + 140f * scale,
                54f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            // --------------------------------------------------------
            // TOGETHER SECTION
            // --------------------------------------------------------

            val together = calculateDuration(
                togetherDate,
                now
            )

            drawSectionTitle(
                canvas = canvas,
                centerX = centerX,
                y = top + 210f * scale,
                text = "TOGETHER FOR",
                scale = scale,
                heartColor = Color.rgb(255, 55, 100)
            )

            drawText(
                canvas,
                "${together.years} Years  •  " +
                        "${together.months} Months  •  " +
                        "${together.days} Days",
                centerX,
                top + 265f * scale,
                29f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                top + 305f * scale,
                16f * scale,
                Color.rgb(155, 160, 180),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            // --------------------------------------------------------
            // DIVIDER
            // --------------------------------------------------------

            drawDivider(
                canvas,
                centerX,
                top + 340f * scale,
                width * 0.68f,
                scale
            )

            // --------------------------------------------------------
            // MARRIAGE SECTION
            // --------------------------------------------------------

            drawWeddingRings(
                canvas,
                centerX,
                top + 382f * scale,
                18f * scale
            )

            drawText(
                canvas,
                "MARRIED FOR",
                centerX,
                top + 435f * scale,
                20f * scale,
                Color.rgb(255, 85, 125),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            val married = calculateDuration(
                marriageDate,
                now
            )

            drawText(
                canvas,
                "${married.years} Years  •  " +
                        "${married.months} Months  •  " +
                        "${married.days} Days",
                centerX,
                top + 490f * scale,
                29f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                top + 530f * scale,
                16f * scale,
                Color.rgb(155, 160, 180),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            // --------------------------------------------------------
            // SECOND DIVIDER
            // --------------------------------------------------------

            drawDivider(
                canvas,
                centerX,
                top + 565f * scale,
                width * 0.72f,
                scale
            )

            // --------------------------------------------------------
            // TOTAL DAYS
            // --------------------------------------------------------

            val togetherDays = daysBetween(
                togetherDate,
                now
            )

            val marriedDays = daysBetween(
                marriageDate,
                now
            )

            drawText(
                canvas,
                togetherDays.toString(),
                width * 0.33f,
                top + 620f * scale,
                34f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                "TOGETHER DAYS",
                width * 0.33f,
                top + 650f * scale,
                13f * scale,
                Color.rgb(145, 151, 170),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            drawText(
                canvas,
                marriedDays.toString(),
                width * 0.67f,
                top + 620f * scale,
                34f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                "MARRIED DAYS",
                width * 0.67f,
                top + 650f * scale,
                13f * scale,
                Color.rgb(145, 151, 170),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            // Vertical divider between total days.
            linePaint.color = Color.argb(
                90,
                180,
                185,
                205
            )

            linePaint.strokeWidth = 1f * scale

            canvas.drawLine(
                centerX,
                top + 590f * scale,
                centerX,
                top + 660f * scale,
                linePaint
            )

            // --------------------------------------------------------
            // LIVE TIME COMPONENTS
            // --------------------------------------------------------

            val calendar = Calendar.getInstance()

            val hours = calendar.get(Calendar.HOUR_OF_DAY)
            val minutes = calendar.get(Calendar.MINUTE)
            val seconds = calendar.get(Calendar.SECOND)

            drawDivider(
                canvas,
                centerX,
                top + 690f * scale,
                width * 0.72f,
                scale
            )

            val column1 = width * 0.25f
            val column2 = width * 0.50f
            val column3 = width * 0.75f

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    hours
                ),
                column1,
                top + 755f * scale,
                32f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    minutes
                ),
                column2,
                top + 755f * scale,
                32f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    seconds
                ),
                column3,
                top + 755f * scale,
                32f * scale,
                Color.WHITE,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )

            drawText(
                canvas,
                "HOURS",
                column1,
                top + 783f * scale,
                12f * scale,
                Color.rgb(135, 140, 158),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            drawText(
                canvas,
                "MINUTES",
                column2,
                top + 783f * scale,
                12f * scale,
                Color.rgb(135, 140, 158),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            drawText(
                canvas,
                "SECONDS",
                column3,
                top + 783f * scale,
                12f * scale,
                Color.rgb(135, 140, 158),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.NORMAL
                )
            )

            // --------------------------------------------------------
            // BOTTOM MESSAGE
            // --------------------------------------------------------

            drawHeart(
                canvas,
                centerX - 205f * scale,
                top + 850f * scale,
                11f * scale,
                Color.rgb(255, 45, 95)
            )

            drawHeart(
                canvas,
                centerX + 205f * scale,
                top + 850f * scale,
                11f * scale,
                Color.rgb(255, 45, 95)
            )

            drawText(
                canvas,
                "Same People • Same Dreams",
                centerX,
                top + 857f * scale,
                19f * scale,
                Color.rgb(255, 65, 105),
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )
        }

        // ------------------------------------------------------------
        // DURATION CALCULATION
        // ------------------------------------------------------------

        private data class RelationshipDuration(
            val years: Int,
            val months: Int,
            val days: Int
        )

        private fun calculateDuration(
            start: Calendar,
            endDate: Date
        ): RelationshipDuration {

            val end = Calendar.getInstance()
            end.time = endDate

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

                previousMonth.time = end.time

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

            if (years < 0) {
                years = 0
                months = 0
                days = 0
            }

            return RelationshipDuration(
                years = years,
                months = months,
                days = days
            )
        }

        // ------------------------------------------------------------
        // TOTAL DAYS
        // ------------------------------------------------------------

        private fun daysBetween(
            start: Calendar,
            end: Date
        ): Long {

            val startMillis = start.timeInMillis
            val endMillis = end.time

            return TimeUnit.MILLISECONDS.toDays(
                endMillis - startMillis
            )
        }

        // ------------------------------------------------------------
        // TEXT
        // ------------------------------------------------------------

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            align: Paint.Align,
            typeface: android.graphics.Typeface
        ) {

            paint.shader = null
            paint.style = Paint.Style.FILL
            paint.color = color
            paint.textSize = size
            paint.textAlign = align
            paint.typeface = typeface

            canvas.drawText(
                text,
                x,
                y,
                paint
            )
        }

        // ------------------------------------------------------------
        // DIVIDER
        // ------------------------------------------------------------

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            lineWidth: Float,
            scale: Float
        ) {

            linePaint.shader = LinearGradient(
                centerX - lineWidth / 2f,
                y,
                centerX + lineWidth / 2f,
                y,
                Color.TRANSPARENT,
                Color.argb(
                    120,
                    160,
                    165,
                    185
                ),
                Shader.TileMode.MIRROR
            )

            linePaint.strokeWidth = 1f * scale

            canvas.drawLine(
                centerX - lineWidth / 2f,
                y,
                centerX + lineWidth / 2f,
                y,
                linePaint
            )

            linePaint.shader = null
        }

        // ------------------------------------------------------------
        // HEART
        // ------------------------------------------------------------

        private fun drawHeart(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            size: Float,
            color: Int
        ) {

            heartPaint.shader = null
            heartPaint.color = color
            heartPaint.style = Paint.Style.FILL

            val path = Path()

            path.moveTo(
                centerX,
                centerY + size
            )

            path.cubicTo(
                centerX - size * 1.8f,
                centerY - size * 0.2f,
                centerX - size,
                centerY - size * 1.4f,
                centerX,
                centerY - size * 0.6f
            )

            path.cubicTo(
                centerX + size,
                centerY - size * 1.4f,
                centerX + size * 1.8f,
                centerY - size * 0.2f,
                centerX,
                centerY + size
            )

            canvas.drawPath(
                path,
                heartPaint
            )
        }

        // ------------------------------------------------------------
        // SECTION TITLE
        // ------------------------------------------------------------

        private fun drawSectionTitle(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            text: String,
            scale: Float,
            heartColor: Int
        ) {

            val offset = 160f * scale

            drawHeart(
                canvas,
                centerX - offset,
                y - 5f * scale,
                10f * scale,
                heartColor
            )

            drawHeart(
                canvas,
                centerX + offset,
                y - 5f * scale,
                10f * scale,
                heartColor
            )

            drawText(
                canvas,
                text,
                centerX,
                y,
                20f * scale,
                heartColor,
                Paint.Align.CENTER,
                Paint.Typeface.create(
                    "sans-serif",
                    Paint.BOLD
                )
            )
        }

        // ------------------------------------------------------------
        // LOCK ICON
        // ------------------------------------------------------------

        private fun drawLock(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            size: Float
        ) {

            val lockPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            lockPaint.color = Color.rgb(
                255,
                193,
                55
            )

            lockPaint.style = Paint.Style.STROKE
            lockPaint.strokeWidth = size * 0.18f
            lockPaint.strokeCap = Paint.Cap.ROUND

            val left = centerX - size
            val right = centerX + size
            val top = centerY - size * 0.65f
            val bottom = centerY + size * 0.9f

            val shackleRect = android.graphics.RectF(
                left + size * 0.25f,
                top - size * 0.9f,
                right - size * 0.25f,
                top + size * 0.65f
            )

            canvas.drawArc(
                shackleRect,
                180f,
                180f,
                false,
                lockPaint
            )

            lockPaint.style = Paint.Style.FILL

            canvas.drawRoundRect(
                android.graphics.RectF(
                    left,
                    top,
                    right,
                    bottom
                ),
                size * 0.18f,
                size * 0.18f,
                lockPaint
            )

            lockPaint.color = Color.rgb(
                25,
                28,
                36
            )

            canvas.drawCircle(
                centerX,
                top + size * 0.55f,
                size * 0.14f,
                lockPaint
            )
        }

        // ------------------------------------------------------------
        // WEDDING RINGS
        // ------------------------------------------------------------

        private fun drawWeddingRings(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            radius: Float
        ) {

            val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            ringPaint.color = Color.rgb(
                255,
                194,
                55
            )

            ringPaint.style = Paint.Style.STROKE
            ringPaint.strokeWidth = radius * 0.25f

            canvas.drawCircle(
                centerX - radius * 0.55f,
                centerY,
                radius,
                ringPaint
            )

            canvas.drawCircle(
                centerX + radius * 0.55f,
                centerY,
                radius,
                ringPaint
            )

            /*
             * Small heart above the rings.
             */
            drawHeart(
                canvas,
                centerX,
                centerY - radius * 1.45f,
                radius * 0.45f,
                Color.rgb(
                    255,
                    70,
                    105
                )
            )
        }
    }
}
