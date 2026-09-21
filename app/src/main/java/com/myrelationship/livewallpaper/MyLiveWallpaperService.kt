package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var thread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            super.onVisibilityChanged(isVisible)

            if (isVisible) {
                startDrawing()
            } else {
                stopDrawing()
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startDrawing()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            stopDrawing()
            super.onSurfaceDestroyed(holder)
        }

        private fun startDrawing() {

            if (running) return

            running = true

            thread = Thread {

                while (running) {

                    drawWallpaper()

                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        break
                    }
                }
            }

            thread?.start()
        }

        private fun stopDrawing() {

            running = false

            thread?.interrupt()
            thread = null
        }

        private fun drawWallpaper() {

            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {

                canvas = holder.lockCanvas()

                if (canvas == null) return

                val centerX = canvas.width / 2f

                // Background
                canvas.drawColor(Color.rgb(15, 18, 24))

                // -------------------------
                // LOCK
                // -------------------------

                paint.color = Color.WHITE
                paint.textSize = 32f

                canvas.drawText(
                    "🔒",
                    centerX,
                    85f,
                    paint
                )

                paint.color = Color.rgb(150, 155, 165)
                paint.textSize = 18f

                canvas.drawText(
                    "Look down to unlock",
                    centerX,
                    115f,
                    paint
                )

                // -------------------------
                // CURRENT TIME
                // -------------------------

                paint.color = Color.WHITE
                paint.textSize = 36f

                val time = SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                canvas.drawText(
                    time,
                    centerX,
                    165f,
                    paint
                )

                // -------------------------
                // TOGETHER
                // -------------------------

                paint.color = Color.rgb(255, 55, 80)
                paint.textSize = 15f

                canvas.drawText(
                    "♥  TOGETHER FOR  ♥",
                    centerX,
                    215f,
                    paint
                )

                // -------------------------
                // RELATIONSHIP TIME
                // -------------------------

                paint.color = Color.WHITE
                paint.textSize = 26f

                canvas.drawText(
                    "2 Years  •  8 Months  •  6 Days",
                    centerX,
                    250f,
                    paint
                )

                // -------------------------
                // SINCE
                // -------------------------

                paint.color = Color.rgb(130, 135, 145)
                paint.textSize = 13f

                canvas.drawText(
                    "Since 15 January 2024",
                    centerX,
                    280f,
                    paint
                )

                // -------------------------
                // DIVIDER
                // -------------------------

                paint.color = Color.rgb(55, 60, 70)
                paint.strokeWidth = 1f

                canvas.drawLine(
                    centerX - 220f,
                    305f,
                    centerX + 220f,
                    305f,
                    paint
                )

                // -------------------------
                // TOTAL DAYS
                // -------------------------

                paint.color = Color.WHITE
                paint.textSize = 28f

                canvas.drawText(
                    "980",
                    centerX,
                    340f,
                    paint
                )

                paint.color = Color.rgb(130, 135, 145)
                paint.textSize = 11f

                canvas.drawText(
                    "TOTAL DAYS",
                    centerX,
                    360f,
                    paint
                )

                // -------------------------
                // HOURS / MINUTES / SECONDS
                // -------------------------

                paint.color = Color.WHITE
                paint.textSize = 18f

                canvas.drawText(
                    "16",
                    centerX - 150f,
                    405f,
                    paint
                )

                canvas.drawText(
                    "12",
                    centerX,
                    405f,
                    paint
                )

                canvas.drawText(
                    "43",
                    centerX + 150f,
                    405f,
                    paint
                )

                paint.color = Color.rgb(130, 135, 145)
                paint.textSize = 10f

                canvas.drawText(
                    "HOURS",
                    centerX - 150f,
                    425f,
                    paint
                )

                canvas.drawText(
                    "MINUTES",
                    centerX,
                    425f,
                    paint
                )

                canvas.drawText(
                    "SECONDS",
                    centerX + 150f,
                    425f,
                    paint
                )

                // -------------------------
                // MESSAGE
                // -------------------------

                paint.color = Color.rgb(255, 55, 80)
                paint.textSize = 14f

                canvas.drawText(
                    "♥ Same People • Same Dreams ♥",
                    centerX,
                    475f,
                    paint
                )

            } finally {

                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}
