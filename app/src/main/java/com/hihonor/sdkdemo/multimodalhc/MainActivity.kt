/*
 * Copyright (c) Honor Device Co., Ltd. 2024-2024. All rights reserved.
 */

package com.hihonor.sdkdemo.multimodalhc


import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hihonor.sdkdemo.multimodalhc.interfaces.OnMessageListener
import com.hihonor.sdkdemo.multimodalhc.receiver.MessageReceiver
import com.hihonor.sdkdemo.multimodalhc.services.McsService
import com.hihonor.sdkdemo.multimodalhc.ui.theme.MultimodalHCTheme
import com.hihonor.sdkdemo.multimodalhc.viewmodel.MainViewModel

/**
 * Main page
 */
class MainActivity : ComponentActivity() {

    private val viewModel : MainViewModel by viewModels()

    private lateinit var mReceiver: MessageReceiver

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultimodalHCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        receiverMsg = viewModel.receiverMessage,
                        bindMsg = viewModel.bindMessage
                    )
                }
            }
        }
        mReceiver = MessageReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mReceiver, IntentFilter().apply {
                addAction(McsService.ACTION_MSG_BIND)
                addAction(McsService.ACTION_MSG_UNBIND)
                addAction(McsService.ACTION_MSG_DATA)
            }, RECEIVER_EXPORTED)
        }

        mReceiver.setOnMessageListener(object : OnMessageListener {
            override fun onReceiverMessageListener(str: String) {
                viewModel.updateReceiverMessage(str)
            }

            override fun onServiceBindStatusListener(str: String) {
                viewModel.updateBindMessage(str)
            }

        })

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

}

@Preview
@Composable
fun CardTitle() {
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier.size(330.dp, height = 160.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(text = "多模人机交互-测试Demo",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "此Demo用于测试多模人机交互SDK：当开启智慧助手->YOYO助理->语音控制视频->打开语音控制视频开关，将手机静置一会，数据显示见\"当前服务状态\"" ,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontStyle = FontStyle.Italic,
                color = Color.Gray)
        }
    }
}


@Preview
@Composable
fun ShowDataCompose (
    bindMsg: State<String> ?= null,
    receiverMsg: State<String> ?= null
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp , Color.Gray),
        modifier = Modifier
            .size(330.dp, height = 380.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row {
                Text(text = "绑定状态：", fontWeight = FontWeight.Bold,
                    fontSize = 18.sp)
                Text(text = "${if (bindMsg?.value?.isEmpty() ==  true) "未绑定" else bindMsg?.value}" ,
                    fontSize = 18.sp,
                    color = if (bindMsg?.value?.isEmpty() ==  true) Color.Gray else Color.Green)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "接收到的数据：",fontSize = 16.sp , fontWeight = FontWeight.Bold)
            Text(text = "${receiverMsg?.value}" ,fontSize = 15.sp , color = Color.DarkGray)

        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier,
    bindMsg: State<String> ?= null,
    receiverMsg: State<String> ?= null
) {
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(scrollState, true)
            .padding(20.dp)
    ) {
        CardTitle()
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "当前服务状态",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black)
        Spacer(modifier = Modifier.height(5.dp))
        ShowDataCompose(bindMsg, receiverMsg)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MultimodalHCTheme {
        Greeting(modifier = Modifier.fillMaxSize())
    }
}