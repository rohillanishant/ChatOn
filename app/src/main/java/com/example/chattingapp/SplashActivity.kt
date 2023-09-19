@file:Suppress("DEPRECATION")

package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingapp.ui.theme.AppColor
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.google.android.material.dialog.MaterialDialogs

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
            Handler().postDelayed({
                // Start the next activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Greeting() {
    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center, modifier = Modifier
        .background(
            brush = Brush.verticalGradient(
                listOf(
                    Color(255, 255, 255, 255), AppColor)
            )
        )
        .padding(40.dp, 30.dp)) {
        Text(text ="ChatOn 🗨️",
            color= Color(99, 123, 158, 255),
            fontWeight = FontWeight.W500,
            fontSize = 40.sp
        )
        Text(text = "Let's Chat!",
            color = Color.Red,
            fontWeight = FontWeight.W500
        )
    }
}