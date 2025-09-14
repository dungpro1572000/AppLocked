package com.dungz.applocker.util

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalSnackbar {
    private val _message = MutableStateFlow("")
    val message get() = _message

    fun setMessage(msg: String) {
        _message.value = msg
    }
}