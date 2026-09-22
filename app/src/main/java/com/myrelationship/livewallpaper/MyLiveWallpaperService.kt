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
        // MAIN DRAWING
        // ---------------------------------------------------------

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val screenWidth = canvas.width.toFloat()
                val screenHeight = canvas.height.toFloat()

                // Background
                canvas.drawColor(
                    Color.rgb(4, 7, 13)
                )

                // Our fixed design size
                val targetWidth = 1080f
                val targetHeight = 2400f

                // Full screen scaling
                val scaleX =
                    screenWidth / targetWidth

                val scaleY =
                    screenHeight / targetHeight

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

                // Main relationship information
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

            val t =
                animationTime / 1000.0

            // -----------------------------------------------------
            // VERY SOFT RED GLOW AREAS
            // -----------------------------------------------------

            drawSoftGlow(
                canvas,
                100f,
                650f,
                260f,
                Color.rgb(150, 0, 45)
            )

            drawSoftGlow(
                canvas,
                950f,
                1500f,
                300f,
                Color.rgb(150, 0, 45)
            )

            drawSoftGlow(
                canvas,
                150f,
                2150f,
                240f,
                Color.rgb(100, 0, 40)
            )

            // -----------------------------------------------------
            // LARGE HEARTS
            // -----------------------------------------------------

            drawBackgroundHeart(
                canvas,
                85f,
                380f,
                95f,
                35,
                0.0
            )

            drawBackgroundHeart(
                canvas,
                1000f,
                470f,
                70f,
                30,
                1.2
            )

            drawBackgroundHeart(
                canvas,
                75f,
                850f,
                75f,
                30,
                2.4
            )

            drawBackgroundHeart(
                canvas,
                980f,
                900f,
                100f,
                32,
                3.0
            )

            drawBackgroundHeart(
                canvas,
                90f,
                1250f,
                70f,
                28,
                1.8
            )

            drawBackgroundHeart(
                canvas,
                990f,
                1370f,
                90f,
                32,
                2.8
            )

            drawBackgroundHeart(
                canvas,
                110f,
                1660f,
                85f,
                28,
                0.8
            )

            drawBackgroundHeart(
                canvas,
                950f,
                1830f,
                115f,
                30,
                2.1
            )

            drawBackgroundHeart(
                canvas,
                150f,
                2110f,
                95f,
                25,
                3.2
            )

            drawBackgroundHeart(
                canvas,
                850f,
                2220f,
                65f,
                28,
                1.5
            )

            // -----------------------------------------------------
            // CURVED HEART TRAIL - LEFT
            // -----------------------------------------------------

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.0f
            paint.color =
                Color.argb(
                    125,
                    255,
                    35,
                    90
                )

            path.reset()

            path.moveTo(
                -80f,
                430f
            )

            for (i in 0..220) {

                val x =
                    -80f + i * 5.8f

                val wave =
                    sin(
                        i * 0.035 +
                                t * 0.35
                    ) * 55.0

                val y =
                    450f +
                            i * 4.1f +
                            wave

                path.lineTo(
                    x,
                    y.toFloat()
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // CURVED HEART TRAIL - RIGHT
            // -----------------------------------------------------

            paint.color =
                Color.argb(
                    105,
                    255,
                    35,
                    90
                )

            path.reset()

            path.moveTo(
                width + 80f,
                600f
            )

            for (i in 0..230) {

                val x =
                    width + 80f -
                            i * 5.6f

                val wave =
                    cos(
                        i * 0.04 +
                                t * 0.30
                    ) * 60.0

                val y =
                    620f +
                            i * 4.2f +
                            wave

                path.lineTo(
                    x,
                    y.toFloat()
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // BOTTOM CURVE
            // -----------------------------------------------------

            paint.color =
                Color.argb(
                    80,
                    255,
                    30,
                    80
                )

            path.reset()

            path.moveTo(
                -100f,
                1850f
            )

            for (i in 0..250) {

                val x =
                    -100f + i * 5.2f

                val wave =
                    sin(
                        i * 0.035 +
                                t * 0.25
                    ) * 90.0

                val y =
                    1900f +
                            i * 1.5f +
                            wave

                path.lineTo(
                    x,
                    y.toFloat()
                )
            }

            canvas.drawPath(
                path,
                paint
            )

            // -----------------------------------------------------
            // FLOATING PARTICLES
            // -----------------------------------------------------

            paint.style = Paint.Style.FILL

            for (i in 0 until 75) {

                val x =
                    ((i * 149) % width.toInt())
                        .toFloat()

                val speed =
                    4 + (i % 7)

                val movement =
                    (
                            t * speed +
                                    i * 97
                            ) % height.toDouble()

                val y =
                    (
                            height -
                                    movement
                            ).toFloat()

                val alpha =
                    30 + (i % 5) * 12

                val radius =
                    if (i % 9 == 0) {
                        2.8f
                    } else {
                        1.2f
                    }

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        40,
                        100
                    )

                canvas.drawCircle(
                    x,
                    y,
                    radius,
                    paint
                )
            }

            // -----------------------------------------------------
            // RANDOM SMALL HEARTS
            // -----------------------------------------------------

            for (i in 0 until 14) {

                val x =
                    ((i * 83 + 40) %
                            width.toInt())
                        .toFloat()

                val baseY =
                    ((i * 173 + 120) %
                            height.toInt())
                        .toFloat()

                val movement =
                    (
                            sin(
                                t * 0.25 +
                                        i
                            ) * 30.0
                            ).toFloat()

                drawTinyHeart(
                    canvas,
                    x,
                    baseY + movement,
                    9f + (i % 4) * 2f,
                    80
                )
            }
        }

        // =========================================================
        // SOFT GLOW
        // =========================================================

        private fun drawSoftGlow(
            canvas: Canvas,
            x: Float,
            y: Float,
            radius: Float,
            color: Int
        ) {

            paint.style = Paint.Style.FILL

            for (i in 6 downTo 1) {

                val r =
                    radius *
                            i /
                            6f

                val alpha =
                    5 + i * 3

                paint.color =
                    Color.argb(
                        alpha,
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color)
                    )

                canvas.drawCircle(
                    x,
                    y,
                    r,
                    paint
                )
            }
        }

        // =========================================================
        // BACKGROUND HEART
        // =========================================================

        private fun drawBackgroundHeart(
            canvas: Canvas,
            x: Float,
            y: Float,
            size: Float,
            alpha: Int,
            phase: Double
        ) {

            val t =
                animationTime / 1000.0

            val pulse =
                1.0 +
                        sin(
                            t * 0.45 +
                                    phase
                        ) * 0.04

            val finalSize =
                size *
                        pulse.toFloat()

            // Glow
            for (i in 4 downTo 1) {

                val glowAlpha =
                    alpha / (i + 1)

                drawHeartShape(
                    canvas,
                    x,
                    y,
                    finalSize +
                            i * 8f,
                    Color.argb(
                        glowAlpha,
                        255,
                        25,
                        75
                    )
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                finalSize,
                Color.argb(
                    alpha,
                    150,
                    15,
                    55
                )
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
                190f,
                19f,
                Color.rgb(
                    155,
                    158,
                    168
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
                ).format(
                    Date()
                )

            drawText(
                canvas,
                time,
                centerX,
                285f,
                64f,
                Color.WHITE,
                true
            )

            // -----------------------------------------------------
            // TOGETHER
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                375f,
                27f,
                Color.rgb(
                    255,
                    55,
                    95
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
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                490f,
                18f,
                Color.rgb(
                    155,
                    158,
                    168
                ),
                false
            )

            // -----------------------------------------------------
            // DIVIDER
            // -----------------------------------------------------

            drawDividerWithHeart(
                canvas,
                centerX,
                545f
            )

            // -----------------------------------------------------
            // TOTAL DAYS
            // -----------------------------------------------------

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                615f,
                48f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "T O T A L   D A Y S",
                centerX,
                650f,
                13f,
                Color.rgb(
                    145,
                    148,
                    158
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

            // -----------------------------------------------------
            // WEDDING RINGS
            // -----------------------------------------------------

            drawWeddingRings(
                canvas,
                centerX,
                900f
            )

            // -----------------------------------------------------
            // MARRIED
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                1015f,
                27f,
                Color.rgb(
                    255,
                    55,
                    95
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
                1085f,
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                1130f,
                18f,
                Color.rgb(
                    155,
                    158,
                    168
                ),
                false
            )

            // -----------------------------------------------------
            // DIVIDER
            // -----------------------------------------------------

            drawDividerWithHeart(
                canvas,
                centerX,
                1185f
            )

            // -----------------------------------------------------
            // MARRIED TOTAL DAYS
            // -----------------------------------------------------

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1255f,
                48f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "T O T A L   D A Y S",
                centerX,
                1290f,
                13f,
                Color.rgb(
                    145,
                    148,
                    158
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                1375f,
                married.hours,
                married.minutes,
                married.seconds
            )

            // -----------------------------------------------------
            // FINAL MESSAGE
            // -----------------------------------------------------

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1515f,
                23f,
                Color.rgb(
                    255,
                    55,
                    95
                ),
                true
            )

            drawGlowDot(
                canvas,
                centerX - 300f,
                1515f
            )

            drawGlowDot(
                canvas,
                centerX + 300f,
                1515f
            )

            // -----------------------------------------------------
            // EXTRA DECORATION IN LOWER SCREEN
            // -----------------------------------------------------

            drawTinyHeart(
                canvas,
                180f,
                1660f,
                18f,
                120
            )

            drawTinyHeart(
                canvas,
                900f,
                1700f,
                25f,
                100
            )

            drawTinyHeart(
                canvas,
                300f,
                1840f,
                14f,
                90
            )

            drawTinyHeart(
                canvas,
                780f,
                1930f,
                18f,
                100
            )

            drawTinyHeart(
                canvas,
                150f,
                2100f,
                23f,
                90
            )

            drawTinyHeart(
                canvas,
                920f,
                2180f,
                16f,
                90
            )

            // Decorative bottom glow
            drawSoftGlow(
                canvas,
                centerX,
                2250f,
                260f,
                Color.rgb(
                    80,
                    0,
                    35
                )
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
        // DIVIDER WITH HEART
        // =========================================================

        private fun drawDividerWithHeart(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                Color.argb(
                    150,
                    255,
                    45,
                    90
                )

            canvas.drawLine(
                centerX - 460f,
                y,
                centerX - 35f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 35f,
                y,
                centerX + 460f,
                y,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                y - 2f,
                11f,
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
                    centerX - 280f,
                    centerX,
                    centerX + 280f
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
                    36f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 40f,
                    12f,
                    Color.rgb(
                        125,
                        128,
                        138
                    ),
                    false
                )
            }

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                Color.argb(
                    100,
                    150,
                    150,
                    160
                )

            canvas.drawLine(
                centerX - 140f,
                y - 20f,
                centerX - 140f,
                y + 35f,
                paint
            )

            canvas.drawLine(
                centerX + 140f,
                y - 20f,
                centerX + 140f,
                y + 35f,
                paint
            )
        }

        // =========================================================
        // TOP GLOWING HEART
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
                        ).toFloat() * 0.08f

            val size =
                42f * pulse

            for (i in 7 downTo 1) {

                paint.color =
                    Color.argb(
                        14 * i,
                        255,
                        20,
                        75
                    )

                drawHeartShape(
                    canvas,
                    x,
                    y,
                    size + i * 7f,
                    paint.color
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                size,
                Color.rgb(
                    255,
                    40,
                    85
                )
            )

            // Inner highlight
            drawHeartShape(
                canvas,
                x - 3f,
                y - 3f,
                size * 0.72f,
                Color.rgb(
                    255,
                    65,
                    105
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
                cx - size * 1.25f,
                cy - size * 1.45f,
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
                cx + size * 1.25f,
                cy - size * 1.45f,
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
        // TINY HEART
        // =========================================================

        private fun drawTinyHeart(
            canvas: Canvas,
            x: Float,
            y: Float,
            size: Float,
            alpha: Int
        ) {

            drawHeartShape(
                canvas,
                x,
                y,
                size,
                Color.argb(
                    alpha,
                    255,
                    40,
                    90
                )
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
                34f +
                        pulse * 5f

            // Golden glow
            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                5f

            for (i in 4 downTo 1) {

                paint.color =
                    Color.argb(
                        18 * i,
                        255,
                        185,
                        45
                    )

                canvas.drawCircle(
                    centerX - 32f,
                    centerY,
                    glow + i * 5f,
                    paint
                )

                canvas.drawCircle(
                    centerX + 32f,
                    centerY,
                    glow + i * 5f,
                    paint
                )
            }

            // Rings
            paint.color =
                Color.rgb(
                    255,
                    194,
                    60
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

            // Small heart above
            drawHeartShape(
                canvas,
                centerX,
                centerY - 65f,
                10f,
                Color.rgb(
                    255,
                    50,
                    90
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
                    30,
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

        // =========================================================
        // DURATION CALCULATION
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
                        totalSeconds /
                                3600L
                        ).toInt()

            val minutes =
                (
                        (totalSeconds %
                                3600L) /
                                60L
                        ).toInt()

            val seconds =
                (
                        totalSeconds %
                                60L
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
