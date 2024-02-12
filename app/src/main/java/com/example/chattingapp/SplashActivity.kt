@file:Suppress("DEPRECATION")

package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
        .fillMaxSize(1f)
        .background(
            brush = Brush.verticalGradient(
                listOf(
                    Color(255, 255, 255, 255), AppColor
                )
            )
        )
    //    .padding(40.dp, 30.dp)
            )
    {
        var animate by remember {
            mutableStateOf(false)
        }
        val n by animateIntAsState(targetValue = if(animate) 300 else 0)
        Handler().postDelayed({
            // Start the next activity
            animate=true;
        }, 1000)
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "Logo",
            modifier = Modifier.size(n.dp).align(Alignment.CenterHorizontally))


    }
}