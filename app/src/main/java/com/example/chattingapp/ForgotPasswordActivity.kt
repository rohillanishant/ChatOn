package com.example.chattingapp

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChattingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    forgot()
                }
            }
        }
        auth = FirebaseAuth.getInstance()
    }
    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password reset email sent successfully og.d(TAG, "resetPassword:success")
                    Toast.makeText(this@ForgotPasswordActivity,"Link sent to registered email ID",Toast.LENGTH_SHORT).show()

                    // Display a success message to the user or navigate to a different screen
                } else {
                    // Failed to send password reset email
                    Toast.makeText(this@ForgotPasswordActivity,"Failed",Toast.LENGTH_SHORT).show()

                    // Display an error message to the user or handle the failure gracefully
                }
            }
    }
    @Preview(showBackground = true)
    @Composable
    fun forgot() {
        var email by remember {
            mutableStateOf("")
        }
        Column(modifier = Modifier.background(brush= Brush
            .verticalGradient(colors = listOf(
                Color(255, 255, 255, 255),
                Color(22,139,179,37)
            )
            )
        ), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = "ChatOn 🗨️",
                color= Color(99, 123, 158, 255),
                fontSize = 40.sp,
                fontWeight = FontWeight.W500
            )
            Text(text = "Enter your email",
                color= Color(99, 123, 158, 255),
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                modifier = Modifier.padding(top=50.dp,start=63.dp, bottom = 8.dp).align(Alignment.Start)
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "email")
                },
                label = {
                    Text(text = "Email")
                },
                placeholder = {
                    Text(text = "Enter Email")
                },
                isError = email.isEmpty() || !email.contains('@') || !email.contains(".com")
            )
            OutlinedButton(onClick = {
                resetPassword(email)
            },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 30.dp)
                    .clip(
                        CircleShape
                    ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Red
                )) {
                Text(text = "Next", color = Color.White)
            }
        }
    }
}
