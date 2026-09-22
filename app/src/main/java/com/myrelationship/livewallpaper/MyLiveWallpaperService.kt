package com.myrelationship.livewallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
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
        return RelationshipEngine()
    }

    inner class RelationshipEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()

        private var animationTime = 0L

        // ============================================================
        // SETTINGS
        // ============================================================

        private val prefs = getSharedPreferences(
            "relationship_settings",
            MODE_PRIVATE
        )

        // ============================================================
        // BACKGROUND IMAGE
        // ============================================================

        private var backgroundBitmap: Bitmap? = null
        private var loadedBackgroundUri: String? = null

        // ============================================================
        // WALLPAPER LIFECYCLE
        // ============================================================

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

        override fun onDestroy() {
            stopAnimation()

            backgroundBitmap?.recycle()
            backgroundBitmap = null

            super.onDestroy()
        }

        // ============================================================
        // START ANIMATION
        // ============================================================

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

        // ============================================================
        // STOP ANIMATION
        // ============================================================

        private fun stopAnimation() {

            running = false

            drawingThread?.interrupt()
            drawingThread = null
        }

        // ============================================================
        // MAIN WALLPAPER DRAWING
        // ============================================================

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val screenWidth = canvas.width.toFloat()
                val screenHeight = canvas.height.toFloat()

                // ----------------------------------------------------
                // BASE COLOR
                // ----------------------------------------------------

                canvas.drawColor(
                    Color.rgb(4, 7, 13)
                )

                // ----------------------------------------------------
                // DESIGN SIZE
                // ----------------------------------------------------

                val designWidth = 1080f
                val designHeight = 2400f

                // ----------------------------------------------------
                // SCREEN SCALE
                // ----------------------------------------------------

                val scaleX =
                    screenWidth / designWidth

                val scaleY =
                    screenHeight / designHeight

                canvas.save()

                canvas.scale(
                    scaleX,
                    scaleY
                )

                // ----------------------------------------------------
                // LOAD IMAGE IF REQUIRED
                // ----------------------------------------------------

                loadBackgroundIfNeeded()

                // ----------------------------------------------------
                // BACKGROUND IMAGE
                // ----------------------------------------------------

                drawBackgroundImage(
                    canvas,
                    designWidth,
                    designHeight
                )

                // ----------------------------------------------------
                // BACKGROUND EFFECTS
                // ----------------------------------------------------

                drawBackgroundEffects(
                    canvas,
                    designWidth,
                    designHeight
                )

                // ----------------------------------------------------
                // MAIN CONTENT
                // ----------------------------------------------------

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
        // BACKGROUND IMAGE LOADER
        // ============================================================

        private fun loadBackgroundIfNeeded() {

            val uriString = prefs.getString(
                "background_uri",
                null
            )

            // Nothing changed
            if (uriString == loadedBackgroundUri) {
                return
            }

            // Remove old bitmap
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
        // DRAW BACKGROUND IMAGE
        // ============================================================

        private fun drawBackgroundImage(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val bitmap =
                backgroundBitmap ?: return

            if (bitmap.isRecycled) {
                return
            }

            val srcWidth =
                bitmap.width.toFloat()

            val srcHeight =
                bitmap.height.toFloat()

            if (srcWidth <= 0f || srcHeight <= 0f) {
                return
            }

            // --------------------------------------------------------
            // CENTER-CROP
            // --------------------------------------------------------

            val sourceRatio =
                srcWidth / srcHeight

            val targetRatio =
                width / height

            var srcLeft = 0f
            var srcTop = 0f
            var srcRight = srcWidth
            var srcBottom = srcHeight

            if (sourceRatio > targetRatio) {

                // Image is wider
                val newWidth =
                    srcHeight * targetRatio

                srcLeft =
                    (srcWidth - newWidth) / 2f

                srcRight =
                    srcLeft + newWidth

            } else {

                // Image is taller
                val newHeight =
                    srcWidth / targetRatio

                srcTop =
                    (srcHeight - newHeight) / 2f

                srcBottom =
                    srcTop + newHeight
            }

            val srcRect =
                android.graphics.RectF(
                    srcLeft,
                    srcTop,
                    srcRight,
                    srcBottom
                )

            val dstRect =
                android.graphics.RectF(
                    0f,
                    0f,
                    width,
                    height
                )

            paint.style =
                Paint.Style.FILL

            paint.alpha = 255

            canvas.drawBitmap(
                bitmap,
                null,
                dstRect,
                paint
            )

            // --------------------------------------------------------
            // DARK OVERLAY
            // --------------------------------------------------------

            paint.color =
                Color.argb(
                    105,
                    0,
                    0,
                    0
                )

            canvas.drawRect(
                0f,
                0f,
                width,
                height,
                paint
            )

            paint.alpha = 255
        }

        // ============================================================
        // BACKGROUND EFFECTS
        // ============================================================

        private fun drawBackgroundEffects(
            canvas: Canvas,
            width: Float,
            height: Float
        ) {

            val t =
                animationTime / 1000.0

            // --------------------------------------------------------
            // LARGE HEARTS
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
            // SMALL HEARTS
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
            // LEFT CURVE
            // --------------------------------------------------------

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth = 2.2f

            paint.color =
                Color.argb(
                    100,
                    255,
                    30,
                    85
                )

            path.reset()

            val leftStart =
                height * 0.36f

            path.moveTo(
                -100f,
                leftStart
            )

            for (i in 0..220) {

                val x =
                    -100f + i * 6f

                val wave =
                    sin(
                        i * 0.045 +
                                t * 0.45
                    ) * 65.0

                val y =
                    leftStart +
                            i * 4f +
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
            // RIGHT CURVE
            // --------------------------------------------------------

            paint.color =
                Color.argb(
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
                    width + 100f -
                            i * 6f

                val wave =
                    cos(
                        i * 0.05 +
                                t * 0.40
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
            // FLOATING PARTICLES
            // --------------------------------------------------------

            paint.style =
                Paint.Style.FILL

            for (i in 0 until 45) {

                val x =
                    ((i * 173) %
                            width.toInt()).toFloat()

                val movement =
                    (
                        t *
                                (8 + i % 6) %
                                height.toDouble()
                        ).toFloat()

                val y =
                    (
                        height -
                                movement +
                                i * 117f
                        ) % height

                val alpha =
                    25 +
                            (i % 4) * 12

                paint.color =
                    Color.argb(
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
            // GLOW DOTS
            // --------------------------------------------------------

            for (i in 0 until 15) {

                val x =
                    ((i * 271) %
                            width.toInt()).toFloat()

                val y =
                    ((i * 191) %
                            height.toInt()).toFloat()

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

            val centerX =
                width / 2f

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
                Color.rgb(
                    155,
                    158,
                    168
                ),
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
            // CURRENT DATE
            // ========================================================

            val currentDate =
                SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale.getDefault()
                ).format(Date())

            drawText(
                canvas,
                currentDate,
                centerX,
                265f,
                16f,
                Color.rgb(
                    145,
                    148,
                    158
                ),
                false
            )

            // ========================================================
            // TOGETHER
            // ========================================================

            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                335f,
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
                getDateFromSettings(
                    "together_date",
                    14,
                    Calendar.FEBRUARY,
                    2006
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
                400f,
                30f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since " +
                        formatDisplayDate(
                            togetherStart
                        ),
                centerX,
                445f,
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
                490f
            )

            // ========================================================
            // TOGETHER TOTAL DAYS
            // ========================================================

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                555f,
                46f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                585f,
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
                655f,
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
                815f
            )

            // ========================================================
            // MARRIED
            // ========================================================

            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                920f,
                25f,
                Color.rgb(
                    255,
                    55,
                    90
                ),
                true
            )

            val marriedStart =
                getDateFromSettings(
                    "married_date",
                    9,
                    Calendar.APRIL,
                    2025
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
                990f,
                30f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "Since " +
                        formatDisplayDate(
                            marriedStart
                        ),
                centerX,
                1035f,
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
                1080f
            )

            // ========================================================
            // MARRIED TOTAL DAYS
            // ========================================================

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1145f,
                46f,
                Color.WHITE,
                true
            )

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                1175f,
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
                1245f,
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
                1410f,
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
                1410f
            )

            drawGlowDot(
                canvas,
                centerX + 250f,
                1410f
            )
        }

        // ============================================================
        // DATE FROM SETTINGS
        // ============================================================

        private fun getDateFromSettings(
            key: String,
            defaultDay: Int,
            defaultMonth: Int,
            defaultYear: Int
        ): Calendar {

            val value =
                prefs.getString(
                    key,
                    null
                )

            val calendar =
                Calendar.getInstance()

            calendar.set(
                defaultYear,
                defaultMonth,
                defaultDay,
                0,
                0,
                0
            )

            calendar.set(
                Calendar.MILLISECOND,
                0
            )

            if (value.isNullOrBlank()) {
                return calendar
            }

            try {

                val format =
                    SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.US
                    )

                format.isLenient = false

                val date =
                    format.parse(value)

                if (date != null) {

                    calendar.time =
                        date
                }

            } catch (_: Exception) {
                // Keep default date
            }

            return calendar
        }

        // ============================================================
        // DISPLAY DATE
        // ============================================================

        private fun formatDisplayDate(
            calendar: Calendar
        ): String {

            return SimpleDateFormat(
                "dd MMMM yyyy",
                Locale.getDefault()
            ).format(
                calendar.time
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

            paint.strokeWidth =
                1.5f

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

            paint.strokeWidth =
                1f

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
        // GLOWING HEART
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

        // ============================================================
        // HEART SHAPE
        // ============================================================

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
        // LARGE HEART
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

            paint.strokeWidth =
                6f

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
