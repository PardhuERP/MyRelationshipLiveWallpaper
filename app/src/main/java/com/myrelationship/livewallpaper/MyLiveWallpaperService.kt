package com.myrelationship.livewallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.net.Uri
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

        private var running = false
        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var animationTime = 0L

        private val prefs =
    getSharedPreferences(
        "relationship_settings",
        MODE_PRIVATE
    )

private var backgroundBitmap: Bitmap? = null
private var loadedBackgroundUri: String? = null

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

        // ============================================================
        // WALLPAPER DRAWING
        // ============================================================

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
                canvas.scale(
    scaleX,
    scaleY
)

                // ----------------------------------------------------
                // DESIGN SIZE
                // ----------------------------------------------------

                val designWidth = 1080f
                val designHeight = 2400f

                /*
                 * Scale independently so the complete design fills
                 * the complete wallpaper surface.
                 *
                 * This matches the approach you requested.
                 */

                val scaleX = screenWidth / designWidth
                val scaleY = screenHeight / designHeight

                canvas.save()

                canvas.scale(
                    scaleX,
                    scaleY
                )

                drawBackgroundEffects(
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

        // ============================================================
        // BACKGROUND
        // ============================================================

        private fun drawBackgroundEffects(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t = animationTime / 1000.0

            // --------------------------------------------------------
            // Large soft heart silhouettes
            // --------------------------------------------------------

            drawLargeHeart(
                canvas,
                90f,
                540f,
                190f,
                18
            )

            drawLargeHeart(
                canvas,
                930f,
                1950f,
                220f,
                16
            )

            drawLargeHeart(
                canvas,
                870f,
                390f,
                70f,
                25
            )

            drawLargeHeart(
                canvas,
                160f,
                1880f,
                85f,
                22
            )

            drawLargeHeart(
                canvas,
                930f,
                850f,
                70f,
                20
            )

            // --------------------------------------------------------
            // Floating hearts
            // --------------------------------------------------------

            drawSmallHeart(
                canvas,
                85f,
                260f,
                22f,
                80
            )

            drawSmallHeart(
                canvas,
                990f,
                650f,
                24f,
                75
            )

            drawSmallHeart(
                canvas,
                120f,
                1300f,
                25f,
                70
            )

            drawSmallHeart(
                canvas,
                970f,
                1450f,
                30f,
                75
            )

            drawSmallHeart(
                canvas,
                150f,
                2100f,
                22f,
                65
            )

            // --------------------------------------------------------
            // Animated glowing curves
            // --------------------------------------------------------

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.2f
            paint.color = Color.argb(
                100,
                255,
                30,
                85
            )

            path.reset()

            val leftStart = height * 0.36f

            path.moveTo(
                -100f,
                leftStart
            )

            for (i in 0..220) {

                val x = -100f + i * 6f

                val wave =
                    sin(
                        i * 0.045 + t * 0.45
                    ) * 65.0

                val y =
                    leftStart +
                            i * 4.0f +
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

            // --------------------------------------------------------
            // Right curve
            // --------------------------------------------------------

            paint.color = Color.argb(
                80,
                255,
                40,
                100
            )

            path.reset()

            path.moveTo(
                width + 100f,
                height * 0.55f
            )

            for (i in 0..220) {

                val x =
                    width + 100f - i * 6f

                val wave =
                    cos(
                        i * 0.05 + t * 0.40
                    ) * 60.0

                val y =
                    height * 0.55f +
                            i * 3.4f +
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

            // --------------------------------------------------------
            // Floating particles
            // --------------------------------------------------------

            paint.style = Paint.Style.FILL

            for (i in 0 until 45) {

                val x =
                    ((i * 173) % width.toInt()).toFloat()

                val movement =
                    (
                        t * (8 + i % 6)
                        % height.toDouble()
                    ).toFloat()

                val y =
                    (
                        height -
                                movement +
                                i * 117f
                        ) % height

                val alpha =
                    25 + (i % 4) * 12

                paint.color = Color.argb(
                    alpha,
                    255,
                    40,
                    90
                )

                canvas.drawCircle(
                    x,
                    y,
                    if (i % 4 == 0) 3f else 1.5f,
                    paint
                )
            }

            // --------------------------------------------------------
            // Glowing dots
            // --------------------------------------------------------

            for (i in 0 until 15) {

                val x =
                    ((i * 271) % width.toInt()).toFloat()

                val y =
                    ((i * 191) % height.toInt()).toFloat()

                drawGlowDot(
                    canvas,
                    x,
                    y
                )
            }
        }

        // ============================================================
        // MAIN CONTENT
        // ============================================================

        private fun drawMainContent(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val centerX = width / 2f

            // ========================================================
            // TOP HEART
            // ========================================================

            drawGlowingHeart(
                canvas,
                centerX,
                115f
            )

            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                170f,
                18f,
                Color.rgb(155, 158, 168),
                false
            )

            // ========================================================
            // CLOCK
            // ========================================================

            val time =
                SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            drawText(
                canvas,
                time,
                centerX,
                230f,
                56f,
                Color.WHITE,
                true
            )

            // ========================================================
            // TOGETHER
            // ========================================================

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                315f,
                25f,
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
                380f,
                30f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                425f,
                17f,
                Color.rgb(
                    155,
                    158,
                    168
                ),
                false
            )

            drawDivider(
                canvas,
                centerX,
                470f
            )

            // ========================================================
            // TOGETHER TOTAL DAYS
            // ========================================================

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                535f,
                46f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                565f,
                13f,
                Color.rgb(
                    130,
                    133,
                    143
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                635f,
                together.hours,
                together.minutes,
                together.seconds
            )

            // ========================================================
            // WEDDING RINGS
            // ========================================================

            drawWeddingRings(
                canvas,
                centerX,
                795f
            )

            // ========================================================
            // MARRIED
            // ========================================================

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                900f,
                25f,
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
                970f,
                30f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                1015f,
                17f,
                Color.rgb(
                    155,
                    158,
                    168
                ),
                false
            )

            drawDivider(
                canvas,
                centerX,
                1060f
            )

            // ========================================================
            // MARRIED TOTAL DAYS
            // ========================================================

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1125f,
                46f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                1155f,
                13f,
                Color.rgb(
                    130,
                    133,
                    143
                ),
                false
            )

            drawTimeColumns(
                canvas,
                centerX,
                1225f,
                married.hours,
                married.minutes,
                married.seconds
            )

            // ========================================================
            // BOTTOM MESSAGE
            // ========================================================

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1390f,
                21f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            drawGlowDot(
                canvas,
                centerX - 250f,
                1390f
            )

            drawGlowDot(
                canvas,
                centerX + 250f,
                1390f
            )
        }

        // ============================================================
        // TEXT
        // ============================================================

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

        // ============================================================
        // DIVIDER
        // ============================================================

        private fun drawDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 1.5f

            paint.color =
                Color.rgb(
                    100,
                    25,
                    50
                )

            canvas.drawLine(
                centerX - 430f,
                y,
                centerX - 45f,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 45f,
                y,
                centerX + 430f,
                y,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                y - 2f,
                8f,
                Color.WHITE
            )
        }

        // ============================================================
        // TIME COLUMNS
        // ============================================================

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
                    32f,
                    Color.WHITE,
                    true
                )

                drawText(
                    canvas,
                    labels[i],
                    positions[i],
                    y + 34f,
                    12f,
                    Color.rgb(
                        120,
                        123,
                        133
                    ),
                    false
                )
            }

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 1f

            paint.color =
                Color.rgb(
                    70,
                    50,
                    60
                )

            canvas.drawLine(
                centerX - 135f,
                y - 12f,
                centerX - 135f,
                y + 30f,
                paint
            )

            canvas.drawLine(
                centerX + 135f,
                y - 12f,
                centerX + 135f,
                y + 30f,
                paint
            )
        }

        // ============================================================
        // HEART
        // ============================================================

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

            val glowRadius =
                50f * pulse

            paint.style =
                Paint.Style.FILL

            for (i in 7 downTo 1) {

                val alpha =
                    12 * i

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
                    glowRadius + i * 12f,
                    paint
                )
            }

            drawHeartShape(
                canvas,
                x,
                y,
                38f * pulse,
                Color.rgb(
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

        // ============================================================
        // LARGE BACKGROUND HEART
        // ============================================================

        private fun drawLargeHeart(
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
                    180,
                    10,
                    55
                )
            )
        }

        // ============================================================
        // SMALL HEART
        // ============================================================

        private fun drawSmallHeart(
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
                    30,
                    80
                )
            )
        }

        // ============================================================
        // WEDDING RINGS
        // ============================================================

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

            paint.strokeWidth = 6f

            for (i in 1..5) {

                paint.color =
                    Color.argb(
                        18,
                        255,
                        190,
                        50
                    )

                canvas.drawCircle(
                    centerX - 25f,
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
                38f,
                paint
            )

            canvas.drawCircle(
                centerX + 25f,
                centerY,
                38f,
                paint
            )

            drawHeartShape(
                canvas,
                centerX,
                centerY - 65f,
                9f,
                Color.rgb(
                    255,
                    45,
                    85
                )
            )
        }

       // ============================================================
        // LOAD BACKGROUND 
        // ============================================================

        
        private fun loadBackgroundIfNeeded() {

    val uriString = prefs.getString(
        "background_uri",
        null
    )

    if (uriString == loadedBackgroundUri) {
        return
    }

    backgroundBitmap?.recycle()
    backgroundBitmap = null

    loadedBackgroundUri = uriString

    if (uriString.isNullOrEmpty()) {
        return
    }

    try {

        val uri = Uri.parse(uriString)

        val inputStream =
            contentResolver.openInputStream(uri)

        if (inputStream != null) {

            backgroundBitmap =
                BitmapFactory.decodeStream(
                    inputStream
                )

            inputStream.close()
        }

    } catch (_: Exception) {

        backgroundBitmap = null
    }
}

        // ============================================================
        // GLOW DOT
        // ============================================================

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
                    30,
                    90
                )

            canvas.drawCircle(
                x,
                y,
                14f,
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

// ====================================================================
// DURATION RESULT
// IMPORTANT: This is OUTSIDE RelationshipEngine.
// ====================================================================

private data class DurationResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalDays: Long
)

// ====================================================================
// DURATION CALCULATOR
// ====================================================================

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

    // ------------------------------------------------------------
    // YEARS
    // ------------------------------------------------------------

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

    // ------------------------------------------------------------
    // MONTHS
    // ------------------------------------------------------------

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

    // ------------------------------------------------------------
    // DAYS
    // ------------------------------------------------------------

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

    // ------------------------------------------------------------
    // REMAINING TIME
    // ------------------------------------------------------------

    val remaining =
        end.timeInMillis -
                cursor.timeInMillis

    val totalSeconds =
        remaining / 1000L

    val hours =
        (totalSeconds / 3600L)
            .toInt()

    val minutes =
        (
            (totalSeconds % 3600L) /
                    60L
            ).toInt()

    val seconds =
        (totalSeconds % 60L)
            .toInt()

    // ------------------------------------------------------------
    // TOTAL DAYS
    // ------------------------------------------------------------

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
