package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chattingapp.model.MenuItem
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import kotlin.coroutines.coroutineContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

val auth: FirebaseAuth = FirebaseAuth.getInstance()
fun saveDetails(username: String, name: String, phoneNumber: String,profilePhoto: Uri?, callback: (String) -> Unit) {
    var isSameUsername: Boolean = false
    val dRef = FirebaseDatabase.getInstance().getReference("User")
    val uid = auth.currentUser?.uid!!

    dRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            for (postsnapshot in snapshot.children) {
                val username2 = postsnapshot.child("userName").value.toString()
                if (username == username2 && uid != postsnapshot.child("userId").value.toString()) {
                    isSameUsername = true
                    break
                }
            }
            if (isSameUsername) {
                callback("This Username already belongs to someone. Use another one")
            } else if (username.isEmpty()) {
                callback("Enter username")
            } else if (name.isEmpty()) {
                callback("Enter Name")
            } else if (name.length < 4) {
                callback("Name should contain at least 3 characters")
            } else if (phoneNumber.isEmpty()) {
                callback("Enter Phone Number")
            } else if (phoneNumber.length != 10) {
                callback("Phone number must contain exactly 10 digits")
            } else {
                val dbRef = FirebaseDatabase.getInstance().reference.child("User")
                    .child(auth.currentUser?.uid!!)
                dbRef.child("name").setValue(name)
                dbRef.child("userName").setValue(username)
                dbRef.child("phoneNumber").setValue(phoneNumber)
                if(profilePhoto!=null){
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReference("profilePhoto/${auth.currentUser?.uid!!}");
                    val uploadTask = storageRef.putFile(profilePhoto)
                    uploadTask.addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                            val downloadUrl = downloadUri.toString()
                            dbRef.child("profilePhoto").setValue(downloadUrl)
                            // TODO: Save the downloadUrl to the user's profile or database
                        }
                    }.addOnFailureListener {
                        callback("Failed to upload profile Photo")
                    }
                }

                callback("") // Success, no error message
            }
        }
        override fun onCancelled(error: DatabaseError) {
            callback("Error occurred: ${error.message}") // Report any errors
        }
    })
}
@SuppressLint("UnrememberedMutableState")
@Composable
fun profile(name:String,userName:String,phoneNumber:String,email:String,profilePhoto:String) {
    var selectedImage by remember { mutableStateOf<Uri?>(null) }

// Create an ActivityResultLauncher to handle the gallery selection result
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
                selectedImage=it
            }
    )
    var isEdit by remember {
        mutableStateOf(false)
    }
    var newUsername by remember {
        mutableStateOf("")
    }
    newUsername=userName
    var newName by remember {
        mutableStateOf("")
    }
    newName=name
    var newPhoneNumber by remember {
        mutableStateOf("")
    }
    var result =mutableStateOf("")
    newPhoneNumber=phoneNumber
    var showProfilePhoto by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(255, 255, 255, 255), Color(
                            141,
                            153,
                            247,
                            255
                        )
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(shape = RoundedCornerShape(25.dp), elevation = 0.dp),
                Alignment.BottomCenter
            ) {
                AsyncImage(model = if(selectedImage!=null) selectedImage
                    else profilePhoto,
                    contentDescription = "photo",
                    modifier = Modifier
                        .shadow(elevation = 10.dp, shape = RoundedCornerShape(75.dp))
                        .border(
                            5.dp,
                            color = Color(22, 139, 179, 37),
                            shape = RoundedCornerShape(75.dp)
                        )
                        .clip(shape = RoundedCornerShape(75.dp))
                        .size(150.dp)
                        .clickable { showProfilePhoto = true },
                    contentScale = ContentScale.Crop
                )
                if(isEdit){
                    IconButton(
                        onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier
                            .padding(start = 105.dp, bottom = 20.dp)
                            .size(20.dp)
                            .align(Alignment.BottomCenter)
                            .shadow(shape = CircleShape, elevation = 5.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.White,
                                        Color(11, 179, 236, 255)
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change Profile Photo"
                        )
                    }
                }

            }
        }
        AnimatedVisibility(visible = isEdit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, end = 40.dp, bottom = 50.dp),
                backgroundColor = Color.LightGray,
                elevation = 5.dp,
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    5.dp, Color(
                        141,
                        153,
                        247,
                        255
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(22, 139, 179, 37),
                                    Color.White
                                )
                            )
                        ),
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material.Text(text = "Edit Details",
                        color= Color(99, 123, 158, 255),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start=50.dp,top=10.dp)
                    )
                    for(i in 0..2){
                        OutlinedTextField(
                            value = when(i){
                                0->newName
                                1->newUsername
                                else->newPhoneNumber
                            },
                            onValueChange = {
                                when(i){
                                    0->newName=it
                                    1->newUsername=it
                                    else-> if(it.length<=10) newPhoneNumber=it
                                }
                            },
                            maxLines = 1,
                            leadingIcon = {
                                when(i){
                                    0-> Icon(Icons.Default.Person, contentDescription = "person")
                                    1-> Icon(Icons.Default.Person, contentDescription = "person")
                                    else-> Icon(Icons.Default.Phone, contentDescription = "phone")
                                }
                            },
                            keyboardOptions = when(i){
                                0-> KeyboardOptions(keyboardType = KeyboardType.Text)
                                1-> KeyboardOptions(keyboardType = KeyboardType.Text)
                                else-> KeyboardOptions(keyboardType = KeyboardType.Phone)
                            },
                            label = {
                                androidx.compose.material.Text(text = when(i){
                                    0->"Name"
                                    1->"Username"
                                    else->"Phone Number"
                                })
                            },
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(0.7f)
                                .height(80.dp)
                        )
                    }
                    Text(text=result.value,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=20.dp,start=10.dp))
                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Button(
                            onClick = {
                                saveDetails(newUsername, newName, newPhoneNumber,selectedImage) { output ->
                                    if (output.isEmpty()) isEdit=false
                                    else {
                                        result.value=output
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(
                                text = "Update Details",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        AnimatedVisibility(visible = !isEdit) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, end = 40.dp, bottom = 50.dp),
                backgroundColor = Color.LightGray,
                elevation = 5.dp,
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    5.dp, Color(
                        141,
                        153,
                        247,
                        255
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    Color(22, 139, 179, 37),
                                    Color.White
                                )
                            )
                        )
                ) {
                    Text(
                        text = name,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth(),
                        fontFamily = FontFamily.Monospace,
                        fontStyle = FontStyle.Italic
                    )
                    for (i in 0..1) {
                        Row(
                            modifier = Modifier
                                .padding(top = 40.dp, start = 20.dp)
                                .fillMaxWidth()
                        ) {
                            Image(
                                imageVector = if (i == 0) Icons.Default.Email
                                else Icons.Default.Phone, contentDescription = "contact",
                                modifier = Modifier.size(30.dp)
                            )
                            Column(
                                modifier = Modifier.padding(start = 10.dp)
                                // horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (i == 0) "Email"
                                    else "Phone",
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start
                                )
                                Text(
                                    text = if (i == 0) email
                                    else phoneNumber,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(top = 4.dp),
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Button(
                            onClick = { isEdit=true },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Text(
                                text = "Edit Profile",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
    AnimatedVisibility(visible = showProfilePhoto) {
        AsyncImage(model = if(selectedImage!=null) selectedImage
        else profilePhoto,
            contentDescription = "photo",
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White)
                .padding(top=30.dp)
                //.shadow(elevation = 10.dp, shape = RoundedCornerShape(75.dp))
                .border(
                    5.dp,
                    color = Color(22, 139, 179, 37),
                   // shape = RoundedCornerShape(150.dp)
                )
               // .clip(shape =RoundedCornerShape(150.dp))
                .size(250.dp)
                .clickable { showProfilePhoto=false },
           contentScale = ContentScale.Crop
        )
        Text(
            text="Click photo to go back",
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.W500,
            modifier = Modifier.padding(top=2.dp).fillMaxWidth()
        )
    }
}