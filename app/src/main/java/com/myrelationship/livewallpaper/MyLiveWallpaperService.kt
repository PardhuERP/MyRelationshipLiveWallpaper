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

private data class DurationResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalDays: Long
)

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): WallpaperService.Engine {
        return RelationshipEngine()
    }

    inner class RelationshipEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var animationTime = 0L

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

        /*
         * FULL SCREEN WALLPAPER
         */
        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val screenWidth = canvas.width.toFloat()
                val screenHeight = canvas.height.toFloat()

                canvas.drawColor(
                    Color.rgb(4, 7, 13)
                )

                /*
                 * Base design size
                 */
                val designWidth = 1080f
                val designHeight = 2400f

                /*
                 * Full screen scaling
                 */
                val scaleX =
                    screenWidth / designWidth

                val scaleY =
                    screenHeight / designHeight

                canvas.save()

                /*
                 * Stretch exactly to the screen.
                 * This prevents the large empty lower area.
                 */
                canvas.scale(
                    scaleX,
                    scaleY
                )

                /*
                 * Background
                 */
                drawBackgroundEffects(
                    canvas,
                    designWidth,
                    designHeight
                )

                /*
                 * Main relationship content
                 */
                drawMainContent(
                    canvas,
                    designWidth,
                    designHeight
                )

                canvas.restore()

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        /*
         * ============================================================
         * BACKGROUND
         * ============================================================
         */
        private fun drawBackgroundEffects(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t =
                animationTime / 1000.0

            /*
             * -----------------------------------------
             * Large soft hearts
             * -----------------------------------------
             */

            val hearts = arrayOf(
                floatArrayOf(70f, 420f, 1.9f, 0.0f),
                floatArrayOf(950f, 470f, 1.35f, 1.5f),
                floatArrayOf(80f, 850f, 0.9f, 2.0f),
                floatArrayOf(990f, 900f, 1.1f, 0.7f),
                floatArrayOf(150f, 1250f, 1.0f, 1.8f),
                floatArrayOf(930f, 1320f, 1.6f, 0.4f),
                floatArrayOf(70f, 1800f, 1.3f, 2.3f),
                floatArrayOf(930f, 1840f, 1.8f, 1.2f),
                floatArrayOf(170f, 2200f, 1.5f, 0.5f),
                floatArrayOf(900f, 2240f, 1.2f, 1.7f)
            )

            for (heart in hearts) {

                val baseX = heart[0]
                val baseY = heart[1]
                val size = heart[2] * 65f
                val phase = heart[3]

                val movement =
                    sin(t * 0.25 + phase) * 8.0

                val alphaPulse =
                    (
                        25 +
                            sin(t * 0.6 + phase) * 8
                        ).toInt()

                drawSoftHeart(
                    canvas,
                    baseX.toFloat(),
                    (baseY + movement).toFloat(),
                    size,
                    alphaPulse.coerceIn(15, 40)
                )
            }

            /*
             * -----------------------------------------
             * Curved neon lines
             * -----------------------------------------
             */

            drawCurveLine(
                canvas,
                width,
                height,
                false,
                t
            )

            drawCurveLine(
                canvas,
                width,
                height,
                true,
                t + 2.5
            )

            /*
             * -----------------------------------------
             * Floating glowing particles
             * -----------------------------------------
             */

            drawParticles(
                canvas,
                width,
                height,
                t
            )
        }

        /*
         * Soft background heart
         */
        private fun drawSoftHeart(
            canvas: Canvas,
            cx: Float,
            cy: Float,
            size: Float,
            alpha: Int
        ) {

            path.reset()

            path.moveTo(
                cx,
                cy + size
            )

            path.cubicTo(
                cx - size * 1.7f,
                cy - size * 0.05f,
                cx - size * 1.25f,
                cy - size * 1.25f,
                cx - size * 0.45f,
                cy - size * 0.85f
            )

            path.cubicTo(
                cx,
                cy - size * 1.45f,
                cx + size * 0.45f,
                cy - size * 1.45f,
                cx + size * 0.45f,
                cy - size * 0.85f
            )

            path.cubicTo(
                cx + size * 1.25f,
                cy - size * 1.25f,
                cx + size * 1.7f,
                cy - size * 0.05f,
                cx,
                cy + size
            )

            paint.style =
                Paint.Style.FILL

            paint.color =
                Color.argb(
                    alpha,
                    255,
                    25,
                    80
                )

            canvas.drawPath(
                path,
                paint
            )
        }

        /*
         * Neon curved line
         */
        private fun drawCurveLine(
            canvas: Canvas,
            width: Float,
            height: Float,
            reverse: Boolean,
            time: Double
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                Color.argb(
                    85,
                    255,
                    25,
                    85
                )

            path.reset()

            val startY =
                if (!reverse) {
                    height * 0.45f
                } else {
                    height * 0.70f
                }

            path.moveTo(
                -100f,
                startY
            )

            for (i in 0..220) {

                val x =
                    -100f + i * 6f

                val wave =
                    sin(
                        i * 0.035 +
                            time * 0.35
                    ) * 55.0

                val curve =
                    if (!reverse) {
                        i * 1.9f
                    } else {
                        -i * 1.3f
                    }

                val y =
                    startY +
                        curve +
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
        }

        /*
         * Floating particles
         */
        private fun drawParticles(
            canvas: Canvas,
            width: Float,
            height: Float,
            time: Double
        ) {

            paint.style =
                Paint.Style.FILL

            for (i in 0 until 45) {

                val baseX =
                    ((i * 197) % width.toInt()).toFloat()

                val speed =
                    4 + (i % 6)

                val movement =
                    (
                        time * speed +
                            i * 97
                        ) % height

                val y =
                    height - movement.toFloat()

                val pulse =
                    (
                        sin(
                            time * 1.5 +
                                i
                        ) + 1.0
                        ) / 2.0

                val radius =
                    1.0f +
                        pulse.toFloat() * 1.8f

                paint.color =
                    Color.argb(
                        (25 + pulse * 55).toInt(),
                        255,
                        40,
                        95
                    )

                canvas.drawCircle(
                    baseX,
                    y,
                    radius,
                    paint
                )
            }
        }

        /*
         * ============================================================
         * MAIN CONTENT
         * ============================================================
         */
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
                105f
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                150f,
                16f,
                Color.rgb(
                    145,
                    148,
                    158
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
                205f,
                44f,
                Color.WHITE,
                true
            )

            /*
             * ========================================================
             * TOGETHER
             * ========================================================
             */

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                275f,
                20f,
                Color.rgb(
                    255,
                    55,
                    90
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
                330f,
                27f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                370f,
                14f,
                Color.rgb(
                    145,
                    148,
                    158
                ),
                false
            )

            drawDividerWithHeart(
                canvas,
                centerX,
                410f
            )

            /*
             * TOTAL DAYS
             */
            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                465f,
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                490f,
                11f,
                Color.rgb(
                    125,
                    128,
                    138
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                545f,
                together.hours,
                together.minutes,
                together.seconds
            )

            /*
             * ========================================================
             * RINGS
             * ========================================================
             */

            drawWeddingRings(
                canvas,
                centerX,
                675f
            )

            /*
             * ========================================================
             * MARRIED
             * ========================================================
             */

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                760f,
                20f,
                Color.rgb(
                    255,
                    55,
                    90
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
                815f,
                27f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                855f,
                14f,
                Color.rgb(
                    145,
                    148,
                    158
                ),
                false
            )

            drawDividerWithHeart(
                canvas,
                centerX,
                895f
            )

            /*
             * TOTAL MARRIED DAYS
             */

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                950f,
                34f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                975f,
                11f,
                Color.rgb(
                    125,
                    128,
                    138
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                1030f,
                married.hours,
                married.minutes,
                married.seconds
            )

            /*
             * ========================================================
             * BOTTOM MESSAGE
             * ========================================================
             */

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1130f,
                18f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            drawGlowDot(
                canvas,
                centerX - 210f,
                1130f
            )

            drawGlowDot(
                canvas,
                centerX + 210f,
                1130f
            )
        }

        /*
         * ============================================================
         * TEXT
         * ============================================================
         */
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

        /*
         * ============================================================
         * DIVIDER
         * ============================================================
         */
        private fun drawDividerWithHeart(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1f

            paint.color =
                Color.rgb(
                    105,
                    25,
                    48
                )

            canvas.drawLine(
                centerX - 390f,
                y,
                centerX - 35f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 35f,
                y,
                centerX + 390f,
                y,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                y,
                7f,
                Color.WHITE
            )
        }

        /*
         * ============================================================
         * TIME COLUMNS
         * ============================================================
         */
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
                    27f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 30f,
                    10f,
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
                centerX - 135f,
                y - 12f,
                centerX - 135f,
                y + 28f,
                paint
            )

            canvas.drawLine(
                centerX + 135f,
                y - 12f,
                centerX + 135f,
                y + 28f,
                paint
            )
        }

        /*
         * ============================================================
         * GLOWING TOP HEART
         * ============================================================
         */
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
                    0.10f

            val glowRadius =
                42f * pulse

            paint.style =
                Paint.Style.FILL

            for (i in 7 downTo 1) {

                val alpha =
                    10 * i

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        20,
                        75
                    )

                canvas.drawCircle(
                    x,
                    y,
                    glowRadius + i * 9f,
                    paint
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                30f * pulse,
                Color.rgb(
                    255,
                    40,
                    85
                )
            )
        }

        /*
         * ============================================================
         * HEART SHAPE
         * ============================================================
         */
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

        /*
         * ============================================================
         * WEDDING RINGS
         * ============================================================
         */
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
                30f +
                    pulse * 5f

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
                        50
                    )

                canvas.drawCircle(
                    centerX - 22f,
                    centerY,
                    glow + i * 5f,
                    paint
                )
            }

            paint.color =
                Color.rgb(
                    255,
                    194,
                    60
                )

            canvas.drawCircle(
                centerX - 25f,
                centerY,
                31f,
                paint
            )

            canvas.drawCircle(
                centerX + 25f,
                centerY,
                31f,
                paint
            )

            /*
             * Small heart above rings
             */
            drawHeartShape(
                canvas,
                centerX,
                centerY - 47f,
                7f,
                Color.rgb(
                    255,
                    45,
                    85
                )
            )
        }

        /*
         * ============================================================
         * GLOW DOT
         * ============================================================
         */
        private fun drawGlowDot(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.FILL

            paint.color =
                Color.argb(
                    40,
                    255,
                    40,
                    90
                )

            canvas.drawCircle(
                x,
                y,
                9f,
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
                2f,
                paint
            )
        }
    }
}

/*
 * ================================================================
 * DURATION CALCULATION
 *
 * IMPORTANT:
 * This is outside RelationshipEngine.
 * That avoids the previous "Class is prohibited here" error.
 * ================================================================
 */
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
     * Remaining time
     */
    val remaining =
        end.timeInMillis -
            cursor.timeInMillis

    val totalSeconds =
        remaining / 1000L

    val hours =
        (totalSeconds / 3600L)
            .toInt()

    val minutes =
        ((totalSeconds % 3600L) / 60L)
            .toInt()

    val seconds =
        (totalSeconds % 60L)
            .toInt()

    /*
     * Total days
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
