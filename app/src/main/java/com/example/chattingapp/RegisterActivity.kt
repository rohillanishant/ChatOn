package com.example.chattingapp

import android.content.Intent
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingapp.model.User
import com.example.chattingapp.ui.theme.AppColor
import com.example.chattingapp.ui.theme.ChattingAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.*

class RegisterActivity : ComponentActivity() {
   // lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContent {
            ChattingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    register()
                }
            }
        }
    }
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    @SuppressLint("UnrememberedMutableState")
//    @Composable
//    fun chooseDate(context: Context) {
//        val year: Int
//        val month: Int
//        val day: Int
//        val calendar = Calendar.getInstance()
//        year = calendar.get(Calendar.YEAR)
//        month = calendar.get(Calendar.MONTH)
//        day = calendar.get(Calendar.DAY_OF_MONTH)
//        calendar.time = Date()
//        val dialog = DatePickerDialog(
//            context,
//            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
//                val date = "$dayOfMonth/${month+1}/$year"
//                sharedPreferences.edit().putString("dob","Date of Birth :$date").apply()
//            },
//            year,
//            month,
//            day
//        )
//        dialog.show()
//      //  Toast.makeText(this@RegisterActivity, date, Toast.LENGTH_SHORT).show()
//    }
    fun signUp(username:String,name:String,email: String,phoneNumber:String, password: String,confirmPassword:String) {
        var capital:Boolean=false
        var small:Boolean=false
        var specialChar : Boolean=false
        var number:Boolean=false
        var isSameUsername:Boolean=false
        val dRef= FirebaseDatabase.getInstance().getReference("User")
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postsnapshot in snapshot.children){
                    val username2= postsnapshot.child("userName").value.toString()
                    if(username==username2){
                        isSameUsername=true
                        break
                    }
                }
                if(isSameUsername){
                    Toast.makeText(this@RegisterActivity,"This username already belongs to a user,Please enter another one!",
                        Toast.LENGTH_SHORT).show()
                } else if(username.isEmpty()){
                    Toast.makeText(this@RegisterActivity,"Enter username", Toast.LENGTH_SHORT).show()
                }else if(name.isEmpty())
                    Toast.makeText(this@RegisterActivity,"Enter Name", Toast.LENGTH_SHORT).show()
                else if(name.length<4) {
                    Toast.makeText(this@RegisterActivity,"Name should contain at least 3 characters",
                        Toast.LENGTH_SHORT).show()
                }
                else if(email.isEmpty())
                    Toast.makeText(this@RegisterActivity,"Enter Email Id", Toast.LENGTH_SHORT).show()
                else if(!email.contains('@') || !email.contains(".")){
                    Toast.makeText(this@RegisterActivity,"Invalid email", Toast.LENGTH_SHORT).show()
                }
                else if(phoneNumber.isEmpty())
                    Toast.makeText(this@RegisterActivity,"Enter Phone Number", Toast.LENGTH_LONG).show()
                else if(phoneNumber.length!=10) {
                    Toast.makeText(this@RegisterActivity,"Phone number must contain exactly 10 digits",
                        Toast.LENGTH_LONG).show()
                }
                else if(password.isEmpty())
                    Toast.makeText(this@RegisterActivity,"Enter Password", Toast.LENGTH_LONG).show()
                else if(password!=confirmPassword)
                    Toast.makeText(this@RegisterActivity,"Passwords doesn't match. Please try again!",
                        Toast.LENGTH_LONG).show()
                else if (password.length<4) {
                    Toast.makeText(this@RegisterActivity, "Password size should be more than 4", Toast.LENGTH_LONG).show()
                }else {
                    for(i in password.indices){
                        if(password[i].isUpperCase()){
                            capital=true
                        }
                        if(password[i].isLowerCase()){
                            small=true
                        }
                        if(password[i].isDigit()){
                            number=true
                        }
                        if(password[i]>='!' && password[i]<='~' && !password[i].isLetterOrDigit()){
                            specialChar=true
                        }
                    }
                    if(!small){
                        Toast.makeText(this@RegisterActivity,"Password must contain a small letter (a,b,..z)",
                            Toast.LENGTH_SHORT).show()
                    }else if(!capital){
                        Toast.makeText(this@RegisterActivity,"Password must contain a Capital letter (A,B..Z",
                            Toast.LENGTH_SHORT).show()
                    }else if(!number){
                        Toast.makeText(this@RegisterActivity,"Password must contain a numeric digit (0,1,..9)",
                            Toast.LENGTH_SHORT).show()
                    }else if(!specialChar){
                        Toast.makeText(this@RegisterActivity,"Password must contain a special Character (!,@,#..)",
                            Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this@RegisterActivity,"Registered", Toast.LENGTH_SHORT).show()
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign-up successful, user is now signed in]
                                    dbRef= FirebaseDatabase.getInstance().reference
                                    dbRef.child("User").child(auth.currentUser?.uid!!).setValue(User(auth.currentUser?.uid!!,username,"",name,email,phoneNumber,null))
                                    Toast.makeText(this@RegisterActivity,"Registered", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                                    // You can perform further actions here, such as updating user profile, sending verification email, etc.
                                } else {
                                    // Handle sign-up errors
                                    val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                                    val errorMessage = task.exception?.message
                                    Toast.makeText(this@RegisterActivity,"$errorCode $errorMessage",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //Log.e("Firebase", "Error reading data: ${error.message}")
                Toast.makeText(this@RegisterActivity,"${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    @Preview
    @Composable
    fun register() {
        var username by remember {
            mutableStateOf("")
        }
        var name by remember {
            mutableStateOf("")
        }
        var phoneNumber by remember {
            mutableStateOf("")
        }
        var email by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var confirmPassword by remember {
            mutableStateOf("")
        }
        var isVisible1 by remember {
            mutableStateOf(false)
        }
        var isVisible2 by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.background(brush= Brush
            .verticalGradient(colors =
            listOf(
                Color(255, 255, 255, 255), AppColor
            )
            )
        ), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.material.Text(text = "Create Account",
                color= Color(99, 123, 158, 255),
                fontSize = 40.sp,
                fontWeight = FontWeight.W500
            )
            for(i in 0..3){
                OutlinedTextField(
                    value = when(i){
                        0->username
                        1->name
                        2->email
                        else->phoneNumber
                    },
                    onValueChange = {
                        when(i){
                            0->username=it
                            1->name=it
                            2->email=it
                            else-> if(it.length<=10) phoneNumber=it
                        }
                    },
                    maxLines = 1,
                    leadingIcon = {
                        when(i){
                            0-> Icon(Icons.Default.Person, contentDescription = "person")
                            1-> Icon(Icons.Default.Person, contentDescription = "person")
                            2-> Icon(Icons.Default.Email, contentDescription = "email")
                            else-> Icon(Icons.Default.Phone, contentDescription = "phone")
                        }
                    },
                    keyboardOptions = when(i){
                        0-> KeyboardOptions(keyboardType = KeyboardType.Text)
                        1-> KeyboardOptions(keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words)
                        2-> KeyboardOptions(keyboardType = KeyboardType.Email)
                        else-> KeyboardOptions(keyboardType = KeyboardType.Phone)
                    },
                    label = {
                        androidx.compose.material.Text(text = when(i){
                            0->"username"
                            1->"Name"
                            2->"Email"
                            else->"Phone Number"
                        })
                    },
                    placeholder = {
                        androidx.compose.material.Text(text = when(i){
                            0->"Enter Username"
                            1->"Enter Name"
                            2->"Enter Email"
                            else->"Enter Phone Number"
                        })
                    },
                    modifier = Modifier.padding(top=20.dp)
                )
            }
//            var chosenDate by remember {
//                mutableStateOf("Click to Choose Date of Birth")
//            }
//            var isSelected by remember {
//                mutableStateOf(false)
//            }
//            if(isSelected){
//                chooseDate(context = this@RegisterActivity)
//                chosenDate= sharedPreferences.getString("dob","Choose Date of Birth").toString()
//                isSelected=false
//            }
//            Card(modifier = Modifier
//                .fillMaxWidth(1f)
//                .height(60.dp)
//                .align(Alignment.CenterHorizontally)
//                .padding(start = 50.dp,top=20.dp, end = 50.dp)
//                .clickable { isSelected = true },
//                backgroundColor = Color.LightGray,
//                elevation = 5.dp,
//                shape = RoundedCornerShape(25.dp)
//            ){
//                Row(){
//                    Icon(imageVector = Icons.Default.DateRange, contentDescription ="dob" ,Modifier.size(30.dp).padding(start=10.dp,top=10.dp))
//                      //  Toast.makeText(this@RegisterActivity,chosenDate,Toast.LENGTH_SHORT).show()
//                    chosenDate= sharedPreferences.getString("dob","Choose Date of Birth").toString()
//                    Text(text=chosenDate, modifier = Modifier.padding(start=10.dp,top=10.dp))
//                }
//            }
            for(i in 0..1){
                OutlinedTextField(
                    value = if(i==0) password
                    else confirmPassword,
                    onValueChange = {if(i==0) password=it
                    else confirmPassword=it
                    },
                    maxLines = 1,
                    visualTransformation =
                    if(i==0) {
                        if (isVisible1) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    }else {
                        if (isVisible2) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "password")
                    },
                    trailingIcon = {
                        if(i==0) {
                            if(isVisible1){
                                IconButton(onClick = { isVisible1=false }) {
                                    Icon(painter = painterResource(id = R.drawable.eye), contentDescription = "visible",
                                        modifier = Modifier.size(25.dp))
                                }
                            }else{
                                IconButton(onClick = { isVisible1=true }) {
                                    Icon(painter = painterResource(id = R.drawable.invisible), contentDescription = "invisible",
                                        modifier = Modifier.size(25.dp))
                                }
                            }
                        }else {
                            if(isVisible2){
                                IconButton(onClick = { isVisible2=false }) {
                                    Icon(painter = painterResource(id = R.drawable.eye), contentDescription = "visible",
                                        modifier = Modifier.size(25.dp))
                                }
                            }else{
                                IconButton(onClick = { isVisible2=true }) {
                                    Icon(painter = painterResource(id = R.drawable.invisible), contentDescription = "invisible",
                                        modifier = Modifier.size(25.dp))
                                }
                            }
                        }
                    },
                    label = {
                        androidx.compose.material.Text(text = when(i){
                            0->"Password"
                            else->"Confirm Password"
                        }
                        )
                    },
                    placeholder = {
                        androidx.compose.material.Text(text = when(i){
                            0->"Enter password"
                            else->"Enter password Again"
                        }
                        )
                    },
                    modifier = Modifier.padding(top=20.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
            }
            OutlinedButton(onClick = {
              //  sharedPreferences.edit().clear().apply()
                signUp(username,name,email,phoneNumber,password,confirmPassword)
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
                androidx.compose.material.Text(text = "Register", color = Color.White)
            }
        }
    }
}