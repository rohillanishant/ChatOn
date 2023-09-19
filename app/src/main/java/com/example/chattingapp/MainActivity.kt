package com.example.chattingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        if(isLoggedIn){
            startActivity(Intent(this@MainActivity,HomeActivity::class.java))
        }
        setContent {
            ChattingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    login()
                }
            }
        }
        auth = FirebaseAuth.getInstance()
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                    val intent=Intent(this@MainActivity,HomeActivity::class.java)
                    intent.putExtra("userid",auth.currentUser?.uid!!)
                    startActivity(intent)
                  Toast.makeText(this@MainActivity,"Logged In",Toast.LENGTH_SHORT).show()
                    // Proceed to the next screen or perform any desired action
                } else {
                    Toast.makeText(this@MainActivity,"Wrong Credentials",Toast.LENGTH_SHORT).show()
                    // Display an error message to the user or handle the failure gracefully
                }
            }
    }
    @Preview
    @Composable
    fun login(){
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var isVisible by remember {
            mutableStateOf(false)
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

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLines = 1,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "person")
                },
                label = {
                    Text(text = "Email")
                },
                placeholder = {
                    Text(text = "Enter email")
                },
                modifier = Modifier
                    .padding(top = 50.dp)
                    .clip(shape = RoundedCornerShape(5.dp)),
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                maxLines=1,
                visualTransformation = if(isVisible){
                    VisualTransformation.None
                }else{
                    PasswordVisualTransformation()
                     },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "password")
                },
                trailingIcon = {
                    if(isVisible){
                        IconButton(onClick = { isVisible=false }) {
                            Icon(painter = painterResource(id = R.drawable.eye), contentDescription = "visible",
                            modifier = Modifier.size(25.dp))
                        }
                    }else{
                        IconButton(onClick = { isVisible=true }) {
                            Icon(painter = painterResource(id = R.drawable.invisible), contentDescription = "invisible",
                            modifier = Modifier.size(25.dp))
                        }
                    }
                },
                label = {
                    Text(text = "Password")
                },
                placeholder = {
                    Text(text = "Enter password")
                },
                modifier = Modifier.padding(top=20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            OutlinedButton(

                onClick = {
                    if(email.isEmpty()){
                        Toast.makeText(this@MainActivity,"Enter username",Toast.LENGTH_SHORT).show()
                    }else if(password.isEmpty()){
                        Toast.makeText(this@MainActivity,"Enter Password",Toast.LENGTH_SHORT).show()
                    }else{
                        loginUser(email,password)
                    }
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
                Text(text = "Login", color = Color.White)
            }
            Text(text="Forgot Password?",
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier=Modifier.clickable { startActivity(Intent(this@MainActivity,ForgotPasswordActivity::class.java)) }
            )
            Text(
                text = "New User? Register Here",
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable {
                        startActivity(
                            Intent(
                                this@MainActivity,
                                RegisterActivity::class.java
                            )
                        )
                    }
            )
        }
    }

}
