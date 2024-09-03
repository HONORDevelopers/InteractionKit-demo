/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.sdkdemo.multimodalhc.services

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import com.hihonor.sdkdemo.multimodalhc.const.Const
import java.lang.ref.WeakReference

/**
 *  this service works : Receiver message from Interaction Information
 */
class McsService : Service() {
    companion object {
        private const val TAG = "McsService"
        const val ACTION_MSG_BIND = "action_msg_bind"
        const val ACTION_MSG_UNBIND = "action_msg_unbind"
        const val ACTION_MSG_DATA = "action_msg_data"
    }

    // service's Messenger
    lateinit var mMessenger: Messenger

    class MessengerHandler(
        service: McsService,
    ) : Handler(Looper.getMainLooper()) {
        private var weakReference: WeakReference<McsService>? = null

        init {
            weakReference = WeakReference(service)
        }

        override fun handleMessage(msg: Message) {
            val sendingUid = msg.sendingUid
            // 此处可以通过sendingUid进行安全校验
            Log.i(TAG, "MessengerHandler = ${msg.what} , msg.sendingUid = ${msg.sendingUid} , msg = $msg")
            weakReference?.get()?.sendBroadcast(
                Intent(ACTION_MSG_DATA).apply {
                    putExtra("what", msg.what)
                    putExtra("sendingUid", msg.sendingUid)
                    putExtra("msg", msg)
                },
            )
            // relpy to system messages
            relpyMessage(msg)
            super.handleMessage(msg)
        }

        /**
         * 将结果发送给系统侧
         */
        private fun relpyMessage(msg: Message) {
            val eventId = msg.what
            val messenger = msg.replyTo
            val relpyMessage = Message.obtain()
            relpyMessage.what = Const.RESULT_OK
            relpyMessage.data =
                Bundle().apply {
                    putInt("event_id", eventId)
                    putString("result_msg", "OK ${Const.RESULT_OK}")
                }
            try {
                messenger.send(relpyMessage)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "onBind: ")
        sendBroadcast(Intent(ACTION_MSG_BIND))
        mMessenger = Messenger(MessengerHandler(this))
        return mMessenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind: ")
        sendBroadcast(Intent(ACTION_MSG_UNBIND))
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
    }
}
