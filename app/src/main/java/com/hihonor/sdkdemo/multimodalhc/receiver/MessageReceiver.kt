/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.sdkdemo.multimodalhc.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.util.Log
import com.hihonor.sdkdemo.multimodalhc.const.Const
import com.hihonor.sdkdemo.multimodalhc.interfaces.OnMessageListener
import com.hihonor.sdkdemo.multimodalhc.services.McsService

class MessageReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MessageReceiver"
    }

    private var listener : OnMessageListener?= null

    fun setOnMessageListener(listener: OnMessageListener){
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            when (it.action) {
                McsService.ACTION_MSG_BIND -> {
                    listener?.onServiceBindStatusListener("服务已绑定")
                    Log.i(TAG, "onReceive: the service has connected")
                }
                McsService.ACTION_MSG_UNBIND -> {
                    listener?.onServiceBindStatusListener("服务断开")
                    Log.i(TAG, "onReceive: the service has disconnected")
                }
                McsService.ACTION_MSG_DATA -> {
                    Log.i(TAG, "onReceive: receive data from service")
                    handlerReceiveData(intent)
                }
                else -> {}
            }
        }
    }

    private fun handlerReceiveData(intent:Intent?) {
        val what = intent?.getIntExtra("what",-1)
        val sendingUid = intent?.getIntExtra("sendingUid" , -1)
        val msg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra("msg", Message::class.java)
        } else {
            intent?.getParcelableExtra("msg")
        }
        val sb = StringBuilder()
        sb.append("${what}, sendingUid = $sendingUid , msg = $msg ,")
        val resultMsg = when (what){
            Const.TYPE_PLAY-> sb.append("播放").toString()
            Const.TYPE_PAUSE -> sb.append("暂停").toString()
            Const.TYPE_FAVORITE->sb.append("收藏").toString()
            Const.TYPE_LAST-> sb.append("切换到上一个视频").toString()
            Const.TYPE_DOWN_SHARPNESS -> sb.append("调低灵敏度").toString()
            Const.TYPE_ENTER_FULL_SCREEN -> sb.append("进入全屏").toString()
            Const.TYPE_EXIT_FULL_SCREEN -> sb.append("退出全屏").toString()
            Const.TYPE_FAST_FORWARD -> sb.append("倍速播放").toString()
            Const.TYPE_FOLLOW -> sb.append("关注").toString()
            Const.TYPE_JUMP_TO -> sb.append("视频跳转到XXX").toString()
            Const.TYPE_LIKE -> sb.append("点赞").toString()
            Const.TYPE_NEXT -> sb.append("切换到下一个视频").toString()
            Const.TYPE_OPEN_EPISODE -> sb.append("播放某一集").toString()
            Const.TYPE_PLAY_OR_PAUSE -> sb.append("切换当前播放或暂停状态").toString()
            Const.TYPE_REPLAY -> sb.append("重播").toString()
            Const.TYPE_SET_SHARPNESS -> sb.append("设置清晰度").toString()
            Const.TYPE_STEP_BACKWARD -> sb.append("快退").toString()
            Const.TYPE_STEP_FORWARD -> sb.append("快进").toString()
            Const.TYPE_UP_SHARPNESS -> sb.append("清晰度（向上）").toString()
            else-> ""
        }
        listener?.onReceiverMessageListener(resultMsg)
    }

}