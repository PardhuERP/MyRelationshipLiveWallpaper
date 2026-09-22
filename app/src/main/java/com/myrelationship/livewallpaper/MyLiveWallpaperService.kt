package com.myrelationship.livewallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return RelationshipEngine()
    }

    inner class RelationshipEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val paint = Paint(
            Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
        )

        private val path = Path()

        private var animationTime = 0L

    // ============================================================
// NEXT ANNIVERSARY
// ============================================================

private fun getNextAnniversary(
    marriedStart: Calendar
): Calendar {

    val today = Calendar.getInstance()

    val anniversary =
        Calendar.getInstance()

    anniversary.timeInMillis =
        marriedStart.timeInMillis

    anniversary.set(
        Calendar.YEAR,
        today.get(Calendar.YEAR)
    )

    anniversary.set(
        Calendar.HOUR_OF_DAY,
        0
    )

    anniversary.set(
        Calendar.MINUTE,
        0
    )

    anniversary.set(
        Calendar.SECOND,
        0
    )

    anniversary.set(
        Calendar.MILLISECOND,
        0
    )

    if (
        anniversary.before(today) &&
        !isSameDay(anniversary, today)
    ) {

        anniversary.add(
            Calendar.YEAR,
            1
        )
    }

    return anniversary
}

// ============================================================
// DAYS UNTIL ANNIVERSARY
// ============================================================

private fun calculateDaysUntil(
    target: Calendar,
    now: Calendar
): Long {

    val start = Calendar.getInstance()

    start.timeInMillis =
        now.timeInMillis

    start.set(
        Calendar.HOUR_OF_DAY,
        0
    )

    start.set(
        Calendar.MINUTE,
        0
    )

    start.set(
        Calendar.SECOND,
        0
    )

    start.set(
        Calendar.MILLISECOND,
        0
    )

    val difference =
        target.timeInMillis -
                start.timeInMillis

    return difference /
            86400000L
}

// ============================================================
// ANNIVERSARY CHECK
// ============================================================

private fun isAnniversaryToday(
    anniversary: Calendar
): Boolean {

    val today =
        Calendar.getInstance()

    return isSameDay(
        anniversary,
        today
    )
}

private fun isSameDay(
    a: Calendar,
    b: Calendar
): Boolean {

    return a.get(Calendar.YEAR) ==
            b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) ==
            b.get(Calendar.DAY_OF_YEAR)
}

// ============================================================
// ANNIVERSARY CELEBRATION
// ============================================================

private fun drawAnniversaryCelebration(
    canvas: Canvas,
    width: Float,
    height: Float
) {

    val t =
        animationTime / 1000.0

    // --------------------------------------------------------
    // FLOATING HEARTS
    // --------------------------------------------------------

    for (i in 0 until 35) {

        val baseX =
            ((i * 137) % width.toInt()).toFloat()

        val speed =
            12f + (i % 7) * 4f

        val movement =
            (
                t * speed +
                        i * 95f
                ).toFloat()

        val y =
            height -
                    (movement % (height + 200f))

        val wave =
            sin(
                t * 1.5 +
                        i
            ) * 45.0

        val x =
            baseX +
                    wave.toFloat()

        val size =
            7f +
                    (i % 5) * 3f

        drawHeartShape(
            canvas,
            x,
            y,
            size,
            Color.argb(
                130,
                255,
                40,
                90
            )
        )
    }

    // --------------------------------------------------------
    // FLOWER / PETAL PARTICLES
    // --------------------------------------------------------

    for (i in 0 until 45) {

        val baseX =
            ((i * 211) % width.toInt()).toFloat()

        val speed =
            18f + (i % 8) * 5f

        val movement =
            (
                t * speed +
                        i * 83f
                ).toFloat()

        val y =
            height -
                    (movement % (height + 250f))

        val wind =
            sin(
                t * 1.8 +
                        i * 0.7
            ) * 90.0

        val x =
            baseX +
                    wind.toFloat()

        val rotation =
            (
                t * 90 +
                        i * 37
                ).toFloat()

        drawPetal(
            canvas,
            x,
            y,
            5f + (i % 4),
            rotation
        )
    }

    // --------------------------------------------------------
    // EXTRA GLOW
    // --------------------------------------------------------

    for (i in 0 until 18) {

        val x =
            ((i * 311) %
                    width.toInt()).toFloat()

        val y =
            ((i * 173 +
                    t * 35)
                    % height)
                .toFloat()

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                80,
                255,
                80,
                120
            )

        canvas.drawCircle(
            x,
            y,
            3f,
            paint
        )
    }
}

