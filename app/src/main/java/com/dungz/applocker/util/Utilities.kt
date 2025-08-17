package com.dungz.applocker.util

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.createBitmap

fun drawableToBitmapPainter(drawable: Drawable): BitmapPainter? {
    val bitmap = when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        else -> {
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
            createBitmap(width, height).also { bmp ->
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
    }
    return bitmap?.asImageBitmap()?.let { BitmapPainter(it) }
}