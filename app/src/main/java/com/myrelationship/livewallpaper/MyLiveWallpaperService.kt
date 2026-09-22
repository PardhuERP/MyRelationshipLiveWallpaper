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

    inner class RelationshipEngine : Engine() {

        @Volatile
        private var running = false

        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var animationTime = System.currentTimeMillis()

        // ---------------------------------------------------------
        // VISIBILITY
        // ---------------------------------------------------------

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

        // ---------------------------------------------------------
        // ANIMATION
        // ---------------------------------------------------------

        private fun startAnimation() {

            if (running) return

            running = true

            drawingThread = Thread {

                while (running) {

                    animationTime = System.currentTimeMillis()

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

        // ---------------------------------------------------------
        // MAIN DRAW
        // ---------------------------------------------------------

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val screenWidth = canvas.width.toFloat()
                val screenHeight = canvas.height.toFloat()

                /*
                 * Design reference:
                 *
                 * Width  = 1080
                 * Height = 2400
                 *
                 * We scale using width so tall phones can use
                 * the complete vertical area.
                 */

                val scale = screenWidth / 1080f

                canvas.save()

                canvas.scale(scale, scale)

                val width = 1080f
                val height = screenHeight / scale

                drawBackground(
                    canvas,
                    width,
                    height
                )

                drawAnimatedBackground(
                    canvas,
                    width,
                    height
                )

                drawMainContent(
                    canvas,
                    width,
                    height
                )

                canvas.restore()

            } catch (_: Exception) {

                // Prevent wallpaper thread from crashing

            } finally {

                if (canvas != null) {

                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        // ---------------------------------------------------------
        // BACKGROUND
        // ---------------------------------------------------------

        private fun drawBackground(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            canvas.drawColor(
                Color.rgb(
                    5,
                    7,
                    13
                )
            )

            /*
             * Large subtle center glow
             */

            paint.style = Paint.Style.FILL

            val centerX = width / 2f
            val centerY = height * 0.48f

            for (i in 8 downTo 1) {

                paint.color = Color.argb(
                    3 * i,
                    255,
                    20,
                    75
                )

                canvas.drawCircle(
                    centerX,
                    centerY,
                    180f * i,
                    paint
                )
            }
        }

        // ---------------------------------------------------------
        // ANIMATED BACKGROUND
        // ---------------------------------------------------------

        private fun drawAnimatedBackground(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t = animationTime / 1000.0

            drawHeartCurves(
                canvas,
                width,
                height,
                t
            )

            drawFloatingHearts(
                canvas,
                width,
                height,
                t
            )

            drawParticles(
                canvas,
                width,
                height,
                t
            )
        }

        // ---------------------------------------------------------
        // NEON HEART CURVES
        // ---------------------------------------------------------

        private fun drawHeartCurves(
            canvas: Canvas,
            width: Float,
            height: Float,
            t: Double
        ) {

            paint.style = Paint.Style.STROKE

            /*
             * LEFT CURVE
             */

            path.reset()

            val leftStart = height * 0.17f

            path.moveTo(
                -100f,
                leftStart
            )

            for (i in 0..220) {

                val x = -100f + i * 5.8f

                val wave =
                    sin(
                        i * 0.035 +
                                t * 0.35
                    ) * 45.0

                val y =
                    leftStart +
                            i * 4.0f +
                            wave.toFloat()

                path.lineTo(
                    x,
                    y
                )
            }

            paint.strokeWidth = 2.5f

            paint.color =
                Color.argb(
                    130,
                    255,
                    35,
                    90
                )

            canvas.drawPath(
                path,
                paint
            )

            /*
             * LEFT GLOW
             */

            paint.strokeWidth = 8f

            paint.color =
                Color.argb(
                    18,
                    255,
                    25,
                    80
                )

            canvas.drawPath(
                path,
                paint
            )

            /*
             * RIGHT CURVE
             */

            path.reset()

            val rightStart =
                height * 0.45f

            path.moveTo(
                width + 100f,
                rightStart
            )

            for (i in 0..230) {

                val x =
                    width +
                            100f -
                            i * 5.5f

                val wave =
                    cos(
                        i * 0.04 +
                                t * 0.30
                    ) * 50.0

                val y =
                    rightStart +
                            i * 4.0f +
                            wave.toFloat()

                path.lineTo(
                    x,
                    y
                )
            }

            paint.strokeWidth = 2.5f

            paint.color =
                Color.argb(
                    120,
                    255,
                    35,
                    90
                )

            canvas.drawPath(
                path,
                paint
            )
        }

        // ---------------------------------------------------------
        // FLOATING HEARTS
        // ---------------------------------------------------------

        private fun drawFloatingHearts(
            canvas: Canvas,
            width: Float,
            height: Float,
            t: Double
        ) {

            val heartPositions = arrayOf(
                floatArrayOf(90f, 500f, 0.8f),
                floatArrayOf(950f, 430f, 0.65f),
                floatArrayOf(80f, 1150f, 0.95f),
                floatArrayOf(970f, 1250f, 0.75f),
                floatArrayOf(160f, 1550f, 0.55f),
                floatArrayOf(900f, 1700f, 0.85f),
                floatArrayOf(70f, 1950f, 0.65f),
                floatArrayOf(1010f, 2050f, 0.55f)
            )

            for (i in heartPositions.indices) {

                val baseX =
                    heartPositions[i][0]

                val baseY =
                    heartPositions[i][1]

                val size =
                    heartPositions[i][2] * 35f

                val floatMovement =
                    sin(
                        t * 0.6 +
                                i
                    ) * 12.0

                val x =
                    baseX +
                            cos(
                                t * 0.3 + i
                            ).toFloat() * 8f

                val y =
                    baseY +
                            floatMovement.toFloat()

                val alpha =
                    (45 +
                            sin(
                                t * 1.2 + i
                            ) * 20)
                        .toInt()
                        .coerceIn(
                            20,
                            80
                        )

                drawGlowingHeart(
                    canvas,
                    x,
                    y,
                    size,
                    alpha
                )
            }
        }

        // ---------------------------------------------------------
        // PARTICLES
        // ---------------------------------------------------------

        private fun drawParticles(
            canvas: Canvas,
            width: Float,
            height: Float,
            t: Double
        ) {

            paint.style = Paint.Style.FILL

            for (i in 0 until 55) {

                val x =
                    ((i * 197) %
                            width.toInt())
                        .toFloat()

                val speed =
                    5f +
                            (i % 6) * 1.8f

                val movement =
                    (
                            t * speed +
                                    i * 137
                            ) % height

                val y =
                    height -
                            movement.toFloat()

                val pulse =
                    (
                            sin(
                                t * 2.0 +
                                        i
                            ) + 1.0
                            ) / 2.0

                val alpha =
                    (
                            25 +
                                    pulse * 50
                            ).toInt()

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        50,
                        100
                    )

                val radius =
                    1.0f +
                            (i % 3) * 0.7f

                canvas.drawCircle(
                    x,
                    y,
                    radius,
                    paint
                )
            }
        }

        // ---------------------------------------------------------
        // MAIN CONTENT
        // ---------------------------------------------------------

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
                105f,
                38f,
                255
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                180f,
                18f,
                Color.rgb(
                    175,
                    175,
                    185
                ),
                false
            )

            /*
             * CLOCK
             */

            val currentTime =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(
                    Date()
                )

            drawGlowText(
                canvas,
                currentTime,
                centerX,
                285f,
                76f,
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
                395f,
                28f,
                Color.rgb(
                    255,
                    65,
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
                "${together.years} Years • ${together.months} Months • ${together.days} Days",
                centerX,
                455f,
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                505f,
                19f,
                Color.rgb(
                    175,
                    175,
                    185
                ),
                false
            )

            drawHeartDivider(
                canvas,
                centerX,
                560f
            )

            /*
             * TOTAL TOGETHER DAYS
             */

            drawGlowText(
                canvas,
                together.totalDays.toString(),
                centerX,
                640f,
                55f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "T O T A L   D A Y S",
                centerX,
                680f,
                15f,
                Color.rgb(
                    170,
                    170,
                    180
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                770f,
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
                930f
            )

            /*
             * MARRIED
             */

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                1045f,
                28f,
                Color.rgb(
                    255,
                    65,
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
                "${married.years} Year • ${married.months} Months • ${married.days} Days",
                centerX,
                1105f,
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                1155f,
                19f,
                Color.rgb(
                    175,
                    175,
                    185
                ),
                false
            )

            drawHeartDivider(
                canvas,
                centerX,
                1210f
            )

            /*
             * MARRIED TOTAL DAYS
             */

            drawGlowText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1290f,
                55f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "T O T A L   D A Y S",
                centerX,
                1330f,
                15f,
                Color.rgb(
                    170,
                    170,
                    180
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                1420f,
                married.hours,
                married.minutes,
                married.seconds
            )

            /*
             * BOTTOM MESSAGE
             */

            drawText(
                canvas,
                "♥  Same People • Same Dreams  ♥",
                centerX,
                1570f,
                25f,
                Color.rgb(
                    255,
                    70,
                    110
                ),
                true
            )

            /*
             * Additional decorative hearts
             */

            drawGlowingHeart(
                canvas,
                centerX - 360f,
                1565f,
                18f,
                150
            )

            drawGlowingHeart(
                canvas,
                centerX + 360f,
                1565f,
                18f,
                150
            )
        }

        // ---------------------------------------------------------
        // HEART
        // ---------------------------------------------------------

        private fun drawGlowingHeart(
            canvas: Canvas,
            x: Float,
            y: Float,
            size: Float,
            alpha: Int
        ) {

            paint.style =
                Paint.Style.FILL

            /*
             * Glow layers
             */

            for (i in 5 downTo 1) {

                paint.color =
                    Color.argb(
                        (alpha * 0.08f * i)
                            .toInt()
                            .coerceIn(1, 255),
                        255,
                        20,
                        75
                    )

                drawHeartShape(
                    canvas,
                    x,
                    y,
                    size +
                            i * 7f,
                    paint.color
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                size,
                Color.argb(
                    alpha,
                    255,
                    45,
                    90
                )
            )
        }

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
                cy - size * 0.05f,
                cx - size * 1.3f,
                cy - size * 1.25f,
                cx - size * 0.45f,
                cy - size * 0.9f
            )

            path.cubicTo(
                cx - size * 0.05f,
                cy - size * 1.45f,
                cx + size * 0.55f,
                cy - size * 1.45f,
                cx + size * 0.45f,
                cy - size * 0.9f
            )

            path.cubicTo(
                cx + size * 1.3f,
                cy - size * 1.25f,
                cx + size * 1.7f,
                cy - size * 0.05f,
                cx,
                cy + size
            )

            paint.style =
                Paint.Style.FILL

            paint.color = color

            canvas.drawPath(
                path,
                paint
            )
        }

        // ---------------------------------------------------------
        // GLOW TEXT
        // ---------------------------------------------------------

        private fun drawGlowText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            bold: Boolean
        ) {

            /*
             * Outer glow
             */

            for (i in 4 downTo 1) {

                drawText(
                    canvas,
                    text,
                    x,
                    y,
                    size,
                    Color.argb(
                        18 * i,
                        255,
                        35,
                        85
                    ),
                    bold
                )
            }

            drawText(
                canvas,
                text,
                x,
                y,
                size,
                color,
                bold
            )
        }

        // ---------------------------------------------------------
        // TEXT
        // ---------------------------------------------------------

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

            paint.color = color

            paint.textSize = size

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

        // ---------------------------------------------------------
        // HEART DIVIDER
        // ---------------------------------------------------------

        private fun drawHeartDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 1.5f

            paint.color =
                Color.argb(
                    150,
                    255,
                    65,
                    105
                )

            canvas.drawLine(
                centerX - 410f,
                y,
                centerX - 35f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 35f,
                y,
                centerX + 410f,
                y,
                paint
            )

            drawGlowingHeart(
                canvas,
                centerX,
                y - 2f,
                16f,
                255
            )
        }

        // ---------------------------------------------------------
        // TIME COLUMNS
        // ---------------------------------------------------------

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
                    centerX - 300f,
                    centerX,
                    centerX + 300f
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

                drawGlowText(
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
                    y + 40f,
                    14f,
                    Color.rgb(
                        180,
                        180,
                        190
                    ),
                    false
                )
            }

            /*
             * Separators
             */

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 1f

            paint.color =
                Color.argb(
                    120,
                    255,
                    100,
                    130
                )

            canvas.drawLine(
                centerX - 150f,
                y - 20f,
                centerX - 150f,
                y + 35f,
                paint
            )

            canvas.drawLine(
                centerX + 150f,
                y - 20f,
                centerX + 150f,
                y + 35f,
                paint
            )
        }

        // ---------------------------------------------------------
        // WEDDING RINGS
        // ---------------------------------------------------------

        private fun drawWeddingRings(
            canvas: Canvas,
            centerX: Float,
            centerY: Float
        ) {

            val pulse =
                sin(
                    animationTime / 450.0
                ).toFloat()

            /*
             * Glow
             */

            paint.style =
                Paint.Style.STROKE

            for (i in 5 downTo 1) {

                paint.strokeWidth =
                    3f + i

                paint.color =
                    Color.argb(
                        15 * i,
                        255,
                        180,
                        45
                    )

                canvas.drawCircle(
                    centerX - 30f,
                    centerY,
                    42f +
                            pulse * 3f +
                            i * 5f,
                    paint
                )

                canvas.drawCircle(
                    centerX + 30f,
                    centerY,
                    42f +
                            pulse * 3f +
                            i * 5f,
                    paint
                )
            }

            /*
             * Rings
             */

            paint.strokeWidth = 7f

            paint.color =
                Color.rgb(
                    255,
                    195,
                    65
                )

            canvas.drawCircle(
                centerX - 30f,
                centerY,
                42f,
                paint
            )

            canvas.drawCircle(
                centerX + 30f,
                centerY,
                42f,
                paint
            )

            /*
             * Small heart above rings
             */

            drawGlowingHeart(
                canvas,
                centerX,
                centerY - 65f,
                13f,
                255
            )
        }

        // ---------------------------------------------------------
        // DURATION CALCULATION
        // ---------------------------------------------------------

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

            val cursor =
                Calendar.getInstance()

            cursor.timeInMillis =
                start.timeInMillis

            var years = 0
            var months = 0
            var days = 0

            /*
             * YEARS
             */

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

            /*
             * MONTHS
             */

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

            /*
             * DAYS
             */

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

            /*
             * REMAINING TIME
             */

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

            /*
             * TOTAL DAYS
             */

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
