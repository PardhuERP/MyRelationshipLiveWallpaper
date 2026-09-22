package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var drawingThread: Thread? = null

        private val bgColor = Color.rgb(8, 11, 17)
        private val white = Color.rgb(250, 250, 253)
        private val softWhite = Color.rgb(205, 208, 218)
        private val grey = Color.rgb(120, 125, 138)
        private val darkGrey = Color.rgb(55, 59, 70)

        private val pink = Color.rgb(255, 55, 90)
        private val lightPink = Color.rgb(255, 105, 130)
        private val gold = Color.rgb(255, 190, 70)

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
        }

        private val heartPath = Path()

        private var animationTime = 0L

        /*
         * RELATIONSHIP START
         */
        private val relationshipStart =
            LocalDate.of(2006, 2, 14)

        /*
         * MARRIAGE START
         */
        private val marriageStart =
            LocalDate.of(2025, 4, 9)

        /*
         * Floating particles
         */
        private val particles = ArrayList<Particle>()

        /*
         * Animated hearts
         */
        private val hearts = ArrayList<FloatingHeart>()

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)

            createParticles()
            createHearts()

            startDrawing()
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            super.onVisibilityChanged(isVisible)

            if (isVisible) {
                startDrawing()
            } else {
                stopDrawing()
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

            if (running) {
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

            drawingThread = Thread {

                while (running) {

                    animationTime =
                        System.currentTimeMillis()

                    drawWallpaper()

                    try {
                        Thread.sleep(33L)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }

            drawingThread?.start()
        }

        private fun stopDrawing() {

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

                /*
                 * Background
                 */
                canvas.drawColor(bgColor)

                /*
                 * Scale based on screen width.
                 */
                val scale =
                    (width / 1080f).coerceIn(
                        0.75f,
                        1.15f
                    )

                val centerX = width / 2f

                /*
                 * Animated background.
                 */
                drawAnimatedBackground(
                    canvas,
                    width,
                    height,
                    scale
                )

                /*
                 * Main content.
                 */
                drawMainContent(
                    canvas,
                    centerX,
                    width,
                    height,
                    scale
                )

                /*
                 * Animated particles are drawn again
                 * over the content very subtly.
                 */
                drawParticles(
                    canvas,
                    width,
                    height,
                    scale
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }

        // ---------------------------------------------------------
        // MAIN UI
        // ---------------------------------------------------------

        private fun drawMainContent(
            canvas: Canvas,
            centerX: Float,
            width: Float,
            height: Float,
            scale: Float
        ) {

            /*
             * Keep content toward the upper/middle part
             * so it remains visible above the phone dock.
             */
            var y = 95f * scale

            /*
             * Main glowing heart
             */
            drawGlowingHeart(
                canvas,
                centerX,
                y,
                32f * scale
            )

            y += 52f * scale

            /*
             * Unlock text
             */
            drawText(
                canvas,
                "Look down to unlock",
                centerX,
                y,
                13f * scale,
                grey,
                false
            )

            y += 62f * scale

            /*
             * Current time
             */
            val time =
                java.text.SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(
                    java.util.Date()
                )

            drawText(
                canvas,
                time,
                centerX,
                y,
                44f * scale,
                white,
                true,
                glow = true
            )

            y += 66f * scale

            /*
             * TOGETHER FOR
             */
            drawText(
                canvas,
                "♥  TOGETHER FOR  ♥",
                centerX,
                y,
                15f * scale,
                lightPink,
                true,
                glow = true
            )

            y += 45f * scale

            /*
             * Relationship duration
             */
            val relationship =
                getPeriod(relationshipStart)

            val relationshipText =
                "${relationship.years} Years  •  " +
                "${relationship.months} Months  •  " +
                "${relationship.days} Days"

            drawText(
                canvas,
                relationshipText,
                centerX,
                y,
                28f * scale,
                white,
                true,
                glow = true
            )

            y += 34f * scale

            drawText(
                canvas,
                "Since 14 February 2006",
                centerX,
                y,
                12f * scale,
                softWhite,
                false
            )

            y += 30f * scale

            /*
             * Divider
             */
            drawFancyDivider(
                canvas,
                centerX,
                y,
                width * 0.70f,
                scale
            )

            y += 48f * scale

            /*
             * Total relationship days
             */
            val relationshipElapsed =
                getElapsedTime(
                    relationshipStart
                )

            drawText(
                canvas,
                relationshipElapsed.totalDays.toString(),
                centerX,
                y,
                34f * scale,
                white,
                true,
                glow = true
            )

            y += 21f * scale

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                y,
                9f * scale,
                grey,
                false
            )

            y += 58f * scale

            /*
             * Relationship H/M/S
             */
            drawTimeColumns(
                canvas,
                centerX,
                width,
                y,
                relationshipElapsed,
                scale
            )

            y += 88f * scale

            /*
             * GOLD MARRIAGE RINGS
             */
            drawWeddingRings(
                canvas,
                centerX,
                y,
                34f * scale
            )

            y += 70f * scale

            /*
             * MARRIED FOR
             */
            drawText(
                canvas,
                "♥  MARRIED FOR  ♥",
                centerX,
                y,
                15f * scale,
                lightPink,
                true,
                glow = true
            )

            y += 45f * scale

            /*
             * Marriage duration
             */
            val marriage =
                getPeriod(marriageStart)

            val marriageText =
                "${marriage.years} Years  •  " +
                "${marriage.months} Months  •  " +
                "${marriage.days} Days"

            drawText(
                canvas,
                marriageText,
                centerX,
                y,
                28f * scale,
                white,
                true,
                glow = true
            )

            y += 34f * scale

            drawText(
                canvas,
                "Since 09 April 2025",
                centerX,
                y,
                12f * scale,
                softWhite,
                false
            )

            y += 30f * scale

            /*
             * Marriage divider
             */
            drawFancyDivider(
                canvas,
                centerX,
                y,
                width * 0.70f,
                scale
            )

            y += 48f * scale

            /*
             * Marriage total days
             */
            val marriageElapsed =
                getElapsedTime(
                    marriageStart
                )

            drawText(
                canvas,
                marriageElapsed.totalDays.toString(),
                centerX,
                y,
                34f * scale,
                white,
                true,
                glow = true
            )

            y += 21f * scale

            drawText(
                canvas,
                "TOTAL DAYS",
                centerX,
                y,
                9f * scale,
                grey,
                false
            )

            y += 58f * scale

            /*
             * Marriage H/M/S
             */
            drawTimeColumns(
                canvas,
                centerX,
                width,
                y,
                marriageElapsed,
                scale
            )

            y += 67f * scale

            /*
             * Final message
             */
            drawText(
                canvas,
                "♥ Same People • Same Dreams ♥",
                centerX,
                y,
                14f * scale,
                lightPink,
                true,
                glow = true
            )
        }

        // ---------------------------------------------------------
        // TIME COLUMNS
        // ---------------------------------------------------------

        private fun drawTimeColumns(
            canvas: Canvas,
            centerX: Float,
            width: Float,
            y: Float,
            elapsed: ElapsedTime,
            scale: Float
        ) {

            val leftX =
                width * 0.30f

            val middleX =
                centerX

            val rightX =
                width * 0.70f

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.hours
                ),
                leftX,
                y,
                25f * scale,
                white,
                true,
                glow = true
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.minutes
                ),
                middleX,
                y,
                25f * scale,
                white,
                true,
                glow = true
            )

            drawText(
                canvas,
                String.format(
                    Locale.getDefault(),
                    "%02d",
                    elapsed.seconds
                ),
                rightX,
                y,
                25f * scale,
                white,
                true,
                glow = true
            )

            val labelY =
                y + 22f * scale

            drawText(
                canvas,
                "HOURS",
                leftX,
                labelY,
                8f * scale,
                grey,
                false
            )

            drawText(
                canvas,
                "MINUTES",
                middleX,
                labelY,
                8f * scale,
                grey,
                false
            )

            drawText(
                canvas,
                "SECONDS",
                rightX,
                labelY,
                8f * scale,
                grey,
                false
            )

            /*
             * Small vertical separators
             */
            paint.color =
                Color.argb(
                    80,
                    255,
                    255,
                    255
                )

            paint.strokeWidth =
                1f * scale

            val separatorTop =
                y - 16f * scale

            val separatorBottom =
                y + 12f * scale

            canvas.drawLine(
                width * 0.40f,
                separatorTop,
                width * 0.40f,
                separatorBottom,
                paint
            )

            canvas.drawLine(
                width * 0.60f,
                separatorTop,
                width * 0.60f,
                separatorBottom,
                paint
            )
        }

        // ---------------------------------------------------------
        // DIVIDER
        // ---------------------------------------------------------

        private fun drawFancyDivider(
            canvas: Canvas,
            centerX: Float,
            y: Float,
            lineWidth: Float,
            scale: Float
        ) {

            paint.color =
                Color.rgb(
                    55,
                    45,
                    55
                )

            paint.strokeWidth =
                1f * scale

            val left =
                centerX - lineWidth / 2f

            val right =
                centerX + lineWidth / 2f

            canvas.drawLine(
                left,
                y,
                centerX - 13f * scale,
                y,
                paint
            )

            canvas.drawLine(
                centerX + 13f * scale,
                y,
                right,
                y,
                paint
            )

            drawGlowingHeart(
                canvas,
                centerX,
                y,
                9f * scale
            )
        }

        // ---------------------------------------------------------
        // HEART
        // ---------------------------------------------------------

        private fun drawGlowingHeart(
            canvas: Canvas,
            x: Float,
            y: Float,
            size: Float
        ) {

            heartPath.reset()

            heartPath.moveTo(
                x,
                y + size * 0.85f
            )

            heartPath.cubicTo(
                x - size * 1.15f,
                y - size * 0.05f,
                x - size * 0.65f,
                y - size * 0.90f,
                x,
                y - size * 0.25f
            )

            heartPath.cubicTo(
                x + size * 0.65f,
                y - size * 0.90f,
                x + size * 1.15f,
                y - size * 0.05f,
                x,
                y + size * 0.85f
            )

            /*
             * Glow
             */
            paint.style = Paint.Style.FILL
            paint.color =
                Color.argb(
                    70,
                    255,
                    45,
                    80
                )

            paint.setShadowLayer(
                size * 0.75f,
                0f,
                0f,
                Color.argb(
                    170,
                    255,
                    40,
                    80
                )
            )

            canvas.drawPath(
                heartPath,
                paint
            )

            paint.clearShadowLayer()

            /*
             * Main heart
             */
            paint.color = pink

            canvas.drawPath(
                heartPath,
                paint
            )
        }

        // ---------------------------------------------------------
        // WEDDING RINGS
        // ---------------------------------------------------------

        private fun drawWeddingRings(
            canvas: Canvas,
            centerX: Float,
            centerY: Float,
            radius: Float
        ) {

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                radius * 0.18f

            paint.color = gold

            paint.setShadowLayer(
                radius * 0.35f,
                0f,
                0f,
                Color.argb(
                    130,
                    255,
                    180,
                    50
                )
            )

            canvas.drawCircle(
                centerX - radius * 0.40f,
                centerY,
                radius * 0.62f,
                paint
            )

            canvas.drawCircle(
                centerX + radius * 0.40f,
                centerY,
                radius * 0.62f,
                paint
            )

            paint.clearShadowLayer()

            paint.style =
                Paint.Style.FILL

            /*
             * Small heart above rings.
             */
            drawGlowingHeart(
                canvas,
                centerX,
                centerY - radius * 0.90f,
                radius * 0.22f
            )
        }

        // ---------------------------------------------------------
        // ANIMATED BACKGROUND
        // ---------------------------------------------------------

        private fun drawAnimatedBackground(
            canvas: Canvas,
            width: Float,
            height: Float,
            scale: Float
        ) {

            /*
             * Floating heart trails.
             */
            for (heart in hearts) {

                heart.phase +=
                    heart.speed

                val x =
                    heart.baseX +
                        sin(
                            heart.phase
                        ) *
                        heart.wave

                val y =
                    heart.baseY -
                        (
                            heart.phase * 15f
                        ) % (
                            height + 200f
                        )

                val alpha =
                    (
                        55 +
                            sin(
                                heart.phase * 0.8
                            ) * 35
                        )
                            .toInt()
                            .coerceIn(
                                15,
                                100
                            )

                paint.alpha = alpha

                drawGlowingHeart(
                    canvas,
                    x,
                    y,
                    heart.size * scale
                )

                paint.alpha = 255
            }

            /*
             * Curved light trails.
             */
            drawHeartTrail(
                canvas,
                width,
                height,
                scale,
                0
            )

            drawHeartTrail(
                canvas,
                width,
                height,
                scale,
                1
            )
        }

        private fun drawHeartTrail(
            canvas: Canvas,
            width: Float,
            height: Float,
            scale: Float,
            side: Int
        ) {

            val path = Path()

            if (side == 0) {

                path.moveTo(
                    -50f,
                    height * 0.28f
                )

                path.cubicTo(
                    width * 0.15f,
                    height * 0.20f,
                    width * 0.02f,
                    height * 0.52f,
                    width * 0.25f,
                    height * 0.64f
                )

            } else {

                path.moveTo(
                    width + 50f,
                    height * 0.48f
                )

                path.cubicTo(
                    width * 0.75f,
                    height * 0.60f,
                    width * 0.95f,
                    height * 0.76f,
                    width * 0.70f,
                    height * 0.90f
                )
            }

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f * scale

            paint.color =
                Color.argb(
                    95,
                    255,
                    45,
                    90
                )

            paint.setShadowLayer(
                8f * scale,
                0f,
                0f,
                Color.argb(
                    110,
                    255,
                    35,
                    80
                )
            )

            canvas.drawPath(
                path,
                paint
            )

            paint.clearShadowLayer()

            paint.style =
                Paint.Style.FILL
        }

        // ---------------------------------------------------------
        // PARTICLES
        // ---------------------------------------------------------

        private fun createParticles() {

            particles.clear()

            repeat(55) {

                particles.add(
                    Particle(
                        x = Random.nextFloat(),
                        y = Random.nextFloat(),
                        size =
                            Random.nextFloat()
                                .coerceIn(
                                    0.7f,
                                    2.2f
                                ),
                        speed =
                            Random.nextFloat()
                                .coerceIn(
                                    0.10f,
                                    0.35f
                                ),
                        phase =
                            Random.nextFloat() *
                                6.28f
                    )
                )
            }
        }

        private fun createHearts() {

            hearts.clear()

            repeat(12) {

                hearts.add(
                    FloatingHeart(
                        baseX =
                            Random.nextFloat(),
                        baseY =
                            Random.nextFloat(),
                        size =
                            Random.nextFloat()
                                .coerceIn(
                                    5f,
                                    13f
                                ),
                        wave =
                            Random.nextFloat()
                                .coerceIn(
                                    8f,
                                    30f
                                ),
                        speed =
                            Random.nextFloat()
                                .coerceIn(
                                    0.002f,
                                    0.008f
                                ),
                        phase =
                            Random.nextFloat() *
                                6.28f
                    )
                )
            }
        }

        private fun drawParticles(
            canvas: Canvas,
            width: Float,
            height: Float,
            scale: Float
        ) {

            val now =
                System.currentTimeMillis()

            for (particle in particles) {

                val pulse =
                    (
                        sin(
                            now * 0.002 *
                                particle.speed +
                                particle.phase
                        ) + 1
                    ) / 2

                val alpha =
                    (
                        25 +
                            pulse * 75
                    )
                        .toInt()
                        .coerceIn(
                            10,
                            100
                        )

                paint.color =
                    Color.argb(
                        alpha,
                        255,
                        65,
                        95
                    )

                val x =
                    particle.x * width

                var y =
                    particle.y * height

                y -=
                    (
                        now *
                            particle.speed
                    ) % height

                if (y < 0f) {
                    y += height
                }

                canvas.drawCircle(
                    x,
                    y,
                    particle.size * scale,
                    paint
                )
            }
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
            bold: Boolean,
            glow: Boolean = false
        ) {

            paint.style =
                Paint.Style.FILL

            paint.textAlign =
                Paint.Align.CENTER

            paint.textSize =
                size

            paint.typeface =
                if (bold) {
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )
                } else {
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.NORMAL
                    )
                }

            paint.color =
                color

            if (glow) {

                paint.setShadowLayer(
                    size * 0.16f,
                    0f,
                    0f,
                    Color.argb(
                        100,
                        255,
                        55,
                        90
                    )
                )
            }

            canvas.drawText(
                text,
                x,
                y,
                paint
            )

            paint.clearShadowLayer()
        }

        // ---------------------------------------------------------
        // DATE CALCULATIONS
        // ---------------------------------------------------------

        private fun getPeriod(
            startDate: LocalDate
        ): java.time.Period {

            val today =
                LocalDate.now()

            return java.time.Period.between(
                startDate,
                today
            )
        }

        private fun getElapsedTime(
            startDate: LocalDate
        ): ElapsedTime {

            val zone =
                ZoneId.systemDefault()

            val start =
                startDate.atStartOfDay(
                    zone
                )

            val now =
                java.time.ZonedDateTime.now(
                    zone
                )

            val duration =
                Duration.between(
                    start,
                    now
                )

            val totalSeconds =
                duration.seconds.coerceAtLeast(
                    0L
                )

            val totalDays =
                totalSeconds / 86400L

            val remaining =
                totalSeconds % 86400L

            val hours =
                remaining / 3600L

            val remainingAfterHours =
                remaining % 3600L

            val minutes =
                remainingAfterHours / 60L

            val seconds =
                remainingAfterHours % 60L

            return ElapsedTime(
                totalDays = totalDays,
                hours = hours,
                minutes = minutes,
                seconds = seconds
            )
        }
    }

    // -------------------------------------------------------------
    // DATA CLASSES
    // -------------------------------------------------------------

    private data class ElapsedTime(
        val totalDays: Long,
        val hours: Long,
        val minutes: Long,
        val seconds: Long
    )

    private data class Particle(
        val x: Float,
        val y: Float,
        val size: Float,
        val speed: Float,
        val phase: Float
    )

    private data class FloatingHeart(
        val baseX: Float,
        val baseY: Float,
        val size: Float,
        val wave: Float,
        val speed: Float,
        var phase: Float
    )
}
