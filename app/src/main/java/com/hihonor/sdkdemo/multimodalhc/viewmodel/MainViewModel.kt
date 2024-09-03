/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.sdkdemo.multimodalhc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _receivedMassage = mutableStateOf("")
    val receiverMessage = _receivedMassage

    private val _bindMessage = mutableStateOf("")
    val bindMessage = _bindMessage


    fun updateReceiverMessage(message : String) {
        _receivedMassage.value = message
    }

    fun updateBindMessage(message: String) {
        _bindMessage.value = message
    }

}