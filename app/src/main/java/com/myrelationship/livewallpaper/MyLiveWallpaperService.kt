package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return RelationshipEngine()
    }

    inner class RelationshipEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var animationTime = System.currentTimeMillis()

        // Cache durations and update them once per second
        private var lastSecond = -1L
        private var togetherDuration = calculateDuration(
            createDate(2006, Calendar.FEBRUARY, 14),
            Calendar.getInstance()
        )

        private var marriedDuration = calculateDuration(
            createDate(2025, Calendar.APRIL, 9),
            Calendar.getInstance()
        )

        override fun onVisibilityChanged(isVisible: Boolean) {
            super.onVisibilityChanged(isVisible)

            if (isVisible) {
                startAnimation()
            } else {
                stopAnimation()
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)

            if (!running) {
                startAnimation()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopAnimation()
            super.onSurfaceDestroyed(holder)
        }

        private fun startAnimation() {

            if (running) return

            running = true

            drawingThread = Thread {

                while (running) {

                    animationTime = System.currentTimeMillis()

                    updateDurations()

                    drawWallpaper()

                    try {
                        Thread.sleep(40)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }

            drawingThread?.start()
        }

        private fun stopAnimation() {

            running = false

            drawingThread?.interrupt()
            drawingThread = null
        }

        private fun updateDurations() {

            val nowMillis = System.currentTimeMillis()
            val currentSecond = nowMillis / 1000L

            if (currentSecond == lastSecond) {
                return
            }

            lastSecond = currentSecond

            val now = Calendar.getInstance()

            togetherDuration = calculateDuration(
                createDate(
                    2006,
                    Calendar.FEBRUARY,
                    14
                ),
                now
            )

            marriedDuration = calculateDuration(
                createDate(
                    2025,
                    Calendar.APRIL,
                    9
                ),
                now
            )
        }

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) {
                    return
                }

                // -------------------------------------------------
                // DIRECT SCREEN DIMENSIONS
                // -------------------------------------------------

                val screenWidth = canvas.width.toFloat()
                val screenHeight = canvas.height.toFloat()

                canvas.drawColor(
                    Color.rgb(4, 7, 13)
                )

                // -------------------------------------------------
                // TARGET DESIGN
                // -------------------------------------------------

                val targetWidth = 1080f
                val targetHeight = 2400f

                val scaleX = screenWidth / targetWidth
                val scaleY = screenHeight / targetHeight

                canvas.save()

                canvas.scale(
                    scaleX,
                    scaleY
                )

                // Background first
                drawBackgroundEffects(
                    canvas,
                    targetWidth,
                    targetHeight
                )

                // Main content
                drawMainContent(
                    canvas,
                    targetWidth,
                    targetHeight
                )

                canvas.restore()

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // =========================================================
        // BACKGROUND
        // =========================================================

        private fun drawBackgroundEffects(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t = animationTime / 1000.0

            // -----------------------------------------------------
            // SOFT LARGE HEARTS
            // -----------------------------------------------------

            drawBackgroundHeart(
                canvas,
                100f,
                440f,
                115f,
                22
            )

            drawBackgroundHeart(
                canvas,
                940f,
                430f,
                85f,
                20
            )

            drawBackgroundHeart(
                canvas,
                75f,
                820f,
                55f,
                18
            )

            drawBackgroundHeart(
                canvas,
                1000f,
                900f,
                65f,
                18
            )

            drawBackgroundHeart(
                canvas,
                130f,
                1200f,
                85f,
                16
            )

            drawBackgroundHeart(
                canvas,
                940f,
                1250f,
                115f,
                17
            )

            drawBackgroundHeart(
                canvas,
                90f,
                1750f,
                70f,
                15
            )

            drawBackgroundHeart(
                canvas,
                930f,
                1900f,
                130f,
                15
            )

            drawBackgroundHeart(
                canvas,
                150f,
                2200f,
                90f,
                13
            )

            // -----------------------------------------------------
            // MOVING GLOWING CURVE - LEFT
            // -----------------------------------------------------

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.2f
            paint.color = Color.argb(
                85,
                255,
                35,
                95
            )

            path.reset()

            path.moveTo(
                -120f,
                900f
            )

            for (i in 0..220) {

                val x = -120f + i * 6f

                val wave =
                    sin(
                        i * 0.035 +
                                t * 0.25
                    ) * 45.0

                val y =
                    900f +
                            i * 2.1f +
                            wave.toFloat()

                path.lineTo(
                    x,
                    y
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // MOVING CURVE - RIGHT
            // -----------------------------------------------------

            paint.color = Color.argb(
                65,
                255,
                40,
                100
            )

            path.reset()

            path.moveTo(
                width + 100f,
                1450f
            )

            for (i in 0..240) {

                val x =
                    width + 100f - i * 6f

                val wave =
                    cos(
                        i * 0.035 +
                                t * 0.22
                    ) * 55.0

                val y =
                    1450f +
                            i * 2.2f +
                            wave.toFloat()

                path.lineTo(
                    x,
                    y
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // LOWER CURVE
            // -----------------------------------------------------

            paint.color = Color.argb(
                55,
                255,
                40,
                95
            )

            path.reset()

            path.moveTo(
                -100f,
                2050f
            )

            for (i in 0..230) {

                val x =
                    -100f + i * 6f

                val wave =
                    sin(
                        i * 0.04 +
                                t * 0.18
                    ) * 50.0

                val y =
                    2050f -
                            i * 1.5f +
                            wave.toFloat()

                path.lineTo(
                    x,
                    y
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // SMALL FLOATING PARTICLES
            // -----------------------------------------------------

            paint.style = Paint.Style.FILL

            for (i in 0 until 32) {

                val baseX =
                    ((i * 137) % width.toInt()).toFloat()

                val speed =
                    5 + (i % 6)

                val movement =
                    (
                        t * speed +
                                i * 113
                        ) % height

                val y =
                    height -
                            movement.toFloat()

                val pulse =
                    (
                        sin(
                            t * 1.5 +
                                    i
                        ) + 1
                        ) * 0.5

                val alpha =
                    (
                        25 +
                                pulse * 35
                        ).toInt()

                paint.color = Color.argb(
                    alpha,
                    255,
                    35,
                    90
                )

                val radius =
                    if (i % 4 == 0) {
                        2.5f
                    } else {
                        1.3f
                    }

                canvas.drawCircle(
                    baseX,
                    y,
                    radius,
                    paint
                )
            }

            // A few brighter particles
            drawParticle(
                canvas,
                80f,
                280f,
                t
            )

            drawParticle(
                canvas,
                960f,
                250f,
                t + 1
            )

            drawParticle(
                canvas,
                180f,
                1450f,
                t + 2
            )

            drawParticle(
                canvas,
                900f,
                1550f,
                t + 3
            )

            drawParticle(
                canvas,
                300f,
                2050f,
                t + 4
            )

            drawParticle(
                canvas,
                850f,
                2100f,
                t + 5
            )
        }

        // =========================================================
        // BACKGROUND HEART
        // =========================================================

        private fun drawBackgroundHeart(
            canvas: Canvas,
            x: Float,
            y: Float,
            size: Float,
            alpha: Int
        ) {

            val pulse =
                1f +
                        sin(
                            animationTime / 1400.0 +
                                    x
                        ).toFloat() * 0.04f

            drawHeartShape(
                canvas,
                x,
                y,
                size * pulse,
                Color.argb(
                    alpha,
                    170,
                    10,
                    55
                )
            )
        }

        // =========================================================
        // PARTICLE
        // =========================================================

        private fun drawParticle(
            canvas: Canvas,
            x: Float,
            y: Float,
            time: Double
        ) {

            val pulse =
                (
                    sin(time * 2.0) + 1.0
                    ) * 0.5

            paint.style = Paint.Style.FILL

            paint.color = Color.argb(
                (50 + pulse * 70).toInt(),
                255,
                30,
                90
            )

            canvas.drawCircle(
                x,
                y,
                2.5f,
                paint
            )

            paint.color = Color.argb(
                35,
                255,
                30,
                90
            )

            canvas.drawCircle(
                x,
                y,
                8f,
                paint
            )
        }

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        private fun drawMainContent(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val centerX =
                width / 2f

            // -----------------------------------------------------
            // TOP HEART
            // -----------------------------------------------------

            drawGlowingHeart(
                canvas,
                centerX,
                125f
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                170f,
                17f,
                Color.rgb(
                    150,
                    153,
                    163
                ),
                false
            )

            // -----------------------------------------------------
            // CLOCK
            // -----------------------------------------------------

            val time =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            drawText(
                canvas,
                time,
                centerX,
                225f,
                46f,
                Color.WHITE,
                true
            )

            // -----------------------------------------------------
            // TOGETHER TITLE
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                305f,
                22f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            // -----------------------------------------------------
            // TOGETHER DURATION
            // -----------------------------------------------------

            drawText(
                canvas,
                "${togetherDuration.years} Years  •  " +
                        "${togetherDuration.months} Months  •  " +
                        "${togetherDuration.days} Days",
                centerX,
                365f,
                29f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                410f,
                16f,
                Color.rgb(
                    150,
                    153,
                    163
                ),
                false
            )

            // -----------------------------------------------------
            // DIVIDER
            // -----------------------------------------------------

            drawHeartDivider(
                canvas,
                centerX,
                455f
            )

            // -----------------------------------------------------
            // TOTAL TOGETHER DAYS
            // -----------------------------------------------------

            drawText(
                canvas,
                togetherDuration.totalDays.toString(),
                centerX,
                515f,
                39f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                545f,
                12f,
                Color.rgb(
                    125,
                    128,
                    138
                ),
                false
            )

            // -----------------------------------------------------
            // TOGETHER TIME
            // -----------------------------------------------------

            drawTimeColumns(
                canvas,
                centerX,
                610f,
                togetherDuration.hours,
                togetherDuration.minutes,
                togetherDuration.seconds
            )

            // -----------------------------------------------------
            // WEDDING RINGS
            // -----------------------------------------------------

            drawWeddingRings(
                canvas,
                centerX,
                785f
            )

            // -----------------------------------------------------
            // MARRIED TITLE
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                885f,
                22f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            // -----------------------------------------------------
            // MARRIED DURATION
            // -----------------------------------------------------

            drawText(
                canvas,
                "${marriedDuration.years} Years  •  " +
                        "${marriedDuration.months} Months  •  " +
                        "${marriedDuration.days} Days",
                centerX,
                945f,
                29f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                990f,
                16f,
                Color.rgb(
                    150,
                    153,
                    163
                ),
                false
            )

            // -----------------------------------------------------
            // DIVIDER
            // -----------------------------------------------------

            drawHeartDivider(
                canvas,
                centerX,
                1035f
            )

            // -----------------------------------------------------
            // MARRIED TOTAL DAYS
            // -----------------------------------------------------

            drawText(
                canvas,
                marriedDuration.totalDays.toString(),
                centerX,
                1095f,
                39f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                1125f,
                12f,
                Color.rgb(
                    125,
                    128,
                    138
                ),
                false
            )

            // -----------------------------------------------------
            // MARRIED TIME
            // -----------------------------------------------------

            drawTimeColumns(
                canvas,
                centerX,
                1190f,
                marriedDuration.hours,
                marriedDuration.minutes,
                marriedDuration.seconds
            )

            // -----------------------------------------------------
            // BOTTOM MESSAGE
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1325f,
                19f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            // -----------------------------------------------------
            // SMALL GLOW DOTS
            // -----------------------------------------------------

            drawGlowDot(
                canvas,
                centerX - 300f,
                1325f
            )

            drawGlowDot(
                canvas,
                centerX + 300f,
                1325f
            )
        }

        // =========================================================
        // TEXT
        // =========================================================

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            bold: Boolean
        ) {

            paint.style =
                Paint.Style.FILL

            paint.color =
                color

            paint.textSize =
                size

            paint.textAlign =
                Paint.Align.CENTER

            paint.setFakeBoldText(
                bold
            )

            canvas.drawText(
                text,
                x,
                y,
                paint
            )
        }

        // =========================================================
        // HEART DIVIDER
        // =========================================================

        private fun drawHeartDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                Color.rgb(
                    120,
                    25,
                    55
                )

            canvas.drawLine(
                centerX - 430f,
                y,
                centerX - 35f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 35f,
                y,
                centerX + 430f,
                y,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                y,
                8f,
                Color.WHITE
            )
        }

        // =========================================================
        // TIME COLUMNS
        // =========================================================

        private fun drawTimeColumns(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            hours: Int,
            minutes: Int,
            seconds: Int
        ) {

            val positions =
                floatArrayOf(
                    centerX - 275f,
                    centerX,
                    centerX + 275f
                )

            val values =
                arrayOf(
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        hours
                    ),
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        minutes
                    ),
                    String.format(
                        Locale.getDefault(),
                        "%02d",
                        seconds
                    )
                )

            val labels =
                arrayOf(
                    "HOURS",
                    "MINUTES",
                    "SECONDS"
                )

            for (i in 0..2) {

                drawText(
                    canvas,
                    values[i],
                    positions[i],
                    y,
                    30f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 32f,
                    11f,
                    Color.rgb(
                        115,
                        118,
                        128
                    ),
                    false
                )
            }

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1f

            paint.color =
                Color.rgb(
                    55,
                    58,
                    67
                )

            canvas.drawLine(
                centerX - 140f,
                y - 12f,
                centerX - 140f,
                y + 30f,
                paint
            )

            canvas.drawLine(
                centerX + 140f,
                y - 12f,
                centerX + 140f,
                y + 30f,
                paint
            )
        }

        // =========================================================
        // GLOWING TOP HEART
        // =========================================================

        private fun drawGlowingHeart(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            val pulse =
                1f +
                        sin(
                            animationTime / 350.0
                        ).toFloat() *
                        0.06f

            val baseSize =
                35f * pulse

            paint.style =
                Paint.Style.FILL

            // Soft outer glow
            for (i in 6 downTo 1) {

                val alpha =
                    10 * i

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        20,
                        80
                    )

                canvas.drawCircle(
                    x,
                    y,
                    40f + i * 7f,
                    paint
                )
            }

            // Red glow rings
            for (i in 1..3) {

                paint.style =
                    Paint.Style.STROKE

                paint.strokeWidth =
                    5f

                paint.color =
                    Color.argb(
                        35,
                        255,
                        20,
                        80
                    )

                canvas.drawCircle(
                    x,
                    y,
                    45f + i * 7f,
                    paint
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                baseSize,
                Color.rgb(
                    255,
                    40,
                    90
                )
            )
        }

        // =========================================================
        // HEART SHAPE
        // =========================================================

        private fun drawHeartShape(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            size: Float,
            color: Int
        ) {

            path.reset()

            path.moveTo(
                cx,
                cy + size
            )

            path.cubicTo(
                cx - size * 1.75f,
                cy - size * 0.10f,
                cx - size * 1.25f,
                cy - size * 1.45f,
                cx - size * 0.45f,
                cy - size * 0.95f
            )

            path.cubicTo(
                cx,
                cy - size * 1.60f,
                cx + size * 0.45f,
                cy - size * 1.60f,
                cx + size * 0.45f,
                cy - size * 0.95f
            )

            path.cubicTo(
                cx + size * 1.25f,
                cy - size * 1.45f,
                cx + size * 1.75f,
                cy - size * 0.10f,
                cx,
                cy + size
            )

            paint.style =
                Paint.Style.FILL

            paint.color =
                color

            canvas.drawPath(
                path,
                paint
            )
        }

        // =========================================================
        // WEDDING RINGS
        // =========================================================

        private fun drawWeddingRings(
            canvas: Canvas,
            centerX: Float,
            centerY: Float
        ) {

            val pulse =
                (
                    sin(
                        animationTime / 500.0
                    ) + 1.0
                    ) * 0.5

            // Soft golden glow
            for (i in 1..4) {

                paint.style =
                    Paint.Style.STROKE

                paint.strokeWidth =
                    5f

                paint.color =
                    Color.argb(
                        18,
                        255,
                        190,
                        50
                    )

                canvas.drawCircle(
                    centerX - 28f,
                    centerY,
                    38f + i * 5f +
                            pulse.toFloat() * 4f,
                    paint
                )

                canvas.drawCircle(
                    centerX + 28f,
                    centerY,
                    38f + i * 5f +
                            pulse.toFloat() * 4f,
                    paint
                )
            }

            // Main rings
            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                7f

            paint.color =
                Color.rgb(
                    255,
                    194,
                    60
                )

            canvas.drawCircle(
                centerX - 28f,
                centerY,
                42f,
                paint
            )

            canvas.drawCircle(
                centerX + 28f,
                centerY,
                42f,
                paint
            )

            // Small heart above rings
            drawHeartShape(
                canvas,
                centerX,
                centerY - 62f,
                8f,
                Color.rgb(
                    255,
                    45,
                    85
                )
            )
        }

        // =========================================================
        // SMALL GLOW DOT
        // =========================================================

        private fun drawGlowDot(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.FILL

            paint.color =
                Color.argb(
                    35,
                    255,
                    40,
                    90
                )

            canvas.drawCircle(
                x,
                y,
                10f,
                paint
            )

            paint.color =
                Color.rgb(
                    255,
                    50,
                    90
                )

            canvas.drawCircle(
                x,
                y,
                2.5f,
                paint
            )
        }

        // =========================================================
        // DATE CREATION
        // =========================================================

        private fun createDate(
            year: Int,
            month: Int,
            day: Int
        ): Calendar {

            val calendar =
                Calendar.getInstance()

            calendar.set(
                year,
                month,
                day,
                0,
                0,
                0
            )

            calendar.set(
                Calendar.MILLISECOND,
                0
            )

            return calendar
        }

        // =========================================================
        // DURATION DATA
        // =========================================================

        private data class DurationResult(
            val years: Int,
            val months: Int,
            val days: Int,
            val hours: Int,
            val minutes: Int,
            val seconds: Int,
            val totalDays: Long
        )

        // =========================================================
        // DURATION CALCULATION
        // =========================================================

        private fun calculateDuration(
            start: Calendar,
            end: Calendar
        ): DurationResult {

            val cursor =
                Calendar.getInstance()

            cursor.timeInMillis =
                start.timeInMillis

            var years = 0
            var months = 0
            var days = 0

            // -----------------------------------------------------
            // YEARS
            // -----------------------------------------------------

            while (true) {

                val next =
                    Calendar.getInstance()

                next.timeInMillis =
                    cursor.timeInMillis

                next.add(
                    Calendar.YEAR,
                    1
                )

                if (
                    next.timeInMillis <=
                    end.timeInMillis
                ) {

                    cursor.timeInMillis =
                        next.timeInMillis

                    years++

                } else {
                    break
                }
            }

            // -----------------------------------------------------
            // MONTHS
            // -----------------------------------------------------

            while (true) {

                val next =
                    Calendar.getInstance()

                next.timeInMillis =
                    cursor.timeInMillis

                next.add(
                    Calendar.MONTH,
                    1
                )

                if (
                    next.timeInMillis <=
                    end.timeInMillis
                ) {

                    cursor.timeInMillis =
                        next.timeInMillis

                    months++

                } else {
                    break
                }
            }

            // -----------------------------------------------------
            // DAYS
            // -----------------------------------------------------

            while (true) {

                val next =
                    Calendar.getInstance()

                next.timeInMillis =
                    cursor.timeInMillis

                next.add(
                    Calendar.DAY_OF_MONTH,
                    1
                )

                if (
                    next.timeInMillis <=
                    end.timeInMillis
                ) {

                    cursor.timeInMillis =
                        next.timeInMillis

                    days++

                } else {
                    break
                }
            }

            // -----------------------------------------------------
            // REMAINING TIME
            // -----------------------------------------------------

            val remaining =
                end.timeInMillis -
                        cursor.timeInMillis

            val totalSeconds =
                remaining / 1000L

            val hours =
                (
                    totalSeconds / 3600L
                    ).toInt()

            val minutes =
                (
                    (totalSeconds % 3600L) /
                            60L
                    ).toInt()

            val seconds =
                (
                    totalSeconds % 60L
                    ).toInt()

            // -----------------------------------------------------
            // TOTAL DAYS
            // -----------------------------------------------------

            val totalDays =
                (
                    end.timeInMillis -
                            start.timeInMillis
                    ) / 86400000L

            return DurationResult(
                years = years,
                months = months,
                days = days,
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                totalDays = totalDays
            )
        }
    }
}
