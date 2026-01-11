package com.dungz.applocker.util

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.createBitmap
import java.security.MessageDigest

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

object PasswordHasher {
    private const val HASH_PREFIX = "SHA256:"

    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        val hashString = hashBytes.joinToString("") { "%02x".format(it) }
        return "$HASH_PREFIX$hashString"
    }

    fun verifyPassword(inputPassword: String, storedPassword: String): Boolean {
        return if (storedPassword.startsWith(HASH_PREFIX)) {
            // Password is hashed, compare hashes
            hashPassword(inputPassword) == storedPassword
        } else {
            // Legacy plain text password (for migration)
            inputPassword == storedPassword
        }
    }

    fun isHashed(password: String): Boolean = password.startsWith(HASH_PREFIX)
}