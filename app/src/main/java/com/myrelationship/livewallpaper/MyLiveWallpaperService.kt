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
import kotlin.math.min
import kotlin.math.sin

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
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

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val width = canvas.width.toFloat()
                val height = canvas.height.toFloat()

                drawBackground(canvas, width, height)

                val scale = min(width / 1080f, height / 2400f)

                canvas.save()

                canvas.scale(scale, scale)

                val designWidth = width / scale
                val designHeight = height / scale

                drawAnimatedBackground(
                    canvas,
                    designWidth,
                    designHeight
                )

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

        private fun drawBackground(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            canvas.drawColor(
                Color.rgb(7, 10, 17)
            )
        }

        private fun drawAnimatedBackground(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t = animationTime / 1000.0

            // Slow moving red/pink curve on left
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.2f
            paint.color = Color.argb(90, 255, 35, 90)

            path.reset()

            val startY = height * 0.42f

            path.moveTo(
                -40f,
                startY
            )

            for (i in 0..180) {

                val x = -40f + i * 7f

                val wave =
    (sin(i * 0.045 + t * 0.45) * 70.0).toFloat()

                val y =
                    startY +
                            i * 3.0f +
                            wave

                path.lineTo(x, y)
            }

            canvas.drawPath(path, paint)

            // Right side curve
            paint.color = Color.argb(70, 255, 50, 100)

            path.reset()

            path.moveTo(
                width + 40f,
                height * 0.55f
            )

            for (i in 0..180) {

                val x =
                    width + 40f - i * 7f

                val wave =
    (cos(i * 0.05 + t * 0.4) * 60.0).toFloat()

                val y =
                    height * 0.55f +
                            i * 3.1f +
                            wave

                path.lineTo(x, y)
            }

            canvas.drawPath(path, paint)

            // Small floating particles
            paint.style = Paint.Style.FILL

            for (i in 0 until 18) {

                val x =
                    ((i * 173) % width.toInt()).toFloat()

                val movement =
    ((t * (8 + i % 5)) % height.toDouble()).toFloat()

                val y =
                    (height - movement + i * 130f) % height

                paint.color =
                    Color.argb(
                        35 + (i % 3) * 15,
                        255,
                        70,
                        110
                    )

                canvas.drawCircle(
                    x,
                    y,
                    1.5f,
                    paint
                )
            }
        }

        private fun drawMainContent(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val centerX = width / 2f

            /*
             * TOP HEART
             */

            val heartY = 95f

            drawGlowingHeart(
                canvas,
                centerX,
                heartY
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                150f,
                16f,
                Color.rgb(145, 148, 158),
                false
            )

            /*
             * CLOCK
             */

            val time = SimpleDateFormat(
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
             * TOGETHER
             */

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                275f,
                19f,
                Color.rgb(255, 55, 90),
                true
            )

            val now = Calendar.getInstance()

            val togetherStart = Calendar.getInstance()
            togetherStart.set(
                2006,
                Calendar.FEBRUARY,
                14,
                0,
                0,
                0
            )
            togetherStart.set(Calendar.MILLISECOND, 0)

            val together =
                calculateDuration(
                    togetherStart,
                    now
                )

            drawText(
                canvas,
                "${together.years} Years  •  ${together.months} Months  •  ${together.days} Days",
                centerX,
                325f,
                27f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                365f,
                14f,
                Color.rgb(145, 148, 158),
                false
            )

            drawDivider(
                canvas,
                centerX,
                405f
            )

            /*
             * TOTAL TOGETHER DAYS
             */

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                455f,
                32f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                480f,
                11f,
                Color.rgb(125, 128, 138),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                535f,
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
                650f
            )

            /*
             * MARRIED
             */

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                730f,
                19f,
                Color.rgb(255, 55, 90),
                true
            )

            val marriedStart = Calendar.getInstance()

            marriedStart.set(
                2025,
                Calendar.APRIL,
                9,
                0,
                0,
                0
            )

            marriedStart.set(Calendar.MILLISECOND, 0)

            val married =
                calculateDuration(
                    marriedStart,
                    now
                )

            drawText(
                canvas,
                "${married.years} Years  •  ${married.months} Months  •  ${married.days} Days",
                centerX,
                780f,
                27f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                820f,
                14f,
                Color.rgb(145, 148, 158),
                false
            )

            drawDivider(
                canvas,
                centerX,
                860f
            )

            /*
             * MARRIED TOTAL DAYS
             */

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                910f,
                32f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                935f,
                11f,
                Color.rgb(125, 128, 138),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                990f,
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
                1080f,
                17f,
                Color.rgb(255, 55, 90),
                true
            )

            /*
             * Small glowing dots
             */

            drawGlowDot(
                canvas,
                centerX - 210f,
                1080f
            )

            drawGlowDot(
                canvas,
                centerX + 210f,
                1080f
            )
        }

        private fun drawText(
            canvas: Canvas,
            text: String,
            x: Float,
            y: Float,
            size: Float,
            color: Int,
            bold: Boolean
        ) {

            paint.style = Paint.Style.FILL
            paint.color = color
            paint.textSize = size
            paint.textAlign = Paint.Align.CENTER

            if (bold) {
                paint.setFakeBoldText(true)
            } else {
                paint.setFakeBoldText(false)
            }

            canvas.drawText(
                text,
                x,
                y,
                paint
            )
        }

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = Color.rgb(
                38,
                42,
                51
            )

            canvas.drawLine(
                centerX - 390f,
                y,
                centerX + 390f,
                y,
                paint
            )
        }

        private fun drawTimeColumns(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            hours: Int,
            minutes: Int,
            seconds: Int
        ) {

            val positions = floatArrayOf(
                centerX - 210f,
                centerX,
                centerX + 210f
            )

            val values = arrayOf(
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

            val labels = arrayOf(
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
                    25f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 28f,
                    10f,
                    Color.rgb(
                        115,
                        118,
                        128
                    ),
                    false
                )
            }

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            paint.color = Color.rgb(
                55,
                58,
                67
            )

            canvas.drawLine(
                centerX - 105f,
                y - 8f,
                centerX - 105f,
                y + 25f,
                paint
            )

            canvas.drawLine(
                centerX + 105f,
                y - 8f,
                centerX + 105f,
                y + 25f,
                paint
            )
        }

        private fun drawGlowingHeart(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            val pulse =
                1f +
                        sin(
                            animationTime / 180.0
                        ).toFloat() * 0.10f

            val glowRadius =
                38f * pulse

            paint.style = Paint.Style.FILL

            for (i in 5 downTo 1) {

                val alpha =
                    18 * i

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        30,
                        85
                    )

                canvas.drawCircle(
                    x,
                    y,
                    glowRadius + i * 8f,
                    paint
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                28f * pulse,
                Color.rgb(
                    255,
                    40,
                    85
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

            paint.style = Paint.Style.FILL
            paint.color = color

            canvas.drawPath(
                path,
                paint
            )
        }

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
                30f + pulse * 5f

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 5f

            for (i in 1..4) {

                paint.color =
                    Color.argb(
                        20,
                        255,
                        190,
                        50
                    )

                canvas.drawCircle(
                    centerX - 22f,
                    centerY,
                    glow + i * 4f,
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
                centerX - 20f,
                centerY,
                25f,
                paint
            )

            canvas.drawCircle(
                centerX + 20f,
                centerY,
                25f,
                paint
            )

            paint.style = Paint.Style.FILL

            drawHeartShape(
                canvas,
                centerX,
                centerY - 42f,
                6f,
                Color.rgb(
                    255,
                    45,
                    85
                )
            )
        }

        private fun drawGlowDot(
            canvas: Canvas,
            x: Float,
            y: Float
        ) {

            paint.style = Paint.Style.FILL

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
                8f,
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

    // Years
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

    // Months
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

    // Days
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
