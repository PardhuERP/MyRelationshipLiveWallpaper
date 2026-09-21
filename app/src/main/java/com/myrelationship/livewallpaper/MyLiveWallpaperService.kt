package com.myrelationship.livewallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class MyLiveWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine : Engine() {

        private var running = false
        private var thread: Thread? = null

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                startDrawing()
            } else {
                stopDrawing()
            }
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

                canvas.drawColor(Color.rgb(25, 30, 38))

                paint.color = Color.WHITE
                paint.textAlign = Paint.Align.CENTER

                // Lock
                paint.textSize = 42f
                canvas.drawText(
                    "🔒",
                    canvas.width / 2f,
                    100f,
                    paint
                )

                // Unlock text
                paint.textSize = 28f
                canvas.drawText(
                    "Look down to unlock",
                    canvas.width / 2f,
                    150f,
                    paint
                )

                // Current time
                val time = java.text.SimpleDateFormat(
                    "HH:mm:ss",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())

                paint.textSize = 80f

                canvas.drawText(
                    time,
                    canvas.width / 2f,
                    270f,
                    paint
                )

                // Relationship
                paint.textSize = 28f

                canvas.drawText(
                    "♥  together for  ♥",
                    canvas.width / 2f,
                    360f,
                    paint
                )

                paint.textSize = 48f

                canvas.drawText(
                    "0y, 0m, 0d",
                    canvas.width / 2f,
                    430f,
                    paint
                )

                paint.textSize = 24f

                canvas.drawText(
                    "Since 15 Jan 2024",
                    canvas.width / 2f,
                    475f,
                    paint
                )

                // Seconds counter
                paint.textSize = 42f

                canvas.drawText(
                    "987",
                    canvas.width / 2f,
                    570f,
                    paint
                )

                paint.textSize = 20f

                canvas.drawText(
                    "DAYS     HOURS     MIN",
                    canvas.width / 2f,
                    610f,
                    paint
                )

                paint.textSize = 20f

                canvas.drawText(
                    "♥ Same People Same Dreams ♥",
                    canvas.width / 2f,
                    700f,
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