// ============================================================
// FLOWER PETAL
// ============================================================

private fun drawPetal(
    canvas: Canvas,
    x: Float,
    y: Float,
    size: Float,
    rotation: Float
) {

    canvas.save()

    canvas.rotate(
        rotation,
        x,
        y
    )

    paint.style =
        Paint.Style.FILL

    paint.color =
        Color.argb(
            150,
            255,
            80,
            120
        )

    path.reset()

    path.moveTo(
        x,
        y - size
    )

    path.cubicTo(
        x + size * 1.5f,
        y - size * 0.5f,
        x + size * 1.5f,
        y + size * 0.8f,
        x,
        y + size
    )

    path.cubicTo(
        x - size * 1.5f,
        y + size * 0.8f,
        x - size * 1.5f,
        y - size * 0.5f,
        x,
        y - size
    )

    canvas.drawPath(
        path,
        paint
    )

    canvas.restore()
}

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

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            startAnimation()
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

        override fun onSurfaceDestroyed(
            holder: SurfaceHolder
        ) {

            stopAnimation()

            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {

            stopAnimation()

            backgroundBitmap?.let {
                if (!it.isRecycled) {
                    it.recycle()
                }
            }

            backgroundBitmap = null
            loadedBackgroundUri = null

            super.onDestroy()
        }

        // ============================================================
        // START
        // ============================================================

        @Synchronized
        private fun startAnimation() {

            if (running) return

            running = true

            drawingThread = Thread {

                while (running) {

                    try {

                        animationTime =
                            System.currentTimeMillis()

                        drawWallpaper()

                        Thread.sleep(40)

                    } catch (_: InterruptedException) {

                        break

                    } catch (_: Throwable) {

                        // Prevent wallpaper service from
                        // crashing because of a drawing error.

                        try {
                            Thread.sleep(200)
                        } catch (_: Exception) {
                            break
                        }
                    }
                }
            }

            drawingThread?.start()
        }

        // ============================================================
        // STOP
        // ============================================================

        @Synchronized
        private fun stopAnimation() {

            running = false

            drawingThread?.interrupt()
            drawingThread = null
        }

        // ============================================================
        // MAIN DRAWING
        // ============================================================

        private fun drawWallpaper() {

            val holder = surfaceHolder

            if (!holder.surface.isValid) {
                return
            }

            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) {
                    return
                }

                val screenWidth =
                    canvas.width.toFloat()

                val screenHeight =
                    canvas.height.toFloat()

                if (
                    screenWidth <= 0f ||
                    screenHeight <= 0f
                ) {
                    return
                }

                // ----------------------------------------------------
                // BASE
                // ----------------------------------------------------

                canvas.drawColor(
                    Color.rgb(
                        4,
                        7,
                        13
                    )
                )

                // ----------------------------------------------------
                // DESIGN SIZE
                // ----------------------------------------------------

                val designWidth = 1080f
                val designHeight = 2400f

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
                // BACKGROUND IMAGE
                // ----------------------------------------------------

                loadBackgroundIfNeeded()

                drawBackgroundImage(
                    canvas,
                    designWidth,
                    designHeight
                )

                // ----------------------------------------------------
                // EFFECTS
                // ----------------------------------------------------

                drawBackgroundEffects(
                    canvas,
                    designWidth,
                    designHeight
                )

                // ----------------------------------------------------
                // CONTENT
                // ----------------------------------------------------

                drawMainContent(
                    canvas,
                    designWidth,
                    designHeight
                )

                canvas.restore()

            } catch (_: Throwable) {

                // Never allow a drawing exception
                // to kill the wallpaper service.

            } finally {

                if (canvas != null) {

                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (_: Throwable) {
                    }
                }
            }
        }

        // ============================================================
        // LOAD BACKGROUND IMAGE
        // IMPORTANT:
        // Image is downsampled to avoid OOM/service crash.
        // ============================================================

        private fun loadBackgroundIfNeeded() {

            val uriString =
                prefs.getString(
                    "background_uri",
                    null
                )

            if (uriString == loadedBackgroundUri) {
                return
            }

            loadedBackgroundUri = uriString

            val oldBitmap =
                backgroundBitmap

            backgroundBitmap = null

            // Do NOT recycle immediately.
            // This avoids possible bitmap-use crashes.

            if (uriString.isNullOrEmpty()) {

                if (
                    oldBitmap != null &&
                    !oldBitmap.isRecycled
                ) {
                    try {
                        oldBitmap.recycle()
                    } catch (_: Exception) {
                    }
                }

                return
            }

            try {

                val uri =
                    Uri.parse(uriString)

                // ----------------------------------------------------
                // First pass - get image dimensions
                // ----------------------------------------------------

                val boundsStream =
                    contentResolver.openInputStream(uri)

                if (boundsStream == null) {
                    return
                }

                val boundsOptions =
                    BitmapFactory.Options()

                boundsOptions.inJustDecodeBounds = true

                BitmapFactory.decodeStream(
                    boundsStream,
                    null,
                    boundsOptions
                )

                boundsStream.close()

                val imageWidth =
                    boundsOptions.outWidth

                val imageHeight =
                    boundsOptions.outHeight

                if (
                    imageWidth <= 0 ||
                    imageHeight <= 0
                ) {
                    return
                }

                // ----------------------------------------------------
                // Calculate safe sample size
                // ----------------------------------------------------

                val maxWidth = 1080
                val maxHeight = 2400

                var sampleSize = 1

                while (
                    imageWidth / sampleSize > maxWidth * 2 ||
                    imageHeight / sampleSize > maxHeight * 2
                ) {

                    sampleSize *= 2
                }

                // ----------------------------------------------------
                // Second pass - actual bitmap
                // ----------------------------------------------------

                val inputStream =
                    contentResolver.openInputStream(uri)

                if (inputStream == null) {
                    return
                }

                val options =
                    BitmapFactory.Options()

                options.inSampleSize =
                    max(
                        1,
                        sampleSize
                    )

                options.inPreferredConfig =
                    Bitmap.Config.RGB_565

                val bitmap =
                    BitmapFactory.decodeStream(
                        inputStream,
                        null,
                        options
                    )

                inputStream.close()

                backgroundBitmap =
                    bitmap

                // ----------------------------------------------------
                // Recycle old bitmap AFTER new bitmap loaded
                // ----------------------------------------------------

                if (
                    oldBitmap != null &&
                    oldBitmap != bitmap &&
                    !oldBitmap.isRecycled
                ) {

                    try {
                        oldBitmap.recycle()
                    } catch (_: Exception) {
                    }
                }

            } catch (_: Throwable) {

                backgroundBitmap = null

                if (
                    oldBitmap != null &&
                    !oldBitmap.isRecycled
                ) {

                    try {
                        oldBitmap.recycle()
                    } catch (_: Exception) {
                    }
                }
            }
        }

        // ============================================================
        // DRAW BACKGROUND IMAGE
        // CENTER CROP
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

            val bitmapWidth =
                bitmap.width

            val bitmapHeight =
                bitmap.height

            if (
                bitmapWidth <= 0 ||
                bitmapHeight <= 0
            ) {
                return
            }

            val sourceRatio =
                bitmapWidth.toFloat() /
                        bitmapHeight.toFloat()

            val targetRatio =
                width / height

            val srcRect: Rect

            if (sourceRatio > targetRatio) {

                // Image wider than screen

                val cropWidth =
                    (
                        bitmapHeight *
                                targetRatio
                        ).toInt()

                val left =
                    (bitmapWidth - cropWidth) / 2

                srcRect =
                    Rect(
                        left,
                        0,
                        left + cropWidth,
                        bitmapHeight
                    )

            } else {

                // Image taller than screen

                val cropHeight =
                    (
                        bitmapWidth /
                                targetRatio
                        ).toInt()

                val top =
                    (bitmapHeight - cropHeight) / 2

                srcRect =
                    Rect(
                        0,
                        top,
                        bitmapWidth,
                        top + cropHeight
                    )
            }

            val dstRect =
                RectF(
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
                srcRect,
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

// ========================================================
// ANNIVERSARY CELEBRATION
// ========================================================

val marriedStart =
    getDateFromSettings(
        "married_date",
        9,
        Calendar.APRIL,
        2025
    )

val anniversary =
    getNextAnniversary(
        marriedStart
    )

if (isAnniversaryToday(anniversary)) {

    drawAnniversaryCelebration(
        canvas,
        width,
        height
    )
}

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
            // PARTICLES
            // --------------------------------------------------------

            paint.style =
                Paint.Style.FILL

            for (i in 0 until 45) {

                val x =
                    (
                        (i * 173) %
                                width.toInt()
                        ).toFloat()

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
                    if (
                        i % 4 == 0
                    ) {
                        3f
                    } else {
                        1.5f
                    },
                    paint
                )
            }

            // --------------------------------------------------------
            // GLOW DOTS
            // --------------------------------------------------------

            for (i in 0 until 15) {

                val x =
                    (
                        (i * 271) %
                                width.toInt()
                        ).toFloat()

                val y =
                    (
                        (i * 191) %
                                height.toInt()
                        ).toFloat()

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

            // --------------------------------------------------------
            // TOP HEART
            // --------------------------------------------------------

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

            // --------------------------------------------------------
            // CLOCK
            // --------------------------------------------------------

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

            // --------------------------------------------------------
            // DATE
            // --------------------------------------------------------

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

            // --------------------------------------------------------
            // TOGETHER
            // --------------------------------------------------------

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
                32f,
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

            // --------------------------------------------------------
            // TOGETHER TOTAL
            // --------------------------------------------------------

            drawText(
                canvas,
                together.totalDays.toString(),
                centerX,
                555f,
                50f,
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
// NEXT ANNIVERSARY
// ========================================================

val anniversaryStart =
    getDateFromSettings(
        "married_date",
        9,
        Calendar.APRIL,
        2025
    )

val nextAnniversary =
    getNextAnniversary(
        anniversaryStart
    )

val daysUntilAnniversary =
    calculateDaysUntil(
        nextAnniversary,
        now
    )

   // ========================================================
// NEXT ANNIVERSARY COUPLE LOGO
// ========================================================

drawText(
    canvas,
    "👩‍❤️‍👨",
    centerX,
    1335f,
    32f,
    Color.WHITE,
    false
) 

drawText(
    canvas,
    "♥  NEXT ANNIVERSARY  ♥",
    centerX,
    1340f,
    23f,
    Color.rgb(
        255,
        55,
        90
    ),
    true
)

drawText(
    canvas,
    formatDisplayDate(
        nextAnniversary
    ),
    centerX,
    1380f,
    24f,
    Color.WHITE,
    true
)

if (!isAnniversaryToday(nextAnniversary)) {

    drawText(
        canvas,
        "$daysUntilAnniversary DAYS TO GO",
        centerX,
        1415f,
        14f,
        Color.rgb(
            155,
            158,
            168
        ),
        false
    )
}

            // --------------------------------------------------------
            // RINGS
            // --------------------------------------------------------

            drawWeddingRings(
                canvas,
                centerX,
                815f
            )

            // --------------------------------------------------------
            // MARRIED
            // --------------------------------------------------------

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

            // --------------------------------------------------------
            // MARRIED TOTAL
            // --------------------------------------------------------

            drawText(
                canvas,
                married.totalDays.toString(),
                centerX,
                1145f,
                50f,
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

            // --------------------------------------------------------
            // MESSAGE
            // --------------------------------------------------------

            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                1490f,
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
                1490f
            )

            drawGlowDot(
                canvas,
                centerX + 250f,
                1490f
            )
        }

        // ============================================================
        // DATE FROM SETTINGS
        // Compatible with current MainActivity Long values
        // Also supports old String values.
        // ============================================================

        private fun getDateFromSettings(
            key: String,
            defaultDay: Int,
            defaultMonth: Int,
            defaultYear: Int
        ): Calendar {

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

            // --------------------------------------------------------
            // CURRENT MainActivity stores Long
            // --------------------------------------------------------

            val longValue =
                prefs.getLong(
                    key,
                    Long.MIN_VALUE
                )

            if (
                longValue !=
                Long.MIN_VALUE &&
                longValue > 0
            ) {

                calendar.timeInMillis =
                    longValue

                return calendar
            }

            // --------------------------------------------------------
            // OLD VERSION stored String
            // --------------------------------------------------------

            val stringValue =
                prefs.getString(
                    key,
                    null
                )

            if (!stringValue.isNullOrBlank()) {

                try {

                    val format =
                        SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.US
                        )

                    format.isLenient = false

                    val date =
                        format.parse(
                            stringValue
                        )

                    if (date != null) {
                        calendar.time = date
                    }

                } catch (_: Exception) {
                }
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
// NEXT ANNIVERSARY DATE
// ============================================================

private fun getNextAnniversaryDate(
    marriedDate: Calendar
): Calendar {

    val today = Calendar.getInstance()

    val anniversary =
        Calendar.getInstance()

    anniversary.set(
        today.get(Calendar.YEAR),
        marriedDate.get(Calendar.MONTH),
        marriedDate.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0
    )

    anniversary.set(
        Calendar.MILLISECOND,
        0
    )

    // If this year's anniversary has already passed,
    // use next year's anniversary.
    if (
        anniversary.timeInMillis <
        today.timeInMillis
    ) {

        anniversary.add(
            Calendar.YEAR,
            1
        )
    }

    return anniversary
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

    paint.style = Paint.Style.FILL

    paint.color = color

    paint.alpha = 255

    paint.textSize = size

    paint.textAlign = Paint.Align.CENTER

    paint.typeface =
        if (bold) {
            android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        } else {
            android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.NORMAL
            )
        }

    paint.setFakeBoldText(false)

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
                    35f,
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

                paint.color =
                    Color.argb(
                        12 * i,
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
// PERFECT HEART
// ============================================================

private fun drawHeartShape(
    canvas: Canvas,
    cx: Float,
    cy: Float,
    size: Float,
    color: Int
) {

    path.reset()

    // --------------------------------------------------------
    // TOP CENTER NOTCH
    // --------------------------------------------------------

    path.moveTo(
        cx,
        cy - size * 0.18f
    )

    // --------------------------------------------------------
    // LEFT LOBE
    // --------------------------------------------------------

    path.cubicTo(
        cx - size * 0.28f,
        cy - size * 0.62f,
        cx - size * 0.88f,
        cy - size * 0.70f,
        cx - size * 1.08f,
        cy - size * 0.28f
    )

    // --------------------------------------------------------
    // LEFT SIDE → BOTTOM POINT
    // --------------------------------------------------------

    path.cubicTo(
        cx - size * 1.35f,
        cy + size * 0.28f,
        cx - size * 0.78f,
        cy + size * 0.72f,
        cx,
        cy + size * 1.18f
    )

    // --------------------------------------------------------
    // BOTTOM POINT → RIGHT SIDE
    // --------------------------------------------------------

    path.cubicTo(
        cx + size * 0.78f,
        cy + size * 0.72f,
        cx + size * 1.35f,
        cy + size * 0.28f,
        cx + size * 1.08f,
        cy - size * 0.28f
    )

    // --------------------------------------------------------
    // RIGHT LOBE → CENTER NOTCH
    // --------------------------------------------------------

    path.cubicTo(
        cx + size * 0.88f,
        cy - size * 0.70f,
        cx + size * 0.28f,
        cy - size * 0.62f,
        cx,
        cy - size * 0.18f
    )

    // --------------------------------------------------------
    // DRAW HEART
    // --------------------------------------------------------

    paint.style = Paint.Style.FILL
    paint.color = color
    paint.alpha = 255

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
