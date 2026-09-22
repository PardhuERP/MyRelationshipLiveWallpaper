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

    override fun onCreateEngine(): Engine {
        return RelationshipEngine()
    }

    private data class DurationResult(
        val years: Int,
        val months: Int,
        val days: Int,
        val hours: Int,
        val minutes: Int,
        val seconds: Int,
        val totalDays: Long
    )

    private fun calculateDuration(
        start: Calendar,
        end: Calendar
    ): DurationResult {

        val cursor = Calendar.getInstance()
        cursor.timeInMillis = start.timeInMillis

        var years = 0
        var months = 0
        var days = 0

        // YEARS
        while (true) {
            val next = Calendar.getInstance()
            next.timeInMillis = cursor.timeInMillis
            next.add(Calendar.YEAR, 1)

            if (next.timeInMillis <= end.timeInMillis) {
                cursor.timeInMillis = next.timeInMillis
                years++
            } else {
                break
            }
        }

        // MONTHS
        while (true) {
            val next = Calendar.getInstance()
            next.timeInMillis = cursor.timeInMillis
            next.add(Calendar.MONTH, 1)

            if (next.timeInMillis <= end.timeInMillis) {
                cursor.timeInMillis = next.timeInMillis
                months++
            } else {
                break
            }
        }

        // DAYS
        while (true) {
            val next = Calendar.getInstance()
            next.timeInMillis = cursor.timeInMillis
            next.add(Calendar.DAY_OF_MONTH, 1)

            if (next.timeInMillis <= end.timeInMillis) {
                cursor.timeInMillis = next.timeInMillis
                days++
            } else {
                break
            }
        }

        val remaining =
            end.timeInMillis - cursor.timeInMillis

        val totalSeconds =
            remaining / 1000L

        val hours =
            (totalSeconds / 3600L).toInt()

        val minutes =
            ((totalSeconds % 3600L) / 60L).toInt()

        val seconds =
            (totalSeconds % 60L).toInt()

        val totalDays =
            (end.timeInMillis - start.timeInMillis) / 86400000L

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

    inner class RelationshipEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val path =
            Path()

        private var animationTime = 0L

        override fun onVisibilityChanged(
            isVisible: Boolean
        ) {
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
            super.onSurfaceChanged(
                holder,
                format,
                width,
                height
            )

            if (!running) {
                startAnimation()
            }
        }

        override fun onSurfaceDestroyed(
            holder: SurfaceHolder
        ) {
            stopAnimation()
            super.onSurfaceDestroyed(holder)
        }

        private fun startAnimation() {

            if (running) return

            running = true

            drawingThread = Thread {

                while (running) {

                    animationTime =
                        System.currentTimeMillis()

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

        private fun drawWallpaper() {
    val holder = surfaceHolder
    var canvas: Canvas? = null

    try {
        canvas = holder.lockCanvas()
        if (canvas == null) return

        // Direct screen dimensions
        val screenWidth = canvas.width.toFloat()
        val screenHeight = canvas.height.toFloat()

        canvas.drawColor(Color.rgb(4, 7, 13))

        // Target Design Aspect Ratio
        val targetWidth = 1080f
        val targetHeight = 2400f

        // Calculate scaling factors for full screen coverage
        val scaleX = screenWidth / targetWidth
        val scaleY = screenHeight / targetHeight

        canvas.save()
        // Uniform scaling target base content size
        canvas.scale(scaleX, scaleY)

        drawBackgroundEffects(canvas, targetWidth, targetHeight)
        drawMainContent(canvas, targetWidth, targetHeight)

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

            val t =
                animationTime / 1000.0

            /*
             * Large soft glow
             */

            paint.style =
                Paint.Style.FILL

            paint.color =
                Color.argb(
                    18,
                    255,
                    20,
                    70
                )

            canvas.drawCircle(
                width * 0.15f,
                height * 0.25f,
                260f,
                paint
            )

            paint.color =
                Color.argb(
                    15,
                    255,
                    30,
                    80
                )

            canvas.drawCircle(
                width * 0.88f,
                height * 0.72f,
                320f,
                paint
            )

            /*
             * Floating hearts
             */

            val hearts =
                arrayOf(
                    floatArrayOf(90f, 300f, 30f),
                    floatArrayOf(960f, 420f, 24f),
                    floatArrayOf(120f, 820f, 48f),
                    floatArrayOf(950f, 930f, 36f),
                    floatArrayOf(80f, 1280f, 30f),
                    floatArrayOf(990f, 1430f, 50f),
                    floatArrayOf(150f, 1700f, 40f),
                    floatArrayOf(900f, 1900f, 32f),
                    floatArrayOf(100f, 2150f, 24f),
                    floatArrayOf(960f, 2250f, 42f)
                )

            for (i in hearts.indices) {

                val heart =
                    hearts[i]

                val move =
                    sin(
                        t * 0.45 +
                                i * 0.8
                    ) * 12.0

                val alpha =
                    20 + (i % 3) * 8

                drawHeartShape(
                    canvas,
                    heart[0],
                    heart[1] + move.toFloat(),
                    heart[2],
                    Color.argb(
                        alpha,
                        255,
                        35,
                        85
                    )
                )
            }

            /*
             * Glowing curved lines
             */

            drawHeartCurve(
                canvas,
                width,
                height,
                true,
                t
            )

            drawHeartCurve(
                canvas,
                width,
                height,
                false,
                t
            )

            /*
             * Tiny particles
             */

            paint.style =
                Paint.Style.FILL

            for (i in 0 until 70) {

                val x =
                    ((i * 157) %
                            width.toInt())
                        .toFloat()

                val movement =
                    (
                        t *
                                (5 + i % 6)
                        ) %
                        height.toDouble()

                val y =
                    (
                        height -
                                movement +
                                i * 47
                        ) % height

                val alpha =
                    25 + (i % 4) * 12

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        55,
                        100
                    )

                canvas.drawCircle(
                    x,
                    y.toFloat(),
                    if (i % 7 == 0) 2.5f else 1.2f,
                    paint
                )
            }
        }

        private fun drawHeartCurve(
            canvas: Canvas,
            width: Float,
            height: Float,
            left: Boolean,
            time: Double
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                2.2f

            paint.color =
                Color.argb(
                    75,
                    255,
                    35,
                    90
                )

            path.reset()

            if (left) {

                path.moveTo(
                    -100f,
                    height * 0.35f
                )

                for (i in 0..260) {

                    val x =
                        -100f + i * 5.2f

                    val wave =
                        sin(
                            i * 0.035 +
                                    time * 0.35
                        ) * 45.0

                    val y =
                        height * 0.35f +
                                i * 3.8f +
                                wave

                    path.lineTo(
                        x,
                        y.toFloat()
                    )
                }

            } else {

                path.moveTo(
                    width + 100f,
                    height * 0.48f
                )

                for (i in 0..260) {

                    val x =
                        width +
                                100f -
                                i * 5.2f

                    val wave =
                        cos(
                            i * 0.035 +
                                    time * 0.30
                        ) * 45.0

                    val y =
                        height * 0.48f +
                                i * 3.8f +
                                wave

                    path.lineTo(
                        x,
                        y.toFloat()
                    )
                }
            }

            canvas.drawPath(
                path,
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

            /*
             * TOP HEART
             */

            drawGlowingHeart(
                canvas,
                centerX,
                115f
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                185f,
                24f,
                Color.rgb(
                    165,
                    165,
                    175
                ),
                false
            )

            /*
             * CLOCK
             */

            val time =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            drawText(
                canvas,
                time,
                centerX,
                275f,
                64f,
                Color.WHITE,
                true
            )

            /*
             * TOGETHER
             */

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                375f,
                29f,
                Color.rgb(
                    255,
                    70,
                    105
                ),
                true
            )

            val now =
                Calendar.getInstance()

            val togetherStart =
                Calendar.getInstance()

            togetherStart.set(
                2006,
                Calendar.FEBRUARY,
                14,
                0,
                0,
                0
            )

            togetherStart.set(
                Calendar.MILLISECOND,
                0
            )

            val together =
                calculateDuration(
                    togetherStart,
                    now
                )

            drawText(
                canvas,
                "${together.years} Years  •  " +
                        "${together.months} Months  •  " +
                        "${together.days} Days",
                centerX,
                445f,
                38f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                495f,
                21f,
                Color.rgb(
                    160,
                    160,
                    170
                ),
                false
            )

            drawDivider(
                canvas,
                centerX,
                545f
            )

            /*
             * TOTAL TOGETHER DAYS
             */

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                625f,
                48f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                660f,
                15f,
                Color.rgb(
                    140,
                    140,
                    150
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                735f,
                together.hours,
                together.minutes,
                together.seconds
            )

            /*
             * WEDDING RINGS
             */

            drawWeddingRings(
                canvas,
                centerX,
                870f
            )

            /*
             * MARRIED
             */

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                990f,
                29f,
                Color.rgb(
                    255,
                    70,
                    105
                ),
                true
            )

            val marriedStart =
                Calendar.getInstance()

            marriedStart.set(
                2025,
                Calendar.APRIL,
                9,
                0,
                0,
                0
            )

            marriedStart.set(
                Calendar.MILLISECOND,
                0
            )

            val married =
                calculateDuration(
                    marriedStart,
                    now
                )

            drawText(
                canvas,
                "${married.years} Years  •  " +
                        "${married.months} Months  •  " +
                        "${married.days} Days",
                centerX,
                1060f,
                38f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                1110f,
                21f,
                Color.rgb(
                    160,
                    160,
                    170
                ),
                false
            )

            drawDivider(
                canvas,
                centerX,
                1160f
            )

            /*
             * MARRIED TOTAL DAYS
             */

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1240f,
                48f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                1275f,
                15f,
                Color.rgb(
                    140,
                    140,
                    150
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                1350f,
                married.hours,
                married.minutes,
                married.seconds
            )

            /*
             * BOTTOM MESSAGE
             */

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1490f,
                25f,
                Color.rgb(
                    255,
                    70,
                    105
                ),
                true
            )

            drawGlowDot(
                canvas,
                centerX - 330f,
                1490f
            )

            drawGlowDot(
                canvas,
                centerX + 330f,
                1490f
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
        // DIVIDER
        // =========================================================

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                2f

            paint.color =
                Color.argb(
                    100,
                    255,
                    55,
                    95
                )

            canvas.drawLine(
                centerX - 430f,
                y,
                centerX - 40f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 40f,
                y,
                centerX + 430f,
                y,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                y - 2f,
                10f,
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
                    centerX - 270f,
                    centerX,
                    centerX + 270f
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
                    38f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 38f,
                    14f,
                    Color.rgb(
                        140,
                        140,
                        150
                    ),
                    false
                )
            }

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                2f

            paint.color =
                Color.rgb(
                    65,
                    65,
                    75
                )

            canvas.drawLine(
                centerX - 135f,
                y - 15f,
                centerX - 135f,
                y + 32f,
                paint
            )

            canvas.drawLine(
                centerX + 135f,
                y - 15f,
                centerX + 135f,
                y + 32f,
                paint
            )
        }

        // =========================================================
        // GLOWING HEART
        // =========================================================

        private fun drawGlowingHeart(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            val pulse =
                1f +
                        sin(
                            animationTime / 180.0
                        ).toFloat() *
                        0.08f

            paint.style =
                Paint.Style.FILL

            for (i in 8 downTo 1) {

                paint.color =
                    Color.argb(
                        12 * i,
                        255,
                        20,
                        70
                    )

                canvas.drawCircle(
                    x,
                    y,
                    48f * pulse +
                            i * 12f,
                    paint
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                43f * pulse,
                Color.rgb(
                    255,
                    55,
                    95
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
                cx - size * 1.7f,
                cy - size * 0.1f,
                cx - size * 1.2f,
                cy - size * 1.4f,
                cx - size * 0.45f,
                cy - size * 0.95f
            )

            path.cubicTo(
                cx,
                cy - size * 1.55f,
                cx + size * 0.45f,
                cy - size * 1.55f,
                cx + size * 0.45f,
                cy - size * 0.95f
            )

            path.cubicTo(
                cx + size * 1.2f,
                cy - size * 1.4f,
                cx + size * 1.7f,
                cy - size * 0.1f,
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
                sin(
                    animationTime / 450.0
                ).toFloat()

            val glow =
                32f + pulse * 5f

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                5f

            for (i in 1..5) {

                paint.color =
                    Color.argb(
                        18,
                        255,
                        190,
                        60
                    )

                canvas.drawCircle(
                    centerX - 30f,
                    centerY,
                    glow + i * 5f,
                    paint
                )

                canvas.drawCircle(
                    centerX + 30f,
                    centerY,
                    glow + i * 5f,
                    paint
                )
            }

            paint.color =
                Color.rgb(
                    255,
                    195,
                    65
                )

            paint.strokeWidth =
                7f

            canvas.drawCircle(
                centerX - 30f,
                centerY,
                40f,
                paint
            )

            canvas.drawCircle(
                centerX + 30f,
                centerY,
                40f,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                centerY - 68f,
                11f,
                Color.rgb(
                    255,
                    70,
                    100
                )
            )
        }

        // =========================================================
        // GLOW DOT
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
                12f,
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
                3f,
                paint
            )
        }
    }
}
